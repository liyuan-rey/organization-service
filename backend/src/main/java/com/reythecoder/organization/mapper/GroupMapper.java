package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.GroupRsp;
import com.reythecoder.organization.entity.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMapper {
    
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
    
    GroupEntity toEntity(GroupCreateReq req);
    
    GroupRsp toRsp(GroupEntity entity);
    
    void updateEntity(GroupUpdateReq req, @MappingTarget GroupEntity entity);
}