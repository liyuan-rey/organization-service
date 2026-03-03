package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentPersonnelCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.DepartmentPersonnelRsp;
import com.reythecoder.organization.service.DepartmentPersonnelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-personnel")
public class DepartmentPersonnelController {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentPersonnelController.class);
    
    private final DepartmentPersonnelService departmentPersonnelService;
    
    public DepartmentPersonnelController(DepartmentPersonnelService departmentPersonnelService) {
        this.departmentPersonnelService = departmentPersonnelService;
    }
    
    @GetMapping("/department/{departmentId}")
    public ApiResult<List<DepartmentPersonnelRsp>> getByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到获取部门人员列表请求, departmentId: {}", departmentId);
        List<DepartmentPersonnelRsp> list = departmentPersonnelService.getByDepartmentId(departmentId);
        return ApiResult.success(list);
    }
    
    @GetMapping("/personnel/{personnelId}")
    public ApiResult<List<DepartmentPersonnelRsp>> getByPersonnelId(@PathVariable UUID personnelId) {
        logger.info("收到获取人员所属部门列表请求, personnelId: {}", personnelId);
        List<DepartmentPersonnelRsp> list = departmentPersonnelService.getByPersonnelId(personnelId);
        return ApiResult.success(list);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<DepartmentPersonnelRsp> create(@Valid @RequestBody DepartmentPersonnelCreateReq req) {
        logger.info("收到创建部门人员关联请求");
        DepartmentPersonnelRsp rsp = departmentPersonnelService.create(req);
        return ApiResult.success("部门人员关联创建成功", rsp);
    }
    
    @DeleteMapping("/{departmentId}/{personnelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID departmentId, @PathVariable UUID personnelId) {
        logger.info("收到删除部门人员关联请求, departmentId: {}, personnelId: {}", departmentId, personnelId);
        departmentPersonnelService.delete(departmentId, personnelId);
        return ApiResult.success("部门人员关联删除成功", null);
    }
    
    @PutMapping("/set-primary/{personnelId}/{departmentId}")
    public ApiResult<Void> setPrimaryDepartment(@PathVariable UUID personnelId, @PathVariable UUID departmentId) {
        logger.info("收到设置主部门请求, personnelId: {}, departmentId: {}", personnelId, departmentId);
        departmentPersonnelService.setPrimaryDepartment(personnelId, departmentId);
        return ApiResult.success("主部门设置成功", null);
    }
}