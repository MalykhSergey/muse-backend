package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.debut.muse.dto.PostDTO;

import java.time.LocalDateTime;


@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String title;
    @Column
    private String body;
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    PostType postType;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "parent_id", updatable = false)
    private Post parent;
    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Post answer;
    @Column(updatable = false)
    private LocalDateTime created;
    @Column
    private LocalDateTime updated;

    public Post(PostDTO postDTO, User user) {
        this.id = postDTO.id();
        this.title = postDTO.title();
        this.body = postDTO.body();
        this.postType = postDTO.postType();
        this.author = user;
        if (postDTO.parentId() != 0 && postDTO.id() == 0) {
            Post parent = new Post();
            parent.setId(postDTO.parentId());
            this.parent = parent;
        } else {
            this.parent = null;
        }
        if (postDTO.answerId() != 0) {
            Post answer = new Post();
            answer.setId(postDTO.answerId());
            this.answer = answer;
        } else {
            this.answer = null;
        }
        this.created = null;
        this.updated = LocalDateTime.now();
    }
}

