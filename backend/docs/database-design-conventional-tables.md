# 数据库设计：常规关联表模型

## 概述

本项目采用**常规关联表**的设计模式，为每种业务关系创建独立的关联表，通过外键约束保证数据完整性。这种设计是对原有"节点抽象关系模型"的重构，旨在提高查询性能、简化代码逻辑、增强数据一致性。

## 设计架构

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ org_department  │     │  org_personnel  │     │   org_group     │
│   (实体表)       │     │    (实体表)      │     │    (实体表)      │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│org_department_  │     │org_department_  │     │ org_group_      │
│   hierarchy     │     │   personnel     │     │   hierarchy     │
│  (部门层级)      │     │  (部门人员)      │     │  (分组层级)      │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                         ┌──────────────────────────────┤
                         │                              │
                         ▼                              ▼
                ┌─────────────────┐           ┌─────────────────┐
                │org_group_       │           │ org_group_      │
                │   department    │           │   personnel     │
                │  (分组部门)      │           │  (分组人员)      │
                └─────────────────┘           └─────────────────┘
```

## 表结构设计

### 实体表

#### org_department（部门表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键，UUID v7 |
| name | VARCHAR(255) | 部门名称 |
| english_name | VARCHAR(255) | 英文名称 |
| short_name | VARCHAR(100) | 简称 |
| org_code | VARCHAR(50) | 组织编码 |
| phone | VARCHAR(50) | 电话 |
| fax | VARCHAR(50) | 传真 |
| email | VARCHAR(100) | 邮箱 |
| address | VARCHAR(500) | 地址 |
| postal_code | VARCHAR(20) | 邮编 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

#### org_personnel（人员表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键，UUID v7 |
| name | VARCHAR(100) | 姓名 |
| gender | VARCHAR(1) | 性别 (M/F) |
| id_card | VARCHAR(18) | 身份证号 |
| mobile | VARCHAR(20) | 手机号 |
| telephone | VARCHAR(20) | 座机号 |
| fax | VARCHAR(20) | 传真 |
| email | VARCHAR(100) | 邮箱 |
| photo | BYTEA | 照片 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

#### org_group（分组表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键，UUID v7 |
| name | VARCHAR(100) | 分组名称 |
| description | TEXT | 描述 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

### 关联表

#### org_department_hierarchy（部门层级表）

用于存储部门的树形层级结构。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键 |
| parent_id | UUID | 父部门ID，根节点为 NULL |
| child_id | UUID | 子部门ID（唯一约束） |
| level | INTEGER | 层级深度，根节点为 1 |
| path | VARCHAR(1000) | 从根到当前节点的路径 |
| sort_order | INTEGER | 同级节点排序 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

**约束**：`UNIQUE (child_id)` - 每个部门只能有一个父节点

**索引**：
- `idx_org_dept_hierarchy_parent` - 按父节点查询子节点
- `idx_org_dept_hierarchy_child` - 按子节点查询父节点
- `idx_org_dept_hierarchy_path` - 路径查询

#### org_department_personnel（部门人员关联表）

用于存储人员与部门的多对多关系，支持主次部门标识。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键 |
| department_id | UUID | 部门ID |
| personnel_id | UUID | 人员ID |
| is_primary | BOOLEAN | 是否为主部门 |
| position | VARCHAR(100) | 职位名称 |
| sort_order | INTEGER | 排序 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

**约束**：`UNIQUE (department_id, personnel_id)` - 防止重复关联

**索引**：
- `idx_org_dept_personnel_dept` - 按部门查询人员
- `idx_org_dept_personnel_personnel` - 按人员查询部门
- `idx_org_dept_personnel_primary` - 主部门查询

#### org_group_hierarchy（分组层级表）

用于存储分组的树形层级结构，结构与 `org_department_hierarchy` 类似。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键 |
| parent_id | UUID | 父分组ID，根节点为 NULL |
| child_id | UUID | 子分组ID（唯一约束） |
| level | INTEGER | 层级深度 |
| path | VARCHAR(1000) | 路径 |
| sort_order | INTEGER | 排序 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

#### org_group_department（分组部门关联表）

用于存储分组与部门的多对多关系，支持跨部门协作。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键 |
| group_id | UUID | 分组ID |
| department_id | UUID | 部门ID |
| role | VARCHAR(100) | 协作角色 |
| sort_order | INTEGER | 排序 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

**约束**：`UNIQUE (group_id, department_id)`

#### org_group_personnel（分组人员关联表）

用于存储分组与人员的多对多关系，支持不同角色。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | UUID | 主键 |
| group_id | UUID | 分组ID |
| personnel_id | UUID | 人员ID |
| role | VARCHAR(100) | 组内角色 |
| sort_order | INTEGER | 排序 |
| create_time | TIMESTAMPTZ | 创建时间 |
| update_time | TIMESTAMPTZ | 更新时间 |
| tenant_id | UUID | 租户ID |

**约束**：`UNIQUE (group_id, personnel_id)`

## 业务场景映射

### 场景 1：部门层级关系

```
人力资源部(根，level=1)
└── 技术部(level=2)
    └── 开发部(level=3)
