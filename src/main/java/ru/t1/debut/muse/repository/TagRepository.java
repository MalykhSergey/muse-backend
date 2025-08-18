package ru.t1.debut.muse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.debut.muse.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Page<Tag> findAllByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
