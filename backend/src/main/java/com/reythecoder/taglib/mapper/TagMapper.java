package com.reythecoder.taglib.mapper;

import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagEntity toEntity(TagCreateReq req);

    void updateEntity(TagUpdateReq req, @MappingTarget TagEntity entity);

    TagRsp toRsp(TagEntity entity);
}
