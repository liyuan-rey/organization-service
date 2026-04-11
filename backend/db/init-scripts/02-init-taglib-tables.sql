-- ============================================================================
-- 标签库管理 - 建库脚本
-- 包含：标签分类、标签、标签关联表
-- ============================================================================

-- ============================================================================
-- 1. 标签分类表 (taglib_category)
-- ============================================================================
CREATE TABLE IF NOT EXISTS taglib_category (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(100) NOT NULL DEFAULT '',                -- 分类名称
    description VARCHAR(500) NOT NULL DEFAULT '',          -- 分类描述
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0',         -- 排序值（LexoRank）
    removed BOOLEAN NOT NULL DEFAULT FALSE,               -- 逻辑删除标记
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'  -- 租户ID
);

CREATE INDEX IF NOT EXISTS idx_taglib_category_tenant_id ON taglib_category(tenant_id);

COMMENT ON TABLE taglib_category IS '标签分类表';
COMMENT ON COLUMN taglib_category.id IS '主键，UUIDv7';
COMMENT ON COLUMN taglib_category.name IS '分类名称';
COMMENT ON COLUMN taglib_category.description IS '分类描述';
COMMENT ON COLUMN taglib_category.sort_rank IS '排序值（LexoRank）';
COMMENT ON COLUMN taglib_category.removed IS '逻辑删除标记';
COMMENT ON COLUMN taglib_category.create_time IS '创建时间';
COMMENT ON COLUMN taglib_category.update_time IS '更新时间';
COMMENT ON COLUMN taglib_category.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_taglib_category_updated_at BEFORE UPDATE ON taglib_category FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 2. 标签表 (taglib_tag)
-- 树形结构，支持多级标签
-- ============================================================================
CREATE TABLE IF NOT EXISTS taglib_tag (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    name VARCHAR(100) NOT NULL DEFAULT '',                -- 标签名称
    category_id UUID NOT NULL,                            -- 所属分类ID
    parent_id UUID,                                       -- 父标签ID，顶级标签为 NULL
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0',         -- 排序值（LexoRank）
    removed BOOLEAN NOT NULL DEFAULT FALSE,               -- 逻辑删除标记
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- 租户ID
    UNIQUE (category_id, name)                            -- 同一分类下标签名称不重复
);

CREATE INDEX IF NOT EXISTS idx_taglib_tag_category_id ON taglib_tag(category_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_parent_id ON taglib_tag(parent_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_tenant_id ON taglib_tag(tenant_id);

COMMENT ON TABLE taglib_tag IS '标签表';
COMMENT ON COLUMN taglib_tag.id IS '主键，UUIDv7';
COMMENT ON COLUMN taglib_tag.name IS '标签名称';
COMMENT ON COLUMN taglib_tag.category_id IS '所属分类ID';
COMMENT ON COLUMN taglib_tag.parent_id IS '父标签ID，顶级标签为 NULL';
COMMENT ON COLUMN taglib_tag.sort_rank IS '排序值（LexoRank）';
COMMENT ON COLUMN taglib_tag.removed IS '逻辑删除标记';
COMMENT ON COLUMN taglib_tag.create_time IS '创建时间';
COMMENT ON COLUMN taglib_tag.update_time IS '更新时间';
COMMENT ON COLUMN taglib_tag.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_taglib_tag_updated_at BEFORE UPDATE ON taglib_tag FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- ============================================================================
-- 3. 标签关联表 (taglib_tag_relation)
-- 标签与任意对象（人员、部门、分组等）的关联关系
-- ============================================================================
CREATE TABLE IF NOT EXISTS taglib_tag_relation (
    id UUID PRIMARY KEY,                                  -- 主键，UUIDv7
    object_type VARCHAR(50) NOT NULL DEFAULT '',           -- 对象类型（PERSONNEL、DEPARTMENT 等）
    object_id UUID NOT NULL,                              -- 对象ID
    tag_id UUID NOT NULL,                                 -- 标签ID
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 创建时间
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- 更新时间
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- 租户ID
    UNIQUE (object_type, object_id, tag_id)               -- 防止重复打标
);

CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_object ON taglib_tag_relation(object_type, object_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tag_id ON taglib_tag_relation(tag_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tenant_id ON taglib_tag_relation(tenant_id);

COMMENT ON TABLE taglib_tag_relation IS '标签关联表';
COMMENT ON COLUMN taglib_tag_relation.id IS '主键，UUIDv7';
COMMENT ON COLUMN taglib_tag_relation.object_type IS '对象类型（PERSONNEL、DEPARTMENT 等）';
COMMENT ON COLUMN taglib_tag_relation.object_id IS '对象ID';
COMMENT ON COLUMN taglib_tag_relation.tag_id IS '标签ID';
COMMENT ON COLUMN taglib_tag_relation.create_time IS '创建时间';
COMMENT ON COLUMN taglib_tag_relation.update_time IS '更新时间';
COMMENT ON COLUMN taglib_tag_relation.tenant_id IS '租户ID，多租户数据隔离';

CREATE TRIGGER update_taglib_tag_relation_updated_at BEFORE UPDATE ON taglib_tag_relation FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
