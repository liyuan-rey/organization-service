# 部门树结构 API 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现以分组为根的树结构查询 API，支持分层加载和统计信息。

**Architecture:** 新增 DepartmentGroup 关联实体支持部门挂子分组，新建 TreeService/TreeController 提供树结构查询，复用现有层级和关联表统计子节点数量。

**Tech Stack:** Spring Boot 4.0, Spring Data JPA, MapStruct, JUnit 5 + Mockito

---

## 文件结构

### 新建文件
| 文件 | 负责内容 |
|------|---------|
| `entity/DepartmentGroupEntity.java` | 部门-分组关联实体 |
| `repository/DepartmentGroupRepository.java` | 部门-分组 Repository |
| `service/DepartmentGroupService.java` | 部门-分组服务接口 |
| `service/impl/DepartmentGroupServiceImpl.java` | 部门-分组服务实现 |
| `controller/DepartmentGroupController.java` | 部门-分组控制器 |
| `dto/request/DepartmentGroupCreateReq.java` | 部门-分组创建请求 DTO |
| `dto/response/DepartmentGroupRsp.java` | 部门-分组响应 DTO |
| `dto/NodeType.java` | 节点类型枚举 |
| `dto/response/TreeStatistics.java` | 树统计信息 DTO |
| `dto/response/TreeNodeRsp.java` | 树节点响应 DTO |
| `service/TreeService.java` | 树结构服务接口 |
| `service/impl/TreeServiceImpl.java` | 树结构服务实现 |
| `controller/TreeController.java` | 树结构控制器 |
| `test/service/DepartmentGroupServiceTest.java` | 部门-分组服务单元测试 |
| `test/service/TreeServiceTest.java` | 树结构服务单元测试 |
| `test/controller/TreeControllerTest.java` | 树结构控制器测试 |

### 修改文件
| 文件 | 修改内容 |
|------|---------|
| `repository/GroupHierarchyRepository.java` | 添加 `countByParentId` |
| `repository/GroupDepartmentRepository.java` | 添加 `countByGroupId` |
| `repository/GroupPersonnelRepository.java` | 添加 `countByGroupId` |
| `repository/DepartmentHierarchyRepository.java` | 添加 `countByParentId` |
| `repository/DepartmentPersonnelRepository.java` | 添加 `countByDepartmentId` |

---

## Task 1: NodeType 枚举和 TreeStatistics DTO

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/dto/NodeType.java`
- Create: `backend/src/main/java/com/reythecoder/organization/dto/response/TreeStatistics.java`

- [ ] **Step 1: 创建 NodeType 枚举**

```java
package com.reythecoder.organization.dto;

public enum NodeType {
    GROUP,
    DEPARTMENT,
    PERSONNEL
}
```

- [ ] **Step 2: 创建 TreeStatistics DTO**

```java
package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeStatistics {
    private Integer subGroupCount;
    private Integer subDepartmentCount;
    private Integer personnelCount;
}
```

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/dto/NodeType.java src/main/java/com/reythecoder/organization/dto/response/TreeStatistics.java
git commit -m "feat(dto): 添加 NodeType 枚举和 TreeStatistics DTO"
```

---

## Task 2: TreeNodeRsp DTO

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/dto/response/TreeNodeRsp.java`

- [ ] **Step 1: 创建 TreeNodeRsp DTO**

```java
package com.reythecoder.organization.dto.response;

import com.reythecoder.organization.dto.NodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRsp {
    private UUID id;
    private NodeType type;
    private String name;
    private Integer sortOrder;
    private TreeStatistics statistics;
    private List<TreeNodeRsp> children;
}
```

- [ ] **Step 2: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/dto/response/TreeNodeRsp.java
git commit -m "feat(dto): 添加 TreeNodeRsp 树节点响应 DTO"
```

---

