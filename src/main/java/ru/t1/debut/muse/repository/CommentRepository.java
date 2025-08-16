package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Comment;

import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId AND c.author.id = :authorId")
    void deleteByIdAndAuthorId(long commentId, long authorId);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET " +
           "c.body = :body, " +
           "c.updated = :updated " +
           "WHERE c.id = :commentId AND c.author.id = :authorId")
    void updateByIdAndAuthorId(long commentId, String body, LocalDateTime updated, long authorId);

    Page<Comment> findAllByPost_IdOrderById(long postId, Pageable pageable);
}
