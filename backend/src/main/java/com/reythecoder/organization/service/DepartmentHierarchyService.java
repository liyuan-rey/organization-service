package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentHierarchyCreateReq;
import com.reythecoder.organization.dto.response.DepartmentHierarchyRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentHierarchyService {
    
    List<DepartmentHierarchyRsp> getChildrenByParentId(UUID parentId);
    
    List<DepartmentHierarchyRsp> getRootDepartments();
    
    DepartmentHierarchyRsp getByChildId(@NotNull UUID childId);
    
    DepartmentHierarchyRsp create(@NotNull DepartmentHierarchyCreateReq req);
    
    void deleteByChildId(@NotNull UUID childId);
}