package ru.t1.debut.muse.dto;

import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.PostType;

import java.time.LocalDateTime;

public record PostDTO(
        long id,
        String title,
        String body,
        PostType postType,
        UserDTO author,
        long parentId,
        long answerId,
        LocalDateTime created,
        LocalDateTime updated
) {
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
                post.getUpdated()
        );
    }

    private static long getNullableId(Post post) {
        return post != null ? post.getId() : 0L;
    }
}
