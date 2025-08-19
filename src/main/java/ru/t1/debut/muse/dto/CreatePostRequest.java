package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.t1.debut.muse.entity.PostType;

import java.util.Set;

@Data
public class CreatePostRequest {
    @Size(min = 5, max = 150)
    private String title;
    @NotNull
    @Size(min = 20, max = 5000)
    private String body;
    @NotNull
    private PostType postType;
    @Min(1)
    private Long parentId;
    @NotNull
    private Set<TagDTO> tags;
}
