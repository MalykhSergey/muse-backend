package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CommentDTO;
import ru.t1.debut.muse.dto.CreateCommentRequest;
import ru.t1.debut.muse.dto.UpdateCommentRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Comment;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.repository.CommentRepository;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    @Override
    public Page<CommentDTO> getPostComments(long postId, Pageable pageable) {
        return commentRepository.findAllByPost_IdOrderById(postId, pageable).map(comment -> new CommentDTO(comment.getId(), comment.getBody(), new UserDTO(comment.getAuthor()), postId, comment.getCreated(), comment.getUpdated()));
    }

    @Override
    public CommentDTO create(CreateCommentRequest createCommentRequest, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        Post post = new Post();
        post.setId(createCommentRequest.getPostId());
        LocalDateTime now = LocalDateTime.now();
        Comment comment = commentRepository.save(new Comment(null, createCommentRequest.getBody(), author, post, now, now));
        return new CommentDTO(comment.getId(), comment.getBody(), new UserDTO(author), createCommentRequest.getPostId(), now, now);
    }

    @Override
    public void update(long commentId, UpdateCommentRequest updateCommentRequest, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        commentRepository.updateByIdAndAuthorId(commentId, updateCommentRequest.getBody(), LocalDateTime.now(), author.getId());
    }

    @Override
    public void delete(long commentId, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        commentRepository.deleteByIdAndAuthorId(commentId, author.getId());
    }
}
