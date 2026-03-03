package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentHierarchyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentHierarchyRepository extends JpaRepository<DepartmentHierarchyEntity, UUID> {
    
    Optional<DepartmentHierarchyEntity> findByChildId(UUID childId);
    
    List<DepartmentHierarchyEntity> findByParentId(UUID parentId);
    
    List<DepartmentHierarchyEntity> findByParentIdOrderBySortOrderAsc(UUID parentId);
    
    void deleteByChildId(UUID childId);
    
    boolean existsByChildId(UUID childId);
}