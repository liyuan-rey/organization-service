package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.PersonnelPositionReq;
import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.organization.dto.response.PersonnelPositionRsp;
import com.reythecoder.organization.service.PersonnelPositionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/personnel-positions")
public class PersonnelPositionController {
    private static final Logger logger = LoggerFactory.getLogger(PersonnelPositionController.class);
    private final PersonnelPositionService personnelPositionService;

    public PersonnelPositionController(PersonnelPositionService personnelPositionService) {
        this.personnelPositionService = personnelPositionService;
    }

    @GetMapping
    public ApiResult<List<PersonnelPositionRsp>> getAllPersonnelPositions() {
        logger.info("收到获取所有人员岗位关联请求");
        List<PersonnelPositionRsp> relations = personnelPositionService.getAllPersonnelPositions();
        return ApiResult.success(relations);
    }

    @GetMapping("/personnel/{personnelId}")
    public ApiResult<List<PersonnelPositionRsp>> getPositionsByPersonnelId(@PathVariable UUID personnelId) {
        logger.info("收到根据人员 ID 获取岗位列表请求：{}", personnelId);
        List<PersonnelPositionRsp> relations = personnelPositionService.getPositionsByPersonnelId(personnelId);
        return ApiResult.success(relations);
    }

    @GetMapping("/position/{positionId}")
    public ApiResult<List<PersonnelPositionRsp>> getPersonnelByPositionId(@PathVariable UUID positionId) {
        logger.info("收到根据岗位 ID 获取人员列表请求：{}", positionId);
        List<PersonnelPositionRsp> relations = personnelPositionService.getPersonnelByPositionId(positionId);
        return ApiResult.success(relations);
    }

    @GetMapping("/department/{departmentId}")
    public ApiResult<List<PersonnelPositionRsp>> getPersonnelPositionsByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到根据部门 ID 获取人员岗位关联请求：{}", departmentId);
        List<PersonnelPositionRsp> relations = personnelPositionService.getPersonnelPositionsByDepartmentId(departmentId);
        return ApiResult.success(relations);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<PersonnelPositionRsp> createPersonnelPosition(@Valid @RequestBody PersonnelPositionReq req) {
        logger.info("收到创建人员岗位关联请求：人员={}, 岗位={}", req.getPersonnelId(), req.getPositionId());
        PersonnelPositionRsp relation = personnelPositionService.createPersonnelPosition(req);
        return ApiResult.success("人员岗位关联创建成功", relation);
    }

    @PutMapping("/{id}")
    public ApiResult<PersonnelPositionRsp> updatePersonnelPosition(@PathVariable UUID id,
            @Valid @RequestBody PersonnelPositionReq req) {
        logger.info("收到更新人员岗位关联请求：{}", id);
        PersonnelPositionRsp relation = personnelPositionService.updatePersonnelPosition(id, req);
        return ApiResult.success("人员岗位关联更新成功", relation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deletePersonnelPosition(@PathVariable UUID id) {
        logger.info("收到删除人员岗位关联请求：{}", id);
        personnelPositionService.deletePersonnelPosition(id);
        return ApiResult.success("人员岗位关联删除成功", null);
    }
}
