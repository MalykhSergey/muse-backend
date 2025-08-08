package ru.t1.debut.muse.dto;

import org.springframework.security.oauth2.jwt.Jwt;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.entity.UserType;

import java.util.UUID;

public record UserDTO(
        Long id,
        Long externalId,
        UUID internalId,
        UserType userType,
        String name
) {
    public UserDTO(User user) {
        this(user.getId(), user.getExternalId(), user.getInternalId(), user.getUserType(), user.getName());
    }

    public UserDTO(Jwt jwt) {
        this(null, null, UUID.fromString(jwt.getSubject()), UserType.INTERNAL, jwt.getClaims().get("preferred_username").toString());
    }
}
