package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Vote> votes;

    @Transient
    public Integer getScore() {
        if (votes == null || votes.isEmpty()) {
            return 0;
        }
        return votes.stream()
                .mapToInt(vote -> vote.getType() == VoteType.POSITIVE ? 1 : -1)
                .sum();
    }

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Post> answers;

}

