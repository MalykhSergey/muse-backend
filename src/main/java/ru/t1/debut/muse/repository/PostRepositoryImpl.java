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
        String sql = "SELECT * FROM search_posts_function(:q, :user_id, :limit, :offset)";
        List<PostSearchResult> result = entityManager.createNativeQuery(sql, PostSearchResult.class)
                .setParameter("q", q)
                .setParameter("user_id", userId)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .getResultList();
        return new PageImpl<>(result, pageable, extractTotalCount(result));
    }

    public Page<PostSearchResult> getAllByParentId(Pageable pageable, Long userId, Long parentId) {
        String sql = "SELECT * FROM get_posts_rich(:user_id, :parent_id, :limit, :offset)";
        List<PostSearchResult> resultList = entityManager.createNativeQuery(sql, PostSearchResult.class)
                .setParameter("user_id", userId)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .setParameter("parent_id", parentId)
                .getResultList();
        return new PageImpl<>(resultList, pageable, extractTotalCount(resultList));
    }

    public Optional<PostSearchResult> getById(Long postId, Long userId) {
        String sql = "SELECT * FROM get_post_rich(:post_id, :user_id)";

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
        jpaPostRepository.deleteByIdAndAuthorId(id, authorId);
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