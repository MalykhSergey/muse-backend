package ru.t1.debut.muse.dto;

import lombok.Value;
import ru.t1.debut.muse.entity.Vote;
import ru.t1.debut.muse.entity.VoteType;

import java.time.Instant;

@Value
public class VoteDTO {
    Long id;
    Long postId;
    Instant created;
    VoteType type;

    public static VoteDTO fromVote(Vote vote) {
        return new VoteDTO(vote.getId(), vote.getPost().getId(), vote.getCreated(), vote.getType());
    }
}