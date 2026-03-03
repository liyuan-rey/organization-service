package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentHierarchyCreateReq;
import com.reythecoder.organization.dto.response.DepartmentHierarchyRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentHierarchyEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentHierarchyRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.service.impl.DepartmentHierarchyServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentHierarchyServiceTest {

    @Mock
    private DepartmentHierarchyRepository hierarchyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentHierarchyServiceImpl hierarchyService;

    private UUID parentId;
    private UUID childId;
    private DepartmentHierarchyEntity hierarchyEntity;
    private DepartmentHierarchyCreateReq createReq;
    private DepartmentEntity childDepartment;

    @BeforeEach
    void setUp() {
        parentId = UUIDv7.randomUUID();
        childId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        hierarchyEntity = new DepartmentHierarchyEntity();
        hierarchyEntity.setId(UUIDv7.randomUUID());
        hierarchyEntity.setParentId(parentId);
        hierarchyEntity.setChildId(childId);
        hierarchyEntity.setLevel(2);
        hierarchyEntity.setPath("/root/parent/" + childId + "/");
        hierarchyEntity.setSortOrder(1);
        hierarchyEntity.setCreateTime(now);
        hierarchyEntity.setUpdateTime(now);

        createReq = new DepartmentHierarchyCreateReq(
                parentId,
                childId,
                2,
                "/root/parent/" + childId + "/",
                1);

        childDepartment = new DepartmentEntity();
        childDepartment.setId(childId);
        childDepartment.setName("子部门");
    }

    @Test
    void getChildrenByParentId_shouldReturnChildren() {
        when(hierarchyRepository.findByParentIdOrderBySortOrderAsc(parentId)).thenReturn(List.of(hierarchyEntity));
        when(departmentRepository.findById(childId)).thenReturn(Optional.of(childDepartment));

        List<DepartmentHierarchyRsp> result = hierarchyService.getChildrenByParentId(parentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChildId()).isEqualTo(childId);
        assertThat(result.get(0).getChildName()).isEqualTo("子部门");
        verify(hierarchyRepository, times(1)).findByParentIdOrderBySortOrderAsc(parentId);
    }

    @Test
    void getRootDepartments_shouldReturnRoots() {
        DepartmentHierarchyEntity rootEntity = new DepartmentHierarchyEntity();
        rootEntity.setId(UUIDv7.randomUUID());
        rootEntity.setParentId(null);
        rootEntity.setChildId(childId);
        rootEntity.setLevel(1);
        rootEntity.setPath("/" + childId + "/");
        rootEntity.setSortOrder(1);

        when(hierarchyRepository.findByParentId(null)).thenReturn(List.of(rootEntity));
        when(departmentRepository.findById(childId)).thenReturn(Optional.of(childDepartment));

        List<DepartmentHierarchyRsp> result = hierarchyService.getRootDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParentId()).isNull();
        assertThat(result.get(0).getLevel()).isEqualTo(1);
        verify(hierarchyRepository, times(1)).findByParentId(null);
    }

    @Test
    void getByChildId_shouldReturnHierarchyWhenExists() {
        when(hierarchyRepository.findByChildId(childId)).thenReturn(Optional.of(hierarchyEntity));
        when(departmentRepository.findById(childId)).thenReturn(Optional.of(childDepartment));

        DepartmentHierarchyRsp result = hierarchyService.getByChildId(childId);

        assertThat(result.getChildId()).isEqualTo(childId);
        assertThat(result.getChildName()).isEqualTo("子部门");
        verify(hierarchyRepository, times(1)).findByChildId(childId);
    }

    @Test
    void getByChildId_shouldThrowExceptionWhenNotFound() {
        when(hierarchyRepository.findByChildId(childId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hierarchyService.getByChildId(childId))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门层级信息不存在");
        verify(hierarchyRepository, times(1)).findByChildId(childId);
    }

    @Test
    void create_shouldReturnCreatedHierarchy() {
        when(hierarchyRepository.existsByChildId(childId)).thenReturn(false);
        when(hierarchyRepository.save(any(DepartmentHierarchyEntity.class))).thenReturn(hierarchyEntity);

        DepartmentHierarchyRsp result = hierarchyService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getChildId()).isEqualTo(childId);
        verify(hierarchyRepository, times(1)).existsByChildId(childId);
        verify(hierarchyRepository, times(1)).save(any(DepartmentHierarchyEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenHierarchyExists() {
        when(hierarchyRepository.existsByChildId(childId)).thenReturn(true);

        assertThatThrownBy(() -> hierarchyService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该部门已存在层级关系");
        verify(hierarchyRepository, times(1)).existsByChildId(childId);
        verify(hierarchyRepository, never()).save(any());
    }

    @Test
    void deleteByChildId_shouldDeleteHierarchy() {
        doNothing().when(hierarchyRepository).deleteByChildId(childId);

        hierarchyService.deleteByChildId(childId);

        verify(hierarchyRepository, times(1)).deleteByChildId(childId);
    }
}