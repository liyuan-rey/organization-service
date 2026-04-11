package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentPositionReq;
import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.organization.dto.response.DepartmentPositionRsp;
import com.reythecoder.organization.service.DepartmentPositionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-positions")
public class DepartmentPositionController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentPositionController.class);
    private final DepartmentPositionService departmentPositionService;

    public DepartmentPositionController(DepartmentPositionService departmentPositionService) {
        this.departmentPositionService = departmentPositionService;
    }

    @GetMapping
    public ApiResult<List<DepartmentPositionRsp>> getAllDepartmentPositions() {
        logger.info("收到获取所有部门岗位关联请求");
        List<DepartmentPositionRsp> relations = departmentPositionService.getAllDepartmentPositions();
        return ApiResult.success(relations);
    }

    @GetMapping("/department/{departmentId}")
    public ApiResult<List<DepartmentPositionRsp>> getPositionsByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到根据部门 ID 获取岗位列表请求：{}", departmentId);
        List<DepartmentPositionRsp> relations = departmentPositionService.getPositionsByDepartmentId(departmentId);
        return ApiResult.success(relations);
    }

    @GetMapping("/position/{positionId}")
    public ApiResult<List<DepartmentPositionRsp>> getDepartmentsByPositionId(@PathVariable UUID positionId) {
        logger.info("收到根据岗位 ID 获取部门列表请求：{}", positionId);
        List<DepartmentPositionRsp> relations = departmentPositionService.getDepartmentsByPositionId(positionId);
        return ApiResult.success(relations);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<DepartmentPositionRsp> createDepartmentPosition(@Valid @RequestBody DepartmentPositionReq req) {
        logger.info("收到创建部门岗位关联请求：部门={}, 岗位={}", req.getDepartmentId(), req.getPositionId());
        DepartmentPositionRsp relation = departmentPositionService.createDepartmentPosition(req);
        return ApiResult.success("部门岗位关联创建成功", relation);
    }

    @DeleteMapping("/{departmentId}/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deleteDepartmentPosition(@PathVariable UUID departmentId, @PathVariable UUID positionId) {
        logger.info("收到删除部门岗位关联请求：部门={}, 岗位={}", departmentId, positionId);
        departmentPositionService.deleteDepartmentPosition(departmentId, positionId);
        return ApiResult.success("部门岗位关联删除成功", null);
    }
}
