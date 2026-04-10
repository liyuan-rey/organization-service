package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.entity.*;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.*;
import com.reythecoder.organization.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TreeServiceImpl implements TreeService {

    private static final Logger logger = LoggerFactory.getLogger(TreeServiceImpl.class);

    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private final PersonnelRepository personnelRepository;
    private final GroupHierarchyRepository groupHierarchyRepository;
    private final GroupDepartmentRepository groupDepartmentRepository;
    private final GroupPersonnelRepository groupPersonnelRepository;
    private final DepartmentGroupRepository departmentGroupRepository;
    private final DepartmentHierarchyRepository departmentHierarchyRepository;
    private final DepartmentPersonnelRepository departmentPersonnelRepository;

    public TreeServiceImpl(GroupRepository groupRepository,
                           DepartmentRepository departmentRepository,
                           PersonnelRepository personnelRepository,
                           GroupHierarchyRepository groupHierarchyRepository,
                           GroupDepartmentRepository groupDepartmentRepository,
                           GroupPersonnelRepository groupPersonnelRepository,
                           DepartmentGroupRepository departmentGroupRepository,
                           DepartmentHierarchyRepository departmentHierarchyRepository,
                           DepartmentPersonnelRepository departmentPersonnelRepository) {
        this.groupRepository = groupRepository;
        this.departmentRepository = departmentRepository;
        this.personnelRepository = personnelRepository;
        this.groupHierarchyRepository = groupHierarchyRepository;
        this.groupDepartmentRepository = groupDepartmentRepository;
        this.groupPersonnelRepository = groupPersonnelRepository;
        this.departmentGroupRepository = departmentGroupRepository;
        this.departmentHierarchyRepository = departmentHierarchyRepository;
        this.departmentPersonnelRepository = departmentPersonnelRepository;
    }

    @Override
    public TreeNodeRsp getTreeByGroupId(UUID groupId, Integer depth) {
        logger.info("获取树结构, groupId: {}, depth: {}", groupId, depth);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(404, "分组不存在"));

        int actualDepth = depth == null || depth < 1 ? 1 : depth;
        boolean loadAll = actualDepth == -1;

        return buildGroupNode(group, 1, actualDepth, loadAll);
    }

    private TreeNodeRsp buildGroupNode(GroupEntity group, int currentDepth, int maxDepth, boolean loadAll) {
        TreeNodeRsp node = new TreeNodeRsp();
        node.setId(group.getId());
        node.setType(EntityType.GROUP);
        node.setName(group.getName());
        node.setSortOrder(0);

        TreeStatistics stats = new TreeStatistics(
                (int) groupHierarchyRepository.countByParentId(group.getId()),
                (int) groupDepartmentRepository.countByGroupId(group.getId()),
                (int) groupPersonnelRepository.countByGroupId(group.getId())
        );
        node.setStatistics(stats);

        if (loadAll || currentDepth < maxDepth) {
            List<TreeNodeRsp> children = new ArrayList<>();

            // 子分组
            List<GroupHierarchyEntity> groupHierarchies = groupHierarchyRepository.findByParentIdOrderBySortOrderAsc(group.getId());
            for (GroupHierarchyEntity gh : groupHierarchies) {
                groupRepository.findById(gh.getChildId()).ifPresent(childGroup -> {
                    children.add(buildGroupNode(childGroup, currentDepth + 1, maxDepth, loadAll));
                });
            }

            // 子部门
            List<GroupDepartmentEntity> groupDepts = groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(group.getId());
            for (GroupDepartmentEntity gd : groupDepts) {
                departmentRepository.findById(gd.getDepartmentId()).ifPresent(dept -> {
                    children.add(buildDepartmentNode(dept, currentDepth + 1, maxDepth, loadAll, gd.getSortOrder()));
                });
            }

            // 人员
            List<GroupPersonnelEntity> groupPersonnel = groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(group.getId());
            for (GroupPersonnelEntity gp : groupPersonnel) {
                personnelRepository.findById(gp.getPersonnelId()).ifPresent(personnel -> {
                    children.add(buildPersonnelNode(personnel, gp.getSortOrder()));
                });
            }

            node.setChildren(sortChildren(children));
        } else {
            node.setChildren(new ArrayList<>());
        }

        return node;
    }

    private TreeNodeRsp buildDepartmentNode(DepartmentEntity dept, int currentDepth, int maxDepth, boolean loadAll, Integer sortOrder) {
        TreeNodeRsp node = new TreeNodeRsp();
        node.setId(dept.getId());
        node.setType(EntityType.DEPARTMENT);
        node.setName(dept.getName());
        node.setSortOrder(sortOrder != null ? sortOrder : 0);

        TreeStatistics stats = new TreeStatistics(
                (int) departmentGroupRepository.countByDepartmentId(dept.getId()),
                (int) departmentHierarchyRepository.countByParentId(dept.getId()),
                (int) departmentPersonnelRepository.countByDepartmentId(dept.getId())
        );
        node.setStatistics(stats);

        if (loadAll || currentDepth < maxDepth) {
            List<TreeNodeRsp> children = new ArrayList<>();

            // 子分组
            List<DepartmentGroupEntity> deptGroups = departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(dept.getId());
            for (DepartmentGroupEntity dg : deptGroups) {
                groupRepository.findById(dg.getGroupId()).ifPresent(childGroup -> {
                    children.add(buildGroupNode(childGroup, currentDepth + 1, maxDepth, loadAll));
                });
            }

            // 子部门
            List<DepartmentHierarchyEntity> deptHierarchies = departmentHierarchyRepository.findByParentIdOrderBySortOrderAsc(dept.getId());
            for (DepartmentHierarchyEntity dh : deptHierarchies) {
                departmentRepository.findById(dh.getChildId()).ifPresent(childDept -> {
                    children.add(buildDepartmentNode(childDept, currentDepth + 1, maxDepth, loadAll, dh.getSortOrder()));
                });
            }

            // 人员
            List<DepartmentPersonnelEntity> deptPersonnel = departmentPersonnelRepository.findByDepartmentIdOrderBySortOrderAsc(dept.getId());
            for (DepartmentPersonnelEntity dp : deptPersonnel) {
                personnelRepository.findById(dp.getPersonnelId()).ifPresent(personnel -> {
                    children.add(buildPersonnelNode(personnel, dp.getSortOrder()));
                });
            }

            node.setChildren(sortChildren(children));
        } else {
            node.setChildren(new ArrayList<>());
        }

        return node;
    }

    private TreeNodeRsp buildPersonnelNode(PersonnelEntity personnel, Integer sortOrder) {
        TreeNodeRsp node = new TreeNodeRsp();
        node.setId(personnel.getId());
        node.setType(EntityType.PERSONNEL);
        node.setName(personnel.getName());
        node.setSortOrder(sortOrder != null ? sortOrder : 0);
        node.setStatistics(new TreeStatistics(0, 0, 0));
        node.setChildren(new ArrayList<>());
        return node;
    }

    private List<TreeNodeRsp> sortChildren(List<TreeNodeRsp> children) {
        return children.stream()
                .sorted(Comparator
                        .comparing((TreeNodeRsp n) -> n.getType().ordinal())
                        .thenComparing(TreeNodeRsp::getSortOrder))
                .collect(Collectors.toList());
    }
}