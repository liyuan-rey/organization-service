package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.GroupRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.GroupMapper;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupServiceImpl implements GroupService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    
    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = GroupMapper.INSTANCE;
    }
    
    @Override
    public List<GroupRsp> getAllGroups() {
        logger.info("获取所有分组");
        List<GroupEntity> entities = groupRepository.findAll();
        return entities.stream()
                .map(groupMapper::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public GroupRsp getGroupById(UUID id) {
        logger.info("根据ID获取分组: {}", id);
        GroupEntity entity = groupRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "分组不存在"));
        return groupMapper.toRsp(entity);
    }
    
    @Override
    public GroupRsp createGroup(GroupCreateReq req) {
        logger.info("创建分组: {}", req.getName());
        GroupEntity entity = groupMapper.toEntity(req);
        entity.setId(io.github.robsonkades.uuidv7.UUIDv7.randomUUID());
        entity.setCreateTime(java.time.OffsetDateTime.now());
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        entity.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        GroupEntity savedEntity = groupRepository.save(java.util.Objects.requireNonNull(entity));
        return groupMapper.toRsp(savedEntity);
    }
    
    @Override
    public GroupRsp updateGroup(UUID id, GroupUpdateReq req) {
        logger.info("更新分组: {}", id);
        GroupEntity entity = groupRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "分组不存在"));
        groupMapper.updateEntity(req, entity);
        GroupEntity updatedEntity = groupRepository.save(java.util.Objects.requireNonNull(entity));
        return groupMapper.toRsp(updatedEntity);
    }
    
    @Override
    public void deleteGroup(UUID id) {
        logger.info("删除分组: {}", id);
        GroupEntity entity = groupRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "分组不存在"));
        groupRepository.delete(java.util.Objects.requireNonNull(entity));
    }
}