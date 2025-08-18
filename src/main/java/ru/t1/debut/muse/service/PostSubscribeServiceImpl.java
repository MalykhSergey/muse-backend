package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CreateSubscribeRequest;
import ru.t1.debut.muse.dto.PostSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostSubscribe;
import ru.t1.debut.muse.entity.PostSubscribeId;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.repository.PostSubscribeRepository;

import java.util.List;
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
    public Page<PostSubscribeDTO> getAll(Pageable pageable, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        return postSubscribeRepository.findAllByUserId(authUser.getId(), pageable).map(PostSubscribeDTO::new);
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
    public List<UUID> getSubscribersUUIDForPost(long postId) {
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
