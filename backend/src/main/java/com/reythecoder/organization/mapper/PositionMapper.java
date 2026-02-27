package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.organization.dto.response.PositionRsp;
import com.reythecoder.organization.entity.PositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring"
)
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    // 将创建请求 DTO 映射到实体
    PositionEntity toEntity(PositionCreateReq req);

    // 将更新请求 DTO 映射到实体
    void updateEntity(PositionUpdateReq req, @MappingTarget PositionEntity entity);

    // 将实体映射到响应 DTO
    PositionRsp toRsp(PositionEntity entity);
}
