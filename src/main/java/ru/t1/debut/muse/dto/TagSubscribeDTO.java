package ru.t1.debut.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.t1.debut.muse.entity.TagSubscribe;
import ru.t1.debut.muse.repository.TagSubscribeProjection;

@Data
@AllArgsConstructor
public class TagSubscribeDTO {
    private Long tagId;
    private String name;
    private Boolean isNotification;

    public TagSubscribeDTO(TagSubscribeProjection tagSubscribeProjection){
        this.tagId = tagSubscribeProjection.getId();
        this.name = tagSubscribeProjection.getName();
        this.isNotification = tagSubscribeProjection.getIsNotification();
    }
}
