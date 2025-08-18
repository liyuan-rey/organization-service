# 数据库结构设计

## 1. 方案概述

采用优化的通用关联表方案来统一表达人员、分组和部门之间的复杂关联关系，包括部门的层级结构。该方案具有完全通用性、良好的层级支持和强大的扩展性。

## 2. 数据库设计

### 2.1. 部门表 (b_org_department)

```sql
CREATE TABLE IF NOT EXISTS b_org_department (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(255) NOT NULL DEFAULT '', -- Department name
    english_name VARCHAR(255) NOT NULL DEFAULT '', -- Department English name
    short_name VARCHAR(100) NOT NULL DEFAULT '', -- Department short name
    org_code VARCHAR(50) NOT NULL DEFAULT '', -- Organization code
    phone VARCHAR(50) NOT NULL DEFAULT '', -- Department phone number
    fax VARCHAR(50) NOT NULL DEFAULT '', -- Department fax number
    email VARCHAR(100) NOT NULL DEFAULT '', -- Department email address
    address VARCHAR(500) NOT NULL DEFAULT '', -- Department address
    postal_code VARCHAR(20) NOT NULL DEFAULT '', -- Postal code
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT 0 -- Tenant identifier for multi-tenant data isolation
);
```

### 2.2. 人员表 (b_org_personnel)

```sql
CREATE TABLE IF NOT EXISTS b_org_personnel (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Personnel name
    gender CHAR(1) NOT NULL DEFAULT '', -- Personnel gender (M/F)
    id_card VARCHAR(18) NOT NULL DEFAULT '', -- ID card number
    mobile VARCHAR(20) NOT NULL DEFAULT '', -- Mobile phone number
    telephone VARCHAR(20) NOT NULL DEFAULT '', -- Telephone number
    fax VARCHAR(20) NOT NULL DEFAULT '', -- Fax number
    email VARCHAR(100) NOT NULL DEFAULT '', -- Email address
    photo BYTEA, -- Personnel photo (binary data)
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT 0 -- Tenant identifier for multi-tenant data isolation
);
```

### 2.3. 分组表 (b_org_group)

```sql
CREATE TABLE IF NOT EXISTS b_org_group (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Group name
    description TEXT NOT NULL DEFAULT '', -- Group description
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT 0 -- Tenant identifier for multi-tenant data isolation
);
```

### 2.4. 通用关联表 (r_org_entity_relation)

这是本方案的核心表，用于统一表达所有实体间的关联关系：

```sql
CREATE TABLE IF NOT EXISTS r_org_entity_relation (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    source_type VARCHAR(20) NOT NULL, -- Source entity type: 'personnel', 'department', 'group'
    source_id UUID NOT NULL, -- Source entity ID
    target_type VARCHAR(20) NOT NULL, -- Target entity type: 'personnel', 'department', 'group'
    target_id UUID NOT NULL, -- Target entity ID
    relation_type VARCHAR(50) NOT NULL, -- Relation type: 'belongs_to', 'member_of', 'contains', 'parent_of', 'child_of'
    level INTEGER DEFAULT 0, -- Hierarchy level (for hierarchy relationships)
    path VARCHAR(500) DEFAULT '', -- Path information, e.g., "1,5,23" represents the path from root to current node
    sort_order INTEGER DEFAULT 0, -- Sort order
    is_direct BOOLEAN DEFAULT true, -- Whether it's a direct relationship (to distinguish direct and indirect relationships)
    attributes JSONB, -- Extended attributes for storing additional relationship information
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT 0 -- Tenant identifier for multi-tenant data isolation
);
```

## 3. 设计说明

### 3.1. 设计理念

本方案采用统一的通用关联表来表达所有实体间的复杂关系，包括：

- 人员与部门的关系
- 人员与分组的关系
- 部门与分组的关系
- 部门间的层级关系
- 分组间的层级关系

### 3.2. 核心字段说明

| 字段名        | 类型         | 说明                                                             |
| ------------- | ------------ | ---------------------------------------------------------------- |
| source_type   | VARCHAR(20)  | 源实体类型（personnel, department, group）                       |
| source_id     | UUID       | 源实体ID                                                         |
| target_type   | VARCHAR(20)  | 目标实体类型（personnel, department, group）                     |
| target_id     | UUID       | 目标实体ID                                                       |
| relation_type | VARCHAR(50)  | 关系类型（belongs_to, member_of, contains, parent_of, child_of） |
| level         | INTEGER      | 层级深度（用于层级关系）                                         |
| path          | VARCHAR(500) | 路径信息，表示从根到当前节点的完整路径                           |
| sort_order    | INTEGER      | 排序字段                                                         |
| is_direct     | BOOLEAN      | 是否直接关系                                                     |
| attributes    | JSONB        | 扩展属性，用于存储关系的额外信息                                 |

