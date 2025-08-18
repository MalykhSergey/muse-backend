package ru.t1.debut.muse.entity.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCommentEvent extends EventMessage {
    private Long postId;
    private Long commentId;

    public CreateCommentEvent(EventType eventType, Set<UUID> usersUUID, Long postId, Long commentId) {
        super(eventType, usersUUID);
        this.postId = postId;
        this.commentId = commentId;
    }
}
