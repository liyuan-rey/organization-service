package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.GroupPersonnelCreateReq;
import com.reythecoder.organization.dto.response.GroupPersonnelRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.entity.GroupPersonnelEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.GroupPersonnelRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.GroupPersonnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupPersonnelServiceImpl implements GroupPersonnelService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupPersonnelServiceImpl.class);
    
    private final GroupPersonnelRepository groupPersonnelRepository;
    private final GroupRepository groupRepository;
    private final PersonnelRepository personnelRepository;
    
    public GroupPersonnelServiceImpl(GroupPersonnelRepository groupPersonnelRepository,
                                     GroupRepository groupRepository,
                                     PersonnelRepository personnelRepository) {
        this.groupPersonnelRepository = groupPersonnelRepository;
        this.groupRepository = groupRepository;
        this.personnelRepository = personnelRepository;
    }
    
    @Override
    public List<GroupPersonnelRsp> getByGroupId(UUID groupId) {
        logger.info("获取分组成员列表, groupId: {}", groupId);
        List<GroupPersonnelEntity> entities = groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(groupId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GroupPersonnelRsp> getByPersonnelId(UUID personnelId) {
        logger.info("获取人员所属分组列表, personnelId: {}", personnelId);
        List<GroupPersonnelEntity> entities = groupPersonnelRepository.findByPersonnelId(personnelId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public GroupPersonnelRsp create(GroupPersonnelCreateReq req) {
        logger.info("创建分组人员关联, groupId: {}, personnelId: {}", req.getGroupId(), req.getPersonnelId());
        
        if (groupPersonnelRepository.existsByGroupIdAndPersonnelId(req.getGroupId(), req.getPersonnelId())) {
            throw new ApiException(400, "该人员已在此分组中");
        }
        
        GroupPersonnelEntity entity = new GroupPersonnelEntity(
                req.getGroupId(),
                req.getPersonnelId(),
                req.getRole(),
                req.getSortOrder()
        );
        
        GroupPersonnelEntity savedEntity = groupPersonnelRepository.save(entity);
        return toRsp(savedEntity);
    }
    
    @Override
    public void delete(UUID groupId, UUID personnelId) {
        logger.info("删除分组人员关联, groupId: {}, personnelId: {}", groupId, personnelId);
        groupPersonnelRepository.deleteByGroupIdAndPersonnelId(groupId, personnelId);
    }
    
    private GroupPersonnelRsp toRsp(GroupPersonnelEntity entity) {
        GroupPersonnelRsp rsp = new GroupPersonnelRsp();
        rsp.setId(entity.getId());
        rsp.setGroupId(entity.getGroupId());
        rsp.setPersonnelId(entity.getPersonnelId());
        rsp.setRole(entity.getRole());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());
        
        groupRepository.findById(entity.getGroupId())
                .ifPresent(group -> rsp.setGroupName(group.getName()));
        
        personnelRepository.findById(entity.getPersonnelId())
                .ifPresent(personnel -> rsp.setPersonnelName(personnel.getName()));
        
        return rsp;
    }
}