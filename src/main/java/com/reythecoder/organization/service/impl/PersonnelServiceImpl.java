package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.PersonnelMapper;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.PersonnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class PersonnelServiceImpl implements PersonnelService {
    private static final Logger logger = LoggerFactory.getLogger(PersonnelServiceImpl.class);
    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;

    public PersonnelServiceImpl(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
        this.personnelMapper = PersonnelMapper.INSTANCE;
    }

    @Override
    public List<PersonnelRsp> getAllPersonnel() {
        logger.info("获取所有人员");
        List<PersonnelEntity> entities = personnelRepository.findAll();
        return entities.stream()
                .map(personnelMapper::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public PersonnelRsp getPersonnelById(UUID id) {
        logger.info("根据ID获取人员: {}", id);
        PersonnelEntity entity = personnelRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "人员不存在"));
        return personnelMapper.toRsp(entity);
    }

    @Override
    public PersonnelRsp createPersonnel(PersonnelCreateReq req) {
        logger.info("创建人员: {}", req.name());
        PersonnelEntity entity = personnelMapper.toEntity(req);
        // 设置 ID、时间戳和租户ID
        entity.setId(UUID.randomUUID());
        entity.setCreateTime(java.time.OffsetDateTime.now());
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        entity.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        
        PersonnelEntity savedEntity = personnelRepository.save(java.util.Objects.requireNonNull(entity));
        return personnelMapper.toRsp(savedEntity);
    }

    @Override
    public PersonnelRsp updatePersonnel(UUID id, PersonnelUpdateReq req) {
        logger.info("更新人员: {}", id);
        PersonnelEntity entity = personnelRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "人员不存在"));
        personnelMapper.updateEntity(req, entity);
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        PersonnelEntity updatedEntity = personnelRepository.save(java.util.Objects.requireNonNull(entity));
        return personnelMapper.toRsp(updatedEntity);
    }

    @Override
    public void deletePersonnel(UUID id) {
        logger.info("删除人员: {}", id);
        PersonnelEntity entity = personnelRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "人员不存在"));
        personnelRepository.delete(java.util.Objects.requireNonNull(entity));
    }
}