package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.PostSubscribe;
import ru.t1.debut.muse.entity.PostSubscribeId;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, PostSubscribeId> {
    @Query(value = "SELECT * FROM get_subscribed_posts(:userId, :limit, :offset, :sortBy, :sortDir)", nativeQuery = true)
    List<PostSearchProjection> findAllByUserId(long userId, @Param("limit") int limit, @Param("offset") long offset, @Param("sortBy") String sortBy, @Param("sortDir") String sortDir);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostSubscribe ps WHERE ps.post.id = :postId AND ps.user.id = :userId")
    void deleteByPostIdAndUserId(long postId, long userId);

    @Query("SELECT ps.user.internalId FROM PostSubscribe ps WHERE ps.postSubscribeId.postId = :postId AND ps.isNotification = true")
    Set<UUID> findNotificationEnabledUserInternalIdsByPostId(Long postId);
}