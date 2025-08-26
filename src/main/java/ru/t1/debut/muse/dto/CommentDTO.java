package ru.t1.debut.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String body;
    private UserDTO author;
    private Long postId;
    private Instant created;
    private Instant updated;
}
