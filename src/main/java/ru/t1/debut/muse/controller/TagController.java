package ru.t1.debut.muse.controller;

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

    @GetMapping
    public ResponseEntity<Page<TagDTO>> getTags(Pageable pageable) {
        return ResponseEntity.ok(tagService.getTags(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }
}
