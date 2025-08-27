package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.*;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.User;

import java.util.Set;
import java.util.UUID;

public interface PostSubscribeService {
    Page<PostDTO> getAll(UserDTO authUserDTO, int page, int size, SortBy sortBy, SortDir sortDir);

    PostSubscribeDTO create(long postId, CreateSubscribeRequest createSubscribeRequest, UserDTO authUserDTO);

    void create(Post post, User author);

    Set<UUID> getSubscribersUUIDForPost(long postId);

    void update(long postId, UpdateSubscribeRequest updateSubscribeRequest, UserDTO authUserDTO);

    void delete(long postId, UserDTO authUserDTO);
}
