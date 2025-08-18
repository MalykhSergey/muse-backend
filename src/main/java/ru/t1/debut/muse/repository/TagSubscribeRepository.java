package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.TagSubscribe;
import ru.t1.debut.muse.entity.TagSubscribeId;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagSubscribeRepository extends JpaRepository<TagSubscribe, TagSubscribeId> {
    Page<TagSubscribe> findAllByUserId(Long id, Pageable pageable);

    @Transactional
    void deleteByTagIdAndUserId(long tagId, Long id);

    @Query("SELECT ts.user.internalId FROM TagSubscribe ts WHERE ts.tagSubscribeId.tagId = :tagId AND ts.isNotification = true")
    List<UUID> findNotificationEnabledUserInternalIdsByTagId(Long tagId);
}