-- ============================================================================
-- 组织机构管理 - 建库脚本
-- 包含：部门、人员、职位、分组、组织树及各关联关系表
-- ============================================================================

-- ----------------------------
-- 通用触发器函数：自动更新 update_time 字段
-- ----------------------------
DROP FUNCTION IF EXISTS upd_timestamp();
CREATE OR REPLACE FUNCTION upd_timestamp()
RETURNS trigger AS $$
BEGIN
    NEW.update_time := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 1. 部门表 (org_department)
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(255) NOT NULL DEFAULT '',                -- 部门名称
    english_name VARCHAR(255) NOT NULL DEFAULT '',        -- 英文名称
    short_name VARCHAR(100) NOT NULL DEFAULT '',          -- 简称
    org_code VARCHAR(50) NOT NULL DEFAULT '',             -- 组织编码
    phone VARCHAR(50) NOT NULL DEFAULT '',                -- 电话
    fax VARCHAR(50) NOT NULL DEFAULT '',                  -- 传真
    email VARCHAR(100) NOT NULL DEFAULT '',               -- 邮箱
    address VARCHAR(500) NOT NULL DEFAULT '',             -- 地址
    postal_code VARCHAR(20) NOT NULL DEFAULT '',          -- 邮编
    removed BOOLEAN NOT NULL DEFAULT FALSE,               -- 逻辑删除标记
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'  -- 租户ID
);

