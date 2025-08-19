package ru.t1.debut.muse.service;

import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.User;

public interface UserService {
    User getUser(UserDTO userDTO);
}
