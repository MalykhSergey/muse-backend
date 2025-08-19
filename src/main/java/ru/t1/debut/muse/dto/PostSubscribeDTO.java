package ru.t1.debut.muse.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.debut.muse.entity.PostSubscribe;

@Data
@NoArgsConstructor
public class PostSubscribeDTO {
    private Long postId;
    private Boolean isNotification;

    public PostSubscribeDTO(PostSubscribe postSubscribe){
        this.postId = postSubscribe.getPostSubscribeId().getPostId();
        this.isNotification = getIsNotification();
    }
}
