package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagCategoryService {
    List<TagCategoryRsp> getAllCategories();
    TagCategoryRsp getById(@NotNull UUID id);
    TagCategoryRsp create(@Valid @NotNull TagCategoryCreateReq req);
    TagCategoryRsp update(@NotNull UUID id, @Valid @NotNull TagCategoryUpdateReq req);
    void delete(@NotNull UUID id);
}
