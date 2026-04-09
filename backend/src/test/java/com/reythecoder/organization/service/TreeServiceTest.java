package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.NodeType;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.*;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.*;
import com.reythecoder.organization.service.impl.TreeServiceImpl;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreeServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PersonnelRepository personnelRepository;
    @Mock
    private GroupHierarchyRepository groupHierarchyRepository;
    @Mock
    private GroupDepartmentRepository groupDepartmentRepository;
    @Mock
    private GroupPersonnelRepository groupPersonnelRepository;
    @Mock
    private DepartmentGroupRepository departmentGroupRepository;
    @Mock
    private DepartmentHierarchyRepository departmentHierarchyRepository;
    @Mock
    private DepartmentPersonnelRepository departmentPersonnelRepository;

    @InjectMocks
    private TreeServiceImpl treeService;

    private UUID groupId;
    private GroupEntity group;
    private UUID deptId;
    private DepartmentEntity department;

    @BeforeEach
    void setUp() {
        groupId = UUIDv7.randomUUID();
        group = new GroupEntity();
        group.setId(groupId);
        group.setName("基础通讯录");

        deptId = UUIDv7.randomUUID();
        department = new DepartmentEntity();
        department.setId(deptId);
        department.setName("技术部");
    }

    @Test
    void getTreeByGroupId_shouldThrowExceptionWhenGroupNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> treeService.getTreeByGroupId(groupId, 1))
                .isInstanceOf(ApiException.class)
                .hasMessage("分组不存在");
    }

    @Test
    void getTreeByGroupId_shouldReturnRootNodeWithStatistics() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupHierarchyRepository.countByParentId(groupId)).thenReturn(2L);
        when(groupDepartmentRepository.countByGroupId(groupId)).thenReturn(5L);
        when(groupPersonnelRepository.countByGroupId(groupId)).thenReturn(10L);

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(groupId);
        assertThat(result.getType()).isEqualTo(NodeType.GROUP);
        assertThat(result.getName()).isEqualTo("基础通讯录");
        assertThat(result.getStatistics().getSubGroupCount()).isEqualTo(2);
        assertThat(result.getStatistics().getSubDepartmentCount()).isEqualTo(5);
        assertThat(result.getStatistics().getPersonnelCount()).isEqualTo(10);
        // With depth=1, children should not be loaded
        assertThat(result.getChildren()).isEmpty();
    }

    @Test
    void getTreeByGroupId_shouldLoadOneLevelChildrenWithDepth2() {
        UUID childGroupId = UUIDv7.randomUUID();
        GroupEntity childGroup = new GroupEntity();
        childGroup.setId(childGroupId);
        childGroup.setName("子分组");

        GroupHierarchyEntity hierarchy = new GroupHierarchyEntity();
        hierarchy.setChildId(childGroupId);
        hierarchy.setSortOrder(1);

        GroupDepartmentEntity groupDept = new GroupDepartmentEntity();
        groupDept.setDepartmentId(deptId);
        groupDept.setSortOrder(2);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupRepository.findById(childGroupId)).thenReturn(Optional.of(childGroup));
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
        when(groupHierarchyRepository.countByParentId(groupId)).thenReturn(1L);
        when(groupDepartmentRepository.countByGroupId(groupId)).thenReturn(1L);
        when(groupPersonnelRepository.countByGroupId(groupId)).thenReturn(0L);
        when(groupHierarchyRepository.findByParentIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(hierarchy));
        when(groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(groupDept));
        when(groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of());
        // Child group stats
        when(groupHierarchyRepository.countByParentId(childGroupId)).thenReturn(0L);
        when(groupDepartmentRepository.countByGroupId(childGroupId)).thenReturn(0L);
        when(groupPersonnelRepository.countByGroupId(childGroupId)).thenReturn(0L);
        // Department stats
        when(departmentGroupRepository.countByDepartmentId(deptId)).thenReturn(0L);
        when(departmentHierarchyRepository.countByParentId(deptId)).thenReturn(0L);
        when(departmentPersonnelRepository.countByDepartmentId(deptId)).thenReturn(0L);

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 2);

        assertThat(result.getChildren()).hasSize(2);
        assertThat(result.getChildren().get(0).getType()).isEqualTo(NodeType.GROUP);
        assertThat(result.getChildren().get(1).getType()).isEqualTo(NodeType.DEPARTMENT);
        // Children should not have their children loaded (depth=2 loads only one level of children)
        assertThat(result.getChildren().get(0).getChildren()).isEmpty();
        assertThat(result.getChildren().get(1).getChildren()).isEmpty();
    }

    @Test
    void getTreeByGroupId_shouldSortChildrenByTypeThenSortOrder() {
        UUID personnelId = UUIDv7.randomUUID();
        PersonnelEntity personnel = new PersonnelEntity();
        personnel.setId(personnelId);
        personnel.setName("张三");

        GroupDepartmentEntity groupDept = new GroupDepartmentEntity();
        groupDept.setDepartmentId(deptId);
        groupDept.setSortOrder(3);

        GroupPersonnelEntity groupPersonnel = new GroupPersonnelEntity();
        groupPersonnel.setPersonnelId(personnelId);
        groupPersonnel.setSortOrder(1);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));
        when(groupHierarchyRepository.countByParentId(groupId)).thenReturn(0L);
        when(groupDepartmentRepository.countByGroupId(groupId)).thenReturn(1L);
        when(groupPersonnelRepository.countByGroupId(groupId)).thenReturn(1L);
        when(groupHierarchyRepository.findByParentIdOrderBySortOrderAsc(groupId)).thenReturn(List.of());
        when(groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(groupDept));
        when(groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of(groupPersonnel));
        when(departmentGroupRepository.countByDepartmentId(deptId)).thenReturn(0L);
        when(departmentHierarchyRepository.countByParentId(deptId)).thenReturn(0L);
        when(departmentPersonnelRepository.countByDepartmentId(deptId)).thenReturn(0L);

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 2);

        assertThat(result.getChildren()).hasSize(2);
        // GROUP(0) < DEPARTMENT(1) < PERSONNEL(2), so DEPARTMENT should come before PERSONNEL
        assertThat(result.getChildren().get(0).getType()).isEqualTo(NodeType.DEPARTMENT);
        assertThat(result.getChildren().get(1).getType()).isEqualTo(NodeType.PERSONNEL);
    }
}