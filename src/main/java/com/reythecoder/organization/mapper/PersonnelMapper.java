package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.entity.PersonnelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonnelMapper {
    PersonnelMapper INSTANCE = Mappers.getMapper(PersonnelMapper.class);

    // 将创建请求DTO映射到实体
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    PersonnelEntity toEntity(PersonnelCreateReq req);

    // 将更新请求DTO映射到实体
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateEntity(PersonnelUpdateReq req, @MappingTarget PersonnelEntity entity);

    // 将实体映射到响应DTO
    PersonnelRsp toRsp(PersonnelEntity entity);
}