```

```sql
INSERT INTO org_department_hierarchy (id, parent_id, child_id, level, path, sort_order) VALUES
('aaa...', NULL, '人力资源部ID', 1, '/人力资源部ID/', 1),
('bbb...', '人力资源部ID', '技术部ID', 2, '/人力资源部ID/技术部ID/', 1),
('ccc...', '技术部ID', '开发部ID', 3, '/人力资源部ID/技术部ID/开发部ID/', 1);
```

### 场景 2：人员属于多个部门

```sql
INSERT INTO org_department_personnel (id, department_id, personnel_id, is_primary, position, sort_order) VALUES
('ddd...', '人力资源部ID', '张三ID', TRUE, 'HR经理', 1),
('eee...', '行政部ID', '张三ID', FALSE, '行政助理', 2);
```

### 场景 3：分组层级及人员

```sql
INSERT INTO org_group_hierarchy (id, parent_id, child_id, level, path, sort_order) VALUES
('fff...', NULL, '项目组ID', 1, '/项目组ID/', 1);

INSERT INTO org_group_personnel (id, group_id, personnel_id, role, sort_order) VALUES
('ggg...', '项目组ID', '张三ID', '组长', 1);
```

## 常用查询

### 查询部门的子部门

```sql
SELECT h.*, d.name as child_name
FROM org_department_hierarchy h
JOIN org_department d ON h.child_id = d.id
WHERE h.parent_id = :parentId
ORDER BY h.sort_order;
```

### 查询部门的所有人员

```sql
SELECT dp.*, p.name as personnel_name
FROM org_department_personnel dp
JOIN org_personnel p ON dp.personnel_id = p.id
WHERE dp.department_id = :departmentId
ORDER BY dp.sort_order;
```

### 查询人员的主部门

```sql
SELECT dp.*, d.name as department_name
FROM org_department_personnel dp
JOIN org_department d ON dp.department_id = d.id
WHERE dp.personnel_id = :personnelId
  AND dp.is_primary = TRUE;
```

### 查询分组的所有成员

```sql
SELECT gp.*, p.name as personnel_name
FROM org_group_personnel gp
JOIN org_personnel p ON gp.personnel_id = p.id
WHERE gp.group_id = :groupId
ORDER BY gp.sort_order;
```

### 使用 path 查询子树

```sql
SELECT h.*, d.name as child_name
FROM org_department_hierarchy h
JOIN org_department d ON h.child_id = d.id
WHERE h.path LIKE :parentPath || '%'
ORDER BY h.level, h.sort_order;
```

## 与节点抽象模型对比

| 维度 | 节点抽象模型 | 常规关联表模型 |
|------|-------------|---------------|
| 表数量 | 1 关联表 | 5 关联表 |
| 查询复杂度 | 高（需动态 JOIN） | 低（直接 JOIN） |
| 查询性能 | 中 | 高 |
| 数据完整性 | 应用层保证 | 外键约束保证 |
| 代码复杂度 | 高 | 低 |
| 扩展性 | 极高 | 中（需新建表） |
| 学习成本 | 高 | 低 |

## 选择常规关联表的原因

1. **查询性能**：直接 JOIN 比动态 JOIN 性能更好
2. **数据完整性**：外键约束自动保证数据一致性
3. **代码简洁**：每个关联表有独立的服务和控制器
4. **开发效率**：开发人员更容易理解和维护
5. **类型安全**：编译时类型检查，减少运行时错误

---

**相关文档**：
- [数据库设计开发指南](database-design-develop-guide-for-postgresql.md)
- [重构计划](refactoring-plan-node-relation-to-conventional-tables.md)