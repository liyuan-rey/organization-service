package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.GroupHierarchyCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.GroupHierarchyRsp;
import com.reythecoder.organization.service.GroupHierarchyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-hierarchy")
public class GroupHierarchyController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupHierarchyController.class);
    
    private final GroupHierarchyService hierarchyService;
    
    public GroupHierarchyController(GroupHierarchyService hierarchyService) {
        this.hierarchyService = hierarchyService;
    }
    
    @GetMapping("/roots")
    public ApiResult<List<GroupHierarchyRsp>> getRootGroups() {
        logger.info("收到获取根分组列表请求");
        List<GroupHierarchyRsp> roots = hierarchyService.getRootGroups();
        return ApiResult.success(roots);
    }
    
    @GetMapping("/children/{parentId}")
    public ApiResult<List<GroupHierarchyRsp>> getChildren(@PathVariable UUID parentId) {
        logger.info("收到获取子分组列表请求, parentId: {}", parentId);
        List<GroupHierarchyRsp> children = hierarchyService.getChildrenByParentId(parentId);
        return ApiResult.success(children);
    }
    
    @GetMapping("/{childId}")
    public ApiResult<GroupHierarchyRsp> getByChildId(@PathVariable UUID childId) {
        logger.info("收到获取分组层级信息请求, childId: {}", childId);
        GroupHierarchyRsp rsp = hierarchyService.getByChildId(childId);
        return ApiResult.success(rsp);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<GroupHierarchyRsp> create(@Valid @RequestBody GroupHierarchyCreateReq req) {
        logger.info("收到创建分组层级关系请求, childId: {}", req.getChildId());
        GroupHierarchyRsp rsp = hierarchyService.create(req);
        return ApiResult.success("分组层级关系创建成功", rsp);
    }
    
    @DeleteMapping("/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID childId) {
        logger.info("收到删除分组层级关系请求, childId: {}", childId);
        hierarchyService.deleteByChildId(childId);
        return ApiResult.success("分组层级关系删除成功", null);
    }
}