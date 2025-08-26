package ru.t1.debut.muse.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostType;
import ru.t1.debut.muse.entity.UserType;
import ru.t1.debut.muse.entity.VoteType;
import ru.t1.debut.muse.repository.PostSearchProjection;

import java.time.Instant;

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
    private Instant created;
    private Instant updated;
    private Long score;
    private Long answersCount;
    private VoteType usersVote;
    private Boolean subscribe;
    @JsonRawValue
    @Schema(
            example = "[{\"id\": 1, \"name\": \"java\", \"postId\": 1}, {\"id\": 2, \"name\": \"spring\", \"postId\": 2}]",
            type = "string"
    )
    private String tags;


    public static PostDTO fromNewPost(Post post, String tags) {
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
                null,
                null,
                tags
        );
    }

    public static PostDTO fromPostSearchResult(PostSearchProjection post) {
        UserType userType = post.getUserType() == null ? null : UserType.valueOf(post.getUserType());
        UserDTO author = new UserDTO(post.getAuthorId(), post.getExternalId(), post.getInternalId(), userType, post.getAuthorName(), null);
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                PostType.valueOf(post.getPostType()),
                author,
                post.getParentId(),
                post.getAnswerId(),
                post.getCreated(),
                post.getUpdated(),
                post.getScore(),
                post.getAnswerCount(),
                post.getVoteType() == null ? null : VoteType.valueOf(post.getVoteType()),
                post.getIsNotification(),
                post.getTags()
        );
    }

    private static Long getNullableId(Post post) {
        return post != null ? post.getId() : null;
    }
}
