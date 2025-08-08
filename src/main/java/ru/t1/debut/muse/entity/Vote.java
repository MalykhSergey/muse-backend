package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "votes")
@Entity
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;
    @Column
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private VoteType type;

}

