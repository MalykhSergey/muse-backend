package ru.t1.debut.muse.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.dto.VoteDTO;
import ru.t1.debut.muse.entity.VoteType;
import ru.t1.debut.muse.service.VoteService;

@RestController
@RequestMapping("/posts/{id}/votes")
public class VoteController {
    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Operation(summary = "Голосовать за пост", description = "Создает голос за пост от имени аутентифицированного пользователя")
    @PostMapping
    public void createVote(@PathVariable("id") Long postId, @RequestBody VoteType voteType, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        voteService.createVote(author, voteType, postId);
    }

    @Operation(summary = "Получить голос за пост", description = "Загружает голос авторизованного пользователя на указанный пост")
    @GetMapping
    public ResponseEntity<VoteDTO> getVote(@PathVariable("id") Long postId, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        return ResponseEntity.ok(VoteDTO.fromVote(voteService.getUserVoteForPost(postId, author)));
    }
}
