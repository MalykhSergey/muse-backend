package ru.t1.debut.muse.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdatePostRequest {
    private String title;
    private String body;
    private Long answerId;
    private Set<TagDTO> tags;
}
