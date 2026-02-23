-- 创建部门表
CREATE TABLE IF NOT EXISTS org_department (
    id UUID PRIMARY KEY, -- 主键，使用128位UUID v7算法生成
    name VARCHAR(255) NOT NULL DEFAULT '', -- 部门名称
    english_name VARCHAR(255) NOT NULL DEFAULT '', -- 部门英文名称
    short_name VARCHAR(100) NOT NULL DEFAULT '', -- 部门简称
    org_code VARCHAR(50) NOT NULL DEFAULT '', -- 组织机构代码
    phone VARCHAR(50) NOT NULL DEFAULT '', -- 部门电话号码
    fax VARCHAR(50) NOT NULL DEFAULT '', -- 部门传真号码
    email VARCHAR(100) NOT NULL DEFAULT '', -- 部门邮箱地址
    address VARCHAR(500) NOT NULL DEFAULT '', -- 部门地址
    postal_code VARCHAR(20) NOT NULL DEFAULT '', -- 邮政编码
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录最后更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- 租户标识，用于多租户数据隔离
);

-- 为部门表添加索引
CREATE INDEX IF NOT EXISTS idx_org_department_name ON org_department(name);
CREATE INDEX IF NOT EXISTS idx_org_department_org_code ON org_department(org_code);
CREATE INDEX IF NOT EXISTS idx_org_department_tenant_id ON org_department(tenant_id);

-- 为部门表列添加中文注释
COMMENT ON COLUMN org_department.id IS '主键，使用128位UUID v7算法生成';
COMMENT ON COLUMN org_department.name IS '部门名称';
COMMENT ON COLUMN org_department.english_name IS '部门英文名称';
COMMENT ON COLUMN org_department.short_name IS '部门简称';
COMMENT ON COLUMN org_department.org_code IS '组织机构代码';
COMMENT ON COLUMN org_department.phone IS '部门电话号码';
COMMENT ON COLUMN org_department.fax IS '部门传真号码';
COMMENT ON COLUMN org_department.email IS '部门邮箱地址';
COMMENT ON COLUMN org_department.address IS '部门地址';
COMMENT ON COLUMN org_department.postal_code IS '邮政编码';
COMMENT ON COLUMN org_department.create_time IS '记录创建时间';
COMMENT ON COLUMN org_department.update_time IS '记录最后更新时间';
COMMENT ON COLUMN org_department.tenant_id IS '租户标识，用于多租户数据隔离';

-- 创建人员表
CREATE TABLE IF NOT EXISTS org_personnel (
    id UUID PRIMARY KEY, -- 主键，使用128位UUID v7算法生成
    name VARCHAR(100) NOT NULL DEFAULT '', -- 人员姓名
    gender CHAR(1) NOT NULL DEFAULT '', -- 人员性别（M/F）
    id_card VARCHAR(18) NOT NULL DEFAULT '', -- 身份证号码
    mobile VARCHAR(20) NOT NULL DEFAULT '', -- 手机号码
    telephone VARCHAR(20) NOT NULL DEFAULT '', -- 电话号码
    fax VARCHAR(20) NOT NULL DEFAULT '', -- 传真号码
    email VARCHAR(100) NOT NULL DEFAULT '', -- 邮箱地址
    photo BYTEA, -- 人员照片（二进制数据）
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录最后更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- 租户标识，用于多租户数据隔离
);

-- 为人员表添加索引
CREATE INDEX IF NOT EXISTS idx_org_personnel_name ON org_personnel(name);
CREATE INDEX IF NOT EXISTS idx_org_personnel_id_card ON org_personnel(id_card);
CREATE INDEX IF NOT EXISTS idx_org_personnel_mobile ON org_personnel(mobile);
CREATE INDEX IF NOT EXISTS idx_org_personnel_tenant_id ON org_personnel(tenant_id);

