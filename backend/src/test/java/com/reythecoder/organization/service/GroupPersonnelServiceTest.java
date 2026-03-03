package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupPersonnelCreateReq;
import com.reythecoder.organization.dto.response.GroupPersonnelRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.entity.GroupPersonnelEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.GroupPersonnelRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.impl.GroupPersonnelServiceImpl;

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
class GroupPersonnelServiceTest {

    @Mock
    private GroupPersonnelRepository groupPersonnelRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PersonnelRepository personnelRepository;

    @InjectMocks
    private GroupPersonnelServiceImpl groupPersonnelService;

    private UUID groupId;
    private UUID personnelId;
    private GroupPersonnelEntity entity;
    private GroupPersonnelCreateReq createReq;
    private GroupEntity group;
    private PersonnelEntity personnel;

    @BeforeEach
    void setUp() {
        groupId = UUIDv7.randomUUID();
        personnelId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        entity = new GroupPersonnelEntity();
        entity.setId(UUIDv7.randomUUID());
        entity.setGroupId(groupId);
        entity.setPersonnelId(personnelId);
        entity.setRole("组长");
        entity.setSortOrder(1);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        createReq = new GroupPersonnelCreateReq(
                groupId,
                personnelId,
                "组长",
                1);

        group = new GroupEntity();
        group.setId(groupId);
        group.setName("开发组");

        personnel = new PersonnelEntity();
        personnel.setId(personnelId);
        personnel.setName("张三");
    }

    @Test
    void getByGroupId_shouldReturnPersonnelList() {
        when(groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(entity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

        List<GroupPersonnelRsp> result = groupPersonnelService.getByGroupId(groupId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonnelId()).isEqualTo(personnelId);
        assertThat(result.get(0).getPersonnelName()).isEqualTo("张三");
        verify(groupPersonnelRepository, times(1)).findByGroupIdOrderBySortOrderAsc(groupId);
    }

    @Test
    void getByPersonnelId_shouldReturnGroupList() {
        when(groupPersonnelRepository.findByPersonnelId(personnelId)).thenReturn(List.of(entity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

        List<GroupPersonnelRsp> result = groupPersonnelService.getByPersonnelId(personnelId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        assertThat(result.get(0).getGroupName()).isEqualTo("开发组");
        verify(groupPersonnelRepository, times(1)).findByPersonnelId(personnelId);
    }

    @Test
    void create_shouldReturnCreatedEntity() {
        when(groupPersonnelRepository.existsByGroupIdAndPersonnelId(groupId, personnelId)).thenReturn(false);
        when(groupPersonnelRepository.save(any(GroupPersonnelEntity.class))).thenReturn(entity);

        GroupPersonnelRsp result = groupPersonnelService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getPersonnelId()).isEqualTo(personnelId);
        verify(groupPersonnelRepository, times(1)).existsByGroupIdAndPersonnelId(groupId, personnelId);
        verify(groupPersonnelRepository, times(1)).save(any(GroupPersonnelEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRelationExists() {
        when(groupPersonnelRepository.existsByGroupIdAndPersonnelId(groupId, personnelId)).thenReturn(true);

        assertThatThrownBy(() -> groupPersonnelService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该人员已在此分组中");
        verify(groupPersonnelRepository, times(1)).existsByGroupIdAndPersonnelId(groupId, personnelId);
        verify(groupPersonnelRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteRelation() {
        doNothing().when(groupPersonnelRepository).deleteByGroupIdAndPersonnelId(groupId, personnelId);

        groupPersonnelService.delete(groupId, personnelId);

        verify(groupPersonnelRepository, times(1)).deleteByGroupIdAndPersonnelId(groupId, personnelId);
    }
}