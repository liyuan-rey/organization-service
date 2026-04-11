package com.reythecoder.taglib.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.service.TagCategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tag-categories")
public class TagCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(TagCategoryController.class);

    private final TagCategoryService tagCategoryService;

    public TagCategoryController(TagCategoryService tagCategoryService) {
        this.tagCategoryService = tagCategoryService;
    }

    @GetMapping
    public ApiResult<List<TagCategoryRsp>> getAllCategories() {
        logger.info("收到获取所有标签分类请求");
        List<TagCategoryRsp> categories = tagCategoryService.getAllCategories();
        return ApiResult.success(categories);
    }

    @GetMapping("/{id}")
    public ApiResult<TagCategoryRsp> getById(@PathVariable UUID id) {
        logger.info("收到根据ID获取标签分类请求: {}", id);
        TagCategoryRsp category = tagCategoryService.getById(id);
        return ApiResult.success(category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<TagCategoryRsp> create(@Valid @RequestBody TagCategoryCreateReq req) {
        logger.info("收到创建标签分类请求: {}", req.getName());
        TagCategoryRsp category = tagCategoryService.create(req);
        return ApiResult.success("标签分类创建成功", category);
    }

    @PutMapping("/{id}")
    public ApiResult<TagCategoryRsp> update(@PathVariable UUID id,
            @Valid @RequestBody TagCategoryUpdateReq req) {
        logger.info("收到更新标签分类请求: {}", id);
        TagCategoryRsp category = tagCategoryService.update(id, req);
        return ApiResult.success("标签分类更新成功", category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID id) {
        logger.info("收到删除标签分类请求: {}", id);
        tagCategoryService.delete(id);
        return ApiResult.success("标签分类删除成功", null);
    }
}
