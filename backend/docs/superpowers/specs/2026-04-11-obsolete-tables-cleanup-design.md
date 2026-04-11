---
title: 过时表和代码清理设计文档
date: 2026-04-11
author: Claude
status: Approved
---

# 过时表和代码清理设计文档

## 1. 设计背景与目标

### 1.1 当前情况

组织服务已通过 `org_tree` 表实现了统一的树形结构管理，替代了原先分散的层级和关联表。以下过时表和代码已不再使用：

**过时数据库表（6个）：**
- `org_department_hierarchy` - 部门层级关系
- `org_department_personnel` - 部门人员关联
- `org_group_hierarchy` - 分组层级关系
- `org_group_department` - 分组部门关联
- `org_group_personnel` - 分组人员关联
- `org_department_group` - 部门分组关联（反向）

**过时 Java 代码：**
- Entity、Repository、Service、Controller、DTO、Test 等约 44 个文件
- `TreeService` + `TreeServiceImpl` + `TreeController` - 使用过时 repositories

### 1.2 设计目标

- **清理过时代码**：删除所有引用过时表的 Java 文件
- **清理数据库脚本**：从 DDL 和 seed data 脚本中移除过时表定义
- **更新 API 文档**：从 openapi.yaml 中移除过时 API 端点
- **验证系统完整性**：通过单元测试和集成测试确保清理后系统正常运行

## 2. 清理范围

### 2.1 Java 文件删除清单

| 分类 | 文件列表 | 数量 |
|------|----------|------|
| **Entity** | DepartmentHierarchyEntity, DepartmentPersonnelEntity, GroupHierarchyEntity, GroupDepartmentEntity, GroupPersonnelEntity, DepartmentGroupEntity | 6 |
| **Repository** | DepartmentHierarchyRepository, DepartmentPersonnelRepository, GroupHierarchyRepository, GroupDepartmentRepository, GroupPersonnelRepository, DepartmentGroupRepository | 6 |
| **Service Interface** | DepartmentHierarchyService, DepartmentPersonnelService, GroupHierarchyService, GroupDepartmentService, GroupPersonnelService, DepartmentGroupService, TreeService | 7 |
| **Service Impl** | DepartmentHierarchyServiceImpl, DepartmentPersonnelServiceImpl, GroupHierarchyServiceImpl, GroupDepartmentServiceImpl, GroupPersonnelServiceImpl, GroupDepartmentServiceImpl, TreeServiceImpl | 7 |
| **Controller** | DepartmentHierarchyController, DepartmentPersonnelController, GroupHierarchyController, GroupDepartmentController, GroupPersonnelController, DepartmentGroupController, TreeController | 7 |
| **DTO Request** | DepartmentHierarchyCreateReq, DepartmentPersonnelCreateReq, GroupHierarchyCreateReq, GroupDepartmentCreateReq, GroupPersonnelCreateReq, DepartmentGroupCreateReq | 6 |
| **DTO Response** | DepartmentHierarchyRsp, DepartmentPersonnelRsp, GroupHierarchyRsp, GroupDepartmentRsp, GroupPersonnelRsp, DepartmentGroupRsp | 6 |
| **Test** | DepartmentHierarchyControllerTest, DepartmentHierarchyServiceTest, DepartmentPersonnelControllerTest, DepartmentPersonnelServiceTest, GroupHierarchyControllerTest, GroupHierarchyServiceTest, GroupDepartmentServiceTest, GroupPersonnelServiceTest, TreeServiceTest | 9 |

**总计：约 44 个 Java 文件**

### 2.2 数据库脚本修改

**文件：`db/init-scripts/01-init-organization-tables.sql`**

删除以下表的 DDL（包括表结构、索引、注释、触发器）：
- `org_department_hierarchy` (lines 176-205)
- `org_department_personnel` (lines 208-236)
- `org_group_hierarchy` (lines 305-334)
- `org_group_department` (lines 337-362)
- `org_group_personnel` (lines 365-390)

