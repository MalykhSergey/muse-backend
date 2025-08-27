package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.controller.post.SortBy;
import ru.t1.debut.muse.controller.post.SortDir;
import ru.t1.debut.muse.dto.*;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostSubscribe;
import ru.t1.debut.muse.entity.PostSubscribeId;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.repository.PostSearchProjection;
import ru.t1.debut.muse.repository.PostSubscribeRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PostSubscribeServiceImpl implements PostSubscribeService {
    private final PostSubscribeRepository postSubscribeRepository;
    private final UserService userService;

    @Autowired
    public PostSubscribeServiceImpl(PostSubscribeRepository postSubscribeRepository, UserService userService) {
        this.postSubscribeRepository = postSubscribeRepository;
        this.userService = userService;
    }

    @Override
    public Page<PostDTO> getAll(UserDTO authUserDTO, int page, int size, SortBy sortBy, SortDir sortDir) {
        User authUser = userService.getUser(authUserDTO);
        List<PostSearchProjection> result = postSubscribeRepository.findAllByUserId(authUser.getId(), size, (long) page * size, sortBy.name(), sortDir.name());
        long total = result.isEmpty() ? 0 : result.getFirst().getTotalCount();
        return new PageImpl<>(result.stream().map(PostDTO::fromPostSearchResult).toList(), PageRequest.of(page, size), total);
    }

    @Override
    public PostSubscribeDTO create(long postId, CreateSubscribeRequest createSubscribeRequest, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        PostSubscribe postSubscribe = new PostSubscribe();
        Post post = new Post();
        post.setId(postId);
        postSubscribe.setPostSubscribeId(new PostSubscribeId(postId, authUser.getId()));
        postSubscribe.setUser(authUser);
        postSubscribe.setPost(post);
        postSubscribe.setNotification(createSubscribeRequest.getIsNotification());
        return new PostSubscribeDTO(postSubscribeRepository.save(postSubscribe));
    }

    @Override
    public void create(Post post, User author) {
        PostSubscribe postSubscribe = new PostSubscribe();
        postSubscribe.setPost(post);
        postSubscribe.setUser(author);
        postSubscribe.setNotification(true);
        postSubscribe.setPostSubscribeId(new PostSubscribeId(post.getId(), author.getId()));
        postSubscribeRepository.save(postSubscribe);
    }

    @Override
    public Set<UUID> getSubscribersUUIDForPost(long postId) {
        return postSubscribeRepository.findNotificationEnabledUserInternalIdsByPostId(postId);
    }

    @Override
    public void update(long postId, UpdateSubscribeRequest updateSubscribeRequest, UserDTO authUserDTO) {
        CreateSubscribeRequest createSubscribeRequest = new CreateSubscribeRequest();
        createSubscribeRequest.setIsNotification(updateSubscribeRequest.getIsNotification());
        create(postId, createSubscribeRequest, authUserDTO);
    }

    @Override
    public void delete(long postId, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        postSubscribeRepository.deleteByPostIdAndUserId(postId, authUser.getId());
    }
}
