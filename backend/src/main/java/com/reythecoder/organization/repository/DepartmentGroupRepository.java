package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentGroupRepository extends JpaRepository<DepartmentGroupEntity, UUID> {

    List<DepartmentGroupEntity> findByDepartmentId(UUID departmentId);

    List<DepartmentGroupEntity> findByGroupId(UUID groupId);

    List<DepartmentGroupEntity> findByDepartmentIdOrderBySortOrderAsc(UUID departmentId);

    List<DepartmentGroupEntity> findByGroupIdOrderBySortOrderAsc(UUID groupId);

    Optional<DepartmentGroupEntity> findByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    void deleteByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    boolean existsByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    long countByDepartmentId(UUID departmentId);
}