CREATE INDEX IF NOT EXISTS idx_org_department_name ON org_department(name);
CREATE INDEX IF NOT EXISTS idx_org_department_org_code ON org_department(org_code);
CREATE INDEX IF NOT EXISTS idx_org_department_tenant_id ON org_department(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_department_removed ON org_department(removed);
CREATE INDEX IF NOT EXISTS idx_org_department_tenant_removed ON org_department(tenant_id, removed);

COMMENT ON TABLE org_department IS '部门表';
COMMENT ON COLUMN org_department.id IS '主键，UUIDv7';
COMMENT ON COLUMN org_department.name IS '部门名称';
COMMENT ON COLUMN org_department.english_name IS '英文名称';
COMMENT ON COLUMN org_department.short_name IS '简称';
COMMENT ON COLUMN org_department.org_code IS '组织编码';
COMMENT ON COLUMN org_department.phone IS '电话';
COMMENT ON COLUMN org_department.fax IS '传真';
COMMENT ON COLUMN org_department.email IS '邮箱';
COMMENT ON COLUMN org_department.address IS '地址';
COMMENT ON COLUMN org_department.postal_code IS '邮编';
COMMENT ON COLUMN org_department.removed IS '逻辑删除标记';
COMMENT ON COLUMN org_department.create_time IS '创建时间';
COMMENT ON COLUMN org_department.update_time IS '更新时间';
COMMENT ON COLUMN org_department.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_org_department_updated_at BEFORE UPDATE ON org_department FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 2. 人员表 (org_personnel)
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_personnel (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(100) NOT NULL DEFAULT '',                -- 姓名
    gender VARCHAR(1) NOT NULL DEFAULT '',                -- 性别（M/F）
    id_card VARCHAR(18) NOT NULL DEFAULT '',              -- 身份证号
    mobile VARCHAR(20) NOT NULL DEFAULT '',               -- 手机号
    telephone VARCHAR(20) NOT NULL DEFAULT '',            -- 固定电话
    fax VARCHAR(20) NOT NULL DEFAULT '',                  -- 传真
    email VARCHAR(100) NOT NULL DEFAULT '',               -- 邮箱
    photo BYTEA,                                          -- 照片
    removed BOOLEAN NOT NULL DEFAULT FALSE,               -- 逻辑删除标记
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'  -- 租户ID
);

CREATE INDEX IF NOT EXISTS idx_org_personnel_name ON org_personnel(name);
CREATE INDEX IF NOT EXISTS idx_org_personnel_id_card ON org_personnel(id_card);
CREATE INDEX IF NOT EXISTS idx_org_personnel_mobile ON org_personnel(mobile);
CREATE INDEX IF NOT EXISTS idx_org_personnel_tenant_id ON org_personnel(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_personnel_removed ON org_personnel(removed);
CREATE INDEX IF NOT EXISTS idx_org_personnel_tenant_removed ON org_personnel(tenant_id, removed);

COMMENT ON TABLE org_personnel IS '人员表';
COMMENT ON COLUMN org_personnel.id IS '主键，UUIDv7';
COMMENT ON COLUMN org_personnel.name IS '姓名';
COMMENT ON COLUMN org_personnel.gender IS '性别（M/F）';
COMMENT ON COLUMN org_personnel.id_card IS '身份证号';
COMMENT ON COLUMN org_personnel.mobile IS '手机号';
COMMENT ON COLUMN org_personnel.telephone IS '固定电话';
COMMENT ON COLUMN org_personnel.fax IS '传真';
COMMENT ON COLUMN org_personnel.email IS '邮箱';
COMMENT ON COLUMN org_personnel.photo IS '照片（二进制）';
COMMENT ON COLUMN org_personnel.removed IS '逻辑删除标记';
COMMENT ON COLUMN org_personnel.create_time IS '创建时间';
COMMENT ON COLUMN org_personnel.update_time IS '更新时间';
COMMENT ON COLUMN org_personnel.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_org_personnel_updated_at BEFORE UPDATE ON org_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 3. 分组表 (org_group)
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(100) NOT NULL DEFAULT '',                -- 分组名称
    description TEXT NOT NULL DEFAULT '',                  -- 分组描述
    removed BOOLEAN NOT NULL DEFAULT FALSE,               -- 逻辑删除标记
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'  -- 租户ID
);

CREATE INDEX IF NOT EXISTS idx_org_group_name ON org_group(name);
CREATE INDEX IF NOT EXISTS idx_org_group_tenant_id ON org_group(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_group_removed ON org_group(removed);
CREATE INDEX IF NOT EXISTS idx_org_group_tenant_removed ON org_group(tenant_id, removed);

COMMENT ON TABLE org_group IS '分组表';
COMMENT ON COLUMN org_group.id IS '主键，UUIDv7';
COMMENT ON COLUMN org_group.name IS '分组名称';
COMMENT ON COLUMN org_group.description IS '分组描述';
COMMENT ON COLUMN org_group.removed IS '逻辑删除标记';
COMMENT ON COLUMN org_group.create_time IS '创建时间';
COMMENT ON COLUMN org_group.update_time IS '更新时间';
COMMENT ON COLUMN org_group.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_org_group_updated_at BEFORE UPDATE ON org_group FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 4. 职位表 (org_position)
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_position (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(100) NOT NULL DEFAULT '',                -- 职位名称
    code VARCHAR(50) NOT NULL DEFAULT '',                 -- 职位编码
    description VARCHAR(500) NOT NULL DEFAULT '',          -- 职位描述
    job_level VARCHAR(50) NOT NULL DEFAULT '',             -- 职级（P1、P2、M1、M2 等）
    job_category VARCHAR(50) NOT NULL DEFAULT '',          -- 职类（技术、管理、销售等）
    min_salary DECIMAL(12,2),                             -- 最低薪资
    max_salary DECIMAL(12,2),                             -- 最高薪资
    status INTEGER NOT NULL DEFAULT 1,                    -- 状态：1=启用，0=停用
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'  -- 租户ID
);

CREATE INDEX IF NOT EXISTS idx_org_position_name ON org_position(name);
CREATE INDEX IF NOT EXISTS idx_org_position_code ON org_position(code);
CREATE INDEX IF NOT EXISTS idx_org_position_job_level ON org_position(job_level);
CREATE INDEX IF NOT EXISTS idx_org_position_job_category ON org_position(job_category);
CREATE INDEX IF NOT EXISTS idx_org_position_status ON org_position(status);
CREATE INDEX IF NOT EXISTS idx_org_position_tenant_id ON org_position(tenant_id);

COMMENT ON TABLE org_position IS '职位表';
COMMENT ON COLUMN org_position.id IS '主键，UUIDv7';
COMMENT ON COLUMN org_position.name IS '职位名称';
COMMENT ON COLUMN org_position.code IS '职位编码';
COMMENT ON COLUMN org_position.description IS '职位描述';
COMMENT ON COLUMN org_position.job_level IS '职级（P1、P2、M1、M2 等）';
COMMENT ON COLUMN org_position.job_category IS '职类（技术、管理、销售等）';
COMMENT ON COLUMN org_position.min_salary IS '最低薪资';
COMMENT ON COLUMN org_position.max_salary IS '最高薪资';
COMMENT ON COLUMN org_position.status IS '状态：1=启用，0=停用';
COMMENT ON COLUMN org_position.create_time IS '创建时间';
COMMENT ON COLUMN org_position.update_time IS '更新时间';
COMMENT ON COLUMN org_position.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_org_position_updated_at BEFORE UPDATE ON org_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 5. 部门层级关系表 (org_department_hierarchy)
-- 存储部门的树形层级结构
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department_hierarchy (
    id UUID PRIMARY KEY,                                  -- 主键
    parent_id UUID,                                       -- 父部门ID，根节点为 NULL
    child_id UUID NOT NULL,                               -- 子部门ID
    level INTEGER NOT NULL DEFAULT 1,                     -- 层级深度，根节点为 1
    path VARCHAR(1000) NOT NULL DEFAULT '',               -- 从根到当前节点的路径
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 同级节点排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (child_id)                                     -- 每个部门只能有一个父节点
);

CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_parent ON org_department_hierarchy(parent_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_child ON org_department_hierarchy(child_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_tenant ON org_department_hierarchy(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_hierarchy_path ON org_department_hierarchy(path);

COMMENT ON TABLE org_department_hierarchy IS '部门层级关系表';
COMMENT ON COLUMN org_department_hierarchy.id IS '主键';
COMMENT ON COLUMN org_department_hierarchy.parent_id IS '父部门ID，根节点为 NULL';
COMMENT ON COLUMN org_department_hierarchy.child_id IS '子部门ID';
COMMENT ON COLUMN org_department_hierarchy.level IS '层级深度，根节点为 1';
COMMENT ON COLUMN org_department_hierarchy.path IS '从根到当前节点的路径';
COMMENT ON COLUMN org_department_hierarchy.sort_order IS '同级节点排序';

CREATE TRIGGER update_org_dept_hierarchy_updated_at BEFORE UPDATE ON org_department_hierarchy FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 6. 部门人员关联表 (org_department_personnel)
-- 人员与部门的多对多关系，支持主次部门标识
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department_personnel (
    id UUID PRIMARY KEY,                                  -- 主键
    department_id UUID NOT NULL,                          -- 部门ID
    personnel_id UUID NOT NULL,                           -- 人员ID
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,            -- 是否为主部门
    position VARCHAR(100) NOT NULL DEFAULT '',             -- 职位名称
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (department_id, personnel_id)                  -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_dept ON org_department_personnel(department_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_personnel ON org_department_personnel(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_tenant ON org_department_personnel(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_personnel_primary ON org_department_personnel(personnel_id, is_primary);

COMMENT ON TABLE org_department_personnel IS '部门人员关联表';
COMMENT ON COLUMN org_department_personnel.id IS '主键';
COMMENT ON COLUMN org_department_personnel.department_id IS '部门ID';
COMMENT ON COLUMN org_department_personnel.personnel_id IS '人员ID';
COMMENT ON COLUMN org_department_personnel.is_primary IS '是否为主部门';
COMMENT ON COLUMN org_department_personnel.position IS '职位名称';

CREATE TRIGGER update_org_dept_personnel_updated_at BEFORE UPDATE ON org_department_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 7. 部门职位关联表 (org_department_position)
-- 部门与职位的多对多关系
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_department_position (
    id UUID PRIMARY KEY,                                  -- 主键
    department_id UUID NOT NULL,                          -- 部门ID
    position_id UUID NOT NULL,                            -- 职位ID
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,            -- 是否为主职位
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (department_id, position_id)                   -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_dept_position_dept_id ON org_department_position(department_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_position_position_id ON org_department_position(position_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_position_tenant_id ON org_department_position(tenant_id);

COMMENT ON TABLE org_department_position IS '部门职位关联表';
COMMENT ON COLUMN org_department_position.id IS '主键';
COMMENT ON COLUMN org_department_position.department_id IS '部门ID';
COMMENT ON COLUMN org_department_position.position_id IS '职位ID';
COMMENT ON COLUMN org_department_position.is_primary IS '是否为主职位';
COMMENT ON COLUMN org_department_position.sort_order IS '排序';

CREATE TRIGGER update_org_department_position_updated_at BEFORE UPDATE ON org_department_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 8. 人员职位关联表 (org_personnel_position)
-- 人员与职位的多对多关系，支持任职部门和任期
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_personnel_position (
    id UUID PRIMARY KEY,                                  -- 主键
    personnel_id UUID NOT NULL,                           -- 人员ID
    position_id UUID NOT NULL,                            -- 职位ID
    department_id UUID,                                   -- 任职部门ID
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,            -- 是否为主职位
    start_date DATE,                                      -- 任职开始日期
    end_date DATE,                                        -- 任职结束日期（NULL 表示在职）
    status INTEGER NOT NULL DEFAULT 1,                    -- 状态：1=在职，0=离职
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (personnel_id, position_id, department_id)     -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_person_position_person_id ON org_personnel_position(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_position_id ON org_personnel_position(position_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_dept_id ON org_personnel_position(department_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_status ON org_personnel_position(status);
CREATE INDEX IF NOT EXISTS idx_org_person_position_tenant_id ON org_personnel_position(tenant_id);

COMMENT ON TABLE org_personnel_position IS '人员职位关联表';
COMMENT ON COLUMN org_personnel_position.id IS '主键';
COMMENT ON COLUMN org_personnel_position.personnel_id IS '人员ID';
COMMENT ON COLUMN org_personnel_position.position_id IS '职位ID';
COMMENT ON COLUMN org_personnel_position.department_id IS '任职部门ID';
COMMENT ON COLUMN org_personnel_position.is_primary IS '是否为主职位';
COMMENT ON COLUMN org_personnel_position.start_date IS '任职开始日期';
COMMENT ON COLUMN org_personnel_position.end_date IS '任职结束日期（NULL 表示在职）';
COMMENT ON COLUMN org_personnel_position.status IS '状态：1=在职，0=离职';

CREATE TRIGGER update_org_personnel_position_updated_at BEFORE UPDATE ON org_personnel_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 9. 分组层级关系表 (org_group_hierarchy)
-- 存储分组的树形层级结构
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_hierarchy (
    id UUID PRIMARY KEY,                                  -- 主键
    parent_id UUID,                                       -- 父分组ID，根节点为 NULL
    child_id UUID NOT NULL,                               -- 子分组ID
    level INTEGER NOT NULL DEFAULT 1,                     -- 层级深度，根节点为 1
    path VARCHAR(1000) NOT NULL DEFAULT '',               -- 从根到当前节点的路径
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 同级节点排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (child_id)                                     -- 每个分组只能有一个父节点
);

CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_parent ON org_group_hierarchy(parent_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_child ON org_group_hierarchy(child_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_tenant ON org_group_hierarchy(tenant_id);
CREATE INDEX IF NOT EXISTS idx_org_group_hierarchy_path ON org_group_hierarchy(path);

COMMENT ON TABLE org_group_hierarchy IS '分组层级关系表';
COMMENT ON COLUMN org_group_hierarchy.id IS '主键';
COMMENT ON COLUMN org_group_hierarchy.parent_id IS '父分组ID，根节点为 NULL';
COMMENT ON COLUMN org_group_hierarchy.child_id IS '子分组ID';
COMMENT ON COLUMN org_group_hierarchy.level IS '层级深度，根节点为 1';
COMMENT ON COLUMN org_group_hierarchy.path IS '从根到当前节点的路径';
COMMENT ON COLUMN org_group_hierarchy.sort_order IS '同级节点排序';

CREATE TRIGGER update_org_group_hierarchy_updated_at BEFORE UPDATE ON org_group_hierarchy FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 10. 分组部门关联表 (org_group_department)
-- 分组与部门的多对多关系，支持跨部门协作
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_department (
    id UUID PRIMARY KEY,                                  -- 主键
    group_id UUID NOT NULL,                               -- 分组ID
    department_id UUID NOT NULL,                          -- 部门ID
    role VARCHAR(100) NOT NULL DEFAULT '',                 -- 协作角色
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (group_id, department_id)                      -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_group_dept_group ON org_group_department(group_id);
CREATE INDEX IF NOT EXISTS idx_org_group_dept_dept ON org_group_department(department_id);
CREATE INDEX IF NOT EXISTS idx_org_group_dept_tenant ON org_group_department(tenant_id);

COMMENT ON TABLE org_group_department IS '分组部门关联表';
COMMENT ON COLUMN org_group_department.id IS '主键';
COMMENT ON COLUMN org_group_department.group_id IS '分组ID';
COMMENT ON COLUMN org_group_department.department_id IS '部门ID';
COMMENT ON COLUMN org_group_department.role IS '协作角色';

CREATE TRIGGER update_org_group_dept_updated_at BEFORE UPDATE ON org_group_department FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 11. 分组人员关联表 (org_group_personnel)
-- 分组与人员的多对多关系，支持不同角色
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_group_personnel (
    id UUID PRIMARY KEY,                                  -- 主键
    group_id UUID NOT NULL,                               -- 分组ID
    personnel_id UUID NOT NULL,                           -- 人员ID
    role VARCHAR(100) NOT NULL DEFAULT '',                 -- 组内角色
    sort_order INTEGER NOT NULL DEFAULT 0,                -- 排序
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (group_id, personnel_id)                       -- 防止重复关联
);

CREATE INDEX IF NOT EXISTS idx_org_group_personnel_group ON org_group_personnel(group_id);
CREATE INDEX IF NOT EXISTS idx_org_group_personnel_personnel ON org_group_personnel(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_group_personnel_tenant ON org_group_personnel(tenant_id);

COMMENT ON TABLE org_group_personnel IS '分组人员关联表';
COMMENT ON COLUMN org_group_personnel.id IS '主键';
COMMENT ON COLUMN org_group_personnel.group_id IS '分组ID';
COMMENT ON COLUMN org_group_personnel.personnel_id IS '人员ID';
COMMENT ON COLUMN org_group_personnel.role IS '组内角色';

CREATE TRIGGER update_org_group_personnel_updated_at BEFORE UPDATE ON org_group_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 12. 组织树节点表 (org_tree)
-- 基于 LexoRank 排序的统一组织树，支持多类型节点混合排列
-- ============================================================================
CREATE TABLE IF NOT EXISTS org_tree (
    id UUID NOT NULL,                                     -- 主键，UUIDv7
    parent_id UUID NOT NULL,                              -- 父节点ID，虚拟根指向自身
    entity_type VARCHAR(20) NOT NULL,                     -- 实体类型：ROOT、GROUP、DEPARTMENT、PERSONNEL
    entity_id UUID NOT NULL,                              -- 关联业务实体ID
    alias VARCHAR(100) NOT NULL DEFAULT '',                -- 节点别名
    level INTEGER NOT NULL DEFAULT 0,                     -- 层级深度，虚拟根为 0，业务根为 1
    path UUID[] NOT NULL DEFAULT '{}',                    -- 从根到父节点的路径数组
    sort_rank VARCHAR(12) NOT NULL,                       -- LexoRank 排序值
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00',
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00',
    tenant_id UUID NOT NULL,
    CONSTRAINT pk_org_tree PRIMARY KEY (id),
    CONSTRAINT uk_org_tree_parent_entity UNIQUE (parent_id, entity_type, entity_id) -- 同一父节点下不允许重复实体
);

-- 查询子节点并排序的索引
CREATE INDEX IF NOT EXISTS idx_org_tree_parent ON org_tree(parent_id, sort_rank);
-- 按实体类型和ID查询节点的索引
CREATE INDEX IF NOT EXISTS idx_org_tree_entity ON org_tree(entity_type, entity_id);
-- 路径数组的 GIN 索引，支持 ANY(path) 查询
CREATE INDEX IF NOT EXISTS idx_org_tree_path_gin ON org_tree USING GIN (path);

COMMENT ON TABLE org_tree IS '组织树节点表';
COMMENT ON COLUMN org_tree.id IS '主键，UUIDv7';
COMMENT ON COLUMN org_tree.parent_id IS '父节点ID，虚拟根指向自身';
COMMENT ON COLUMN org_tree.entity_type IS '实体类型：ROOT、GROUP、DEPARTMENT、PERSONNEL';
COMMENT ON COLUMN org_tree.entity_id IS '关联业务实体ID';
COMMENT ON COLUMN org_tree.alias IS '节点别名';
COMMENT ON COLUMN org_tree.level IS '层级深度，虚拟根为 0，业务根为 1';
COMMENT ON COLUMN org_tree.path IS '从根到父节点的路径数组';
COMMENT ON COLUMN org_tree.sort_rank IS 'LexoRank 排序值';
COMMENT ON COLUMN org_tree.create_time IS '创建时间';
COMMENT ON COLUMN org_tree.update_time IS '更新时间';
COMMENT ON COLUMN org_tree.tenant_id IS '租户ID，多租户数据隔离';

-- 组织树独立的触发器函数（不依赖通用 upd_timestamp）
CREATE OR REPLACE FUNCTION update_org_tree_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_org_tree_updated_at
    BEFORE UPDATE ON org_tree
    FOR EACH ROW
    EXECUTE FUNCTION update_org_tree_updated_at();
