package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentHierarchyCreateReq;
import com.reythecoder.organization.dto.response.DepartmentHierarchyRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentHierarchyEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentHierarchyRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.service.DepartmentHierarchyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class DepartmentHierarchyServiceImpl implements DepartmentHierarchyService {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentHierarchyServiceImpl.class);
    
    private final DepartmentHierarchyRepository hierarchyRepository;
    private final DepartmentRepository departmentRepository;
    
    public DepartmentHierarchyServiceImpl(DepartmentHierarchyRepository hierarchyRepository,
                                          DepartmentRepository departmentRepository) {
        this.hierarchyRepository = hierarchyRepository;
        this.departmentRepository = departmentRepository;
    }
    
    @Override
    public List<DepartmentHierarchyRsp> getChildrenByParentId(UUID parentId) {
        logger.info("获取子部门列表, parentId: {}", parentId);
        List<DepartmentHierarchyEntity> entities = hierarchyRepository.findByParentIdOrderBySortOrderAsc(parentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DepartmentHierarchyRsp> getRootDepartments() {
        logger.info("获取根部门列表");
        List<DepartmentHierarchyEntity> entities = hierarchyRepository.findByParentId(null);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }
    
    @Override
    public DepartmentHierarchyRsp getByChildId(UUID childId) {
        logger.info("获取部门层级信息, childId: {}", childId);
        DepartmentHierarchyEntity entity = hierarchyRepository.findByChildId(childId)
                .orElseThrow(() -> new ApiException(404, "部门层级信息不存在"));
        return toRsp(entity);
    }
    
    @Override
    public DepartmentHierarchyRsp create(DepartmentHierarchyCreateReq req) {
        logger.info("创建部门层级关系, childId: {}", req.getChildId());
        
        if (hierarchyRepository.existsByChildId(req.getChildId())) {
            throw new ApiException(400, "该部门已存在层级关系");
        }
        
        DepartmentHierarchyEntity entity = new DepartmentHierarchyEntity(
                req.getParentId(),
                req.getChildId(),
                req.getLevel(),
                req.getPath(),
                req.getSortOrder()
        );
        
        DepartmentHierarchyEntity savedEntity = hierarchyRepository.save(entity);
        return toRsp(savedEntity);
    }
    
    @Override
    public void deleteByChildId(UUID childId) {
        logger.info("删除部门层级关系, childId: {}", childId);
        hierarchyRepository.deleteByChildId(childId);
    }
    
    private DepartmentHierarchyRsp toRsp(DepartmentHierarchyEntity entity) {
        DepartmentHierarchyRsp rsp = new DepartmentHierarchyRsp();
        rsp.setId(entity.getId());
        rsp.setParentId(entity.getParentId());
        rsp.setChildId(entity.getChildId());
        rsp.setLevel(entity.getLevel());
        rsp.setPath(entity.getPath());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());
        
        departmentRepository.findById(entity.getChildId())
                .ifPresent(dept -> rsp.setChildName(dept.getName()));
        
        return rsp;
    }
}