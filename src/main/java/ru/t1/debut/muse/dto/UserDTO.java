package ru.t1.debut.muse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.entity.UserType;

import java.util.Collection;
import java.util.UUID;

public record UserDTO(
        Long id,
        Long externalId,
        UUID internalId,
        UserType userType,
        String name,
        @JsonIgnore
        Collection<GrantedAuthority> authorities
) {
    public UserDTO(User user) {
        this(user.getId(), user.getExternalId(), user.getInternalId(), user.getUserType(), user.getName(), null);
    }
}
