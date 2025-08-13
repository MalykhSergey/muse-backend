package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CreatePostRequest;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UpdatePostRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.PostRepository;
import ru.t1.debut.muse.repository.PostSearchProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public Page<PostDTO> getPosts(Long parentId, UserDTO userDTO, Optional<String> query, Pageable pageable) {
        User authUser = userService.getUserByInternalId(userDTO.internalId()).orElseGet(() -> userService.createUser(userDTO));
        List<PostSearchProjection> result = query.map(s -> postRepository.searchPosts(s, authUser.getId(), pageable.getPageSize(), pageable.getOffset()))
                .orElseGet(() -> postRepository.getAllByParentId(authUser.getId(), parentId, pageable.getPageSize(), pageable.getOffset()));
        PostSearchProjection first = result.getFirst();
        long total = first == null ? 0 : first.getTotalCount();
        return new PageImpl<>(result.stream().map(PostDTO::fromPostSearchResult).toList(), pageable, total);
    }

    @Override
    public PostDTO createPost(CreatePostRequest createPostRequest, UserDTO authorDTO) {
        User author = userService.getUserByInternalId(authorDTO.internalId()).orElseGet(() -> userService.createUser(authorDTO));
        Post parent = null;
        if (createPostRequest.getParentId() != null) {
            parent = new Post(createPostRequest.getParentId(), null, null, null, null, null, null, null, null, null, null);
        }
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post(
                null,
                createPostRequest.getTitle(),
                createPostRequest.getBody(),
                createPostRequest.getPostType(),
                author,
                parent,
                null,
                now,
                now,
                null,
                null
        );
        return PostDTO.fromNewPost(postRepository.save(post));
    }

    @Override
    public void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO authorDTO) {
        User author = userService.getUserByInternalId(authorDTO.internalId()).orElseGet(() -> userService.createUser(authorDTO));
        Post answer = null;
        if (updatePostRequest.getAnswerId() != null) {
            answer = new Post(updatePostRequest.getAnswerId(), null, null, null, null, null, null, null, null, null, null);
        }
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post(
                id,
                updatePostRequest.getTitle(),
                updatePostRequest.getBody(),
                null,
                author,
                null,
                answer,
                null,
                now, null, null
        );
        int updated;
        if (answer == null)
            updated = postRepository.updatePostWithoutAnswerByIdAndAuthorId(post);
        else
            updated = postRepository.updatePostWithAnswerByIdAndAuthorId(post);
        if (updated == 0) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void deletePost(Long id, UserDTO author) {
        User authUser = userService.getUserByInternalId(author.internalId()).get();
        postRepository.deleteByIdAndAuthorId(id, authUser.getId());
    }

    @Override
    public PostDTO getPost(Long id, UserDTO userDTO) {
        User authUser = userService.getUserByInternalId(userDTO.internalId()).orElseGet(() -> userService.createUser(userDTO));
        return postRepository.getById(authUser.getId(), id).map(PostDTO::fromPostSearchResult).orElseThrow(ResourceNotFoundException::new);
    }
}
