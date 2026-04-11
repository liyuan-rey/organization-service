package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.GroupRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.impl.GroupServiceImpl;

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
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private UUID groupId;
    private GroupEntity groupEntity;
    private GroupRsp groupRsp;
    private GroupCreateReq groupCreateReq;
    private GroupUpdateReq groupUpdateReq;

    @BeforeEach
    void setUp() {
        groupId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        groupEntity = new GroupEntity(
                groupId,
                "测试分组",
                "测试分组描述",
                now,
                now,
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                false);

        groupRsp = new GroupRsp(
                groupId,
                "测试分组",
                "测试分组描述",
                now,
                now);

        groupCreateReq = new GroupCreateReq(
                "测试分组",
                "测试分组描述");

        groupUpdateReq = new GroupUpdateReq(
                "更新后的分组",
                "更新后的分组描述");
    }

    @Test
    void getAllGroups_shouldReturnAllGroups() {
        when(groupRepository.findAll()).thenReturn(List.of(groupEntity));

        List<GroupRsp> result = groupService.getAllGroups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(groupEntity.getId());
        assertThat(result.get(0).getName()).isEqualTo(groupEntity.getName());
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    void getAllGroups_shouldReturnEmptyListWhenNoGroups() {
        when(groupRepository.findAll()).thenReturn(Collections.emptyList());

        List<GroupRsp> result = groupService.getAllGroups();

        assertThat(result).isEmpty();
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    void getGroupById_shouldReturnGroupWhenExists() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));

        GroupRsp result = groupService.getGroupById(groupId);

        assertThat(result.getId()).isEqualTo(groupEntity.getId());
        assertThat(result.getName()).isEqualTo(groupEntity.getName());
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void getGroupById_shouldThrowExceptionWhenGroupNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroupById(groupId))
                .isInstanceOf(ApiException.class)
                .hasMessage("分组不存在");
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void createGroup_shouldReturnCreatedGroup() {
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(groupEntity);

        GroupRsp result = groupService.createGroup(groupCreateReq);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(groupEntity.getId());
        assertThat(result.getName()).isEqualTo(groupEntity.getName());
        verify(groupRepository, times(1)).save(any(GroupEntity.class));
    }

    @Test
    void updateGroup_shouldReturnUpdatedGroupWhenExists() {
        GroupEntity updatedEntity = new GroupEntity(
                groupId,
                "更新后的分组",
                "更新后的分组描述",
                groupEntity.getCreateTime(),
                OffsetDateTime.now(),
                groupEntity.getTenantId(),
                false);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(groupRepository.save(groupEntity)).thenReturn(updatedEntity);

        GroupRsp result = groupService.updateGroup(groupId, groupUpdateReq);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedEntity.getId());
        assertThat(result.getName()).isEqualTo(updatedEntity.getName());
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).save(groupEntity);
    }

    @Test
    void updateGroup_shouldThrowExceptionWhenGroupNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.updateGroup(groupId, groupUpdateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("分组不存在");
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void deleteGroup_shouldDeleteGroupWhenExists() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));

        groupService.deleteGroup(groupId);

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).delete(groupEntity);
    }

    @Test
    void deleteGroup_shouldThrowExceptionWhenGroupNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.deleteGroup(groupId))
                .isInstanceOf(ApiException.class)
                .hasMessage("分组不存在");
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, never()).delete(any());
    }
}