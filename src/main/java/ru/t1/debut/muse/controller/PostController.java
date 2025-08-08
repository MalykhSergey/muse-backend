package ru.t1.debut.muse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.services.PostService;

@RestController("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Получить список постов",
            description = "Возвращает пагинированный список всех постов",
            parameters = {
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "10"),
                    @Parameter(name = "sort", description = "Поля для сортировки", example = "createdAt,desc")
            }
    )
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает пост по указанному идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @Operation(
            summary = "Создать новый пост, обновить существующий",
            description = "Создает новый пост от имени аутентифицированного пользователя"
    )
    @PostMapping
    public ResponseEntity<PostDTO> savePost(@RequestBody PostDTO postDTO, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        return new ResponseEntity<>(postService.savePost(postDTO, author), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить пост",
            description = "Удаляет пост по идентификатору (только для автора поста)"
    )
    public void deletePost(@PathVariable Long id, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        postService.deletePost(id, author);
    }

}
