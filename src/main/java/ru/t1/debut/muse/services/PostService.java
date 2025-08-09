package ru.t1.debut.muse.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.dto.CreatePostRequest;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UpdatePostRequest;
import ru.t1.debut.muse.dto.UserDTO;

public interface PostService {
    Page<PostDTO> getPosts(Pageable pageable);

    PostDTO createPost(CreatePostRequest createPostRequest, UserDTO userDTO);

    void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO userDTO);

    void deletePost(Long id, UserDTO author);

    PostDTO getPost(Long id);
}
