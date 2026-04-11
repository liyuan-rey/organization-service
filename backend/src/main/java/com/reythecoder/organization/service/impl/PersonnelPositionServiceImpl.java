package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.PersonnelPositionReq;
import com.reythecoder.organization.dto.response.PersonnelPositionRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.entity.PersonnelPositionEntity;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PersonnelPositionRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.PersonnelPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class PersonnelPositionServiceImpl implements PersonnelPositionService {
    private static final Logger logger = LoggerFactory.getLogger(PersonnelPositionServiceImpl.class);
    private final PersonnelPositionRepository personnelPositionRepository;
    private final PersonnelRepository personnelRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    public PersonnelPositionServiceImpl(PersonnelPositionRepository personnelPositionRepository,
                                        PersonnelRepository personnelRepository,
                                        PositionRepository positionRepository,
                                        DepartmentRepository departmentRepository) {
        this.personnelPositionRepository = personnelPositionRepository;
        this.personnelRepository = personnelRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<PersonnelPositionRsp> getAllPersonnelPositions() {
        logger.info("获取所有人员岗位关联");
        List<PersonnelPositionEntity> entities = personnelPositionRepository.findAll();
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonnelPositionRsp> getPositionsByPersonnelId(UUID personnelId) {
        logger.info("根据人员 ID 获取岗位列表：{}", personnelId);
        List<PersonnelPositionEntity> entities = personnelPositionRepository.findByPersonnelId(
                java.util.Objects.requireNonNull(personnelId));
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonnelPositionRsp> getPersonnelByPositionId(UUID positionId) {
        logger.info("根据岗位 ID 获取人员列表：{}", positionId);
        List<PersonnelPositionEntity> entities = personnelPositionRepository.findByPositionId(
                java.util.Objects.requireNonNull(positionId));
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonnelPositionRsp> getPersonnelPositionsByDepartmentId(UUID departmentId) {
        logger.info("根据部门 ID 获取人员岗位关联：{}", departmentId);
        List<PersonnelPositionEntity> entities = personnelPositionRepository.findByDepartmentId(
                java.util.Objects.requireNonNull(departmentId));
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public PersonnelPositionRsp createPersonnelPosition(PersonnelPositionReq req) {
        logger.info("创建人员岗位关联：人员={}, 岗位={}", req.getPersonnelId(), req.getPositionId());
        
        // 验证人员、岗位、部门是否存在
        personnelRepository.findById(req.getPersonnelId())
                .orElseThrow(() -> new ApiException(404, "人员不存在"));
        positionRepository.findById(req.getPositionId())
                .orElseThrow(() -> new ApiException(404, "岗位不存在"));
        if (req.getDepartmentId() != null) {
            departmentRepository.findById(req.getDepartmentId())
                    .orElseThrow(() -> new ApiException(404, "部门不存在"));
        }
        
        // 检查是否已存在
        var existing = personnelPositionRepository.findByPersonnelIdAndPositionIdAndDepartmentId(
                req.getPersonnelId(), req.getPositionId(), req.getDepartmentId());
        if (existing.isPresent()) {
            throw new ApiException(400, "该人员已配置此岗位");
        }
        
        PersonnelPositionEntity entity = new PersonnelPositionEntity(
                req.getPersonnelId(),
                req.getPositionId(),
                req.getDepartmentId(),
                req.getIsPrimary(),
                req.getStartDate(),
                req.getEndDate(),
                req.getStatus()
        );
        PersonnelPositionEntity savedEntity = personnelPositionRepository.save(
                java.util.Objects.requireNonNull(entity));
        return toRspWithDetails(savedEntity);
    }

    @Override
    public PersonnelPositionRsp updatePersonnelPosition(UUID id, PersonnelPositionReq req) {
        logger.info("更新人员岗位关联：{}", id);
        PersonnelPositionEntity entity = personnelPositionRepository.findById(
                java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "人员岗位关联不存在"));
        
        // 更新字段
        if (req.getIsPrimary() != null) {
            entity.setIsPrimary(req.getIsPrimary());
        }
        if (req.getStartDate() != null) {
            entity.setStartDate(req.getStartDate());
        }
        if (req.getEndDate() != null) {
            entity.setEndDate(req.getEndDate());
        }
        if (req.getStatus() != null) {
            entity.setStatus(req.getStatus());
        }
        
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        PersonnelPositionEntity updatedEntity = personnelPositionRepository.save(
                java.util.Objects.requireNonNull(entity));
        return toRspWithDetails(updatedEntity);
    }

    @Override
    public void deletePersonnelPosition(UUID id) {
        logger.info("删除人员岗位关联：{}", id);
        PersonnelPositionEntity entity = personnelPositionRepository.findById(
                java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "人员岗位关联不存在"));
        personnelPositionRepository.delete(java.util.Objects.requireNonNull(entity));
    }

    /**
     * 转换为响应 DTO，包含人员、岗位、部门名称
     */
    private PersonnelPositionRsp toRspWithDetails(PersonnelPositionEntity entity) {
        PersonnelEntity person = personnelRepository.findById(entity.getPersonnelId()).orElse(null);
        PositionEntity pos = positionRepository.findById(entity.getPositionId()).orElse(null);
        DepartmentEntity dept = entity.getDepartmentId() != null ? 
                departmentRepository.findById(entity.getDepartmentId()).orElse(null) : null;
        
        return PersonnelPositionRsp.builder()
                .id(entity.getId())
                .personnelId(entity.getPersonnelId())
                .personnelName(person != null ? person.getName() : "")
                .positionId(entity.getPositionId())
                .positionName(pos != null ? pos.getName() : "")
                .departmentId(entity.getDepartmentId())
                .departmentName(dept != null ? dept.getName() : "")
                .isPrimary(entity.getIsPrimary())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
