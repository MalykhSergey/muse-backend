package ru.t1.debut.muse.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventMessage {
    private EventType eventType;
    private List<UUID> usersUUID;
}