### 3.3. 关系类型定义

| 关系类型   | 说明           | 示例                     |
| ---------- | -------------- | ------------------------ |
| belongs_to | 人员属于部门   | 人员属于某个部门         |
| member_of  | 人员是分组成员 | 人员是某个分组的成员     |
| contains   | 部门包含分组   | 部门包含某个分组         |
| parent_of  | 实体的父子关系 | 部门是另一个部门的父部门 |
| child_of   | 实体的子父关系 | 部门是另一个部门的子部门 |

### 3.4. 层级关系支持

通过`level`和`path`字段完美支持树形结构：

- `level`字段表示层级深度，根节点为0，子节点依次递增
- `path`字段记录完整路径，便于快速查询某个节点的所有父节点或子节点
- `is_direct`字段区分直接关系和间接关系

### 3.5. 扩展性设计

- `attributes`字段使用JSONB类型，可以存储任意的扩展属性
- 不同类型的关系可以存储不同的属性信息
- 无需修改表结构即可支持新的关系类型

### 3.6. 性能优化

创建了多个复合索引以优化常见查询场景：

```sql
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_source ON r_org_entity_relation(source_type, source_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_target ON r_org_entity_relation(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_type ON r_org_entity_relation(relation_type);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_tenant ON r_org_entity_relation(tenant_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_path ON r_org_entity_relation(path);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_level ON r_org_entity_relation(level);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_composite ON r_org_entity_relation(source_type, target_type, relation_type);
```

### 3.7. 使用示例

#### 3.7.1. 存储示例

1. 部门层级关系

```sql
-- 部门层级关系
INSERT INTO r_org_entity_relation
(source_type, source_id, target_type, target_id, relation_type, level, path,
sort_order)
VALUES
('department', 1, 'department', 2, 'parent_of', 1, '1', 1),  --
部门1是部门2的父部门
('department', 2, 'department', 3, 'parent_of', 2, '1,2', 1); --
部门2是部门3的父部门
```

1. 人员与部门关系

```sql
-- 人员属于部门
INSERT INTO r_org_entity_relation
(source_type, source_id, target_type, target_id, relation_type, attributes)
VALUES
('personnel', 1, 'department', 1, 'belongs_to', '{"position": "Manager",
"is_primary": true}');
```

1. 人员与分组关系

```sql
-- 人员是分组成员
INSERT INTO r_org_entity_relation
(source_type, source_id, target_type, target_id, relation_type, attributes)
VALUES
('personnel', 1, 'group', 1, 'member_of', '{"role": "Leader"}');
```

#### 3.7.2. 查询示例

1. 查询部门的所有子部门

```sql
SELECT target_id FROM r_org_entity_relation 
WHERE source_id = 1 AND relation_type = 'parent_of' AND target_type = 'department';
```

1. 查询人员的所有部门

```sql
SELECT target_id, attributes FROM r_org_entity_relation 
WHERE source_id = 1 AND relation_type = 'belongs_to' AND target_type = 'department';
```

1. 查询部门的完整路径

```sql
SELECT path FROM r_org_entity_relation 
WHERE target_id = 3 AND relation_type = 'parent_of' LIMIT 1;
```

### 3.8. 设计优势

1. **完全通用性**
   - 一个表可以表达所有类型的实体关系
   - 层级信息和路径信息统一管理
   - 适用于部门层级、组织架构等多种场景

1. **层级关系表达**
   - level字段表示层级深度
   - path字段记录完整路径
   - is_direct区分直接和间接关系
   - 可以轻松查询某个节点的所有子节点或父节点

1. **灵活的扩展性**
   - attributes字段使用JSONB类型存储扩展属性
   - 可以根据不同关系类型存储不同的额外信息
   - 无需修改表结构即可支持新的关系类型

1. **查询优化**
   - 复合索引支持多种查询场景
   - 路径查询、层级查询、类型查询都很高效
   - 支持租户隔离

1. **符合规范**
   遵循项目命名规范和设计原则

这种设计提供了一个灵活、可扩展且高效的解决方案，能够满足复杂的组织关系管理需求。
