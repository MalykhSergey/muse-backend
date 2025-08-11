package ru.t1.debut.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostType;
import ru.t1.debut.muse.entity.UserType;
import ru.t1.debut.muse.entity.VoteType;
import ru.t1.debut.muse.repository.PostSearchResult;

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
    private Long score;
    private Long answers_count;
    private VoteType usersVote;


    public static PostDTO fromNewPost(Post post) {
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
                0L,
                0L,
                null
        );
    }

    public static PostDTO fromPostSearchResult(PostSearchResult post) {
        UserDTO author = new UserDTO(post.getAuthorId(), post.getExternalId(), post.getInternalId(), UserType.valueOf(post.getUserType()), post.getAuthorName());
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                PostType.valueOf(post.getPostType()),
                author,
                post.getParentId(),
                post.getAnswerId(),
                post.getCreated().toLocalDateTime(),
                post.getUpdated().toLocalDateTime(),
                post.getScore(),
                post.getAnswerCount(),
                post.getUserVote() == null ? null : VoteType.valueOf(post.getUserVote())
        );
    }

    private static Long getNullableId(Post post) {
        return post != null ? post.getId() : null;
    }
}
