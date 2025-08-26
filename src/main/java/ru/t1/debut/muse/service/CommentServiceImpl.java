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
import ru.t1.debut.muse.entity.Role;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.entity.event.CreateCommentEvent;
import ru.t1.debut.muse.entity.event.EventMessage;
import ru.t1.debut.muse.entity.event.EventType;
import ru.t1.debut.muse.entity.event.ModeratorEvent;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.CommentRepository;
import ru.t1.debut.muse.repository.PostRepository;

import java.time.Instant;
import java.util.Set;
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
        Instant now = Instant.now();
        Comment comment = commentRepository.save(new Comment(null, createCommentRequest.getBody(), author, post, now, now));
        sendNotifications(post, comment);
        return new CommentDTO(comment.getId(), comment.getBody(), new UserDTO(author), createCommentRequest.getPostId(), now, now);
    }

    private void sendNotifications(Post post, Comment comment) {
        Set<UUID> parentPostSubscribers = postSubscribeService.getSubscribersUUIDForPost(post.getId());
        EventMessage eventMessage = new CreateCommentEvent(EventType.NEW_COMMENT_FOR_POST, parentPostSubscribers, comment.getReducedTitle(), post.getId(), comment.getId());
        if (post.getAuthor() != null && post.getAuthor().getInternalId() != null) {
            parentPostSubscribers.remove(post.getAuthor().getInternalId());
            EventMessage eventMessageForPostAuthor = new CreateCommentEvent(EventType.NEW_COMMENT_FOR_YOUR_POST, Set.of(post.getAuthor().getInternalId()), comment.getReducedTitle(), post.getId(), comment.getId());
            notificationService.sendNotification(eventMessageForPostAuthor);
        }
        notificationService.sendNotification(eventMessage);
    }

    @Override
    public void update(long commentId, UpdateCommentRequest updateCommentRequest, UserDTO authUserDTO) {
        if (userService.checkUserRole(authUserDTO, Role.ROLE_MUSE_MODER)) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(ResourceNotFoundException::new);
            sendNotificationToAuthor(authUserDTO, comment, EventType.MODERATOR_EDIT_YOUR_COMMENT);
            commentRepository.updateById(commentId, updateCommentRequest.getBody(), Instant.now());
        }
        User author = userService.getUser(authUserDTO);
        commentRepository.updateByIdAndAuthorId(commentId, updateCommentRequest.getBody(), Instant.now(), author.getId());
    }

    @Override
    public void delete(long commentId, UserDTO authUserDTO) {
        if (userService.checkUserRole(authUserDTO, Role.ROLE_MUSE_MODER)) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(ResourceNotFoundException::new);
            sendNotificationToAuthor(authUserDTO, comment, EventType.MODERATOR_DELETE_YOUR_COMMENT);
            commentRepository.deleteById(commentId);
        }
        User author = userService.getUser(authUserDTO);
        commentRepository.deleteByIdAndAuthorId(commentId, author.getId());
    }

    private void sendNotificationToAuthor(UserDTO authUserDTO, Comment comment, EventType eventType) {
        User commentAuthor = comment.getAuthor();
        if (commentAuthor != null && commentAuthor.getInternalId() != null) {
            // Модератор удалил свой коммент
            if (authUserDTO.internalId().equals(commentAuthor.getInternalId())) return;
            EventMessage eventMessage = new ModeratorEvent(eventType, Set.of(commentAuthor.getInternalId()), comment.getReducedTitle(), comment.getId());
            notificationService.sendNotification(eventMessage);
        }
    }
}
