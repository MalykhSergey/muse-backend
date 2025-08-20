package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.PostSubscribe;
import ru.t1.debut.muse.entity.PostSubscribeId;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, PostSubscribeId> {
    @Query("SELECT ps FROM PostSubscribe ps WHERE ps.user.id = :userId")
    Page<PostSubscribe> findAllByUserId(long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostSubscribe ps WHERE ps.post.id = :postId AND ps.user.id = :userId")
    void deleteByPostIdAndUserId(long postId, long userId);

    @Query("SELECT ps.user.internalId FROM PostSubscribe ps WHERE ps.postSubscribeId.postId = :postId AND ps.isNotification = true")
    Set<UUID> findNotificationEnabledUserInternalIdsByPostId(Long postId);
}