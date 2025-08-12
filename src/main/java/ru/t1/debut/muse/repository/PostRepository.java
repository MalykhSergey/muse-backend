package ru.t1.debut.muse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.debut.muse.entity.Post;

import java.util.Optional;

public interface PostRepository {
    public Page<PostSearchResult> searchPosts(String q, Pageable pageable, Long userId);

    public Page<PostSearchResult> getAllByParentId(Pageable pageable, Long userId, Long parentId);

    public Optional<PostSearchResult> getById(Long postId, Long userId);

    public Post save(Post post);

    public void delete(Post post);

    int updatePostWithoutAnswerByIdAndAuthorId(Post post);

    int updatePostWithAnswerByIdAndAuthorId(Post post);

    void deleteByIdAndAuthorId(Long id, Long authorId);

    Optional<Post> findById(Long id);
}
