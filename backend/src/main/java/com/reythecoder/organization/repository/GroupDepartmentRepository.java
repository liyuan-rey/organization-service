package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.GroupDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupDepartmentRepository extends JpaRepository<GroupDepartmentEntity, UUID> {
    
    List<GroupDepartmentEntity> findByGroupId(UUID groupId);
    
    List<GroupDepartmentEntity> findByDepartmentId(UUID departmentId);
    
    Optional<GroupDepartmentEntity> findByGroupIdAndDepartmentId(UUID groupId, UUID departmentId);
    
    List<GroupDepartmentEntity> findByGroupIdOrderBySortOrderAsc(UUID groupId);
    
    void deleteByGroupIdAndDepartmentId(UUID groupId, UUID departmentId);
    
    boolean existsByGroupIdAndDepartmentId(UUID groupId, UUID departmentId);
}