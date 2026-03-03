package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.GroupRsp;
import com.reythecoder.organization.service.GroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    
    private final GroupService groupService;
    
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    
    @GetMapping
    public ApiResult<List<GroupRsp>> getAllGroups() {
        logger.info("收到获取所有分组请求");
        List<GroupRsp> groups = groupService.getAllGroups();
        return ApiResult.success(groups);
    }
    
    @GetMapping("/{id}")
    public ApiResult<GroupRsp> getGroupById(@PathVariable UUID id) {
        logger.info("收到根据ID获取分组请求: {}", id);
        GroupRsp group = groupService.getGroupById(id);
        return ApiResult.success(group);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<GroupRsp> createGroup(@Valid @RequestBody GroupCreateReq req) {
        logger.info("收到创建分组请求: {}", req.getName());
        GroupRsp group = groupService.createGroup(req);
        return ApiResult.success("分组创建成功", group);
    }
    
    @PutMapping("/{id}")
    public ApiResult<GroupRsp> updateGroup(@PathVariable UUID id, @Valid @RequestBody GroupUpdateReq req) {
        logger.info("收到更新分组请求: {}", id);
        GroupRsp group = groupService.updateGroup(id, req);
        return ApiResult.success("分组更新成功", group);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deleteGroup(@PathVariable UUID id) {
        logger.info("收到删除分组请求: {}", id);
        groupService.deleteGroup(id);
        return ApiResult.success("分组删除成功", null);
    }
}