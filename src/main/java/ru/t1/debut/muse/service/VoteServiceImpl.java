package ru.t1.debut.muse.service;

import jakarta.transaction.Transactional;
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
    @Transactional
    public void createVote(UserDTO authorDTO, VoteType voteType, long postId) {
        User author = userService.getUser(authorDTO);
        Optional<Vote> stored_vote = voteRepository.findByPostIdAndAuthorId(postId, author.getId());
        Vote vote;
        if (stored_vote.isEmpty()) {
            Post post = new Post();
            post.setId(postId);
            vote = new Vote(null, author, post, LocalDateTime.now(), voteType);
            voteRepository.save(vote);
        } else {
            vote = stored_vote.get();
            vote.setType(voteType);
            vote.setCreated(LocalDateTime.now());
        }
    }

    @Override
    public Vote getUserVoteForPost(long postId, UserDTO user) {
        User author = userService.getUser(user);
        return voteRepository.findByPostIdAndAuthorId(postId, author.getId())
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void deleteVoteForPost(long postId, UserDTO user) {
        User author = userService.getUser(user);
        voteRepository.deleteByPostIdAndAuthorId(postId, author.getId());
    }
}
