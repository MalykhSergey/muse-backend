package ru.t1.debut.muse.entity.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModeratorEvent extends EventMessage {
    private Long entityId;

    public ModeratorEvent(EventType eventType, Set<UUID> usersUUID, String description, Long entityId) {
        super(eventType, usersUUID, description);
        this.entityId = entityId;
    }
}
