package ru.t1.debut.muse.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage {
    private EventType eventType;
    private Set<UUID> usersUUID;
    private String description;
}
