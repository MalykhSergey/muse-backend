package ru.t1.debut.muse.service;

import ru.t1.debut.muse.dto.CommentDTO;
import ru.t1.debut.muse.dto.CreateCommentRequest;
import ru.t1.debut.muse.dto.UpdateCommentRequest;
import ru.t1.debut.muse.dto.UserDTO;

public interface CommentService {
    CommentDTO create(CreateCommentRequest createCommentRequest, UserDTO authorDTO);

    void update(long commentId, UpdateCommentRequest updateCommentRequest, UserDTO authorDTO);

    void delete(long commentId, UserDTO authorDTO);
}
