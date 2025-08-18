package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.*;

public interface PostService {
    PostDTO createPost(CreatePostRequest createPostRequest, UserDTO userDTO);

    void updatePost(UpdatePostRequest updatePostRequest, Long id, UserDTO userDTO);

    void deletePost(Long id, UserDTO author);

    PostDTO getPost(Long id, UserDTO userDTO);

    Page<PostDTO> getPosts(Long parentId, Long tagId, UserDTO userDTO, String query, int page, int size, SortBy sortBy, SortDir sortDir);

    void setAnswer(SetAnswerRequest setAnswerRequest, Long id, UserDTO author);
}
