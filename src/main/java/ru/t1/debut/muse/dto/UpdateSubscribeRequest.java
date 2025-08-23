package ru.t1.debut.muse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSubscribeRequest {
    @NotNull
    private Boolean isNotification;
}
