package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.dto.CommentDTO;
import ru.t1.debut.muse.dto.CreateCommentRequest;
import ru.t1.debut.muse.dto.UpdateCommentRequest;
import ru.t1.debut.muse.dto.UserDTO;

public interface CommentService {
    Page<CommentDTO> getPostComments(long postId, Pageable pageable);

    CommentDTO create(CreateCommentRequest createCommentRequest, UserDTO authorDTO);

    void update(long commentId, UpdateCommentRequest updateCommentRequest, UserDTO authorDTO);

    void delete(long commentId, UserDTO authorDTO);
}
