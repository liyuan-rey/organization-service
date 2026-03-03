package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.GroupPersonnelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupPersonnelRepository extends JpaRepository<GroupPersonnelEntity, UUID> {
    
    List<GroupPersonnelEntity> findByGroupId(UUID groupId);
    
    List<GroupPersonnelEntity> findByPersonnelId(UUID personnelId);
    
    Optional<GroupPersonnelEntity> findByGroupIdAndPersonnelId(UUID groupId, UUID personnelId);
    
    List<GroupPersonnelEntity> findByGroupIdOrderBySortOrderAsc(UUID groupId);
    
    void deleteByGroupIdAndPersonnelId(UUID groupId, UUID personnelId);
    
    boolean existsByGroupIdAndPersonnelId(UUID groupId, UUID personnelId);
}