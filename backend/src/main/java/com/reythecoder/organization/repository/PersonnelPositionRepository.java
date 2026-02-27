package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.PersonnelPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonnelPositionRepository extends JpaRepository<PersonnelPositionEntity, UUID> {
    
    /**
     * 根据人员 ID 查找所有岗位关联
     */
    List<PersonnelPositionEntity> findByPersonnelId(UUID personnelId);
    
    /**
     * 根据岗位 ID 查找所有人员关联
     */
    List<PersonnelPositionEntity> findByPositionId(UUID positionId);
    
    /**
     * 根据部门 ID 查找所有岗位关联
     */
    List<PersonnelPositionEntity> findByDepartmentId(UUID departmentId);
    
    /**
     * 根据人员 ID 和状态查找岗位关联
     */
    List<PersonnelPositionEntity> findByPersonnelIdAndStatus(UUID personnelId, Integer status);
    
    /**
     * 根据人员 ID、岗位 ID 和部门 ID 查找关联
     */
    Optional<PersonnelPositionEntity> findByPersonnelIdAndPositionIdAndDepartmentId(
        UUID personnelId, UUID positionId, UUID departmentId);
    
    /**
     * 删除人员的所有岗位关联
     */
    void deleteByPersonnelId(UUID personnelId);
    
    /**
     * 删除岗位的所有人员关联
     */
    void deleteByPositionId(UUID positionId);
    
    /**
     * 删除部门的所有岗位关联
     */
    void deleteByDepartmentId(UUID departmentId);
}
