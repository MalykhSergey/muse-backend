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
import ru.t1.debut.muse.entity.*;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.PostRepository;
import ru.t1.debut.muse.repository.PostSearchProjection;
import ru.t1.debut.muse.repository.PostSubscribeRepository;
import ru.t1.debut.muse.repository.TagSubscribeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostSubscribeRepository postSubscribeRepository;
    private final TagSubscribeRepository tagSubscribeRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    PostServiceImpl(PostRepository postRepository, PostSubscribeRepository postSubscribeRepository, TagSubscribeRepository tagSubscribeRepository, NotificationService notificationService, UserService userService, ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.postSubscribeRepository = postSubscribeRepository;
        this.tagSubscribeRepository = tagSubscribeRepository;
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
        sendNotifications(parent, tags);
        return PostDTO.fromNewPost(save, objectMapper.writeValueAsString(createPostRequest.getTags()));
    }

    private void sendNotifications(Post post, Set<Tag> tags) {
        if (post != null) {
            List<UUID> parentPostSubscribers = postSubscribeRepository.findNotificationEnabledUserInternalIdsByPostId(post.getId());
            parentPostSubscribers.remove(post.getAuthor().getInternalId());
            EventMessage eventMessage = new EventMessage(EventType.NEW_ANSWER_FOR_POST, parentPostSubscribers);
            EventMessage eventMessageForAuthor = new EventMessage(EventType.NEW_ANSWER_FOR_YOUR_POST, List.of(post.getAuthor().getInternalId()));
            notificationService.sendNotification(eventMessage);
            notificationService.sendNotification(eventMessageForAuthor);
        }
        if (!tags.isEmpty()) {
            for (Tag tag : tags) {
                // Надо будет переписать на один запрос
                List<UUID> tagSubscribers = tagSubscribeRepository.findNotificationEnabledUserInternalIdsByTagId(tag.getId());
                EventMessage eventMessage = new EventMessage(EventType.NEW_POST_FOR_TAG, tagSubscribers);
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
