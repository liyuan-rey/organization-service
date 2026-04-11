package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRelationRepository extends JpaRepository<TagRelationEntity, UUID> {
    List<TagRelationEntity> findByObjectTypeAndObjectId(String objectType, UUID objectId);
    List<TagRelationEntity> findByTagId(UUID tagId);
    boolean existsByObjectTypeAndObjectIdAndTagId(String objectType, UUID objectId, UUID tagId);
    void deleteByObjectTypeAndObjectIdAndTagId(String objectType, UUID objectId, UUID tagId);

    @Query("SELECT r FROM TagRelationEntity r WHERE r.objectType = :objectType AND r.tagId IN :tagIds GROUP BY r.objectId HAVING COUNT(DISTINCT r.tagId) = :tagCount")
    List<TagRelationEntity> findByObjectTypeAndTagIdsWithAllMatch(@Param("objectType") String objectType, @Param("tagIds") List<UUID> tagIds, @Param("tagCount") long tagCount);
}
