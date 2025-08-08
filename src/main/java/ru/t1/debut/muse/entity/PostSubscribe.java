package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "posts_subscribes")
@Entity
@Getter
@Setter
public class PostSubscribe {
    @EmbeddedId
    private PostSubscribeId postSubscribeId;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

}


