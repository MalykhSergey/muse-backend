package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdatePostRequest {
    @Size(min = 5, max = 150)
    private String title;
    @NotNull
    @Size(min = 20, max = 5000)
    private String body;
    @Min(1)
    private Long answerId;
    @NotNull
    private Set<TagDTO> tags;
}
