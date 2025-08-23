package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTagRequest {
    @NotNull
    @NotBlank
    private String name;
    @Min(1)
    private Long postId;
}
