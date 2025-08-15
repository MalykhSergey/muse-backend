package ru.t1.debut.muse.dto;

import lombok.Data;
import ru.t1.debut.muse.entity.PostType;

import java.util.Set;

@Data
public class CreatePostRequest {
    private String title;
    private String body;
    private PostType postType;
    private Long parentId;
    private Set<TagDTO> tags;
}
