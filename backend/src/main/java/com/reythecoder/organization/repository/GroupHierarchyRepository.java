package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.GroupHierarchyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupHierarchyRepository extends JpaRepository<GroupHierarchyEntity, UUID> {
    
    Optional<GroupHierarchyEntity> findByChildId(UUID childId);
    
    List<GroupHierarchyEntity> findByParentId(UUID parentId);
    
    List<GroupHierarchyEntity> findByParentIdOrderBySortOrderAsc(UUID parentId);
    
    void deleteByChildId(UUID childId);
    
    boolean existsByChildId(UUID childId);
}