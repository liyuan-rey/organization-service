package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagRelationService {
    List<TagRelationRsp> getByObject(@NotNull String objectType, @NotNull UUID objectId);
    List<TagRelationRsp> getByTag(@NotNull UUID tagId);
    List<TagRelationRsp> batchCreate(@Valid @NotNull TagRelationReq req);
    void delete(@NotNull UUID id);
    List<TagRelationRsp> queryByMultipleTags(@Valid @NotNull TagRelationQueryReq req);
}
