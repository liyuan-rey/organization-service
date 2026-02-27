package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.organization.dto.response.PositionRsp;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.PositionMapper;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class PositionServiceImpl implements PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
        this.positionMapper = PositionMapper.INSTANCE;
    }

    @Override
    public List<PositionRsp> getAllPositions() {
        logger.info("获取所有岗位");
        List<PositionEntity> entities = positionRepository.findAll();
        return entities.stream()
                .map(positionMapper::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public PositionRsp getPositionById(UUID id) {
        logger.info("根据 ID 获取岗位：{}", id);
        PositionEntity entity = positionRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "岗位不存在"));
        return positionMapper.toRsp(entity);
    }

    @Override
    public PositionRsp createPosition(PositionCreateReq req) {
        logger.info("创建岗位：{}", req.getName());
        
        // 检查岗位编码是否已存在
        List<PositionEntity> existing = positionRepository.findByCode(req.getCode());
        if (!existing.isEmpty()) {
            throw new ApiException(400, "岗位编码已存在");
        }
        
        PositionEntity entity = positionMapper.toEntity(req);
        entity.setId(io.github.robsonkades.uuidv7.UUIDv7.randomUUID());
        entity.setCreateTime(java.time.OffsetDateTime.now());
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        entity.setTenantId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"));
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        PositionEntity savedEntity = positionRepository.save(java.util.Objects.requireNonNull(entity));
        return positionMapper.toRsp(savedEntity);
    }

    @Override
    public PositionRsp updatePosition(UUID id, PositionUpdateReq req) {
        logger.info("更新岗位：{}", id);
        PositionEntity entity = positionRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "岗位不存在"));
        positionMapper.updateEntity(req, entity);
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        PositionEntity updatedEntity = positionRepository.save(java.util.Objects.requireNonNull(entity));
        return positionMapper.toRsp(updatedEntity);
    }

    @Override
    public void deletePosition(UUID id) {
        logger.info("删除岗位：{}", id);
        PositionEntity entity = positionRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "岗位不存在"));
        
        // 检查是否有关联的部门岗位关系
        // 实际应用中应该先检查并处理关联关系
        positionRepository.delete(java.util.Objects.requireNonNull(entity));
    }
}
