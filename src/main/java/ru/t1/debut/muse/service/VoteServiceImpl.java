package ru.t1.debut.muse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.debut.muse.dto.UserDTO;
import ru.t1.debut.muse.entity.Post;
import ru.t1.debut.muse.entity.User;
import ru.t1.debut.muse.entity.Vote;
import ru.t1.debut.muse.entity.VoteType;
import ru.t1.debut.muse.exception.ResourceNotFoundException;
import ru.t1.debut.muse.repository.VoteRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final UserService userService;

    @Autowired
    public VoteServiceImpl(VoteRepository voteRepository, UserService userService) {
        this.voteRepository = voteRepository;
        this.userService = userService;
    }

    @Override
    public void createVote(UserDTO authorDTO, VoteType voteType, long postId) {
        Optional<Vote> stored_vote = voteRepository.findByPost_IdAndAuthor_InternalId(postId, authorDTO.internalId());
        User author = userService.getUserByInternalId(authorDTO.internalId()).orElseGet(() -> userService.createUser(authorDTO));
        Post post = new Post();
        post.setId(postId);
        Vote vote;
        if (stored_vote.isEmpty()) {
            vote = new Vote(null, author, post, LocalDateTime.now(), voteType);
        } else {
            vote = stored_vote.get();
            vote.setType(voteType);
            vote.setCreated(LocalDateTime.now());
        }
        voteRepository.save(vote);
    }

    @Override
    public Vote getUserVoteForPost(long postId, UserDTO user) {
        return voteRepository.findByPost_IdAndAuthor_InternalId(postId, user.internalId())
                .orElseThrow(ResourceNotFoundException::new);
    }
}
