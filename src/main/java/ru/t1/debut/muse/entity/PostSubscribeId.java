package ru.t1.debut.muse.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
class PostSubscribeId implements Serializable {
    private Long postId;
    private Long userId;
}
