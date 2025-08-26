package ru.t1.debut.muse.repository;

import java.time.Instant;
import java.util.UUID;

public interface PostSearchProjection {
    Long getId();

    String getTitle();

    String getBody();

    String getPostType();

    Long getAuthorId();

    Long getExternalId();

    UUID getInternalId();

    String getUserType();

    String getAuthorName();

    Long getParentId();

    Long getAnswerId();

    Instant getCreated();

    Instant getUpdated();

    Long getScore();

    Long getAnswerCount();

    String getVoteType();

    String getTags();

    Boolean getIsNotification();

    Long getTotalCount();
}