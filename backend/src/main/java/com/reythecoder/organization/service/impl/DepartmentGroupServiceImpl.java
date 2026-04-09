package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentGroupEntity;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentGroupRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.DepartmentGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class DepartmentGroupServiceImpl implements DepartmentGroupService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentGroupServiceImpl.class);

    private final DepartmentGroupRepository departmentGroupRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;

    public DepartmentGroupServiceImpl(DepartmentGroupRepository departmentGroupRepository,
                                      DepartmentRepository departmentRepository,
                                      GroupRepository groupRepository) {
        this.departmentGroupRepository = departmentGroupRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<DepartmentGroupRsp> getByDepartmentId(UUID departmentId) {
        logger.info("获取部门关联分组列表, departmentId: {}", departmentId);
        List<DepartmentGroupEntity> entities = departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(departmentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentGroupRsp> getByGroupId(UUID groupId) {
        logger.info("获取分组关联部门列表, groupId: {}", groupId);
        List<DepartmentGroupEntity> entities = departmentGroupRepository.findByGroupIdOrderBySortOrderAsc(groupId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentGroupRsp create(DepartmentGroupCreateReq req) {
        logger.info("创建部门分组关联, departmentId: {}, groupId: {}", req.getDepartmentId(), req.getGroupId());

        if (departmentGroupRepository.existsByDepartmentIdAndGroupId(req.getDepartmentId(), req.getGroupId())) {
            throw new ApiException(400, "该分组已在此部门中");
        }

        DepartmentGroupEntity entity = new DepartmentGroupEntity(
                req.getDepartmentId(),
                req.getGroupId(),
                req.getSortOrder()
        );

        DepartmentGroupEntity savedEntity = departmentGroupRepository.save(entity);
        return toRsp(savedEntity);
    }

    @Override
    public void delete(UUID departmentId, UUID groupId) {
        logger.info("删除部门分组关联, departmentId: {}, groupId: {}", departmentId, groupId);
        departmentGroupRepository.deleteByDepartmentIdAndGroupId(departmentId, groupId);
    }

    private DepartmentGroupRsp toRsp(DepartmentGroupEntity entity) {
        DepartmentGroupRsp rsp = new DepartmentGroupRsp();
        rsp.setId(entity.getId());
        rsp.setDepartmentId(entity.getDepartmentId());
        rsp.setGroupId(entity.getGroupId());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());

        departmentRepository.findById(entity.getDepartmentId())
                .ifPresent(dept -> rsp.setDepartmentName(dept.getName()));

        groupRepository.findById(entity.getGroupId())
                .ifPresent(group -> rsp.setGroupName(group.getName()));

        return rsp;
    }
}