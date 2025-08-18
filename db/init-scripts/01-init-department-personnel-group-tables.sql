-- Create department table
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

-- Add indexes on department table
CREATE INDEX IF NOT EXISTS idx_b_org_department_name ON b_org_department(name);
CREATE INDEX IF NOT EXISTS idx_b_org_department_org_code ON b_org_department(org_code);
CREATE INDEX IF NOT EXISTS idx_b_org_department_tenant_id ON b_org_department(tenant_id);

-- Add comments for department table columns
COMMENT ON COLUMN b_org_department.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN b_org_department.name IS 'Department name';
COMMENT ON COLUMN b_org_department.english_name IS 'Department English name';
COMMENT ON COLUMN b_org_department.short_name IS 'Department short name';
COMMENT ON COLUMN b_org_department.org_code IS 'Organization code';
COMMENT ON COLUMN b_org_department.phone IS 'Department phone number';
COMMENT ON COLUMN b_org_department.fax IS 'Department fax number';
COMMENT ON COLUMN b_org_department.email IS 'Department email address';
COMMENT ON COLUMN b_org_department.address IS 'Department address';
COMMENT ON COLUMN b_org_department.postal_code IS 'Postal code';
COMMENT ON COLUMN b_org_department.created_at IS 'Record creation time';
COMMENT ON COLUMN b_org_department.updated_at IS 'Record last update time';
COMMENT ON COLUMN b_org_department.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create personnel table
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

-- Add indexes on personnel table
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_name ON b_org_personnel(name);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_id_card ON b_org_personnel(id_card);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_mobile ON b_org_personnel(mobile);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_tenant_id ON b_org_personnel(tenant_id);

-- Add comments for personnel table columns
COMMENT ON COLUMN b_org_personnel.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN b_org_personnel.name IS 'Personnel name';
COMMENT ON COLUMN b_org_personnel.gender IS 'Personnel gender (M/F)';
COMMENT ON COLUMN b_org_personnel.id_card IS 'ID card number';
COMMENT ON COLUMN b_org_personnel.mobile IS 'Mobile phone number';
COMMENT ON COLUMN b_org_personnel.telephone IS 'Telephone number';
COMMENT ON COLUMN b_org_personnel.fax IS 'Fax number';
COMMENT ON COLUMN b_org_personnel.email IS 'Email address';
COMMENT ON COLUMN b_org_personnel.photo IS 'Personnel photo (binary data)';
COMMENT ON COLUMN b_org_personnel.created_at IS 'Record creation time';
COMMENT ON COLUMN b_org_personnel.updated_at IS 'Record last update time';
COMMENT ON COLUMN b_org_personnel.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create group table
CREATE TABLE IF NOT EXISTS b_org_group (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Group name
    description TEXT NOT NULL DEFAULT '', -- Group description
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on group table
CREATE INDEX IF NOT EXISTS idx_b_org_group_name ON b_org_group(name);
CREATE INDEX IF NOT EXISTS idx_b_org_group_tenant_id ON b_org_group(tenant_id);

-- Add comments for group table columns
COMMENT ON COLUMN b_org_group.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN b_org_group.name IS 'Group name';
COMMENT ON COLUMN b_org_group.description IS 'Group description';
COMMENT ON COLUMN b_org_group.created_at IS 'Record creation time';
COMMENT ON COLUMN b_org_group.updated_at IS 'Record last update time';
COMMENT ON COLUMN b_org_group.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create universal entity relation table (core table for unified management of all entity relationships and hierarchies)
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

-- Indexing Strategy for b_org_entity_relation table
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

-- Insert sample data for department
-- Note: In a real application, UUIDs would be generated by the application
INSERT INTO b_org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code) VALUES 
    ('11111111-1111-1111-1111-111111111111', '人力资源部', 'Human Resources Department', 'HR', 'ORG-HR-001', '+1-555-0101', '+1-555-0102', 'hr@company.com', '北京市朝阳区某某街道123号', '100000'),
    ('22222222-2222-2222-2222-222222222222', '技术部', 'Technology Department', 'Tech', 'ORG-TECH-001', '+1-555-0103', '+1-555-0104', 'tech@company.com', '北京市海淀区某某科技园区456号', '100001'),
    ('33333333-3333-3333-3333-333333333333', '开发部', 'Development Department', 'Dev', 'ORG-DEV-001', '+1-555-0105', '+1-555-0106', 'dev@company.com', '北京市海淀区某某科技园区789号', '100002')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for personnel
INSERT INTO b_org_personnel (id, name, gender, id_card, mobile, telephone, fax, email) VALUES 
    ('44444444-4444-4444-4444-444444444444', '张三', 'M', '110101199001011234', '+1-555-0105', '+1-555-0106', '+1-555-0107', 'zhangsan@company.com'),
    ('55555555-5555-5555-5555-555555555555', '李四', 'F', '110101199001015678', '+1-555-0108', '+1-555-0109', '+1-555-0110', 'lisi@company.com'),
    ('66666666-6666-6666-6666-666666666666', '王五', 'M', '110101199001019012', '+1-555-0111', '+1-555-0112', '+1-555-0113', 'wangwu@company.com')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for group
INSERT INTO b_org_group (id, name, description) VALUES 
    ('77777777-7777-7777-7777-777777777777', '开发组', '负责软件开发工作'),
    ('88888888-8888-8888-8888-888888888888', '测试组', '负责软件测试工作'),
    ('99999999-9999-9999-9999-999999999999', '运维组', '负责基础设施和部署工作')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for entity relations
-- Department hierarchy relationships
-- Assume department 1111... is root node, department 2222... is child of department 1111..., department 3333... is child of department 2222...
INSERT INTO b_org_entity_relation
(id, node_id, node_name_alias, node_type, parent_id, level, path, sort_order, tenant_id)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '人力资源部', 'DEPARTMENT', '00000000-0000-0000-0000-000000000000', 1, '/11111111-1111-1111-1111-111111111111/', 1, '00000000-0000-0000-0000-000000000000'), -- Root department
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', '技术部', 'DEPARTMENT', '11111111-1111-1111-1111-111111111111', 2, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/', 1, '00000000-0000-0000-0000-000000000000'), -- Child of department 1111...
('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', '开发部', 'DEPARTMENT', '22222222-2222-2222-2222-222222222222', 3, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/33333333-3333-3333-3333-333333333333/', 1, '00000000-0000-0000-0000-000000000000'); -- Child of department 2222...

-- Personnel-department relationships
-- Assume personnel 4444... belongs to department 1111... and is the primary department
INSERT INTO b_org_entity_relation
(id, node_id, node_name_alias, node_type, parent_id, level, path, sort_order, attributes, tenant_id)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', '44444444-4444-4444-4444-444444444444', '张三', 'USER', '11111111-1111-1111-1111-111111111111', 2, '/11111111-1111-1111-1111-111111111111/44444444-4444-4444-4444-444444444444/', 1, '{"position": "HR经理", "is_primary": true}', '00000000-0000-0000-0000-000000000000');

-- Personnel-group relationships
-- Assume personnel 4444... is member of group 7777... and is leader
INSERT INTO b_org_entity_relation
(id, node_id, node_name_alias, node_type, parent_id, level, path, sort_order, attributes, tenant_id)
VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', '张三', 'USER', '77777777-7777-7777-7777-777777777777', 2, '/77777777-7777-7777-7777-777777777777/44444444-4444-4444-4444-444444444444/', 1, '{"role": "Leader"}', '00000000-0000-0000-0000-000000000000');