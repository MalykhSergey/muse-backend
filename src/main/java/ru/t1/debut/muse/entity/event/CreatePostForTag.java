package ru.t1.debut.muse.entity.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePostForTag extends EventMessage {
    private Long postId;
    private String tagName;

    public CreatePostForTag(Set<UUID> receivers, String description, Long postId, String tagName) {
        super(EventType.NEW_POST_FOR_TAG, receivers, description);
        this.postId = postId;
        this.tagName = tagName;
    }
}
