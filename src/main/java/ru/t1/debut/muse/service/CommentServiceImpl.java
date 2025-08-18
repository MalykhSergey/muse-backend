package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CommentDTO;
import ru.t1.debut.muse.dto.CreateCommentRequest;
import ru.t1.debut.muse.dto.UpdateCommentRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.*;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.CommentRepository;
import ru.t1.debut.muse.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    private final PostSubscribeService postSubscribeService;

    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, NotificationService notificationService,
                              PostSubscribeService postSubscribeService,
                              PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.postSubscribeService = postSubscribeService;
        this.postRepository = postRepository;
    }

    @Override
    public Page<CommentDTO> getPostComments(long postId, Pageable pageable) {
        return commentRepository.findAllByPost_IdOrderById(postId, pageable).map(comment -> new CommentDTO(comment.getId(), comment.getBody(), new UserDTO(comment.getAuthor()), postId, comment.getCreated(), comment.getUpdated()));
    }

    @Override
    public CommentDTO create(CreateCommentRequest createCommentRequest, UserDTO authorDTO) {
        User author = userService.getUser(authorDTO);
        Post post = postRepository.findById(createCommentRequest.getPostId()).orElseThrow(ResourceNotFoundException::new);
        LocalDateTime now = LocalDateTime.now();
        Comment comment = commentRepository.save(new Comment(null, createCommentRequest.getBody(), author, post, now, now));
        sendNotifications(post);
        return new CommentDTO(comment.getId(), comment.getBody(), new UserDTO(author), createCommentRequest.getPostId(), now, now);
    }

    private void sendNotifications(Post post) {
        List<UUID> parentPostSubscribers = postSubscribeService.getSubscribersUUIDForPost(post.getId());
        parentPostSubscribers.remove(post.getAuthor().getInternalId());
        EventMessage eventMessage = new EventMessage(EventType.NEW_COMMENT_FOR_POST, parentPostSubscribers);
        EventMessage eventMessageForPostAuthor = new EventMessage(EventType.NEW_COMMENT_FOR_YOUR_POST, List.of(post.getAuthor().getInternalId()));
        notificationService.sendNotification(eventMessage);
        notificationService.sendNotification(eventMessageForPostAuthor);
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
