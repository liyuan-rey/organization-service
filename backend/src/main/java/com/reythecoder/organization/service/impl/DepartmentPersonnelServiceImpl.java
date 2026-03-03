package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentPersonnelCreateReq;
import com.reythecoder.organization.dto.response.DepartmentPersonnelRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentPersonnelEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentPersonnelRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.DepartmentPersonnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class DepartmentPersonnelServiceImpl implements DepartmentPersonnelService {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentPersonnelServiceImpl.class);
    
    private final DepartmentPersonnelRepository departmentPersonnelRepository;
    private final DepartmentRepository departmentRepository;
    private final PersonnelRepository personnelRepository;
    
    public DepartmentPersonnelServiceImpl(DepartmentPersonnelRepository departmentPersonnelRepository,
                                          DepartmentRepository departmentRepository,
                                          PersonnelRepository personnelRepository) {
        this.departmentPersonnelRepository = departmentPersonnelRepository;
        this.departmentRepository = departmentRepository;
        this.personnelRepository = personnelRepository;
    }
    
    @Override
    public List<DepartmentPersonnelRsp> getByDepartmentId(UUID departmentId) {
        logger.info("获取部门人员列表, departmentId: {}", departmentId);
        List<DepartmentPersonnelEntity> entities = departmentPersonnelRepository.findByDepartmentIdOrderBySortOrderAsc(departmentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DepartmentPersonnelRsp> getByPersonnelId(UUID personnelId) {
        logger.info("获取人员所属部门列表, personnelId: {}", personnelId);
        List<DepartmentPersonnelEntity> entities = departmentPersonnelRepository.findByPersonnelId(personnelId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public DepartmentPersonnelRsp create(DepartmentPersonnelCreateReq req) {
        logger.info("创建部门人员关联, departmentId: {}, personnelId: {}", req.getDepartmentId(), req.getPersonnelId());
        
        if (departmentPersonnelRepository.existsByDepartmentIdAndPersonnelId(req.getDepartmentId(), req.getPersonnelId())) {
            throw new ApiException(400, "该人员已在此部门中");
        }
        
        DepartmentPersonnelEntity entity = new DepartmentPersonnelEntity(
                req.getDepartmentId(),
                req.getPersonnelId(),
                req.getIsPrimary(),
                req.getPosition(),
                req.getSortOrder()
        );
        
        DepartmentPersonnelEntity savedEntity = departmentPersonnelRepository.save(entity);
        return toRsp(savedEntity);
    }
    
    @Override
    public void delete(UUID departmentId, UUID personnelId) {
        logger.info("删除部门人员关联, departmentId: {}, personnelId: {}", departmentId, personnelId);
        departmentPersonnelRepository.deleteByDepartmentIdAndPersonnelId(departmentId, personnelId);
    }
    
    @Override
    @Transactional
    public void setPrimaryDepartment(UUID personnelId, UUID departmentId) {
        logger.info("设置主部门, personnelId: {}, departmentId: {}", personnelId, departmentId);
        
        List<DepartmentPersonnelEntity> existing = departmentPersonnelRepository.findByPersonnelId(personnelId);
        for (DepartmentPersonnelEntity entity : existing) {
            entity.setIsPrimary(entity.getDepartmentId().equals(departmentId));
        }
        departmentPersonnelRepository.saveAll(existing);
    }
    
    private DepartmentPersonnelRsp toRsp(DepartmentPersonnelEntity entity) {
        DepartmentPersonnelRsp rsp = new DepartmentPersonnelRsp();
        rsp.setId(entity.getId());
        rsp.setDepartmentId(entity.getDepartmentId());
        rsp.setPersonnelId(entity.getPersonnelId());
        rsp.setIsPrimary(entity.getIsPrimary());
        rsp.setPosition(entity.getPosition());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());
        
        departmentRepository.findById(entity.getDepartmentId())
                .ifPresent(dept -> rsp.setDepartmentName(dept.getName()));
        
        personnelRepository.findById(entity.getPersonnelId())
                .ifPresent(personnel -> rsp.setPersonnelName(personnel.getName()));
        
        return rsp;
    }
}