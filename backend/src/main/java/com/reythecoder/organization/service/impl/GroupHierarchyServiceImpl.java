package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.GroupHierarchyCreateReq;
import com.reythecoder.organization.dto.response.GroupHierarchyRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.entity.GroupHierarchyEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.GroupHierarchyRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.GroupHierarchyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupHierarchyServiceImpl implements GroupHierarchyService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupHierarchyServiceImpl.class);
    
    private final GroupHierarchyRepository hierarchyRepository;
    private final GroupRepository groupRepository;
    
    public GroupHierarchyServiceImpl(GroupHierarchyRepository hierarchyRepository,
                                     GroupRepository groupRepository) {
        this.hierarchyRepository = hierarchyRepository;
        this.groupRepository = groupRepository;
    }
    
    @Override
    public List<GroupHierarchyRsp> getChildrenByParentId(UUID parentId) {
        logger.info("获取子分组列表, parentId: {}", parentId);
        List<GroupHierarchyEntity> entities = hierarchyRepository.findByParentIdOrderBySortOrderAsc(parentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GroupHierarchyRsp> getRootGroups() {
        logger.info("获取根分组列表");
        List<GroupHierarchyEntity> entities = hierarchyRepository.findByParentId(null);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public GroupHierarchyRsp getByChildId(UUID childId) {
        logger.info("获取分组层级信息, childId: {}", childId);
        GroupHierarchyEntity entity = hierarchyRepository.findByChildId(childId)
                .orElseThrow(() -> new ApiException(404, "分组层级信息不存在"));
        return toRsp(entity);
    }
    
    @Override
    public GroupHierarchyRsp create(GroupHierarchyCreateReq req) {
        logger.info("创建分组层级关系, childId: {}", req.getChildId());
        
        if (hierarchyRepository.existsByChildId(req.getChildId())) {
            throw new ApiException(400, "该分组已存在层级关系");
        }
        
        GroupHierarchyEntity entity = new GroupHierarchyEntity(
                req.getParentId(),
                req.getChildId(),
                req.getLevel(),
                req.getPath(),
                req.getSortOrder()
        );
        
        GroupHierarchyEntity savedEntity = hierarchyRepository.save(entity);
        return toRsp(savedEntity);
    }
    
    @Override
    public void deleteByChildId(UUID childId) {
        logger.info("删除分组层级关系, childId: {}", childId);
        hierarchyRepository.deleteByChildId(childId);
    }
    
    private GroupHierarchyRsp toRsp(GroupHierarchyEntity entity) {
        GroupHierarchyRsp rsp = new GroupHierarchyRsp();
        rsp.setId(entity.getId());
        rsp.setParentId(entity.getParentId());
        rsp.setChildId(entity.getChildId());
        rsp.setLevel(entity.getLevel());
        rsp.setPath(entity.getPath());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());
        
        groupRepository.findById(entity.getChildId())
                .ifPresent(group -> rsp.setChildName(group.getName()));
        
        return rsp;
    }
}