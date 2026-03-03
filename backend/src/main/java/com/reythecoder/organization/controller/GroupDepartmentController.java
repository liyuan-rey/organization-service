package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.GroupDepartmentCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.GroupDepartmentRsp;
import com.reythecoder.organization.service.GroupDepartmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-department")
public class GroupDepartmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupDepartmentController.class);
    
    private final GroupDepartmentService groupDepartmentService;
    
    public GroupDepartmentController(GroupDepartmentService groupDepartmentService) {
        this.groupDepartmentService = groupDepartmentService;
    }
    
    @GetMapping("/group/{groupId}")
    public ApiResult<List<GroupDepartmentRsp>> getByGroupId(@PathVariable UUID groupId) {
        logger.info("收到获取分组关联部门列表请求, groupId: {}", groupId);
        List<GroupDepartmentRsp> list = groupDepartmentService.getByGroupId(groupId);
        return ApiResult.success(list);
    }
    
    @GetMapping("/department/{departmentId}")
    public ApiResult<List<GroupDepartmentRsp>> getByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到获取部门关联分组列表请求, departmentId: {}", departmentId);
        List<GroupDepartmentRsp> list = groupDepartmentService.getByDepartmentId(departmentId);
        return ApiResult.success(list);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<GroupDepartmentRsp> create(@Valid @RequestBody GroupDepartmentCreateReq req) {
        logger.info("收到创建分组部门关联请求");
        GroupDepartmentRsp rsp = groupDepartmentService.create(req);
        return ApiResult.success("分组部门关联创建成功", rsp);
    }
    
    @DeleteMapping("/{groupId}/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID groupId, @PathVariable UUID departmentId) {
        logger.info("收到删除分组部门关联请求, groupId: {}, departmentId: {}", groupId, departmentId);
        groupDepartmentService.delete(groupId, departmentId);
        return ApiResult.success("分组部门关联删除成功", null);
    }
}