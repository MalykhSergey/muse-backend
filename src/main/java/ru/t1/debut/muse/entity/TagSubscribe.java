package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tags_subscribes")
@Entity
@Getter
@Setter
public class TagSubscribe {
    @EmbeddedId
    private TagSubscribeId tagSubscribeId;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private boolean isNotification;

}


