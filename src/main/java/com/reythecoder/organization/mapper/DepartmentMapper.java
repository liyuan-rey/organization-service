package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    // 将创建请求DTO映射到实体
    DepartmentEntity toEntity(DepartmentCreateReq req);

    // 将更新请求DTO映射到实体
    void updateEntity(DepartmentUpdateReq req, @MappingTarget DepartmentEntity entity);

    // 将实体映射到响应DTO
    DepartmentRsp toRsp(DepartmentEntity entity);
}
