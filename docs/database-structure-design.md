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
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
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
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
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
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);
```

### 2.4. 通用关联表 (b_org_entity_relation)

这是本方案的核心表，用于统一存储所有类型的节点以及它们之间的多对多关系：

```sql
CREATE TABLE IF NOT EXISTS b_org_entity_relation (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    node_id UUID NOT NULL, -- Unique identifier of the node (linked to specific entity tables)
    node_name_alias VARCHAR(255) NOT NULL DEFAULT '', -- Display name or alias of the node
    node_type VARCHAR(50) NOT NULL, -- Type of the node (e.g., 'USER', 'DEPARTMENT', 'GROUP')
    parent_id UUID NOT NULL, -- Parent node ID, used to build hierarchical relationships. 'parent_id' of root nodes is '00000000-0000-0000-0000-000000000000'
    level INTEGER NOT NULL DEFAULT 0, -- Hierarchy level (depth), level of root node is 1
    path VARCHAR(1000) NOT NULL DEFAULT '', -- Path from root to current node, e.g., "/1/5/12/"
    sort_order INTEGER NOT NULL DEFAULT 0, -- Sort order for siblings
    attributes JSONB, -- Extended attributes for storing dynamic node or relationship information
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, -- Soft delete flag
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Comments for columns (example for PostgreSQL)
COMMENT ON COLUMN b_org_entity_relation.id IS 'Primary key';
COMMENT ON COLUMN b_org_entity_relation.node_id IS 'Unique identifier of the node (linked to specific entity tables)';
COMMENT ON COLUMN b_org_entity_relation.node_name_alias IS 'Display name or alias of the node';
COMMENT ON COLUMN b_org_entity_relation.node_type IS 'Type of the node (e.g., ''USER'', ''DEPARTMENT'', ''GROUP'')';
COMMENT ON COLUMN b_org_entity_relation.parent_id IS 'Parent node ID, used to build hierarchical relationships. Parent node ID of root nodes is ''00000000-0000-0000-0000-000000000000''. ';
COMMENT ON COLUMN b_org_entity_relation.level IS 'Hierarchy level (depth), level of root node is 1';
COMMENT ON COLUMN b_org_entity_relation.path IS 'Path from root to current node, e.g., "/1/5/12/"';
COMMENT ON COLUMN b_org_entity_relation.sort_order IS 'Sort order for siblings';
COMMENT ON COLUMN b_org_entity_relation.attributes IS 'Extended attributes for storing dynamic node or relationship information';
COMMENT ON COLUMN b_org_entity_relation.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN b_org_entity_relation.created_at IS 'Record creation time';
COMMENT ON COLUMN b_org_entity_relation.updated_at IS 'Record last update time';
COMMENT ON COLUMN b_org_entity_relation.tenant_id IS 'Tenant identifier for multi-tenant data isolation';
```

## 3. 设计说明

### 3.1. 设计理念

本方案的核心思想是用一张表 `b_org_entity_relation` 来统一管理组织内所有类型的实体（节点）以及它们之间的各种关系和层级结构。这包括但不限于：

- 人员与部门的关系
- 人员与分组的关系
- 部门与分组的关系
- 部门间的层级关系
- 分组间的层级关系

这种设计将复杂的网状关系和树形结构抽象为统一的节点和关系模型，简化了数据管理。

### 3.2. 核心字段说明

| 字段名          | 类型                     | 说明                                                                                             |
| --------------- | ------------------------ | ------------------------------------------------------------------------------------------------ |
| id              | UUID                     | 主键，使用128位UUID v7算法生成，全局唯一。                                                       |
| node_id         | UUID                     | 节点的唯一标识符，与具体业务实体表（如用户表、部门表）关联。                                     |
| node_name_alias | VARCHAR(255)             | 节点的显示名称或别名。                                                                           |
| node_type       | VARCHAR(50)              | 节点类型，用于区分不同种类的实体，如 'USER', 'DEPARTMENT', 'GROUP'。                             |
| parent_id       | UUID                     | 父节点ID，用于构建父子层级关系。根节点的 `parent_id` 为 '00000000-0000-0000-0000-000000000000'。 |
| level           | INTEGER                  | 节点在树中的层级（深度），根节点层级为 1。                                                       |
| path            | VARCHAR(1000)            | 从根节点到当前节点的完整路径，格式如 `/1/5/12/`，便于进行子树查询。                              |
| sort_order      | INTEGER                  | 同级节点（具有相同 `parent_id` 的节点）之间的排序优先级。                                        |
| attributes      | JSONB                    | 用于存储节点或关系的动态扩展属性。                                                               |
| is_deleted      | BOOLEAN                  | 软删除标记，用于逻辑删除记录。                                                                   |
| created_at      | TIMESTAMP WITH TIME ZONE | 记录创建时间。                                                                                   |
| updated_at      | TIMESTAMP WITH TIME ZONE | 记录最后更新时间。                                                                               |
| tenant_id       | UUID                     | 租户标识符，用于在多租户环境下实现数据隔离。                                                     |

### 3.3. 层级关系支持

通过 `level` 和 `path` 字段，该表能够完美支持树形结构：

- `level` 字段表示节点的层级深度，便于快速查询某一层的所有节点。
- `path` 字段记录了从根节点到当前节点的完整路径，便于高效地进行子树查询（查找所有子孙节点）或祖先查询（查找所有上级节点）。
- `parent_id` 字段是构建层级关系的基础。

### 3.4. 扩展性设计

- `attributes` 字段使用 `JSONB` 类型，可以存储任意的动态属性信息，无需修改表结构即可适应业务变化。
- `node_type` 字段使得可以轻松引入新的节点类型。
- `node_name_alias` 允许为节点提供灵活的显示名称。

### 3.5. 性能优化

通过精心设计的索引策略来优化常见查询场景，特别是针对多租户和树形结构的查询：

- **节点查找索引 (`node_type, node_id`)**: 快速定位特定类型和ID的节点。
- **子节点查询索引 (`parent_id`)**: 快速查询某个节点的所有直接子节点。
- **子节点排序索引 (`parent_id, sort_order`)**: 在查询子节点时同时完成排序，避免额外的排序开销。
- **多租户隔离索引 (`tenant_id` 结合其他字段)**: 确保查询操作仅在当前租户的数据范围内进行，提高查询效率。
- **软删除过滤索引 (`is_deleted, tenant_id`)**: 快速过滤出未被删除的有效数据。

#### 3.5.1. 3.5.1 索引策略 (Indexing Strategy)

为了优化查询性能，特别是针对树形结构和多租户场景，建议创建以下索引：

```sql
-- Primary Key Index (automatically created)
-- CREATE UNIQUE INDEX pk_b_org_entity_relation ON b_org_entity_relation(id);

-- Core Business Indexes
-- For quickly locating a specific node by type and ID
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_node_lookup ON b_org_entity_relation(node_type, node_id);

-- For querying all direct children of a node
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_parent_lookup ON b_org_entity_relation(parent_id);

-- For querying and sorting children of a node
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_children_sort ON b_org_entity_relation(parent_id, sort_order);

-- Multi-tenant and Status Indexes
-- For locating a node within a tenant
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_tenant_node ON b_org_entity_relation(tenant_id, node_type, node_id);

-- For querying children within a tenant
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_tenant_parent ON b_org_entity_relation(tenant_id, parent_id);

-- For querying and sorting children within a tenant
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_tenant_parent_sort ON b_org_entity_relation(tenant_id, parent_id, sort_order);

-- For filtering active (not deleted) data within a tenant
CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_active_tenant ON b_org_entity_relation(is_deleted, tenant_id);

-- Special Query Indexes (Optional, based on query needs)
-- If frequent path-based queries are needed
-- CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_path ON b_org_entity_relation(path);

-- If frequent level-based queries are needed
-- CREATE INDEX IF NOT EXISTS idx_b_org_entity_relation_level ON b_org_entity_relation(level);
```

### 3.6. 设计优势

- **统一管理**: 一个表即可表达所有类型的实体关系和层级结构，简化了数据模型和管理。
- **层级关系表达**: `level`, `path`, `parent_id` 字段提供了对树形结构的全面支持。
- **灵活的扩展性**: `attributes` 字段和 `node_type` 字段使得模型能够轻松适应新的业务需求。
- **高性能查询**: 通过合理的索引设计，优化了常见查询场景的性能。
- **多租户支持**: `tenant_id` 字段原生支持多租户架构。
- **符合规范**: 遵循项目命名规范和数据库设计最佳实践。

这种设计提供了一个灵活、可扩展且高效的解决方案，能够满足复杂的组织关系和层级管理需求。

## 4. 使用示例

### 4.1. 存储示例

1. 部门层级关系

    ```sql
    -- 部门层级关系
    -- 假设部门1是根节点，部门2是部门1的子部门，部门3是部门2的子部门
    INSERT INTO b_org_entity_relation
    (node_id, node_name_alias, node_type, parent_id, level, path, sort_order, tenant_id)
    VALUES
    ('11111111-1111-1111-1111-111111111111', '部门1', 'DEPARTMENT', '00000000-0000-0000-0000-000000000000', 1, '/11111111-1111-1111-1111-111111111111/', 1, '00000000-0000-0000-0000-000000000000'), -- 根部门
    ('22222222-2222-2222-2222-222222222222', '部门2', 'DEPARTMENT', '11111111-1111-1111-1111-111111111111', 2, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/', 1, '00000000-0000-0000-0000-000000000000'), -- 部门1的子部门
    ('33333333-3333-3333-3333-333333333333', '部门3', 'DEPARTMENT', '22222222-2222-2222-2222-222222222222', 3, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/33333333-3333-3333-3333-333333333333/', 1, '00000000-0000-0000-0000-000000000000'); -- 部门2的子部门
    ```

2. 人员与部门关系

    ```sql
    -- 人员属于部门
    -- 假设人员1属于部门1，并且是主要部门
    INSERT INTO b_org_entity_relation
    (node_id, node_name_alias, node_type, parent_id, level, path, sort_order, attributes, tenant_id)
    VALUES
    ('44444444-4444-4444-4444-444444444444', '人员1', 'USER', '11111111-1111-1111-1111-111111111111', 2, '/11111111-1111-1111-1111-111111111111/44444444-4444-4444-4444-444444444444/', 1, '{"position": "Manager", "is_primary": true}', '00000000-0000-0000-0000-000000000000');
    ```

3. 人员与分组关系

    ```sql
    -- 人员是分组成员
    -- 假设人员1是分组1的成员，并且是领导者
    INSERT INTO b_org_entity_relation
    (node_id, node_name_alias, node_type, parent_id, level, path, sort_order, attributes, tenant_id)
    VALUES
    ('44444444-4444-4444-4444-444444444444', '人员1', 'USER', '55555555-5555-5555-5555-555555555555', 2, '/55555555-5555-5555-5555-555555555555/44444444-4444-4444-4444-444444444444/', 1, '{\"role\": \"Leader\"}', '00000000-0000-0000-0000-000000000000');
    ```

### 4.2. 查询示例

1. 查询部门的所有直接子部门

    ```sql
    SELECT node_id, node_name_alias FROM b_org_entity_relation 
    WHERE parent_id = '11111111-1111-1111-1111-111111111111' AND node_type = 'DEPARTMENT' AND is_deleted = FALSE;
    ```

2. 查询人员所属的所有部门

    ```sql
    SELECT parent_id, attributes FROM b_org_entity_relation 
    WHERE node_id = '44444444-4444-4444-4444-444444444444' AND node_type = 'USER' AND parent_id != '00000000-0000-0000-0000-000000000000' AND is_deleted = FALSE;
    ```

3. 查询部门下的所有人员

    ```sql
    SELECT node_id, node_name_alias, attributes FROM b_org_entity_relation 
    WHERE parent_id = '11111111-1111-1111-1111-111111111111' AND node_type = 'USER' AND is_deleted = FALSE;
    ```

4. 查询部门的完整路径

    ```sql
    SELECT path FROM b_org_entity_relation 
    WHERE node_id = '33333333-3333-3333-3333-333333333333' AND node_type = 'DEPARTMENT' AND is_deleted = FALSE LIMIT 1;
    ```

5. 查询部门及其所有子孙部门

    ```sql
    SELECT node_id, node_name_alias FROM b_org_entity_relation 
    WHERE path LIKE '/11111111-1111-1111-1111-111111111111/%' AND node_type = 'DEPARTMENT' AND is_deleted = FALSE;
    ```
