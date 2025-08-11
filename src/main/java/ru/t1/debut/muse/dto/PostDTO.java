package ru.t1.debut.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public final class PostDTO {
    private Long id;
    private String title;
    private String body;
    private PostType postType;
    private UserDTO author;
    private Long parentId;
    private Long answerId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private int score;
    private int answers_count;


    public static PostDTO fromPost(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getPostType(),
                new UserDTO(post.getAuthor()),
                getNullableId(post.getParent()),
                getNullableId(post.getAnswer()),
                post.getCreated(),
                post.getUpdated(),
                post.getScore(),
                post.getAnswers().size()
        );
    }

    private static Long getNullableId(Post post) {
        return post != null ? post.getId() : null;
    }
}
