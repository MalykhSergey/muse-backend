package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Vote;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId AND v.author.id = :authorId")
    Optional<Vote> findByPostIdAndAuthorId(Long postId, Long authorId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.post.id = :postId AND v.author.id = :authorId")
    void deleteByPostIdAndAuthorId(Long postId, Long authorId);
}
