package ru.t1.debut.muse.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final JpaPostRepository jpaPostRepository;
    private final EntityManager entityManager;

    @Autowired
    public PostRepositoryImpl(JpaPostRepository jpaPostRepository, EntityManager entityManager) {
        this.jpaPostRepository = jpaPostRepository;
        this.entityManager = entityManager;
    }

    public Page<PostSearchResult> searchPosts(String q, Pageable pageable, Long userId) {
        String sql = """
                    WITH tsq AS (
                      SELECT (websearch_to_tsquery('english', :q) || websearch_to_tsquery('russian', :q)) AS q_union
                    )
                    SELECT p.id,
                    p.title,
                    p.body,
                    p.post_type,
                    p.author_id,
                    users.external_id,
                    users.internal_id,
                    users.user_type,
                    users.name,
                    p.parent_id,
                    p.answer_id,
                    p.created,
                    p.updated,
                    COALESCE((
                      SELECT SUM(CASE type
                        WHEN 'POSITIVE' THEN 1
                        WHEN 'NEGATIVE' THEN -1
                        ELSE 0 END)
                      FROM votes
                      WHERE post_id = p.id
                    ), 0) AS score,
                    COALESCE((SELECT COUNT(*)
                    FROM posts a
                    WHERE a.parent_id = p.id), 0) as answer_count,
                    votes.type,
                     COUNT(*) OVER() AS total_count
                    FROM posts p
                    LEFT JOIN users ON p.author_id = users.id
                    LEFT JOIN votes ON p.id = votes.post_id AND :user_id = votes.author_id
                    CROSS JOIN tsq
                    WHERE p.search_vector @@ tsq.q_union
                    ORDER BY ts_rank_cd(p.search_vector, tsq.q_union, 32) DESC
                    LIMIT :limit OFFSET :offset
                """;

        List<PostSearchResult> result = entityManager.createNativeQuery(sql, PostSearchResult.class)
                .setParameter("q", q)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .setParameter("user_id", userId)
                .getResultList();
        return new PageImpl<>(result, pageable, extractTotalCount(result));
    }

    public Page<PostSearchResult> getAll(Pageable pageable, Long userId) {
        String sql = """
                    SELECT p.id,
                    p.title,
                    p.body,
                    p.post_type,
                    p.author_id,
                    users.external_id,
                    users.internal_id,
                    users.user_type,
                    users.name,
                    p.parent_id,
                    p.answer_id,
                    p.created,
                    p.updated,
                    COALESCE((
                      SELECT SUM(CASE type
                        WHEN 'POSITIVE' THEN 1
                        WHEN 'NEGATIVE' THEN -1
                        ELSE 0 END)
                      FROM votes
                      WHERE post_id = p.id
                    ), 0) AS score,
                    COALESCE((SELECT COUNT(*)
                    FROM posts a
                    WHERE a.parent_id = p.id), 0) as answer_count,
                    votes.type,
                     COUNT(*) OVER() AS total_count
                    FROM posts p
                    LEFT JOIN users ON p.author_id = users.id
                    LEFT JOIN votes ON p.id = votes.post_id AND :user_id = votes.author_id
                    ORDER BY p.created DESC
                    LIMIT :limit OFFSET :offset
                """;
        List<PostSearchResult> resultList = entityManager.createNativeQuery(sql, PostSearchResult.class)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .setParameter("user_id", userId)
                .getResultList();
        return new PageImpl<>(resultList, pageable, extractTotalCount(resultList));
    }

    public Optional<PostSearchResult> getById(Long postId, Long userId) {
        String sql = """
                    SELECT p.id,
                    p.title,
                    p.body,
                    p.post_type,
                    p.author_id,
                    users.external_id,
                    users.internal_id,
                    users.user_type,
                    users.name,
                    p.parent_id,
                    p.answer_id,
                    p.created,
                    p.updated,
                    COALESCE((
                    SELECT SUM(CASE type
                      WHEN 'POSITIVE' THEN 1
                      WHEN 'NEGATIVE' THEN -1
                      ELSE 0 END)
                    FROM votes
                    WHERE post_id = p.id
                    ), 0) AS score,
                    COALESCE((SELECT COUNT(*)
                    FROM posts a
                    WHERE a.parent_id = p.id), 0) as answer_count,
                    votes.type,
                    1::BIGINT as total_count
                    FROM posts p
                    LEFT JOIN users ON p.author_id = users.id
                    LEFT JOIN votes ON p.id = votes.post_id AND :user_id = votes.author_id
                    WHERE p.id = :post_id
                """;

        List<PostSearchResult> resultList = entityManager.createNativeQuery(sql, PostSearchResult.class)
                .setParameter("post_id", postId)
                .setParameter("user_id", userId)
                .getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(resultList.getFirst());
    }

    public Post save(Post post) {
        return jpaPostRepository.save(post);
    }

    public void delete(Post post) {
        jpaPostRepository.delete(post);
    }

    @Override
    public int updatePostWithoutAnswerByIdAndAuthorId(Post post) {
        return jpaPostRepository.updatePostWithoutAnswerByIdAndAuthorId(post);
    }

    @Override
    public int updatePostWithAnswerByIdAndAuthorId(Post post) {
        return jpaPostRepository.updatePostWithAnswerByIdAndAuthorId(post);
    }

    @Transactional
    @Override
    public void deleteByIdAndAuthorId(Long id, Long authorId) {
        jpaPostRepository.deleteByIdAndAuthorId(id,authorId);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jpaPostRepository.findById(id);
    }

    private long extractTotalCount(List<PostSearchResult> result) {
        if (result == null) {
            return 0;
        }
        if (result.isEmpty()) {
            return 0;
        }
        return result.getFirst().getTotalCount();
    }
}