## Task 3: DepartmentGroupEntity 实体

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/entity/DepartmentGroupEntity.java`

- [ ] **Step 1: 创建 DepartmentGroupEntity**

```java
package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department_group",
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "group_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupEntity {

    @Id
    private UUID id;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public DepartmentGroupEntity(UUID departmentId, UUID groupId, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.departmentId = departmentId;
        this.groupId = groupId;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
```

- [ ] **Step 2: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/entity/DepartmentGroupEntity.java
git commit -m "feat(entity): 添加 DepartmentGroupEntity 部门-分组关联实体"
```

---

## Task 4: DepartmentGroupRepository

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/repository/DepartmentGroupRepository.java`

- [ ] **Step 1: 创建 DepartmentGroupRepository**

```java
package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentGroupRepository extends JpaRepository<DepartmentGroupEntity, UUID> {

    List<DepartmentGroupEntity> findByDepartmentId(UUID departmentId);

    List<DepartmentGroupEntity> findByGroupId(UUID groupId);

    List<DepartmentGroupEntity> findByDepartmentIdOrderBySortOrderAsc(UUID departmentId);

    List<DepartmentGroupEntity> findByGroupIdOrderBySortOrderAsc(UUID groupId);

    Optional<DepartmentGroupEntity> findByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    void deleteByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    boolean existsByDepartmentIdAndGroupId(UUID departmentId, UUID groupId);

    long countByDepartmentId(UUID departmentId);
}
```

- [ ] **Step 2: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/repository/DepartmentGroupRepository.java
git commit -m "feat(repository): 添加 DepartmentGroupRepository"
```

---

## Task 5: DepartmentGroup DTOs

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/dto/request/DepartmentGroupCreateReq.java`
- Create: `backend/src/main/java/com/reythecoder/organization/dto/response/DepartmentGroupRsp.java`

- [ ] **Step 1: 创建 DepartmentGroupCreateReq**

```java
package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupCreateReq {

    @NotNull(message = "部门ID不能为空")
    private UUID departmentId;

    @NotNull(message = "分组ID不能为空")
    private UUID groupId;

    private Integer sortOrder;
}
```

- [ ] **Step 2: 创建 DepartmentGroupRsp**

```java
package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupRsp {

    private UUID id;

    private UUID departmentId;

    private String departmentName;

    private UUID groupId;

    private String groupName;

    private Integer sortOrder;

    private OffsetDateTime createTime;

    private OffsetDateTime updateTime;
}
```

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/dto/request/DepartmentGroupCreateReq.java src/main/java/com/reythecoder/organization/dto/response/DepartmentGroupRsp.java
git commit -m "feat(dto): 添加 DepartmentGroup DTOs"
```

---

## Task 6: DepartmentGroupService 接口和实现

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/service/DepartmentGroupService.java`
- Create: `backend/src/main/java/com/reythecoder/organization/service/impl/DepartmentGroupServiceImpl.java`

- [ ] **Step 1: 创建 DepartmentGroupService 接口**

```java
package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface DepartmentGroupService {

    List<DepartmentGroupRsp> getByDepartmentId(@NotNull UUID departmentId);

    List<DepartmentGroupRsp> getByGroupId(@NotNull UUID groupId);

    DepartmentGroupRsp create(@NotNull DepartmentGroupCreateReq req);

    void delete(@NotNull UUID departmentId, @NotNull UUID groupId);
}
```

- [ ] **Step 2: 创建 DepartmentGroupServiceImpl**

```java
package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentGroupEntity;
import com.reythecoder.organization.entity.GroupEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentGroupRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.GroupRepository;
import com.reythecoder.organization.service.DepartmentGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class DepartmentGroupServiceImpl implements DepartmentGroupService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentGroupServiceImpl.class);

    private final DepartmentGroupRepository departmentGroupRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;

    public DepartmentGroupServiceImpl(DepartmentGroupRepository departmentGroupRepository,
                                      DepartmentRepository departmentRepository,
                                      GroupRepository groupRepository) {
        this.departmentGroupRepository = departmentGroupRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<DepartmentGroupRsp> getByDepartmentId(UUID departmentId) {
        logger.info("获取部门关联分组列表, departmentId: {}", departmentId);
        List<DepartmentGroupEntity> entities = departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(departmentId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentGroupRsp> getByGroupId(UUID groupId) {
        logger.info("获取分组关联部门列表, groupId: {}", groupId);
        List<DepartmentGroupEntity> entities = departmentGroupRepository.findByGroupIdOrderBySortOrderAsc(groupId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentGroupRsp create(DepartmentGroupCreateReq req) {
        logger.info("创建部门分组关联, departmentId: {}, groupId: {}", req.getDepartmentId(), req.getGroupId());

        if (departmentGroupRepository.existsByDepartmentIdAndGroupId(req.getDepartmentId(), req.getGroupId())) {
            throw new ApiException(400, "该分组已在此部门中");
        }

        DepartmentGroupEntity entity = new DepartmentGroupEntity(
                req.getDepartmentId(),
                req.getGroupId(),
                req.getSortOrder()
        );

        DepartmentGroupEntity savedEntity = departmentGroupRepository.save(entity);
        return toRsp(savedEntity);
    }

    @Override
    public void delete(UUID departmentId, UUID groupId) {
        logger.info("删除部门分组关联, departmentId: {}, groupId: {}", departmentId, groupId);
        departmentGroupRepository.deleteByDepartmentIdAndGroupId(departmentId, groupId);
    }

    private DepartmentGroupRsp toRsp(DepartmentGroupEntity entity) {
        DepartmentGroupRsp rsp = new DepartmentGroupRsp();
        rsp.setId(entity.getId());
        rsp.setDepartmentId(entity.getDepartmentId());
        rsp.setGroupId(entity.getGroupId());
        rsp.setSortOrder(entity.getSortOrder());
        rsp.setCreateTime(entity.getCreateTime());
        rsp.setUpdateTime(entity.getUpdateTime());

        departmentRepository.findById(entity.getDepartmentId())
                .ifPresent(dept -> rsp.setDepartmentName(dept.getName()));

        groupRepository.findById(entity.getGroupId())
                .ifPresent(group -> rsp.setGroupName(group.getName()));

        return rsp;
    }
}
```

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/service/DepartmentGroupService.java src/main/java/com/reythecoder/organization/service/impl/DepartmentGroupServiceImpl.java
git commit -m "feat(service): 添加 DepartmentGroupService 接口和实现"
```

---

## Task 7: DepartmentGroupController

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/controller/DepartmentGroupController.java`

- [ ] **Step 1: 创建 DepartmentGroupController**

```java
package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.DepartmentGroupCreateReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.DepartmentGroupRsp;
import com.reythecoder.organization.service.DepartmentGroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-group")
public class DepartmentGroupController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentGroupController.class);

    private final DepartmentGroupService departmentGroupService;

    public DepartmentGroupController(DepartmentGroupService departmentGroupService) {
        this.departmentGroupService = departmentGroupService;
    }

    @GetMapping("/department/{departmentId}")
    public ApiResult<List<DepartmentGroupRsp>> getByDepartmentId(@PathVariable UUID departmentId) {
        logger.info("收到获取部门关联分组列表请求, departmentId: {}", departmentId);
        List<DepartmentGroupRsp> list = departmentGroupService.getByDepartmentId(departmentId);
        return ApiResult.success(list);
    }

    @GetMapping("/group/{groupId}")
    public ApiResult<List<DepartmentGroupRsp>> getByGroupId(@PathVariable UUID groupId) {
        logger.info("收到获取分组关联部门列表请求, groupId: {}", groupId);
        List<DepartmentGroupRsp> list = departmentGroupService.getByGroupId(groupId);
        return ApiResult.success(list);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<DepartmentGroupRsp> create(@Valid @RequestBody DepartmentGroupCreateReq req) {
        logger.info("收到创建部门分组关联请求");
        DepartmentGroupRsp rsp = departmentGroupService.create(req);
        return ApiResult.success("部门分组关联创建成功", rsp);
    }

    @DeleteMapping("/{departmentId}/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID departmentId, @PathVariable UUID groupId) {
        logger.info("收到删除部门分组关联请求, departmentId: {}, groupId: {}", departmentId, groupId);
        departmentGroupService.delete(departmentId, groupId);
        return ApiResult.success("部门分组关联删除成功", null);
    }
}
```

- [ ] **Step 2: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/controller/DepartmentGroupController.java
git commit -m "feat(controller): 添加 DepartmentGroupController"
```

---

## Task 8: Repository 计数方法

**Files:**
- Modify: `backend/src/main/java/com/reythecoder/organization/repository/GroupHierarchyRepository.java`
- Modify: `backend/src/main/java/com/reythecoder/organization/repository/GroupDepartmentRepository.java`
- Modify: `backend/src/main/java/com/reythecoder/organization/repository/GroupPersonnelRepository.java`
- Modify: `backend/src/main/java/com/reythecoder/organization/repository/DepartmentHierarchyRepository.java`
- Modify: `backend/src/main/java/com/reythecoder/organization/repository/DepartmentPersonnelRepository.java`

- [ ] **Step 1: 在 GroupHierarchyRepository 添加 countByParentId**

在现有方法后添加：
```java
long countByParentId(UUID parentId);
```

- [ ] **Step 2: 在 GroupDepartmentRepository 添加 countByGroupId**

在现有方法后添加：
```java
long countByGroupId(UUID groupId);
```

- [ ] **Step 3: 在 GroupPersonnelRepository 添加 countByGroupId**

先读取文件确认现有方法，然后添加：
```java
long countByGroupId(UUID groupId);
```

- [ ] **Step 4: 在 DepartmentHierarchyRepository 添加 countByParentId**

在现有方法后添加：
```java
long countByParentId(UUID parentId);
```

- [ ] **Step 5: 在 DepartmentPersonnelRepository 添加 countByDepartmentId**

在现有方法后添加：
```java
long countByDepartmentId(UUID departmentId);
```

- [ ] **Step 6: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/repository/
git commit -m "feat(repository): 添加树统计所需的计数方法"
```

---

## Task 9: TreeService 接口和实现

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/service/TreeService.java`
- Create: `backend/src/main/java/com/reythecoder/organization/service/impl/TreeServiceImpl.java`

- [ ] **Step 1: 创建 TreeService 接口**

```java
package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface TreeService {

    TreeNodeRsp getTreeByGroupId(@NotNull UUID groupId, Integer depth);
}
```

- [ ] **Step 2: 创建 TreeServiceImpl**

```java
package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.NodeType;
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
        node.setType(NodeType.GROUP);
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
        node.setType(NodeType.DEPARTMENT);
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
        node.setType(NodeType.PERSONNEL);
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
```

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/service/TreeService.java src/main/java/com/reythecoder/organization/service/impl/TreeServiceImpl.java
git commit -m "feat(service): 添加 TreeService 接口和实现"
```

---

## Task 10: TreeController

**Files:**
- Create: `backend/src/main/java/com/reythecoder/organization/controller/TreeController.java`

- [ ] **Step 1: 创建 TreeController**

```java
package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trees")
public class TreeController {

    private static final Logger logger = LoggerFactory.getLogger(TreeController.class);

    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping("/{groupId}")
    public ApiResult<TreeNodeRsp> getTree(@PathVariable UUID groupId,
                                          @RequestParam(required = false) Integer depth) {
        logger.info("收到获取树结构请求, groupId: {}, depth: {}", groupId, depth);
        TreeNodeRsp tree = treeService.getTreeByGroupId(groupId, depth);
        return ApiResult.success(tree);
    }
}
```

- [ ] **Step 2: 提交**

```bash
cd backend && git add src/main/java/com/reythecoder/organization/controller/TreeController.java
git commit -m "feat(controller): 添加 TreeController 树结构查询 API"
```

---

## Task 11: DepartmentGroupServiceTest

**Files:**
- Create: `backend/src/test/java/com/reythecoder/organization/service/DepartmentGroupServiceTest.java`

- [ ] **Step 1: 创建 DepartmentGroupServiceTest**

```java
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
```

- [ ] **Step 2: 运行测试验证**

```bash
cd backend && ./gradlew test --tests DepartmentGroupServiceTest -v
```
Expected: All tests PASS

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/test/java/com/reythecoder/organization/service/DepartmentGroupServiceTest.java
git commit -m "test: 添加 DepartmentGroupServiceTest 单元测试"
```

---

## Task 12: TreeServiceTest

**Files:**
- Create: `backend/src/test/java/com/reythecoder/organization/service/TreeServiceTest.java`

- [ ] **Step 1: 创建 TreeServiceTest**

```java
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
        when(groupHierarchyRepository.findByParentIdOrderBySortOrderAsc(groupId)).thenReturn(List.of());
        when(groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of());
        when(groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(groupId)).thenReturn(List.of());

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(groupId);
        assertThat(result.getType()).isEqualTo(NodeType.GROUP);
        assertThat(result.getName()).isEqualTo("基础通讯录");
        assertThat(result.getStatistics().getSubGroupCount()).isEqualTo(2);
        assertThat(result.getStatistics().getSubDepartmentCount()).isEqualTo(5);
        assertThat(result.getStatistics().getPersonnelCount()).isEqualTo(10);
    }

    @Test
    void getTreeByGroupId_shouldLoadChildrenWithDepth1() {
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
        when(groupHierarchyRepository.findByParentIdOrderBySortOrderAsc(childGroupId)).thenReturn(List.of());
        when(groupDepartmentRepository.findByGroupIdOrderBySortOrderAsc(childGroupId)).thenReturn(List.of());
        when(groupPersonnelRepository.findByGroupIdOrderBySortOrderAsc(childGroupId)).thenReturn(List.of());
        // Department stats
        when(departmentGroupRepository.countByDepartmentId(deptId)).thenReturn(0L);
        when(departmentHierarchyRepository.countByParentId(deptId)).thenReturn(0L);
        when(departmentPersonnelRepository.countByDepartmentId(deptId)).thenReturn(0L);
        when(departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());
        when(departmentHierarchyRepository.findByParentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());
        when(departmentPersonnelRepository.findByDepartmentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 1);

        assertThat(result.getChildren()).hasSize(2);
        assertThat(result.getChildren().get(0).getType()).isEqualTo(NodeType.GROUP);
        assertThat(result.getChildren().get(1).getType()).isEqualTo(NodeType.DEPARTMENT);
        // Children should not have their children loaded (depth=1)
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
        when(departmentGroupRepository.findByDepartmentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());
        when(departmentHierarchyRepository.findByParentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());
        when(departmentPersonnelRepository.findByDepartmentIdOrderBySortOrderAsc(deptId)).thenReturn(List.of());

        TreeNodeRsp result = treeService.getTreeByGroupId(groupId, 1);

        assertThat(result.getChildren()).hasSize(2);
        // GROUP(0) < DEPARTMENT(1) < PERSONNEL(2), so DEPARTMENT should come before PERSONNEL
        assertThat(result.getChildren().get(0).getType()).isEqualTo(NodeType.DEPARTMENT);
        assertThat(result.getChildren().get(1).getType()).isEqualTo(NodeType.PERSONNEL);
    }
}
```

- [ ] **Step 2: 运行测试验证**

```bash
cd backend && ./gradlew test --tests TreeServiceTest -v
```
Expected: All tests PASS

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/test/java/com/reythecoder/organization/service/TreeServiceTest.java
git commit -m "test: 添加 TreeServiceTest 单元测试"
```

---

## Task 13: TreeControllerTest

**Files:**
- Create: `backend/src/test/java/com/reythecoder/organization/controller/TreeControllerTest.java`

- [ ] **Step 1: 创建 TreeControllerTest**

```java
package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.NodeType;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.service.TreeService;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TreeControllerTest {

    @Mock
    private TreeService treeService;

    @InjectMocks
    private TreeController treeController;

    private MockMvc mockMvc;
    private UUID groupId;
    private TreeNodeRsp treeRsp;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(treeController).build();
        groupId = UUIDv7.randomUUID();

        treeRsp = new TreeNodeRsp();
        treeRsp.setId(groupId);
        treeRsp.setType(NodeType.GROUP);
        treeRsp.setName("基础通讯录");
        treeRsp.setSortOrder(1);
        treeRsp.setStatistics(new TreeStatistics(2, 5, 10));
    }

    @Test
    void getTree_shouldReturnTreeWithDefaultDepth() throws Exception {
        when(treeService.getTreeByGroupId(groupId, null)).thenReturn(treeRsp);

        mockMvc.perform(get("/api/trees/{groupId}", groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(groupId.toString()))
                .andExpect(jsonPath("$.data.type").value("GROUP"))
                .andExpect(jsonPath("$.data.name").value("基础通讯录"))
                .andExpect(jsonPath("$.data.statistics.subGroupCount").value(2))
                .andExpect(jsonPath("$.data.statistics.subDepartmentCount").value(5))
                .andExpect(jsonPath("$.data.statistics.personnelCount").value(10));

        verify(treeService, times(1)).getTreeByGroupId(groupId, null);
    }

    @Test
    void getTree_shouldReturnTreeWithSpecifiedDepth() throws Exception {
        when(treeService.getTreeByGroupId(groupId, 2)).thenReturn(treeRsp);

        mockMvc.perform(get("/api/trees/{groupId}", groupId)
                        .param("depth", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("基础通讯录"));

        verify(treeService, times(1)).getTreeByGroupId(groupId, 2);
    }
}
```

- [ ] **Step 2: 运行测试验证**

```bash
cd backend && ./gradlew test --tests TreeControllerTest -v
```
Expected: All tests PASS

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/test/java/com/reythecoder/organization/controller/TreeControllerTest.java
git commit -m "test: 添加 TreeControllerTest 单元测试"
```

---

## Task 14: 运行全部测试并验证

- [ ] **Step 1: 运行全部单元测试**

```bash
cd backend && ./gradlew test -v
```
Expected: All tests PASS

- [ ] **Step 2: 运行构建验证**

```bash
cd backend && ./gradlew clean build -x testIntegration
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 最终提交**

```bash
cd backend && git status
git add -A
git commit -m "feat: 完成部门树结构 API 实现

- 新增 DepartmentGroupEntity 支持部门挂子分组
- 新增 TreeService/TreeController 提供树结构查询
- 支持分层加载 (depth 参数) 和统计信息
- 添加完整的单元测试覆盖"
```

---

## Self-Review Checklist

- [x] **Spec coverage**: 所有设计文档中的需求都有对应任务
- [x] **Placeholder scan**: 无 TBD/TODO/模糊描述
- [x] **Type consistency**: NodeType、TreeStatistics、TreeNodeRsp 类型定义一致