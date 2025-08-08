package ru.t1.debut.muse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Post;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    void deleteByIdAndAuthor_InternalId(Long postId, UUID authorInternalId);
}
