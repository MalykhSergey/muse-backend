package ru.t1.debut.muse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import java.time.LocalDateTime;

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
    public Page<PostDTO> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDTO::fromPost);
    }

    @Override
    public PostDTO createPost(CreatePostRequest createPostRequest, UserDTO authorDTO) {
        User author = userService.getUserByInternalId(authorDTO.internalId()).orElseGet(() -> userService.createUser(authorDTO));
        Post parent = null;
        if (createPostRequest.getParentId() != null) {
            parent = new Post(createPostRequest.getParentId(), null, null, null, null, null, null, null, null);
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
                now
        );
        return PostDTO.fromPost(postRepository.save(post));
    }

    @Override
    public void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO authorDTO) {
        User author = userService.getUserByInternalId(authorDTO.internalId()).orElseGet(() -> userService.createUser(authorDTO));
        Post answer = null;
        if (updatePostRequest.getAnswerId() != null) {
            answer = new Post(updatePostRequest.getAnswerId(), null, null, null, null, null, null, null, null);
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
                now
        );
        int updated = 0;
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
        postRepository.deleteByIdAndAuthor_InternalId(id, author.internalId());
    }

    @Override
    public PostDTO getPost(Long id) {
        return postRepository.findById(id).map(PostDTO::fromPost).orElseThrow(ResourceNotFoundException::new);
    }
}
