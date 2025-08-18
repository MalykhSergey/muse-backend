package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CreateSubscribeRequest;
import ru.t1.debut.muse.dto.TagSubscribeDTO;
import ru.t1.debut.muse.dto.UpdateSubscribeRequest;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Tag;
import ru.t1.debut.muse.entity.TagSubscribe;
import ru.t1.debut.muse.entity.TagSubscribeId;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.repository.TagSubscribeRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TagSubscribeServiceImpl implements TagSubscribeService {
    private final TagSubscribeRepository tagSubscribeRepository;
    private final UserService userService;

    @Autowired
    public TagSubscribeServiceImpl(TagSubscribeRepository tagSubscribeRepository, UserService userService) {
        this.tagSubscribeRepository = tagSubscribeRepository;
        this.userService = userService;
    }

    @Override
    public Page<TagSubscribeDTO> getAll(Pageable pageable, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        return tagSubscribeRepository.findAllByUserId(authUser.getId(), pageable).map(TagSubscribeDTO::new);
    }

    @Override
    public List<UUID> getSubscribersUUIDForTag(long tagId) {
        return tagSubscribeRepository.findNotificationEnabledUserInternalIdsByTagId(tagId);
    }

    @Override
    public TagSubscribeDTO create(long tagId, CreateSubscribeRequest createSubscribeRequest, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        TagSubscribe tagSubscribe = new TagSubscribe();
        Tag tag = new Tag();
        tag.setId(tagId);
        tagSubscribe.setTagSubscribeId(new TagSubscribeId(tagId, authUser.getId()));
        tagSubscribe.setUser(authUser);
        tagSubscribe.setTag(tag);
        tagSubscribe.setNotification(createSubscribeRequest.getIsNotification());
        return new TagSubscribeDTO(tagSubscribeRepository.save(tagSubscribe));
    }

    @Override
    public void update(long tagId, UpdateSubscribeRequest updateSubscribeRequest, UserDTO authUserDTO) {
        CreateSubscribeRequest createSubscribeRequest = new CreateSubscribeRequest();
        createSubscribeRequest.setIsNotification(updateSubscribeRequest.getIsNotification());
        create(tagId, createSubscribeRequest, authUserDTO);
    }

    @Override
    public void delete(long tagId, UserDTO authUserDTO) {
        User authUser = userService.getUser(authUserDTO);
        tagSubscribeRepository.deleteByTagIdAndUserId(tagId, authUser.getId());
    }
}
