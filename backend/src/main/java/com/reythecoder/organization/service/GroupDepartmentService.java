package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupDepartmentCreateReq;
import com.reythecoder.organization.dto.response.GroupDepartmentRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface GroupDepartmentService {
    
    List<GroupDepartmentRsp> getByGroupId(@NotNull UUID groupId);
    
    List<GroupDepartmentRsp> getByDepartmentId(@NotNull UUID departmentId);
    
    GroupDepartmentRsp create(@NotNull GroupDepartmentCreateReq req);
    
    void delete(@NotNull UUID groupId, @NotNull UUID departmentId);
}