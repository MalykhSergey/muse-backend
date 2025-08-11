package ru.t1.debut.muse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Vote;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPost_IdAndAuthor_InternalId(Long postId, UUID authorInternalId);
}