-- 为人员表列添加中文注释
COMMENT ON COLUMN org_personnel.id IS '主键，使用128位UUID v7算法生成';
COMMENT ON COLUMN org_personnel.name IS '人员姓名';
COMMENT ON COLUMN org_personnel.gender IS '人员性别（M/F）';
COMMENT ON COLUMN org_personnel.id_card IS '身份证号码';
COMMENT ON COLUMN org_personnel.mobile IS '手机号码';
COMMENT ON COLUMN org_personnel.telephone IS '电话号码';
COMMENT ON COLUMN org_personnel.fax IS '传真号码';
COMMENT ON COLUMN org_personnel.email IS '邮箱地址';
COMMENT ON COLUMN org_personnel.photo IS '人员照片（二进制数据）';
COMMENT ON COLUMN org_personnel.create_time IS '记录创建时间';
COMMENT ON COLUMN org_personnel.update_time IS '记录最后更新时间';
COMMENT ON COLUMN org_personnel.tenant_id IS '租户标识，用于多租户数据隔离';

-- 创建群组表
CREATE TABLE IF NOT EXISTS org_group (
    id UUID PRIMARY KEY, -- 主键，使用128位UUID v7算法生成
    name VARCHAR(100) NOT NULL DEFAULT '', -- 群组名称
    description TEXT NOT NULL DEFAULT '', -- 群组描述
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录最后更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- 租户标识，用于多租户数据隔离
);

-- 为群组表添加索引
CREATE INDEX IF NOT EXISTS idx_org_group_name ON org_group(name);
CREATE INDEX IF NOT EXISTS idx_org_group_tenant_id ON org_group(tenant_id);

-- 为群组表列添加中文注释
COMMENT ON COLUMN org_group.id IS '主键，使用128位UUID v7算法生成';
COMMENT ON COLUMN org_group.name IS '群组名称';
COMMENT ON COLUMN org_group.description IS '群组描述';
COMMENT ON COLUMN org_group.create_time IS '记录创建时间';
COMMENT ON COLUMN org_group.update_time IS '记录最后更新时间';
COMMENT ON COLUMN org_group.tenant_id IS '租户标识，用于多租户数据隔离';

-- 创建通用实体关系表（核心表，用于统一管理所有实体关系和层级结构）
CREATE TABLE IF NOT EXISTS org_entity_relation (
    id UUID PRIMARY KEY, -- 主键，使用128位UUID v7算法生成
    node_id UUID NOT NULL, -- 节点的唯一标识符（链接到特定实体表）
    node_name_alias VARCHAR(255) NOT NULL DEFAULT '', -- 节点的显示名称或别名
    node_type VARCHAR(50) NOT NULL, -- 节点类型（例如：'PERSONNEL'人员、'DEPARTMENT'部门、'GROUP'群组）
    related_node_id UUID NOT NULL, -- 相关节点ID，用于构建层级关系。根节点的'related_node_id'为'00000000-0000-0000-0000-000000000000'
    relationship VARCHAR(50) NOT NULL, -- 两个节点之间的关系类型（例如：'org-structure'组织结构、'address-book'通讯录、'directly-managed'直属条线、'peer-C.G.M.'党委政府条线）
    level INTEGER NOT NULL DEFAULT 0, -- 层级深度，根节点层级为1
    path VARCHAR(1000) NOT NULL DEFAULT '', -- 从根节点到当前节点的路径，例如："/1/5/12/"
    sort_order INTEGER NOT NULL DEFAULT 0, -- 同级节点的排序顺序
    attributes JSONB, -- 扩展属性，用于存储动态的节点或关系信息
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, -- 软删除标志
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 记录最后更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- 租户标识，用于多租户数据隔离
);

