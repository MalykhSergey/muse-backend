package ru.t1.debut.muse.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.CreateTagRequest;
import ru.t1.debut.muse.dto.TagDTO;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.Tag;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagDTO getTag(Long id) {
        return tagRepository.findById(id).map(TagDTO::fromTag).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Page<TagDTO> getTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(TagDTO::fromTag);
    }

    @Override
    public Page<TagDTO> getTagsByPrefix(String tagPrefix, Pageable pageable) {
        return tagRepository.findAllByNameStartsWithIgnoreCase(tagPrefix, pageable).map(TagDTO::fromTag);
    }

    @Override
    public TagDTO create(@Valid CreateTagRequest createTagRequest) {
        Post post;
        if (createTagRequest.getPostId() != null) {
            post = new Post();
            post.setId(createTagRequest.getPostId());
        } else post = null;
        return TagDTO.fromTag(tagRepository.save(new Tag(null, createTagRequest.getName(), post, null)));
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}
