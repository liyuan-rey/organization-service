package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagService {
    List<TagTreeRsp> getTagTreeByCategory(@NotNull UUID categoryId);
    TagRsp getById(@NotNull UUID id);
    TagRsp create(@Valid @NotNull TagCreateReq req);
    TagRsp update(@NotNull UUID id, @Valid @NotNull TagUpdateReq req);
    void delete(@NotNull UUID id);
}
