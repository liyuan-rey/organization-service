package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import com.reythecoder.organization.service.DepartmentGroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-group")
public class DepartmentGroupController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentGroupController.class);

    private final DepartmentGroupService departmentGroupService;

    public DepartmentGroupController(DepartmentGroupService departmentGroupService) {
        this.departmentGroupService = departmentGroupService;
    }

    @GetMapping("/department/{departmentId}")
    public ApiResult<List<DepartmentGroupRsp>> getByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到获取部门关联分组列表请求, departmentId: {}", departmentId);
        List<DepartmentGroupRsp> list = departmentGroupService.getByDepartmentId(departmentId);
        return ApiResult.success(list);
    }

    @GetMapping("/group/{groupId}")
    public ApiResult<List<DepartmentGroupRsp>> getByGroupId(@PathVariable UUID groupId) {
        logger.info("收到获取分组关联部门列表请求, groupId: {}", groupId);
        List<DepartmentGroupRsp> list = departmentGroupService.getByGroupId(groupId);
        return ApiResult.success(list);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<DepartmentGroupRsp> create(@Valid @RequestBody DepartmentGroupCreateReq req) {
        logger.info("收到创建部门分组关联请求");
        DepartmentGroupRsp rsp = departmentGroupService.create(req);
        return ApiResult.success("部门分组关联创建成功", rsp);
    }

    @DeleteMapping("/{departmentId}/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID departmentId, @PathVariable UUID groupId) {
        logger.info("收到删除部门分组关联请求, departmentId: {}, groupId: {}", departmentId, groupId);
        departmentGroupService.delete(departmentId, groupId);
        return ApiResult.success("部门分组关联删除成功", null);
    }
}