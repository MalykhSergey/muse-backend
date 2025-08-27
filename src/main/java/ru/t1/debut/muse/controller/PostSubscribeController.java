package ru.t1.debut.muse.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.*;
import ru.t1.debut.muse.service.PostSubscribeService;

@RestController
@RequestMapping("/subscribes/posts")
@Validated
public class PostSubscribeController {
    private final PostSubscribeService postSubscribeService;

    @Autowired
    public PostSubscribeController(PostSubscribeService postSubscribeService) {
        this.postSubscribeService = postSubscribeService;
    }

    @Operation(summary = "Получить подписанные посты")
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAllPostSubscribe(@RequestParam @Min(0) int page, @RequestParam @Min(1) @Max(100) int size, @RequestParam(required = false) SortBy sortBy, @RequestParam(required = false) SortDir sortDir, @AuthenticationPrincipal UserDTO userDTO) {
        if (sortBy == null) {
            sortBy = SortBy.CREATED;
        }
        if (sortDir == null) {
            sortDir = SortDir.DESC;
        }
        return ResponseEntity.ok(postSubscribeService.getAll(userDTO, page, size, sortBy, sortDir));
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
