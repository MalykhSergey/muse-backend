package ru.t1.debut.muse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.dto.TagDTO;

public interface TagService {
    TagDTO getTag(Long id);

    Page<TagDTO> getTags(Pageable pageable);
    Page<TagDTO> getTagsByPrefix(String tagPrefix,Pageable pageable);
}
