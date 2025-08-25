package ru.t1.debut.muse.entity.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateAnswerEvent extends EventMessage {
    private Long parentId;
    private Long answerId;

    public CreateAnswerEvent(EventType eventType, Set<UUID> usersUUID, String description, Long parentId, Long answerId) {
        super(eventType, usersUUID, description);
        this.parentId = parentId;
        this.answerId = answerId;
    }
}
