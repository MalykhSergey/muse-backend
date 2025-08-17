package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.dto.CreateSubscribeRequest;
import ru.t1.debut.muse.dto.PostSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;

public interface PostSubscribeService {
    Page<PostSubscribeDTO> getAll(Pageable pageable, UserDTO authUserDTO);

    PostSubscribeDTO create(long postId, CreateSubscribeRequest createSubscribeRequest, UserDTO authUserDTO);

    void update(long postId, UpdateSubscribeRequest updateSubscribeRequest, UserDTO authUserDTO);

    void delete(long postId, UserDTO authUserDTO);
}
