package ru.t1.debut.muse.services;

import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByInternalId(UUID uuid);
    User createUser(UserDTO userDTO);
}
