package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.NotNull;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.Tag;

public record TagDTO(
        @NotNull
        Long id,
        String name,
        Long postId
) {
    public static TagDTO fromTag(Tag tag) {
        Post post = tag.getPost();
        Long postId = post == null ? null : post.getId();
        return new TagDTO(tag.getId(), tag.getName(), postId);
    }
}
