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
import ru.t1.debut.muse.dto.TagSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.service.TagSubscribeService;

@RestController
@RequestMapping("/subscribes/tags")
public class TagSubscribeController {
    private final TagSubscribeService tagSubscribeService;

    @Autowired
    public TagSubscribeController(TagSubscribeService tagSubscribeService) {
        this.tagSubscribeService = tagSubscribeService;
    }

    @Operation(summary = "Получить подписки на тэги")
    @GetMapping
    public ResponseEntity<Page<TagSubscribeDTO>> getAllTagSubscribe(@RequestParam int size, @RequestParam int page, @AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(tagSubscribeService.getAll(PageRequest.of(page, size), userDTO));
    }

    @Operation(summary = "Создать подписку на тэг")
    @PostMapping("/{id}")
    public ResponseEntity<TagSubscribeDTO> createTagSubscribe(@PathVariable long id, @Valid @RequestBody CreateSubscribeRequest createSubscribeRequest, @AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(tagSubscribeService.create(id, createSubscribeRequest, userDTO));
    }

    @Operation(summary = "Обновить подписку на тэг (пока повторяет функцию создания)")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTagSubscribe(@PathVariable long id, @Valid @RequestBody UpdateSubscribeRequest updateSubscribeRequest, @AuthenticationPrincipal UserDTO userDTO) {
        tagSubscribeService.update(id, updateSubscribeRequest, userDTO);
    }

    @Operation(summary = "Удалить подписку на тэг")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTagSubscribe(@PathVariable long id, @AuthenticationPrincipal UserDTO userDTO) {
        tagSubscribeService.delete(id, userDTO);
    }
}
