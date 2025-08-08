package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tags")
@Entity
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
