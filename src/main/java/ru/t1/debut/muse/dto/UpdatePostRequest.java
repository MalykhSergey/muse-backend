package ru.t1.debut.muse.dto;

import lombok.Data;

@Data
public class UpdatePostRequest {
    private String title;
    private String body;
    private Long answerId;
}