-- 为列添加中文注释（PostgreSQL示例）
COMMENT ON COLUMN org_entity_relation.id IS '主键';
COMMENT ON COLUMN org_entity_relation.node_id IS '节点的唯一标识符（链接到特定实体表）';
COMMENT ON COLUMN org_entity_relation.node_name_alias IS '节点的显示名称或别名';
COMMENT ON COLUMN org_entity_relation.node_type IS '节点类型（例如：''PERSONNEL''人员、''DEPARTMENT''部门、''GROUP''群组）';
COMMENT ON COLUMN org_entity_relation.related_node_id IS '相关节点ID，用于构建层级关系。根节点的''related_node_id''为''00000000-0000-0000-0000-000000000000''';
COMMENT ON COLUMN org_entity_relation.relationship IS '两个节点之间的关系类型（例如：''org-structure''组织结构、''address-book''通讯录、''directly-managed''直属条线、''peer-C.G.M.''党委政府条线）';
COMMENT ON COLUMN org_entity_relation.level IS '层级深度，根节点层级为1';
COMMENT ON COLUMN org_entity_relation.path IS '从根节点到当前节点的路径，例如："/1/5/12/"';
COMMENT ON COLUMN org_entity_relation.sort_order IS '同级节点的排序顺序';
COMMENT ON COLUMN org_entity_relation.attributes IS '扩展属性，用于存储动态的节点或关系信息';
COMMENT ON COLUMN org_entity_relation.is_deleted IS '软删除标志';
COMMENT ON COLUMN org_entity_relation.create_time IS '记录创建时间';
COMMENT ON COLUMN org_entity_relation.update_time IS '记录最后更新时间';
COMMENT ON COLUMN org_entity_relation.tenant_id IS '租户标识，用于多租户数据隔离';

-- 实体关系表的索引策略
-- 主键索引（自动创建）
-- CREATE UNIQUE INDEX pk_org_entity_relation ON org_entity_relation(id);

-- 核心业务索引
-- 为node_type字段添加索引
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_node_type ON org_entity_relation(node_type);

-- 为relationship字段添加索引
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_relationship ON org_entity_relation(relationship);

-- 用于通过类型和ID快速定位特定节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_node_lookup ON org_entity_relation(node_type, node_id);

-- 用于查询节点的所有直接子节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_related_lookup ON org_entity_relation(related_node_id);

-- 用于查询和排序节点的子节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_children_sort ON org_entity_relation(related_node_id, sort_order);

-- 多租户和状态索引
-- 用于在租户内定位节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_tenant_node ON org_entity_relation(tenant_id, node_type, node_id);

-- 用于在租户内查询子节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_tenant_related ON org_entity_relation(tenant_id, related_node_id);

-- 用于在租户内查询和排序子节点
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_tenant_related_sort ON org_entity_relation(tenant_id, related_node_id, sort_order);

-- 用于在租户内过滤活动（未删除）数据
CREATE INDEX IF NOT EXISTS idx_org_entity_relation_active_tenant ON org_entity_relation(is_deleted, tenant_id);

-- 特殊查询索引（可选，基于查询需求）
-- 如果需要频繁基于路径查询
-- CREATE INDEX IF NOT EXISTS idx_org_entity_relation_path ON org_entity_relation(path);

-- 如果需要频繁基于层级查询
-- CREATE INDEX IF NOT EXISTS idx_org_entity_relation_level ON org_entity_relation(level);

-- ----------------------------
-- 更新时间戳函数结构
-- ----------------------------
DROP FUNCTION IF EXISTS upd_timestamp();
CREATE OR REPLACE FUNCTION upd_timestamp()
RETURNS trigger AS $$
BEGIN
    NEW.update_time := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为各表创建触发器
CREATE TRIGGER update_org_department_updated_at BEFORE UPDATE ON org_department FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_personnel_updated_at BEFORE UPDATE ON org_personnel FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_group_updated_at BEFORE UPDATE ON org_group FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_entity_relation_updated_at BEFORE UPDATE ON org_entity_relation FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- 插入部门示例数据
-- 注意：在实际应用中，UUID应由应用程序生成
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code) VALUES 
    ('11111111-1111-1111-1111-111111111111', '人力资源部', 'Human Resources Department', 'HR', 'ORG-HR-001', '+1-555-0101', '+1-555-0102', 'hr@company.com', '北京市朝阳区某某街道123号', '100000'),
    ('22222222-2222-2222-2222-222222222222', '技术部', 'Technology Department', 'Tech', 'ORG-TECH-001', '+1-555-0103', '+1-555-0104', 'tech@company.com', '北京市海淀区某某科技园区456号', '100001'),
    ('33333333-3333-3333-3333-333333333333', '开发部', 'Development Department', 'Dev', 'ORG-DEV-001', '+1-555-0105', '+1-555-0106', 'dev@company.com', '北京市海淀区某某科技园区789号', '100002')
