package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentHierarchyCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.DepartmentHierarchyRsp;
import com.reythecoder.organization.service.DepartmentHierarchyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-hierarchy")
public class DepartmentHierarchyController {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentHierarchyController.class);
    
    private final DepartmentHierarchyService hierarchyService;
    
    public DepartmentHierarchyController(DepartmentHierarchyService hierarchyService) {
        this.hierarchyService = hierarchyService;
    }
    
    @GetMapping("/roots")
    public ApiResult<List<DepartmentHierarchyRsp>> getRootDepartments() {
        logger.info("收到获取根部门列表请求");
        List<DepartmentHierarchyRsp> roots = hierarchyService.getRootDepartments();
        return ApiResult.success(roots);
    }
    
    @GetMapping("/children/{parentId}")
    public ApiResult<List<DepartmentHierarchyRsp>> getChildren(@PathVariable UUID parentId) {
        logger.info("收到获取子部门列表请求, parentId: {}", parentId);
        List<DepartmentHierarchyRsp> children = hierarchyService.getChildrenByParentId(parentId);
        return ApiResult.success(children);
    }
    
    @GetMapping("/{childId}")
    public ApiResult<DepartmentHierarchyRsp> getByChildId(@PathVariable UUID childId) {
        logger.info("收到获取部门层级信息请求, childId: {}", childId);
        DepartmentHierarchyRsp rsp = hierarchyService.getByChildId(childId);
        return ApiResult.success(rsp);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<DepartmentHierarchyRsp> create(@Valid @RequestBody DepartmentHierarchyCreateReq req) {
        logger.info("收到创建部门层级关系请求, childId: {}", req.getChildId());
        DepartmentHierarchyRsp rsp = hierarchyService.create(req);
        return ApiResult.success("部门层级关系创建成功", rsp);
    }
    
    @DeleteMapping("/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID childId) {
        logger.info("收到删除部门层级关系请求, childId: {}", childId);
        hierarchyService.deleteByChildId(childId);
        return ApiResult.success("部门层级关系删除成功", null);
    }
}