package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategoryEntity, UUID> {
    List<TagCategoryEntity> findByRemovedFalseOrderBySortRankAsc();
    boolean existsByNameAndRemovedFalse(String name);
}
