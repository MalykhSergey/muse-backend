package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.TagDTO;
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
}
