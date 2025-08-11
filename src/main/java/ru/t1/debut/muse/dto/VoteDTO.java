package ru.t1.debut.muse.dto;

import lombok.Value;
import ru.t1.debut.muse.entity.Vote;
import ru.t1.debut.muse.entity.VoteType;

import java.time.LocalDateTime;

@Value
public class VoteDTO {
    Long id;
    Long postId;
    LocalDateTime created;
    VoteType type;

    public static VoteDTO fromVote(Vote vote) {
        return new VoteDTO(vote.getId(), vote.getPost().getId(), vote.getCreated(), vote.getType());
    }
}