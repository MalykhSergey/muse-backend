package ru.t1.debut.muse.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.*;
import ru.t1.debut.muse.service.PostService;

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
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long tagId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) SortDir sortDir,
            @AuthenticationPrincipal UserDTO userDTO) {
        if (sortBy == null) {
            sortBy = SortBy.CREATED;
        }
        if (sortDir == null) {
            sortDir = SortDir.DESC;
        }
        return ResponseEntity.ok(postService.getPosts(parentId, tagId, userDTO, query, page, size, sortBy, sortDir));
    }

    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает пост по указанному идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id, @AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(postService.getPost(id, userDTO));
    }

    @Operation(
            summary = "Создать новый пост",
            description = "Создает новый пост от имени аутентифицированного пользователя"
    )
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostRequest createPostRequest, @AuthenticationPrincipal UserDTO userDTO) {
        return new ResponseEntity<>(postService.createPost(createPostRequest, userDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить пост")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@PathVariable Long id, @Valid @RequestBody UpdatePostRequest updatePostRequest, @AuthenticationPrincipal UserDTO userDTO) {
        postService.updatePost(updatePostRequest, id, userDTO);
    }

    @Operation(summary = "Устанавливает ответ к посту")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setAnswer(@PathVariable Long id, @Valid @RequestBody SetAnswerRequest setAnswerRequest, @AuthenticationPrincipal UserDTO userDTO) {
        postService.setAnswer(setAnswerRequest, id, userDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить пост",
            description = "Удаляет пост по идентификатору (только для автора поста)"
    )
    public void deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDTO userDTO) {
        postService.deletePost(id, userDTO);
    }

}


