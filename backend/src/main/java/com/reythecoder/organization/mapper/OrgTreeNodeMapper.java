package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrgTreeNodeMapper {
    OrgTreeNodeMapper INSTANCE = Mappers.getMapper(OrgTreeNodeMapper.class);

    @Mapping(target = "id", source = "entityId")
    @Mapping(target = "type", source = "entityType")
    @Mapping(target = "name", source = "alias")
    @Mapping(target = "sortOrder", source = "level")
    @Mapping(target = "statistics", ignore = true)
    @Mapping(target = "children", ignore = true)
    TreeNodeRsp toTreeNodeRsp(OrgTreeNodeEntity entity);

    List<TreeNodeRsp> toTreeNodeRspList(List<OrgTreeNodeEntity> entities);
}
