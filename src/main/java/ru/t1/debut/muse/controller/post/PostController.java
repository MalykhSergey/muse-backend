package ru.t1.debut.muse.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.CreatePostRequest;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UpdatePostRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.service.PostService;

import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Получить список постов",
            description = "Возвращает пагинированный список постов, по параметру поиска или постов, если параметр не задан"
    )
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(required = false) Optional<String> query,
            @RequestParam(required = false) Long parentId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) SortDir sortDir,
            @AuthenticationPrincipal Jwt user) {
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(postService.getPosts(parentId, userDTO, query, page, size, sortBy, sortDir));
    }

    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает пост по указанному идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id, @AuthenticationPrincipal Jwt user) {
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(postService.getPost(id, userDTO));
    }

    @Operation(
            summary = "Создать новый пост",
            description = "Создает новый пост от имени аутентифицированного пользователя"
    )
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostRequest createPostRequest, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        return new ResponseEntity<>(postService.createPost(createPostRequest, author), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновить пост",
            description = "Обновляет пост аутентифицированного пользователя"
    )
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@PathVariable Long id, @RequestBody UpdatePostRequest updatePostRequest, @AuthenticationPrincipal Jwt user) {
        UserDTO author = new UserDTO(user);
        postService.updatePost(updatePostRequest, id, author);
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


