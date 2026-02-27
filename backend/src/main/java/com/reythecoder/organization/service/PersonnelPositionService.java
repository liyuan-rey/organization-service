package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PersonnelPositionReq;
import com.reythecoder.organization.dto.response.PersonnelPositionRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface PersonnelPositionService {
    /**
     * 获取所有人员岗位关联
     * 
     * @return 人员岗位关联列表
     */
    List<PersonnelPositionRsp> getAllPersonnelPositions();

    /**
     * 根据人员 ID 获取岗位列表
     * 
     * @param personnelId 人员 ID
     * @return 岗位列表
     */
    List<PersonnelPositionRsp> getPositionsByPersonnelId(@NotNull UUID personnelId);

    /**
     * 根据岗位 ID 获取人员列表
     * 
     * @param positionId 岗位 ID
     * @return 人员列表
     */
    List<PersonnelPositionRsp> getPersonnelByPositionId(@NotNull UUID positionId);

    /**
     * 根据部门 ID 获取人员岗位关联
     * 
     * @param departmentId 部门 ID
     * @return 人员岗位关联列表
     */
    List<PersonnelPositionRsp> getPersonnelPositionsByDepartmentId(@NotNull UUID departmentId);

    /**
     * 创建人员岗位关联
     * 
     * @param req 创建请求
     * @return 创建后的关联信息
     */
    PersonnelPositionRsp createPersonnelPosition(@NotNull PersonnelPositionReq req);

    /**
     * 更新人员岗位关联
     * 
     * @param id  关联 ID
     * @param req 更新请求
     * @return 更新后的关联信息
     */
    PersonnelPositionRsp updatePersonnelPosition(@NotNull UUID id, @NotNull PersonnelPositionReq req);

    /**
     * 删除人员岗位关联
     * 
     * @param id 关联 ID
     */
    void deletePersonnelPosition(@NotNull UUID id);
}
