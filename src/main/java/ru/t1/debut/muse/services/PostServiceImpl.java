package ru.t1.debut.muse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.PostRepository;

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
    public PostDTO savePost(PostDTO postDTO, UserDTO userDTO) {
        User author = userService.getUserByInternalId(userDTO.internalId()).orElseGet(() -> userService.createUser(userDTO));
        return PostDTO.fromPost(postRepository.save(new Post(postDTO, author)));
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
