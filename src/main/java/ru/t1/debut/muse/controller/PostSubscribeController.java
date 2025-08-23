package ru.t1.debut.muse.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.CreateSubscribeRequest;
import ru.t1.debut.muse.dto.PostSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.service.PostSubscribeService;

@RestController
@RequestMapping("/subscribes/posts")
public class PostSubscribeController {
    private final PostSubscribeService postSubscribeService;

    @Autowired
    public PostSubscribeController(PostSubscribeService postSubscribeService) {
        this.postSubscribeService = postSubscribeService;
    }

    @Operation(summary = "Получить подписки на посты")
    @GetMapping
    public ResponseEntity<Page<PostSubscribeDTO>> getAllPostSubscribe(@RequestParam int size, @RequestParam int page, @AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(postSubscribeService.getAll(PageRequest.of(page, size), userDTO));
    }

    @Operation(summary = "Создать подписку на пост")
    @PostMapping("/{id}")
    public ResponseEntity<PostSubscribeDTO> createPostSubscribe(@PathVariable long id, @Valid @RequestBody CreateSubscribeRequest createSubscribeRequest, @AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(postSubscribeService.create(id, createSubscribeRequest, userDTO));
    }

    @Operation(summary = "Обновить подписку на пост (пока повторяет функцию создания)")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePostSubscribe(@PathVariable long id, @Valid @RequestBody UpdateSubscribeRequest updateSubscribeRequest, @AuthenticationPrincipal UserDTO userDTO) {
        postSubscribeService.update(id, updateSubscribeRequest, userDTO);
    }

    @Operation(summary = "Удалить подписку на пост")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostSubscribe(@PathVariable long id, @AuthenticationPrincipal UserDTO userDTO) {
        postSubscribeService.delete(id, userDTO);
    }
}
