package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(UserDTO userDTO) {
        return userRepository.findByInternalId(userDTO.internalId()).orElseGet(() -> userRepository.save(new User(userDTO)));
    }
}
