package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.CreatePostRequest;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UpdatePostRequest;
import ru.t1.debut.muse.dto.UserDTO;

import java.util.Optional;

public interface PostService {
    PostDTO createPost(CreatePostRequest createPostRequest, UserDTO userDTO);

    void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO userDTO);

    void deletePost(Long id, UserDTO author);

    PostDTO getPost(Long id, UserDTO userDTO);

    Page<PostDTO>  getPosts(Long parentId, UserDTO userDTO, Optional<String> query, int page, int size, SortBy sortBy, SortDir sortDir);
}
