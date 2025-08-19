package ru.t1.debut.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String body;
    private UserDTO author;
    private Long postId;
    private LocalDateTime created;
    private LocalDateTime updated;
}
