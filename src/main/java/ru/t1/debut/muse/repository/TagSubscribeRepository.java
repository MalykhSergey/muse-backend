package ru.t1.debut.muse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.TagSubscribe;
import ru.t1.debut.muse.entity.TagSubscribeId;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TagSubscribeRepository extends JpaRepository<TagSubscribe, TagSubscribeId> {
    @Query(value = "SELECT tags.id, tags.name, tags_subscribes.is_notification FROM tags_subscribes JOIN tags ON tags_subscribes.tag_id = tags.id WHERE user_id = :id", nativeQuery = true)
    Page<TagSubscribeProjection> findAllByUserId(Long id, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM TagSubscribe ts WHERE ts.tag.id = :tagId AND ts.user.id = :id")
    void deleteByTagIdAndUserId(long tagId, Long id);

    @Query("SELECT ts.user.internalId FROM TagSubscribe ts WHERE ts.tagSubscribeId.tagId = :tagId AND ts.isNotification = true")
    Set<UUID> findNotificationEnabledUserInternalIdsByTagId(Long tagId);
}