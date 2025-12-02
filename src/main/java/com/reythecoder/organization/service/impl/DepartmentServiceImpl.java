package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.DepartmentMapper;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = DepartmentMapper.INSTANCE;
    }

    @Override
    public List<DepartmentRsp> getAllDepartments() {
        logger.info("获取所有部门");
        List<DepartmentEntity> entities = departmentRepository.findAll();
        return entities.stream()
                .map(departmentMapper::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentRsp getDepartmentById(UUID id) {
        logger.info("根据ID获取部门: {}", id);
        DepartmentEntity entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "部门不存在"));
        return departmentMapper.toRsp(entity);
    }

    @Override
    public DepartmentRsp createDepartment(DepartmentCreateReq req) {
        logger.info("创建部门: {}", req.name());
        DepartmentEntity entity = departmentMapper.toEntity(req);
        DepartmentEntity savedEntity = departmentRepository.save(entity);
        return departmentMapper.toRsp(savedEntity);
    }

    @Override
    public DepartmentRsp updateDepartment(UUID id, DepartmentUpdateReq req) {
        logger.info("更新部门: {}", id);
        DepartmentEntity entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "部门不存在"));
        departmentMapper.updateEntity(req, entity);
        DepartmentEntity updatedEntity = departmentRepository.save(entity);
        return departmentMapper.toRsp(updatedEntity);
    }

    @Override
    public void deleteDepartment(UUID id) {
        logger.info("删除部门: {}", id);
        DepartmentEntity entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "部门不存在"));
        departmentRepository.delete(entity);
    }
}