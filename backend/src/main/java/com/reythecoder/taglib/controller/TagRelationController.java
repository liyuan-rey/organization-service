package com.reythecoder.taglib.controller;

import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.service.TagRelationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tag-relations")
public class TagRelationController {

    private static final Logger logger = LoggerFactory.getLogger(TagRelationController.class);

    private final TagRelationService tagRelationService;

    public TagRelationController(TagRelationService tagRelationService) {
        this.tagRelationService = tagRelationService;
    }

    @GetMapping(params = {"objectType", "objectId"})
    public ApiResult<List<TagRelationRsp>> getByObject(
            @RequestParam String objectType,
            @RequestParam UUID objectId) {
        logger.info("收到根据对象获取标签关联请求, objectType: {}, objectId: {}", objectType, objectId);
        List<TagRelationRsp> relations = tagRelationService.getByObject(objectType, objectId);
        return ApiResult.success(relations);
    }

    @GetMapping(params = "tagId")
    public ApiResult<List<TagRelationRsp>> getByTag(@RequestParam UUID tagId) {
        logger.info("收到根据标签获取关联请求, tagId: {}", tagId);
        List<TagRelationRsp> relations = tagRelationService.getByTag(tagId);
        return ApiResult.success(relations);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<List<TagRelationRsp>> batchCreate(@Valid @RequestBody TagRelationReq req) {
        logger.info("收到批量创建标签关联请求, objectType: {}, objectId: {}", req.getObjectType(), req.getObjectId());
        List<TagRelationRsp> relations = tagRelationService.batchCreate(req);
        return ApiResult.success("标签关联创建成功", relations);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID id) {
        logger.info("收到删除标签关联请求, id: {}", id);
        tagRelationService.delete(id);
        return ApiResult.success("标签关联删除成功", null);
    }

    @PostMapping("/query")
    public ApiResult<List<TagRelationRsp>> queryByMultipleTags(@Valid @RequestBody TagRelationQueryReq req) {
        logger.info("收到多标签查询关联请求, objectType: {}, tagIds: {}", req.getObjectType(), req.getTagIds());
        List<TagRelationRsp> relations = tagRelationService.queryByMultipleTags(req);
        return ApiResult.success(relations);
    }
}
