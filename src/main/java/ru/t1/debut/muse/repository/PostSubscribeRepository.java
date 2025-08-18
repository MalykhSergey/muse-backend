package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.PostSubscribe;
import ru.t1.debut.muse.entity.PostSubscribeId;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, PostSubscribeId> {
    Page<PostSubscribe> findAllByUserId(long userId, Pageable pageable);

    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Query("SELECT ps.user.internalId FROM PostSubscribe ps WHERE ps.postSubscribeId.postId = :postId AND ps.isNotification = true")
    List<UUID> findNotificationEnabledUserInternalIdsByPostId(Long postId);
}