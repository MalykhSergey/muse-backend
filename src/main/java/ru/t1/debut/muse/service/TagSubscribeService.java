package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.dto.CreateSubscribeRequest;
import ru.t1.debut.muse.dto.TagSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;

public interface TagSubscribeService {
    Page<TagSubscribeDTO> getAll(Pageable pageable, UserDTO authUserDTO);

    TagSubscribeDTO create(long tagId, CreateSubscribeRequest createSubscribeRequest, UserDTO authUserDTO);

    void update(long tagId, UpdateSubscribeRequest updateSubscribeRequest, UserDTO authUserDTO);

    void delete(long tagId, UserDTO authUserDTO);
}
