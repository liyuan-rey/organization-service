package com.reythecoder.taglib.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.service.TagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResult<List<TagTreeRsp>> getTagTree(@RequestParam UUID categoryId) {
        logger.info("收到获取标签树请求, categoryId: {}", categoryId);
        List<TagTreeRsp> tags = tagService.getTagTreeByCategory(categoryId);
        return ApiResult.success(tags);
    }

    @GetMapping("/{id}")
    public ApiResult<TagRsp> getById(@PathVariable UUID id) {
        logger.info("收到根据ID获取标签请求: {}", id);
        TagRsp tag = tagService.getById(id);
        return ApiResult.success(tag);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<TagRsp> create(@Valid @RequestBody TagCreateReq req) {
        logger.info("收到创建标签请求: {}", req.getName());
        TagRsp tag = tagService.create(req);
        return ApiResult.success("标签创建成功", tag);
    }

    @PutMapping("/{id}")
    public ApiResult<TagRsp> update(@PathVariable UUID id,
            @Valid @RequestBody TagUpdateReq req) {
        logger.info("收到更新标签请求: {}", id);
        TagRsp tag = tagService.update(id, req);
        return ApiResult.success("标签更新成功", tag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID id) {
        logger.info("收到删除标签请求: {}", id);
        tagService.delete(id);
        return ApiResult.success("标签删除成功", null);
    }
}
