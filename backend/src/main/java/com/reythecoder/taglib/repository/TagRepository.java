package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {
    List<TagEntity> findByCategoryIdAndRemovedFalseOrderBySortRankAsc(UUID categoryId);
    List<TagEntity> findByParentIdAndRemovedFalseOrderBySortRankAsc(UUID parentId);
    Optional<TagEntity> findByIdAndRemovedFalse(UUID id);
    boolean existsByCategoryIdAndNameAndRemovedFalse(UUID categoryId, String name);
    List<TagEntity> findByParentIdAndRemovedFalse(UUID parentId);
}
