package ru.t1.debut.muse.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PostSearchResult {
    private Long id;
    private String title;
    private String body;
    private String postType;
    private Long authorId;
    private Long externalId;
    private UUID internalId;
    private String userType;
    private String authorName;
    private Long parentId;
    private Long answerId;
    private Timestamp created;
    private Timestamp updated;
    private Long score;
    private Long answerCount;
    private String userVote;
    private Long totalCount;
}
