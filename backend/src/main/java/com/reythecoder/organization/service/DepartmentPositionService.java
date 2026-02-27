package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentPositionReq;
import com.reythecoder.organization.dto.response.DepartmentPositionRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentPositionService {
    /**
     * 获取所有部门岗位关联
     * 
     * @return 部门岗位关联列表
     */
    List<DepartmentPositionRsp> getAllDepartmentPositions();

    /**
     * 根据部门 ID 获取岗位列表
     * 
     * @param departmentId 部门 ID
     * @return 岗位列表
     */
    List<DepartmentPositionRsp> getPositionsByDepartmentId(@NotNull UUID departmentId);

    /**
     * 根据岗位 ID 获取部门列表
     * 
     * @param positionId 岗位 ID
     * @return 部门列表
     */
    List<DepartmentPositionRsp> getDepartmentsByPositionId(@NotNull UUID positionId);

    /**
     * 创建部门岗位关联
     * 
     * @param req 创建请求
     * @return 创建后的关联信息
     */
    DepartmentPositionRsp createDepartmentPosition(@NotNull DepartmentPositionReq req);

    /**
     * 删除部门岗位关联
     * 
     * @param departmentId 部门 ID
     * @param positionId   岗位 ID
     */
    void deleteDepartmentPosition(@NotNull UUID departmentId, @NotNull UUID positionId);
}
