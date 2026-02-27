package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentPositionRepository extends JpaRepository<DepartmentPositionEntity, UUID> {
    
    /**
     * 根据部门 ID 查找所有岗位关联
     */
    List<DepartmentPositionEntity> findByDepartmentId(UUID departmentId);
    
    /**
     * 根据岗位 ID 查找所有部门关联
     */
    List<DepartmentPositionEntity> findByPositionId(UUID positionId);
    
    /**
     * 根据部门 ID 和岗位 ID 查找关联
     */
    Optional<DepartmentPositionEntity> findByDepartmentIdAndPositionId(UUID departmentId, UUID positionId);
    
    /**
     * 删除部门的所有岗位关联
     */
    void deleteByDepartmentId(UUID departmentId);
    
    /**
     * 删除岗位的所有部门关联
     */
    void deleteByPositionId(UUID positionId);
}
