package com.reythecoder.taglib.mapper;

import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagCategoryMapper {
    TagCategoryMapper INSTANCE = Mappers.getMapper(TagCategoryMapper.class);

    TagCategoryEntity toEntity(TagCategoryCreateReq req);

    void updateEntity(TagCategoryUpdateReq req, @MappingTarget TagCategoryEntity entity);

    TagCategoryRsp toRsp(TagCategoryEntity entity);
}
