package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentPersonnelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentPersonnelRepository extends JpaRepository<DepartmentPersonnelEntity, UUID> {
    
    List<DepartmentPersonnelEntity> findByDepartmentId(UUID departmentId);
    
    List<DepartmentPersonnelEntity> findByPersonnelId(UUID personnelId);
    
    Optional<DepartmentPersonnelEntity> findByDepartmentIdAndPersonnelId(UUID departmentId, UUID personnelId);
    
    Optional<DepartmentPersonnelEntity> findByPersonnelIdAndIsPrimaryTrue(UUID personnelId);
    
    List<DepartmentPersonnelEntity> findByDepartmentIdOrderBySortOrderAsc(UUID departmentId);
    
    void deleteByDepartmentIdAndPersonnelId(UUID departmentId, UUID personnelId);
    
    boolean existsByDepartmentIdAndPersonnelId(UUID departmentId, UUID personnelId);
}