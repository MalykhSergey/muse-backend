package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdatePostRequest {
    @Size(min = 5, max = 150)
    private String title;
    @Size(min = 5, max = 150)
    private String body;
    @Min(1)
    private Long answerId;
    private Set<TagDTO> tags;
}