ON CONFLICT (id) DO NOTHING;

-- 插入人员示例数据
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email) VALUES 
    ('44444444-4444-4444-4444-444444444444', '张三', 'M', '110101199001011234', '+1-555-0105', '+1-555-0106', '+1-555-0107', 'zhangsan@company.com'),
    ('55555555-5555-5555-5555-555555555555', '李四', 'F', '110101199001015678', '+1-555-0108', '+1-555-0109', '+1-555-0110', 'lisi@company.com'),
    ('66666666-6666-6666-6666-666666666666', '王五', 'M', '110101199001019012', '+1-555-0111', '+1-555-0112', '+1-555-0113', 'wangwu@company.com')
ON CONFLICT (id) DO NOTHING;

-- 插入群组示例数据
INSERT INTO org_group (id, name, description) VALUES 
    ('77777777-7777-7777-7777-777777777777', '开发组', '负责软件开发工作'),
    ('88888888-8888-8888-8888-888888888888', '测试组', '负责软件测试工作'),
    ('99999999-9999-9999-9999-999999999999', '运维组', '负责基础设施和部署工作')
ON CONFLICT (id) DO NOTHING;

-- 插入实体关系示例数据
-- 部门层级关系
-- 假设部门1111...是根节点，部门2222...是部门1111...的子节点，部门3333...是部门2222...的子节点
INSERT INTO org_entity_relation
(id, node_id, node_name_alias, node_type, related_node_id,  relationship, level, path, sort_order, tenant_id)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '人力资源部', 'DEPARTMENT', '00000000-0000-0000-0000-000000000000', 'org-structure', 1, '/11111111-1111-1111-1111-111111111111/', 1, '00000000-0000-0000-0000-000000000000'), -- 根部门
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', '技术部', 'DEPARTMENT', '11111111-1111-1111-1111-111111111111', 'org-structure', 2, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/', 1, '00000000-0000-0000-0000-000000000000'), -- 部门1111...的子部门
('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', '开发部', 'DEPARTMENT', '22222222-2222-2222-2222-222222222222', 'org-structure', 3, '/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222/33333333-3333-3333-3333-333333333333/', 1, '00000000-0000-0000-0000-000000000000'); -- 部门2222...的子部门

-- 人员-部门关系
-- 假设人员4444...属于部门1111...并且是主部门
INSERT INTO org_entity_relation
(id, node_id, node_name_alias, node_type, related_node_id, relationship, level, path, sort_order, attributes, tenant_id)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', '44444444-4444-4444-4444-444444444444', '张三', 'PERSONNEL', '11111111-1111-1111-1111-111111111111', 'org-structure', 2, '/11111111-1111-1111-1111-111111111111/44444444-4444-4444-4444-444444444444/', 1, '{"position": "HR经理", "is_primary": true}', '00000000-0000-0000-0000-000000000000');

-- 人员-群组关系
-- 假设人员4444...是群组7777...的成员并且是负责人
INSERT INTO org_entity_relation
(id, node_id, node_name_alias, node_type, related_node_id, relationship, level, path, sort_order, attributes, tenant_id)
VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', '张三', 'PERSONNEL', '77777777-7777-7777-7777-777777777777', 'org-structure', 2, '/77777777-7777-7777-7777-777777777777/44444444-4444-4444-4444-444444444444/', 1, '{"role": "Leader"}', '00000000-0000-0000-0000-000000000000');