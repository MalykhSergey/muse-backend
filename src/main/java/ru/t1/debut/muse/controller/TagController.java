package ru.t1.debut.muse.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.debut.muse.dto.TagDTO;
import ru.t1.debut.muse.service.TagService;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Получить список тэгов. Если указан префикс, возвращает тэги с префиксом")
    @GetMapping
    public ResponseEntity<Page<TagDTO>> getTags(@RequestParam String prefix, Pageable pageable) {
        if (prefix != null && !prefix.isBlank())
            return ResponseEntity.ok(tagService.getTagsByPrefix(prefix, pageable));
        return ResponseEntity.ok(tagService.getTags(pageable));
    }

    @Operation(summary = "Получить тэг")
    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }
}
