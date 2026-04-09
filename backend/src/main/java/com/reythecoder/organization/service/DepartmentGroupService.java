package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentGroupService {

    List<DepartmentGroupRsp> getByDepartmentId(@NotNull UUID departmentId);

    List<DepartmentGroupRsp> getByGroupId(@NotNull UUID groupId);

    DepartmentGroupRsp create(@NotNull DepartmentGroupCreateReq req);

    void delete(@NotNull UUID departmentId, @NotNull UUID groupId);
}