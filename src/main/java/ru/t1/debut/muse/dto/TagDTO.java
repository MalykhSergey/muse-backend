package ru.t1.debut.muse.dto;

import ru.t1.debut.muse.entity.Tag;

public record TagDTO(
        Long id,
        String name,
        Long postId
) {
    public static TagDTO fromTag(Tag tag) {
        return new TagDTO(tag.getId(),tag.getName(),tag.getPost().getId());
    }
}