**注意：** `org_department_group` 表的 DDL 未在此文件中定义，但 Entity 存在，需确认是否为遗漏。清理时应一并删除相关 Entity。

**文件：`db/init-scripts/03-seed-sample-data.sql`**

删除以下表的 seed data：
- `org_department_hierarchy` INSERT 语句 (lines 50-54)
- `org_department_personnel` INSERT 语句 (lines 57-59)
- `org_group_personnel` INSERT 语句 (lines 62-64)

### 2.3 OpenAPI 文档更新

**文件：`docs/openapi.yaml`**

移除以下 API 端点路径定义：
- `/api/department-hierarchy/*` - 部门层级相关端点
- `/api/department-personnel/*` - 部门人员关联端点
- `/api/group-hierarchy/*` - 分组层级端点
- `/api/group-department/*` - 分组部门关联端点
- `/api/group-personnel/*` - 分组人员关联端点
- `/api/department-group/*` - 部门分组关联端点
- `/api/trees/{groupId}` - 旧版树查询端点（使用过时 TreeService）

**保留端点：**
- `/api/tree/*` - 新版组织树端点（OrgTreeNodeController）
- `/api/departments/*` - 部门 CRUD
- `/api/personnel/*` - 人员 CRUD
- `/api/positions/*` - 职位 CRUD
- `/api/groups/*` - 分组 CRUD
- `/api/department-positions/*` - 部门职位关联（保留）
- `/api/personnel-positions/*` - 人员职位关联（保留）

## 3. 保留内容

以下表和代码不受本次清理影响：

| 类型 | 内容 | 说明 |
|------|------|------|
| **表** | `org_tree` | 新版统一组织树，替代所有层级表 |
| **表** | `org_department_position` | 部门职位关联，业务语义独立 |
| **表** | `org_personnel_position` | 人员职位关联，包含任期等信息 |
| **表** | `org_department`, `org_personnel`, `org_position`, `org_group` | 核心实体表 |
| **Service** | `OrgTreeNodeService` + `OrgTreeNodeServiceImpl` | 新版组织树服务 |
| **Controller** | `OrgTreeNodeController` | 新版组织树 API |

## 4. 测试策略

### 4.1 清理后验证

**单元测试：**
- 运行现有 `./gradlew test` 确保所有保留模块测试通过
- 删除过时模块的测试文件后不应有测试失败

**集成测试：**
- 运行 `./gradlew testIntegration` 验证完整应用启动
- 验证 `OrgTreeNodeController` 所有端点正常工作
- 验证数据库连接和 `org_tree` 表查询正常

### 4.2 验收标准

1. 所有过时 Java 文件已删除
2. 数据库初始化脚本不包含过时表定义
3. openapi.yaml 不包含过时 API 端点
4. `./gradlew clean build` 成功完成
5. `./gradlew test` 全部通过
6. `./gradlew testIntegration` 全部通过
7. 应用可正常启动并响应 `/api/tree/root` 等端点

## 5. 实施步骤

1. **删除 Java 文件** - 按层顺序删除（Controller → Service → Repository → Entity → DTO → Test）
2. **修改数据库脚本** - 移除过时表 DDL 和 seed data
3. **更新 openapi.yaml** - 移除过时端点定义
4. **运行测试验证** - 单元测试和集成测试
5. **提交变更** - 单个 commit 包含所有清理变更

## 6. 影响分析

### 6.1 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 遗漏删除文件导致编译失败 | 低 | 中 | 编译验证 + IDE 搜索确认 |
| openapi.yaml 端点遗漏 | 低 | 低 | API 文档与代码同步检查 |
| 测试依赖过时模块 | 中 | 中 | 删除过时测试文件 |

### 6.2 回滚方案

如果清理导致问题，可通过 git revert 回滚本次 commit。由于无数据迁移，回滚无风险。

---

**文档版本：** 1.0
**最后更新：** 2026-04-11