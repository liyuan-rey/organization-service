package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.GroupDepartmentCreateReq;
import com.reythecoder.organization.dto.response.GroupDepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.GroupDepartmentEntity;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.GroupDepartmentRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.GroupDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupDepartmentServiceImpl implements GroupDepartmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupDepartmentServiceImpl.class);
    
    private final GroupDepartmentRepository groupDepartmentRepository;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    
    public GroupDepartmentServiceImpl(GroupDepartmentRepository groupDepartmentRepository,
                                      GroupRepository groupRepository,
                                      DepartmentRepository departmentRepository) {
        this.groupDepartmentRepository = groupDepartmentRepository;
        this.groupRepository = groupRepository;
        this.departmentRepository = departmentRepository;
    }
    
    @Override
    public List<GroupDepartmentRsp> getByGroupId(UUID groupId) {
        logger.info("获取分组关联部门列表, groupId: {}", groupId);
        List<GroupDepartmentEntity> entities = groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(groupId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GroupDepartmentRsp> getByDepartmentId(UUID departmentId) {
        logger.info("获取部门关联分组列表, departmentId: {}", departmentId);
        List<GroupDepartmentEntity> entities = groupDepartmentRepository.findByDepartmentId(departmentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public GroupDepartmentRsp create(GroupDepartmentCreateReq req) {
        logger.info("创建分组部门关联, groupId: {}, departmentId: {}", req.getGroupId(), req.getDepartmentId());
        
        if (groupDepartmentRepository.existsByGroupIdAndDepartmentId(req.getGroupId(), req.getDepartmentId())) {
            throw new ApiException(400, "该部门已在此分组中");
        }
        
        GroupDepartmentEntity entity = new GroupDepartmentEntity(
                req.getGroupId(),
                req.getDepartmentId(),
                req.getRole(),
                req.getSortOrder()
        );
        
        GroupDepartmentEntity savedEntity = groupDepartmentRepository.save(entity);
        return toRsp(savedEntity);
    }
    
    @Override
    public void delete(UUID groupId, UUID departmentId) {
        logger.info("删除分组部门关联, groupId: {}, departmentId: {}", groupId, departmentId);
        groupDepartmentRepository.deleteByGroupIdAndDepartmentId(groupId, departmentId);
    }
    
    private GroupDepartmentRsp toRsp(GroupDepartmentEntity entity) {
        GroupDepartmentRsp rsp = new GroupDepartmentRsp();
        rsp.setId(entity.getId());
        rsp.setGroupId(entity.getGroupId());
        rsp.setDepartmentId(entity.getDepartmentId());
        rsp.setRole(entity.getRole());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());
        
        groupRepository.findById(entity.getGroupId())
                .ifPresent(group -> rsp.setGroupName(group.getName()));
        
        departmentRepository.findById(entity.getDepartmentId())
                .ifPresent(dept -> rsp.setDepartmentName(dept.getName()));
        
        return rsp;
    }
}