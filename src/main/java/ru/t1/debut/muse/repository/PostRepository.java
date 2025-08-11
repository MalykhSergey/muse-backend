package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Post;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(
            value = """
                    WITH tsq AS (
                      SELECT (websearch_to_tsquery('english', :q) || websearch_to_tsquery('russian', :q)) AS q_union
                    )
                    SELECT
                      p.id,
                      p.title,
                      p.body,
                      p.post_type,
                      p.author_id,
                      p.parent_id,
                      p.answer_id,
                      p.created,
                      p.updated,
                      ts_rank_cd(p.search_vector, tsq.q_union, 32) AS tscore
                    FROM posts p
                    CROSS JOIN tsq
                    WHERE p.search_vector @@ tsq.q_union
                    ORDER BY tscore DESC
                    """,
            countQuery = """
                    WITH tsq AS (
                      SELECT (websearch_to_tsquery('english', :q) || websearch_to_tsquery('russian', :q)) AS q_union
                    )
                    SELECT COUNT(*)
                    FROM posts p
                    CROSS JOIN tsq
                    WHERE p.search_vector @@ tsq.q_union
                    """,
            nativeQuery = true
    )
    Page<Post> search(@Param("q") String query, Pageable pageable);


    void deleteByIdAndAuthor_InternalId(Long postId, UUID authorInternalId);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET " +
           "p.title = :#{#post.title}, " +
           "p.body = :#{#post.body}, " +
           "p.answer.id = :#{#post.answer.id}, " +
           "p.updated = :#{#post.updated} " +
           "WHERE p.id = :#{#post.id} AND p.author.id = :#{#post.author.id}")
    int updatePostWithAnswerByIdAndAuthorId(@Param("post") Post post);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET " +
           "p.title = :#{#post.title}, " +
           "p.body = :#{#post.body}, " +
           "p.updated = :#{#post.updated} " +
           "WHERE p.id = :#{#post.id} AND p.author.id = :#{#post.author.id}")
    int updatePostWithoutAnswerByIdAndAuthorId(@Param("post") Post post);
}
