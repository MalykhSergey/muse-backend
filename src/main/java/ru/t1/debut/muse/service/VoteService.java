package ru.t1.debut.muse.service;

import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Vote;
import ru.t1.debut.muse.entity.VoteType;

public interface VoteService {
    void createVote(UserDTO author, VoteType voteType, long postId);

    Vote getUserVoteForPost(long postId, UserDTO user);

    void deleteVoteForPost(long postId, UserDTO user);
}
