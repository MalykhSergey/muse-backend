package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    @NotNull
    @Size(min = 1, max = 1000)
    private String body;
    @NotNull
    @Min(1)
    private Long postId;
}
