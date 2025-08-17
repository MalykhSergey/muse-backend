package ru.t1.debut.muse.dto;

import lombok.Data;
import ru.t1.debut.muse.entity.TagSubscribe;

@Data
public class TagSubscribeDTO {
    private Long tagId;
    private Boolean isNotification;

    public TagSubscribeDTO(TagSubscribe tagSubscribe) {
        this.tagId = tagSubscribe.getTagSubscribeId().getTagId();
        isNotification = tagSubscribe.isNotification();
    }
}
