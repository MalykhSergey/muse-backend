package ru.t1.debut.muse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.*;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.Tag;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.entity.event.CreateAnswerEvent;
import ru.t1.debut.muse.entity.event.CreatePostForTag;
import ru.t1.debut.muse.entity.event.EventMessage;
import ru.t1.debut.muse.entity.event.EventType;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.PostRepository;
import ru.t1.debut.muse.repository.PostSearchProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostSubscribeService postSubscribeService;
    private final TagSubscribeService tagSubscribeService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    PostServiceImpl(PostRepository postRepository, PostSubscribeService postSubscribeService, TagSubscribeService tagSubscribeService, NotificationService notificationService, UserService userService, ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.postSubscribeService = postSubscribeService;
        this.tagSubscribeService = tagSubscribeService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Page<PostDTO> getPosts(Long parentId, Long tagId, UserDTO userDTO, String query, int page, int size, SortBy sortBy, SortDir sortDir) {
        User authUser = userService.getUser(userDTO);
        long offset = (long) page * size;
        List<PostSearchProjection> result;
        if (tagId != null) {
            result = postRepository.getAllByTagId(authUser.getId(), tagId, size, offset, sortBy.name(), sortDir.name());
        } else if (query != null) {
            result = postRepository.searchPosts(query, authUser.getId(), size, offset, sortBy.name(), sortDir.name());
        } else {
            result = postRepository.getAllByParentId(authUser.getId(), parentId, size, offset, sortBy.name(), sortDir.name());
        }
        long total = result.isEmpty() ? 0 : result.getFirst().getTotalCount();
        return new PageImpl<>(result.stream().map(PostDTO::fromPostSearchResult).toList(), PageRequest.of(page, size), total);
    }

    // Можно установить ответом пост-вопрос или вообще другой пост
    @Override
    public void setAnswer(SetAnswerRequest setAnswerRequest, Long postId, UserDTO userDTO) {
        User authUser = userService.getUser(userDTO);
        postRepository.setAnswerByIdAndAuthorId(setAnswerRequest.getAnswerId(), postId, authUser.getId());
    }

    @SneakyThrows
    @Override
    public PostDTO createPost(CreatePostRequest createPostRequest, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        Post parent = null;
        if (createPostRequest.getParentId() != null) {
            parent = postRepository.findById(createPostRequest.getParentId()).orElseThrow(ResourceNotFoundException::new);
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Tag> tags = createPostRequest.getTags().stream().map(tag -> new Tag(tag.id(), null, null, null)).collect(Collectors.toSet());
        Post post = new Post(null, createPostRequest.getTitle(), createPostRequest.getBody(), createPostRequest.getPostType(), author, parent, null, now, now, null, null, tags);
        Post save = postRepository.save(post);
        sendNotifications(parent, post, tags);
        postSubscribeService.create(post, author);
        return PostDTO.fromNewPost(save, objectMapper.writeValueAsString(createPostRequest.getTags()));
    }

    private void sendNotifications(Post parent, Post answer, Set<Tag> tags) {
        boolean author_UUID_exists = parent != null && parent.getAuthor() != null && parent.getAuthor().getInternalId() != null;
        if (parent != null) {
            Set<UUID> parentPostSubscribers = postSubscribeService.getSubscribersUUIDForPost(parent.getId());
            if (author_UUID_exists) {
                parentPostSubscribers.remove(parent.getAuthor().getInternalId());
                EventMessage eventMessageForAuthor = new CreateAnswerEvent(EventType.NEW_ANSWER_FOR_YOUR_POST, Set.of(parent.getAuthor().getInternalId()), parent.getId(), answer.getId());
                notificationService.sendNotification(eventMessageForAuthor);
            }
            EventMessage eventMessage = new CreateAnswerEvent(EventType.NEW_ANSWER_FOR_POST, parentPostSubscribers, parent.getId(), answer.getId());
            notificationService.sendNotification(eventMessage);
        }
        if (!tags.isEmpty()) {
            // Надо будет переписать на один запрос
            for (Tag tag : tags) {
                Set<UUID> tagSubscribers = tagSubscribeService.getSubscribersUUIDForTag(tag.getId());
                if (author_UUID_exists)
                    tagSubscribers.remove(parent.getAuthor().getInternalId());
                EventMessage eventMessage = new CreatePostForTag(tagSubscribers, answer.getId(), tag.getName());
                notificationService.sendNotification(eventMessage);
            }
        }
    }

    @Override
    public void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        Post answer = null;
        if (updatePostRequest.getAnswerId() != null) {
            answer = new Post(updatePostRequest.getAnswerId(), null, null, null, null, null, null, null, null, null, null, null);
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Tag> tags = updatePostRequest.getTags().stream().map(tag -> new Tag(tag.id(), null, null, null)).collect(Collectors.toSet());
        Post post = postRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!post.getAuthor().getId().equals(author.getId())) {
            // Попытка отредактировать чужой пост
            return;
        }
        post.setTitle(updatePostRequest.getTitle());
        post.setBody(updatePostRequest.getBody());
        post.setAnswer(answer);
        post.setUpdated(now);
        post.setTags(tags);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long id, UserDTO author) {
        User authUser = userService.getUser(author);
        postRepository.deleteByIdAndAuthorId(id, authUser.getId());
    }

    @Override
    public PostDTO getPost(Long id, UserDTO userDTO) {
        User authUser = userService.getUser(userDTO);
        return postRepository.getById(authUser.getId(), id).map(PostDTO::fromPostSearchResult).orElseThrow(ResourceNotFoundException::new);
    }
}
