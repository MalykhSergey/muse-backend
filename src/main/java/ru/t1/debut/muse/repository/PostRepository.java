package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.debut.muse.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM search_posts_function(:q, :userId, :limit, :offset, :sortBy, :sortDir)", nativeQuery = true)
    public List<PostSearchProjection> searchPosts(@Param("q") String q, @Param("userId") Long userId, @Param("limit") int limit, @Param("offset") long offset, @Param("sortBy") String sortBy, @Param("sortDir") String sortDir);

    @Query(value = "SELECT * FROM get_posts_rich(:userId, :parentId, :limit, :offset, :sortBy, :sortDir)", nativeQuery = true)
    public List<PostSearchProjection> getAllByParentId(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("limit") int limit, @Param("offset") long offset, @Param("sortBy") String sortBy, @Param("sortDir") String sortDir);

    @Query(value = "SELECT * FROM get_post_rich(:userId, :postId)", nativeQuery = true)
    public Optional<PostSearchProjection> getById(@Param("userId") Long userId, @Param("postId") Long postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.id = :postId AND p.author.id = :authorId")
    void deleteByIdAndAuthorId(@Param("postId") Long postId, @Param("authorId") Long authorId);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.answer.id = :answerId WHERE p.id = :postId AND p.author.id = :authorId")
    void setAnswerByIdAndAuthorId(@Param("answerId") Long answerId, @Param("postId") Long postId, @Param("authorId") Long authorId);
}
