package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, UUID> {
    
    /**
     * 根据岗位编码查找岗位
     */
    List<PositionEntity> findByCode(String code);
    
    /**
     * 根据岗位名称查找岗位
     */
    List<PositionEntity> findByNameContaining(String name);
    
    /**
     * 根据职级查找岗位
     */
    List<PositionEntity> findByJobLevel(String jobLevel);
    
    /**
     * 根据岗位类别查找岗位
     */
    List<PositionEntity> findByJobCategory(String jobCategory);
    
    /**
     * 根据状态查找岗位
     */
    List<PositionEntity> findByStatus(Integer status);
}
