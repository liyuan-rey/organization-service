package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupHierarchyCreateReq;
import com.reythecoder.organization.dto.response.GroupHierarchyRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface GroupHierarchyService {
    
    List<GroupHierarchyRsp> getChildrenByParentId(UUID parentId);
    
    List<GroupHierarchyRsp> getRootGroups();
    
    GroupHierarchyRsp getByChildId(@NotNull UUID childId);
    
    GroupHierarchyRsp create(@NotNull GroupHierarchyCreateReq req);
    
    void deleteByChildId(@NotNull UUID childId);
}