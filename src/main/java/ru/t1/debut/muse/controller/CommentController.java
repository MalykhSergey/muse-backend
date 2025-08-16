package ru.t1.debut.muse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.CommentDTO;
import ru.t1.debut.muse.dto.CreateCommentRequest;
import ru.t1.debut.muse.dto.UpdateCommentRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    ResponseEntity<Page<CommentDTO>> getPostComments(
            @RequestParam("postId") long postId,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getPostComments(postId, pageable));
    }

    @PostMapping
    ResponseEntity<CommentDTO> createComment(@RequestBody CreateCommentRequest createCommentRequest, @AuthenticationPrincipal Jwt authUser) {
        UserDTO userDTO = new UserDTO(authUser);
        return new ResponseEntity<>(commentService.create(createCommentRequest, userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateComment(@PathVariable long id, @RequestBody UpdateCommentRequest updateCommentRequest, @AuthenticationPrincipal Jwt authUser) {
        UserDTO userDTO = new UserDTO(authUser);
        commentService.update(id, updateCommentRequest, userDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable long id, @AuthenticationPrincipal Jwt authUser) {
        UserDTO userDTO = new UserDTO(authUser);
        commentService.delete(id, userDTO);
    }
}
