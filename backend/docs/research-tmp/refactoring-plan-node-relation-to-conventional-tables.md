# 重构计划：节点抽象关系模型 → 常规关联表设计

## 现状分析

已实现（常规关联表）：
- ✅ org_position 岗位实体
- ✅ org_department_position 部门-岗位关联
- ✅ org_personnel_position 人员-岗位关联

待重构（通用关联表 org_entity_relation）：
- ❌ 部门层级关系（树形）
- ❌ 部门-人员关联
- ❌ 分组模块完整实现
- ❌ 分组层级关系
- ❌ 分组-部门关联
- ❌ 分组-人员关联

## 新建关联表设计

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| org_department_hierarchy | 部门层级 | parent_id, child_id, level, path, sort_order |
| org_department_personnel | 部门人员 | department_id, personnel_id, is_primary, position |
| org_group_hierarchy | 分组层级 | parent_id, child_id, level, path, sort_order |
| org_group_department | 分组部门 | group_id, department_id, role |
| org_group_personnel | 分组人员 | group_id, personnel_id, role |

## TODO 列表

阶段一：数据库设计

| 任务 | 优先级 |
|------|--------|
| 1.1 删除 org_entity_relation 表，创建迁移脚本 | High |
| 1.2 创建 org_department_hierarchy 表 | High |
| 1.3 创建 org_department_personnel 表 | High |
| 1.4 创建 org_group_hierarchy 表 | Medium |
| 1.5 创建 org_group_department 表 | Medium |
| 1.6 创建 org_group_personnel 表 | Medium |

阶段二：实体层

| 任务 | 优先级 |
|------|--------|
| 2.1 GroupEntity.java | High |
| 2.2 DepartmentHierarchyEntity.java | High |
| 2.3 DepartmentPersonnelEntity.java | High |
| 2.4 GroupHierarchyEntity.java | Medium |
| 2.5 GroupDepartmentEntity.java | Medium |
| 2.6 GroupPersonnelEntity.java | Medium |

阶段三：仓储层

| 任务 | 优先级 |
|------|--------|
| 3.1 GroupRepository.java | High |
| 3.2 DepartmentHierarchyRepository.java | High |
| 3.3 DepartmentPersonnelRepository.java | High |
| 3.4-3.6 分组相关 Repository | Medium |

阶段四：DTO & Mapper

| 任务 | 优先级 |
|------|--------|
| 4.1 分组相关 DTO | High |
| 4.2 部门层级/人员 DTO | High |
| 4.3 分组关联 DTO | Medium |

阶段五：服务层

| 任务 | 优先级 |
|------|--------|
| 5.1 GroupService | High |
| 5.2 DepartmentHierarchyService | High |
| 5.3 DepartmentPersonnelService | High |
| 5.4-5.6 分组关联 Service | Medium |

阶段六：控制器层

| 任务 | 优先级 |
|------|--------|
| 6.1 GroupController | High |
| 6.2 DepartmentHierarchyController | Medium |
| 6.3 DepartmentPersonnelController | High |
| 6.4-6.6 分组关联 Controller | Medium |

阶段七：测试

| 任务 | 优先级 |
|------|--------|
| 7.1 分组模块单元测试 | High |
| 7.2 部门关联测试 | High |
| 7.3 分组关联测试 | Medium |
| 7.4 集成测试 | Medium |

阶段八：文档更新

| 任务 | 优先级 |
|------|--------|
| 8.1 更新数据库设计文档 | Medium |
| 8.2 更新 AGENTS.md | Low |

---

建议执行顺序：
1. 先完成部门相关功能（层级 + 人员关联）
2. 再完成分组模块（实体 + 层级 + 关联）
3. 最后完成测试和文档
