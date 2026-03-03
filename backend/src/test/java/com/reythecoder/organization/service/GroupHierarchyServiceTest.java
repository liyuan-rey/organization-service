package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupHierarchyCreateReq;
import com.reythecoder.organization.dto.response.GroupHierarchyRsp;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.entity.GroupHierarchyEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.GroupHierarchyRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.impl.GroupHierarchyServiceImpl;

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
class GroupHierarchyServiceTest {

    @Mock
    private GroupHierarchyRepository hierarchyRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupHierarchyServiceImpl hierarchyService;

    private UUID parentId;
    private UUID childId;
    private GroupHierarchyEntity hierarchyEntity;
    private GroupHierarchyCreateReq createReq;
    private GroupEntity childGroup;

    @BeforeEach
    void setUp() {
        parentId = UUIDv7.randomUUID();
        childId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        hierarchyEntity = new GroupHierarchyEntity();
        hierarchyEntity.setId(UUIDv7.randomUUID());
        hierarchyEntity.setParentId(parentId);
        hierarchyEntity.setChildId(childId);
        hierarchyEntity.setLevel(2);
        hierarchyEntity.setPath("/root/parent/" + childId + "/");
        hierarchyEntity.setSortOrder(1);
        hierarchyEntity.setCreateTime(now);
        hierarchyEntity.setUpdateTime(now);

        createReq = new GroupHierarchyCreateReq(
                parentId,
                childId,
                2,
                "/root/parent/" + childId + "/",
                1);

        childGroup = new GroupEntity();
        childGroup.setId(childId);
        childGroup.setName("子分组");
    }

    @Test
    void getChildrenByParentId_shouldReturnChildren() {
        when(hierarchyRepository.findByParentIdOrderBySortOrderAsc(parentId)).thenReturn(List.of(hierarchyEntity));
        when(groupRepository.findById(childId)).thenReturn(Optional.of(childGroup));

        List<GroupHierarchyRsp> result = hierarchyService.getChildrenByParentId(parentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChildId()).isEqualTo(childId);
        assertThat(result.get(0).getChildName()).isEqualTo("子分组");
        verify(hierarchyRepository, times(1)).findByParentIdOrderBySortOrderAsc(parentId);
    }

    @Test
    void getRootGroups_shouldReturnRoots() {
        GroupHierarchyEntity rootEntity = new GroupHierarchyEntity();
        rootEntity.setId(UUIDv7.randomUUID());
        rootEntity.setParentId(null);
        rootEntity.setChildId(childId);
        rootEntity.setLevel(1);
        rootEntity.setPath("/" + childId + "/");
        rootEntity.setSortOrder(1);

        when(hierarchyRepository.findByParentId(null)).thenReturn(List.of(rootEntity));
        when(groupRepository.findById(childId)).thenReturn(Optional.of(childGroup));

        List<GroupHierarchyRsp> result = hierarchyService.getRootGroups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParentId()).isNull();
        assertThat(result.get(0).getLevel()).isEqualTo(1);
        verify(hierarchyRepository, times(1)).findByParentId(null);
    }

    @Test
    void getByChildId_shouldReturnHierarchyWhenExists() {
        when(hierarchyRepository.findByChildId(childId)).thenReturn(Optional.of(hierarchyEntity));
        when(groupRepository.findById(childId)).thenReturn(Optional.of(childGroup));

        GroupHierarchyRsp result = hierarchyService.getByChildId(childId);

        assertThat(result.getChildId()).isEqualTo(childId);
        assertThat(result.getChildName()).isEqualTo("子分组");
        verify(hierarchyRepository, times(1)).findByChildId(childId);
    }

    @Test
    void getByChildId_shouldThrowExceptionWhenNotFound() {
        when(hierarchyRepository.findByChildId(childId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hierarchyService.getByChildId(childId))
                .isInstanceOf(ApiException.class)
                .hasMessage("分组层级信息不存在");
        verify(hierarchyRepository, times(1)).findByChildId(childId);
    }

    @Test
    void create_shouldReturnCreatedHierarchy() {
        when(hierarchyRepository.existsByChildId(childId)).thenReturn(false);
        when(hierarchyRepository.save(any(GroupHierarchyEntity.class))).thenReturn(hierarchyEntity);

        GroupHierarchyRsp result = hierarchyService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getChildId()).isEqualTo(childId);
        verify(hierarchyRepository, times(1)).existsByChildId(childId);
        verify(hierarchyRepository, times(1)).save(any(GroupHierarchyEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenHierarchyExists() {
        when(hierarchyRepository.existsByChildId(childId)).thenReturn(true);

        assertThatThrownBy(() -> hierarchyService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该分组已存在层级关系");
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