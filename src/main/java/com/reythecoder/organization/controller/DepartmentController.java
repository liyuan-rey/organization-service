package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.ApiResponse;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.service.DepartmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ApiResponse<List<DepartmentRsp>> getAllDepartments() {
        logger.info("收到获取所有部门请求");
        List<DepartmentRsp> departments = departmentService.getAllDepartments();
        return ApiResponse.success(departments);
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentRsp> getDepartmentById(@PathVariable UUID id) {
        logger.info("收到根据ID获取部门请求: {}", id);
        DepartmentRsp department = departmentService.getDepartmentById(id);
        return ApiResponse.success(department);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DepartmentRsp> createDepartment(@Valid @RequestBody DepartmentCreateReq req) {
        logger.info("收到创建部门请求: {}", req.name());
        DepartmentRsp department = departmentService.createDepartment(req);
        return ApiResponse.success("部门创建成功", department);
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentRsp> updateDepartment(@PathVariable UUID id,
            @Valid @RequestBody DepartmentUpdateReq req) {
        logger.info("收到更新部门请求: {}", id);
        DepartmentRsp department = departmentService.updateDepartment(id, req);
        return ApiResponse.success("部门更新成功", department);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteDepartment(@PathVariable UUID id) {
        logger.info("收到删除部门请求: {}", id);
        departmentService.deleteDepartment(id);
        return ApiResponse.success("部门删除成功", null);
    }
}
