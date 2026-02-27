package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentPositionReq;
import com.reythecoder.organization.dto.response.DepartmentPositionRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentPositionEntity;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentPositionRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.DepartmentPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class DepartmentPositionServiceImpl implements DepartmentPositionService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentPositionServiceImpl.class);
    private final DepartmentPositionRepository departmentPositionRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public DepartmentPositionServiceImpl(DepartmentPositionRepository departmentPositionRepository,
                                         DepartmentRepository departmentRepository,
                                         PositionRepository positionRepository) {
        this.departmentPositionRepository = departmentPositionRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public List<DepartmentPositionRsp> getAllDepartmentPositions() {
        logger.info("获取所有部门岗位关联");
        List<DepartmentPositionEntity> entities = departmentPositionRepository.findAll();
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentPositionRsp> getPositionsByDepartmentId(UUID departmentId) {
        logger.info("根据部门 ID 获取岗位列表：{}", departmentId);
        List<DepartmentPositionEntity> entities = departmentPositionRepository.findByDepartmentId(
                java.util.Objects.requireNonNull(departmentId));
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentPositionRsp> getDepartmentsByPositionId(UUID positionId) {
        logger.info("根据岗位 ID 获取部门列表：{}", positionId);
        List<DepartmentPositionEntity> entities = departmentPositionRepository.findByPositionId(
                java.util.Objects.requireNonNull(positionId));
        return entities.stream()
                .map(this::toRspWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentPositionRsp createDepartmentPosition(DepartmentPositionReq req) {
        logger.info("创建部门岗位关联：部门={}, 岗位={}", req.getDepartmentId(), req.getPositionId());
        
        // 验证部门和岗位是否存在
        departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ApiException(404, "部门不存在"));
        positionRepository.findById(req.getPositionId())
                .orElseThrow(() -> new ApiException(404, "岗位不存在"));
        
        // 检查是否已存在
        var existing = departmentPositionRepository.findByDepartmentIdAndPositionId(
                req.getDepartmentId(), req.getPositionId());
        if (existing.isPresent()) {
            throw new ApiException(400, "该部门已配置此岗位");
        }
        
        DepartmentPositionEntity entity = new DepartmentPositionEntity(
                req.getDepartmentId(),
                req.getPositionId(),
                req.getIsPrimary(),
                req.getSortOrder()
        );
        DepartmentPositionEntity savedEntity = departmentPositionRepository.save(
                java.util.Objects.requireNonNull(entity));
        return toRspWithDetails(savedEntity);
    }

    @Override
    public void deleteDepartmentPosition(UUID departmentId, UUID positionId) {
        logger.info("删除部门岗位关联：部门={}, 岗位={}", departmentId, positionId);
        var entity = departmentPositionRepository.findByDepartmentIdAndPositionId(
                java.util.Objects.requireNonNull(departmentId),
                java.util.Objects.requireNonNull(positionId));
        if (entity.isEmpty()) {
            throw new ApiException(404, "部门岗位关联不存在");
        }
        departmentPositionRepository.delete(entity.get());
    }

    /**
     * 转换为响应 DTO，包含部门和岗位名称
     */
    private DepartmentPositionRsp toRspWithDetails(DepartmentPositionEntity entity) {
        DepartmentEntity dept = departmentRepository.findById(entity.getDepartmentId()).orElse(null);
        PositionEntity pos = positionRepository.findById(entity.getPositionId()).orElse(null);
        
        return DepartmentPositionRsp.builder()
                .id(entity.getId())
                .departmentId(entity.getDepartmentId())
                .departmentName(dept != null ? dept.getName() : "")
                .positionId(entity.getPositionId())
                .positionName(pos != null ? pos.getName() : "")
                .isPrimary(entity.getIsPrimary())
                .sortOrder(entity.getSortOrder())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
