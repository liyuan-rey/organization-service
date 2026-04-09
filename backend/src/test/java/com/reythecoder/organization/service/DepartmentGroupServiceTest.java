package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentGroupEntity;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentGroupRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.impl.DepartmentGroupServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentGroupServiceTest {

    @Mock
    private DepartmentGroupRepository departmentGroupRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private DepartmentGroupServiceImpl departmentGroupService;

    private UUID departmentId;
    private UUID groupId;
    private DepartmentGroupEntity entity;
    private DepartmentGroupCreateReq createReq;
    private DepartmentEntity department;
    private GroupEntity group;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        groupId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        entity = new DepartmentGroupEntity();
        entity.setId(UUIDv7.randomUUID());
        entity.setDepartmentId(departmentId);
        entity.setGroupId(groupId);
        entity.setSortOrder(1);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        createReq = new DepartmentGroupCreateReq(departmentId, groupId, 1);

        department = new DepartmentEntity();
        department.setId(departmentId);
        department.setName("技术部");

        group = new GroupEntity();
        group.setId(groupId);
        group.setName("防汛组");
    }

    @Test
    void getByDepartmentId_shouldReturnGroupList() {
        when(departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(departmentId)).thenReturn(List.of(entity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        List<DepartmentGroupRsp> result = departmentGroupService.getByDepartmentId(departmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        assertThat(result.get(0).getGroupName()).isEqualTo("防汛组");
        verify(departmentGroupRepository, times(1)).findByDepartmentIdOrderBySortOrderAsc(departmentId);
    }

    @Test
    void getByGroupId_shouldReturnDepartmentList() {
        when(departmentGroupRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(entity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        List<DepartmentGroupRsp> result = departmentGroupService.getByGroupId(groupId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("技术部");
        verify(departmentGroupRepository, times(1)).findByGroupIdOrderBySortOrderAsc(groupId);
    }

    @Test
    void create_shouldReturnCreatedEntity() {
        when(departmentGroupRepository.existsByDepartmentIdAndGroupId(departmentId, groupId)).thenReturn(false);
        when(departmentGroupRepository.save(any(DepartmentGroupEntity.class))).thenReturn(entity);

        DepartmentGroupRsp result = departmentGroupService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(groupId);
        verify(departmentGroupRepository, times(1)).existsByDepartmentIdAndGroupId(departmentId, groupId);
        verify(departmentGroupRepository, times(1)).save(any(DepartmentGroupEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRelationExists() {
        when(departmentGroupRepository.existsByDepartmentIdAndGroupId(departmentId, groupId)).thenReturn(true);

        assertThatThrownBy(() -> departmentGroupService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该分组已在此部门中");
        verify(departmentGroupRepository, times(1)).existsByDepartmentIdAndGroupId(departmentId, groupId);
        verify(departmentGroupRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteRelation() {
        doNothing().when(departmentGroupRepository).deleteByDepartmentIdAndGroupId(departmentId, groupId);

        departmentGroupService.delete(departmentId, groupId);

        verify(departmentGroupRepository, times(1)).deleteByDepartmentIdAndGroupId(departmentId, groupId);
    }
}