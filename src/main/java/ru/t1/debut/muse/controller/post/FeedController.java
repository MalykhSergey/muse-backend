package ru.t1.debut.muse.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.debut.muse.dto.PostDTO;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.service.PostService;

@RestController
@RequestMapping("/feed")
@Validated
public class FeedController {
    private final PostService postService;

    @Autowired
    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Возвращает открытые вопросы по подпискам пользователя")
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPostsBySubscribedTags(
            @RequestParam(required = false, defaultValue = "false") Boolean opened,
            @RequestParam @Min(0) int page,
            @RequestParam @Min(1) @Max(100) int size,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) SortDir sortDir,
            @AuthenticationPrincipal UserDTO userDTO) {
        if (sortBy == null) {
            sortBy = SortBy.CREATED;
        }
        if (sortDir == null) {
            sortDir = SortDir.DESC;
        }
        return ResponseEntity.ok(postService.getPostsBySubscribedTags(userDTO,opened, page, size, sortBy, sortDir));
    }
}
