-- Create department table
CREATE TABLE IF NOT EXISTS org_department (
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
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on department table
CREATE INDEX IF NOT EXISTS idx_org_department_name ON org_department(name);
CREATE INDEX IF NOT EXISTS idx_org_department_org_code ON org_department(org_code);
CREATE INDEX IF NOT EXISTS idx_org_department_tenant_id ON org_department(tenant_id);

-- Add comments for department table columns
COMMENT ON COLUMN org_department.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN org_department.name IS 'Department name';
COMMENT ON COLUMN org_department.english_name IS 'Department English name';
COMMENT ON COLUMN org_department.short_name IS 'Department short name';
COMMENT ON COLUMN org_department.org_code IS 'Organization code';
COMMENT ON COLUMN org_department.phone IS 'Department phone number';
COMMENT ON COLUMN org_department.fax IS 'Department fax number';
COMMENT ON COLUMN org_department.email IS 'Department email address';
COMMENT ON COLUMN org_department.address IS 'Department address';
COMMENT ON COLUMN org_department.postal_code IS 'Postal code';
COMMENT ON COLUMN org_department.create_time IS 'Record creation time';
COMMENT ON COLUMN org_department.update_time IS 'Record last update time';
COMMENT ON COLUMN org_department.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create personnel table
CREATE TABLE IF NOT EXISTS org_personnel (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Personnel name
    gender VARCHAR(1) NOT NULL DEFAULT '', -- Personnel gender (M/F)
    id_card VARCHAR(18) NOT NULL DEFAULT '', -- ID card number
    mobile VARCHAR(20) NOT NULL DEFAULT '', -- Mobile phone number
    telephone VARCHAR(20) NOT NULL DEFAULT '', -- Telephone number
    fax VARCHAR(20) NOT NULL DEFAULT '', -- Fax number
    email VARCHAR(100) NOT NULL DEFAULT '', -- Email address
    photo BYTEA, -- Personnel photo (binary data)
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on personnel table
CREATE INDEX IF NOT EXISTS idx_org_personnel_name ON org_personnel(name);
CREATE INDEX IF NOT EXISTS idx_org_personnel_id_card ON org_personnel(id_card);
CREATE INDEX IF NOT EXISTS idx_org_personnel_mobile ON org_personnel(mobile);
CREATE INDEX IF NOT EXISTS idx_org_personnel_tenant_id ON org_personnel(tenant_id);

-- Add comments for personnel table columns
COMMENT ON COLUMN org_personnel.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN org_personnel.name IS 'Personnel name';
COMMENT ON COLUMN org_personnel.gender IS 'Personnel gender (M/F)';
COMMENT ON COLUMN org_personnel.id_card IS 'ID card number';
COMMENT ON COLUMN org_personnel.mobile IS 'Mobile phone number';
COMMENT ON COLUMN org_personnel.telephone IS 'Telephone number';
COMMENT ON COLUMN org_personnel.fax IS 'Fax number';
COMMENT ON COLUMN org_personnel.email IS 'Email address';
COMMENT ON COLUMN org_personnel.photo IS 'Personnel photo (binary data)';
COMMENT ON COLUMN org_personnel.create_time IS 'Record creation time';
COMMENT ON COLUMN org_personnel.update_time IS 'Record last update time';
COMMENT ON COLUMN org_personnel.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create group table
CREATE TABLE IF NOT EXISTS org_group (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Group name
    description TEXT NOT NULL DEFAULT '', -- Group description
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on group table
CREATE INDEX IF NOT EXISTS idx_org_group_name ON org_group(name);
CREATE INDEX IF NOT EXISTS idx_org_group_tenant_id ON org_group(tenant_id);

-- Add comments for group table columns
COMMENT ON COLUMN org_group.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN org_group.name IS 'Group name';
COMMENT ON COLUMN org_group.description IS 'Group description';
COMMENT ON COLUMN org_group.create_time IS 'Record creation time';
COMMENT ON COLUMN org_group.update_time IS 'Record last update time';
COMMENT ON COLUMN org_group.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- ============================================================================
-- 部门层级关系表 (Department Hierarchy Table)
-- 用于存储部门的树形层级结构
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department_hierarchy (
    id UUID PRIMARY KEY,
    parent_id UUID, -- 父部门ID，根节点为 NULL
    child_id UUID NOT NULL, -- 子部门ID
    level INTEGER NOT NULL DEFAULT 1, -- 层级深度，根节点为 1
    path VARCHAR(1000) NOT NULL DEFAULT '', -- 从根到当前节点的路径
    sort_order INTEGER NOT NULL DEFAULT 0, -- 同级节点排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (child_id) -- 每个部门只能有一个父节点
);

CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_parent ON org_department_hierarchy(parent_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_child ON org_department_hierarchy(child_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_tenant ON org_department_hierarchy(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_path ON org_department_hierarchy(path);

COMMENT ON COLUMN org_department_hierarchy.id IS 'Primary key';
COMMENT ON COLUMN org_department_hierarchy.parent_id IS 'Parent department ID, NULL for root node';
COMMENT ON COLUMN org_department_hierarchy.child_id IS 'Child department ID';
COMMENT ON COLUMN org_department_hierarchy.level IS 'Hierarchy level, root node is 1';
COMMENT ON COLUMN org_department_hierarchy.path IS 'Path from root to current node';
COMMENT ON COLUMN org_department_hierarchy.sort_order IS 'Sort order among siblings';

-- ============================================================================
-- 部门人员关联表 (Department Personnel Relation Table)
-- 用于存储人员与部门的多对多关系，支持主次部门标识
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department_personnel (
    id UUID PRIMARY KEY,
    department_id UUID NOT NULL, -- 部门ID
    personnel_id UUID NOT NULL, -- 人员ID
    is_primary BOOLEAN NOT NULL DEFAULT FALSE, -- 是否为主部门
    position VARCHAR(100) NOT NULL DEFAULT '', -- 职位名称
    sort_order INTEGER NOT NULL DEFAULT 0, -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (department_id, personnel_id) -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_dept ON org_department_personnel(department_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_personnel ON org_department_personnel(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_tenant ON org_department_personnel(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_primary ON org_department_personnel(personnel_id, is_primary);

COMMENT ON COLUMN org_department_personnel.id IS 'Primary key';
COMMENT ON COLUMN org_department_personnel.department_id IS 'Department ID';
COMMENT ON COLUMN org_department_personnel.personnel_id IS 'Personnel ID';
COMMENT ON COLUMN org_department_personnel.is_primary IS 'Is this the primary department for this person';
COMMENT ON COLUMN org_department_personnel.position IS 'Position name in this department';

-- ============================================================================
-- 分组层级关系表 (Group Hierarchy Table)
-- 用于存储分组的树形层级结构
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_hierarchy (
    id UUID PRIMARY KEY,
    parent_id UUID, -- 父分组ID，根节点为 NULL
    child_id UUID NOT NULL, -- 子分组ID
    level INTEGER NOT NULL DEFAULT 1, -- 层级深度，根节点为 1
    path VARCHAR(1000) NOT NULL DEFAULT '', -- 从根到当前节点的路径
    sort_order INTEGER NOT NULL DEFAULT 0, -- 同级节点排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (child_id) -- 每个分组只能有一个父节点
);

CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_parent ON org_group_hierarchy(parent_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_child ON org_group_hierarchy(child_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_tenant ON org_group_hierarchy(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_path ON org_group_hierarchy(path);

COMMENT ON COLUMN org_group_hierarchy.id IS 'Primary key';
COMMENT ON COLUMN org_group_hierarchy.parent_id IS 'Parent group ID, NULL for root node';
COMMENT ON COLUMN org_group_hierarchy.child_id IS 'Child group ID';
COMMENT ON COLUMN org_group_hierarchy.level IS 'Hierarchy level, root node is 1';
COMMENT ON COLUMN org_group_hierarchy.path IS 'Path from root to current node';
COMMENT ON COLUMN org_group_hierarchy.sort_order IS 'Sort order among siblings';

-- ============================================================================
-- 分组部门关联表 (Group Department Relation Table)
-- 用于存储分组与部门的多对多关系，支持跨部门协作
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_department (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL, -- 分组ID
    department_id UUID NOT NULL, -- 部门ID
    role VARCHAR(100) NOT NULL DEFAULT '', -- 协作角色
    sort_order INTEGER NOT NULL DEFAULT 0, -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (group_id, department_id) -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_group_dept_group ON org_group_department(group_id);
CREATE INDEX IF NOT EXISTS idx_org_group_dept_dept ON org_group_department(department_id);
CREATE INDEX IF NOT EXISTS idx_org_group_dept_tenant ON org_group_department(tenant_id);

COMMENT ON COLUMN org_group_department.id IS 'Primary key';
COMMENT ON COLUMN org_group_department.group_id IS 'Group ID';
COMMENT ON COLUMN org_group_department.department_id IS 'Department ID';
COMMENT ON COLUMN org_group_department.role IS 'Collaboration role';

-- ============================================================================
-- 分组人员关联表 (Group Personnel Relation Table)
-- 用于存储分组与人员的多对多关系，支持不同角色
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_personnel (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL, -- 分组ID
    personnel_id UUID NOT NULL, -- 人员ID
    role VARCHAR(100) NOT NULL DEFAULT '', -- 组内角色
    sort_order INTEGER NOT NULL DEFAULT 0, -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (group_id, personnel_id) -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_group_personnel_group ON org_group_personnel(group_id);
CREATE INDEX IF NOT EXISTS idx_org_group_personnel_personnel ON org_group_personnel(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_group_personnel_tenant ON org_group_personnel(tenant_id);

COMMENT ON COLUMN org_group_personnel.id IS 'Primary key';
COMMENT ON COLUMN org_group_personnel.group_id IS 'Group ID';
COMMENT ON COLUMN org_group_personnel.personnel_id IS 'Personnel ID';
COMMENT ON COLUMN org_group_personnel.role IS 'Role in the group';

-- ----------------------------
-- Function structure for upd_timestamp
-- ----------------------------
DROP FUNCTION IF EXISTS upd_timestamp();
CREATE OR REPLACE FUNCTION upd_timestamp()
RETURNS trigger AS $$
BEGIN
    NEW.update_time := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for update timestamps
CREATE TRIGGER update_org_department_updated_at BEFORE UPDATE ON org_department FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_personnel_updated_at BEFORE UPDATE ON org_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_group_updated_at BEFORE UPDATE ON org_group FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_dept_hierarchy_updated_at BEFORE UPDATE ON org_department_hierarchy FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_dept_personnel_updated_at BEFORE UPDATE ON org_department_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_group_hierarchy_updated_at BEFORE UPDATE ON org_group_hierarchy FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_group_dept_updated_at BEFORE UPDATE ON org_group_department FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_group_personnel_updated_at BEFORE UPDATE ON org_group_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- Insert sample data for department
-- Note: In a real application, UUIDs would be generated by the application
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code) VALUES 
    ('11111111-1111-1111-1111-111111111111', '人力资源部', 'Human Resources Department', 'HR', 'ORG-HR-001', '+1-555-0101', '+1-555-0102', 'hr@company.com', '北京市朝阳区某某街道123号', '100000'),
    ('22222222-2222-2222-2222-222222222222', '技术部', 'Technology Department', 'Tech', 'ORG-TECH-001', '+1-555-0103', '+1-555-0104', 'tech@company.com', '北京市海淀区某某科技园区456号', '100001'),
    ('33333333-3333-3333-3333-333333333333', '开发部', 'Development Department', 'Dev', 'ORG-DEV-001', '+1-555-0105', '+1-555-0106', 'dev@company.com', '北京市海淀区某某科技园区789号', '100002')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for personnel
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email) VALUES 
    ('44444444-4444-4444-4444-444444444444', '张三', 'M', '110101199001011234', '+1-555-0105', '+1-555-0106', '+1-555-0107', 'zhangsan@company.com'),
    ('55555555-5555-5555-5555-555555555555', '李四', 'F', '110101199001015678', '+1-555-0108', '+1-555-0109', '+1-555-0110', 'lisi@company.com'),
    ('66666666-6666-6666-6666-666666666666', '王五', 'M', '110101199001019012', '+1-555-0111', '+1-555-0112', '+1-555-0113', 'wangwu@company.com')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for group
INSERT INTO org_group (id, name, description) VALUES 
    ('77777777-7777-7777-7777-777777777777', '开发组', '负责软件开发工作'),
    ('88888888-8888-8888-8888-888888888888', '测试组', '负责软件测试工作'),
    ('99999999-9999-9999-9999-999999999999', '运维组', '负责基础设施和部署工作')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- Sample Data for Department Hierarchy
-- 人力资源部(根) -> 技术部 -> 开发部
-- ============================================================================
INSERT INTO org_department_hierarchy (id, parent_id, child_id, level, path, sort_order, tenant_id) VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL, '11111111-1111-1111-1111-111111111111', 1, '/11111111-1111-1111-1111-111111111111/', 1, '00000000-0000-0000-0000-000000000000'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 2, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/', 1, '00000000-0000-0000-0000-000000000000'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 3, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/33333333-3333-3333-3333-333333333333/', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (child_id) DO NOTHING;

-- ============================================================================
-- Sample Data for Department Personnel
-- 张三属于人力资源部，职位HR经理，主部门
-- ============================================================================
INSERT INTO org_department_personnel (id, department_id, personnel_id, is_primary, position, sort_order, tenant_id) VALUES 
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', TRUE, 'HR经理', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (department_id, personnel_id) DO NOTHING;

-- ============================================================================
-- Sample Data for Group Personnel
-- 张三属于开发组，角色为组长
-- ============================================================================
INSERT INTO org_group_personnel (id, group_id, personnel_id, role, sort_order, tenant_id) VALUES 
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', '组长', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (group_id, personnel_id) DO NOTHING;