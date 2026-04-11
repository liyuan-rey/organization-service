package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.organization.dto.response.PositionRsp;
import com.reythecoder.organization.service.PositionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/positions")
public class PositionController {
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public ApiResult<List<PositionRsp>> getAllPositions() {
        logger.info("收到获取所有岗位请求");
        List<PositionRsp> positions = positionService.getAllPositions();
        return ApiResult.success(positions);
    }

    @GetMapping("/{id}")
    public ApiResult<PositionRsp> getPositionById(@PathVariable UUID id) {
        logger.info("收到根据 ID 获取岗位请求：{}", id);
        PositionRsp position = positionService.getPositionById(id);
        return ApiResult.success(position);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<PositionRsp> createPosition(@Valid @RequestBody PositionCreateReq req) {
        logger.info("收到创建岗位请求：{}", req.getName());
        PositionRsp position = positionService.createPosition(req);
        return ApiResult.success("岗位创建成功", position);
    }

    @PutMapping("/{id}")
    public ApiResult<PositionRsp> updatePosition(@PathVariable UUID id,
            @Valid @RequestBody PositionUpdateReq req) {
        logger.info("收到更新岗位请求：{}", id);
        PositionRsp position = positionService.updatePosition(id, req);
        return ApiResult.success("岗位更新成功", position);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deletePosition(@PathVariable UUID id) {
        logger.info("收到删除岗位请求：{}", id);
        positionService.deletePosition(id);
        return ApiResult.success("岗位删除成功", null);
    }
}
