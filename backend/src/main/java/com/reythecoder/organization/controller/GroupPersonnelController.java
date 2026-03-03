package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.GroupPersonnelCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.GroupPersonnelRsp;
import com.reythecoder.organization.service.GroupPersonnelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-personnel")
public class GroupPersonnelController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupPersonnelController.class);
    
    private final GroupPersonnelService groupPersonnelService;
    
    public GroupPersonnelController(GroupPersonnelService groupPersonnelService) {
        this.groupPersonnelService = groupPersonnelService;
    }
    
    @GetMapping("/group/{groupId}")
    public ApiResult<List<GroupPersonnelRsp>> getByGroupId(@PathVariable UUID groupId) {
        logger.info("收到获取分组成员列表请求, groupId: {}", groupId);
        List<GroupPersonnelRsp> list = groupPersonnelService.getByGroupId(groupId);
        return ApiResult.success(list);
    }
    
    @GetMapping("/personnel/{personnelId}")
    public ApiResult<List<GroupPersonnelRsp>> getByPersonnelId(@PathVariable UUID personnelId) {
        logger.info("收到获取人员所属分组列表请求, personnelId: {}", personnelId);
        List<GroupPersonnelRsp> list = groupPersonnelService.getByPersonnelId(personnelId);
        return ApiResult.success(list);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<GroupPersonnelRsp> create(@Valid @RequestBody GroupPersonnelCreateReq req) {
        logger.info("收到创建分组人员关联请求");
        GroupPersonnelRsp rsp = groupPersonnelService.create(req);
        return ApiResult.success("分组人员关联创建成功", rsp);
    }
    
    @DeleteMapping("/{groupId}/{personnelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID groupId, @PathVariable UUID personnelId) {
        logger.info("收到删除分组人员关联请求, groupId: {}, personnelId: {}", groupId, personnelId);
        groupPersonnelService.delete(groupId, personnelId);
        return ApiResult.success("分组人员关联删除成功", null);
    }
}