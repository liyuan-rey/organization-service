-- Create taglib category table (标签分类表)
CREATE TABLE IF NOT EXISTS taglib_category (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Category name
    description VARCHAR(500) NOT NULL DEFAULT '', -- Category description
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0', -- Sort rank for ordering
    removed BOOLEAN NOT NULL DEFAULT FALSE, -- Soft delete flag
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on taglib category table
CREATE INDEX IF NOT EXISTS idx_taglib_category_tenant_id ON taglib_category(tenant_id);

-- Add comments for taglib category table columns
COMMENT ON COLUMN taglib_category.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_category.name IS 'Category name';
COMMENT ON COLUMN taglib_category.description IS 'Category description';
COMMENT ON COLUMN taglib_category.sort_rank IS 'Sort rank for ordering';
COMMENT ON COLUMN taglib_category.removed IS 'Soft delete flag';
COMMENT ON COLUMN taglib_category.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_category.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_category.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create taglib tag table (标签表)
CREATE TABLE IF NOT EXISTS taglib_tag (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Tag name
    category_id UUID NOT NULL, -- Category ID this tag belongs to
    parent_id UUID, -- Parent tag ID for tree structure
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0', -- Sort rank for ordering
    removed BOOLEAN NOT NULL DEFAULT FALSE, -- Soft delete flag
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- Tenant identifier for multi-tenant data isolation
    UNIQUE (category_id, name) -- Prevent duplicate tag names within a category
);

-- Add indexes on taglib tag table
CREATE INDEX IF NOT EXISTS idx_taglib_tag_category_id ON taglib_tag(category_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_parent_id ON taglib_tag(parent_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_tenant_id ON taglib_tag(tenant_id);

-- Add comments for taglib tag table columns
COMMENT ON COLUMN taglib_tag.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_tag.name IS 'Tag name';
COMMENT ON COLUMN taglib_tag.category_id IS 'Category ID this tag belongs to';
COMMENT ON COLUMN taglib_tag.parent_id IS 'Parent tag ID for tree structure';
COMMENT ON COLUMN taglib_tag.sort_rank IS 'Sort rank for ordering';
COMMENT ON COLUMN taglib_tag.removed IS 'Soft delete flag';
COMMENT ON COLUMN taglib_tag.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_tag.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_tag.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create taglib tag relation table (标签关联表)
-- Links tags to arbitrary objects (personnel, departments, groups, etc.)
CREATE TABLE IF NOT EXISTS taglib_tag_relation (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    object_type VARCHAR(50) NOT NULL DEFAULT '', -- Type of the tagged object (e.g., PERSONNEL, DEPARTMENT)
    object_id UUID NOT NULL, -- ID of the tagged object
    tag_id UUID NOT NULL, -- Tag ID
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- Tenant identifier for multi-tenant data isolation
    UNIQUE (object_type, object_id, tag_id) -- Prevent duplicate tag assignments
);

-- Add indexes on taglib tag relation table
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_object ON taglib_tag_relation(object_type, object_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tag_id ON taglib_tag_relation(tag_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tenant_id ON taglib_tag_relation(tenant_id);

-- Add comments for taglib tag relation table columns
COMMENT ON COLUMN taglib_tag_relation.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_tag_relation.object_type IS 'Type of the tagged object (e.g., PERSONNEL, DEPARTMENT)';
COMMENT ON COLUMN taglib_tag_relation.object_id IS 'ID of the tagged object';
COMMENT ON COLUMN taglib_tag_relation.tag_id IS 'Tag ID';
COMMENT ON COLUMN taglib_tag_relation.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_tag_relation.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_tag_relation.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Add triggers for taglib tables
CREATE TRIGGER update_taglib_category_updated_at BEFORE UPDATE ON taglib_category FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_taglib_tag_updated_at BEFORE UPDATE ON taglib_tag FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_taglib_tag_relation_updated_at BEFORE UPDATE ON taglib_tag_relation FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
