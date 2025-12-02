package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    /**
     * 获取所有部门
     * 
     * @return 部门列表
     */
    List<DepartmentRsp> getAllDepartments();

    /**
     * 根据ID获取部门
     * 
     * @param id 部门ID
     * @return 部门信息
     */
    DepartmentRsp getDepartmentById(@NotNull UUID id);

    /**
     * 创建部门
     * 
     * @param req 创建部门请求
     * @return 创建后的部门信息
     */
    DepartmentRsp createDepartment(@NotNull DepartmentCreateReq req);

    /**
     * 更新部门
     * 
     * @param id  部门ID
     * @param req 更新部门请求
     * @return 更新后的部门信息
     */
    DepartmentRsp updateDepartment(@NotNull UUID id, @NotNull DepartmentUpdateReq req);

    /**
     * 删除部门
     * 
     * @param id 部门ID
     */
    void deleteDepartment(@NotNull UUID id);
}
