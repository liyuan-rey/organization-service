package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupDepartmentCreateReq;
import com.reythecoder.organization.dto.response.GroupDepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.GroupDepartmentEntity;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.GroupDepartmentRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.impl.GroupDepartmentServiceImpl;

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
class GroupDepartmentServiceTest {

    @Mock
    private GroupDepartmentRepository groupDepartmentRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private GroupDepartmentServiceImpl groupDepartmentService;

    private UUID groupId;
    private UUID departmentId;
    private GroupDepartmentEntity entity;
    private GroupDepartmentCreateReq createReq;
    private GroupEntity group;
    private DepartmentEntity department;

    @BeforeEach
    void setUp() {
        groupId = UUIDv7.randomUUID();
        departmentId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        entity = new GroupDepartmentEntity();
        entity.setId(UUIDv7.randomUUID());
        entity.setGroupId(groupId);
        entity.setDepartmentId(departmentId);
        entity.setRole("协作部门");
        entity.setSortOrder(1);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        createReq = new GroupDepartmentCreateReq(
                groupId,
                departmentId,
                "协作部门",
                1);

        group = new GroupEntity();
        group.setId(groupId);
        group.setName("开发组");

        department = new DepartmentEntity();
        department.setId(departmentId);
        department.setName("技术部");
    }

    @Test
    void getByGroupId_shouldReturnDepartmentList() {
        when(groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(entity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        List<GroupDepartmentRsp> result = groupDepartmentService.getByGroupId(groupId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("技术部");
        verify(groupDepartmentRepository, times(1)).findByGroupIdOrderBySortOrderAsc(groupId);
    }

    @Test
    void getByDepartmentId_shouldReturnGroupList() {
        when(groupDepartmentRepository.findByDepartmentId(departmentId)).thenReturn(List.of(entity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        List<GroupDepartmentRsp> result = groupDepartmentService.getByDepartmentId(departmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        assertThat(result.get(0).getGroupName()).isEqualTo("开发组");
        verify(groupDepartmentRepository, times(1)).findByDepartmentId(departmentId);
    }

    @Test
    void create_shouldReturnCreatedEntity() {
        when(groupDepartmentRepository.existsByGroupIdAndDepartmentId(groupId, departmentId)).thenReturn(false);
        when(groupDepartmentRepository.save(any(GroupDepartmentEntity.class))).thenReturn(entity);

        GroupDepartmentRsp result = groupDepartmentService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).isEqualTo(departmentId);
        verify(groupDepartmentRepository, times(1)).existsByGroupIdAndDepartmentId(groupId, departmentId);
        verify(groupDepartmentRepository, times(1)).save(any(GroupDepartmentEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRelationExists() {
        when(groupDepartmentRepository.existsByGroupIdAndDepartmentId(groupId, departmentId)).thenReturn(true);

        assertThatThrownBy(() -> groupDepartmentService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该部门已在此分组中");
        verify(groupDepartmentRepository, times(1)).existsByGroupIdAndDepartmentId(groupId, departmentId);
        verify(groupDepartmentRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteRelation() {
        doNothing().when(groupDepartmentRepository).deleteByGroupIdAndDepartmentId(groupId, departmentId);

        groupDepartmentService.delete(groupId, departmentId);

        verify(groupDepartmentRepository, times(1)).deleteByGroupIdAndDepartmentId(groupId, departmentId);
    }
}