package ru.t1.debut.muse.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
class TagSubscribeId implements Serializable {
    private Long tagId;
    private Long userId;
}
