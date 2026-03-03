package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentPersonnelCreateReq;
import com.reythecoder.organization.dto.response.DepartmentPersonnelRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentPersonnelService {
    
    List<DepartmentPersonnelRsp> getByDepartmentId(@NotNull UUID departmentId);
    
    List<DepartmentPersonnelRsp> getByPersonnelId(@NotNull UUID personnelId);
    
    DepartmentPersonnelRsp create(@NotNull DepartmentPersonnelCreateReq req);
    
    void delete(@NotNull UUID departmentId, @NotNull UUID personnelId);
    
    void setPrimaryDepartment(@NotNull UUID personnelId, @NotNull UUID departmentId);
}