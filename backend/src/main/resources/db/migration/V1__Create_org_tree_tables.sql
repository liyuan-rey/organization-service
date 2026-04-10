-- V1__Create_org_tree_tables.sql
-- Flyway migration script for organization tree tables

-- =====================================================
-- 1. Create org_tree table
-- =====================================================
CREATE TABLE IF NOT EXISTS org_tree (
    id UUID NOT NULL,
    parent_id UUID NOT NULL,
    entity_type VARCHAR(20) NOT NULL,
    entity_id UUID NOT NULL,
    alias VARCHAR(100) NOT NULL DEFAULT '',
    level INTEGER NOT NULL DEFAULT 0,
    path UUID[] NOT NULL DEFAULT '{}',
    sort_rank VARCHAR(12) NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00',
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00',
    tenant_id UUID NOT NULL,
    CONSTRAINT pk_org_tree PRIMARY KEY (id)
);

-- Add comments
COMMENT ON TABLE org_tree IS 'Organization tree nodes';
COMMENT ON COLUMN org_tree.id IS 'Primary key, UUID v7';
COMMENT ON COLUMN org_tree.parent_id IS 'Parent node ID, virtual root points to itself';
COMMENT ON COLUMN org_tree.entity_type IS 'Entity type: ROOT, GROUP, DEPARTMENT, PERSONNEL';
COMMENT ON COLUMN org_tree.entity_id IS 'Associated business entity ID';
COMMENT ON COLUMN org_tree.alias IS 'Node alias, empty string means no alias';
COMMENT ON COLUMN org_tree.level IS 'Hierarchy level, virtual root is 0, business root is 1';
COMMENT ON COLUMN org_tree.path IS 'Path array from root to parent node';
COMMENT ON COLUMN org_tree.sort_rank IS 'LexoRank sort value';
COMMENT ON COLUMN org_tree.create_time IS 'Record creation time';
COMMENT ON COLUMN org_tree.update_time IS 'Record last update time';
COMMENT ON COLUMN org_tree.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- =====================================================
-- 2. Create indexes
-- =====================================================

-- Index for querying children with sort order
CREATE INDEX IF NOT EXISTS idx_org_tree_parent ON org_tree(parent_id, sort_rank);

-- Index for querying entity nodes
CREATE INDEX IF NOT EXISTS idx_org_tree_entity ON org_tree(entity_type, entity_id);

-- GIN index for path array queries
CREATE INDEX IF NOT EXISTS idx_org_tree_path_gin ON org_tree USING GIN (path);

-- Unique constraint to prevent duplicate nodes under same parent
ALTER TABLE org_tree
    ADD CONSTRAINT uk_org_tree_parent_entity
    UNIQUE (parent_id, entity_type, entity_id);

-- =====================================================
-- 3. Create trigger for auto-updating update_time
-- =====================================================

-- Create trigger function
CREATE OR REPLACE FUNCTION update_org_tree_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
CREATE TRIGGER update_org_tree_updated_at
    BEFORE UPDATE ON org_tree
    FOR EACH ROW
    EXECUTE FUNCTION update_org_tree_updated_at();

-- =====================================================
-- 4. Add removed column to business entity tables
-- =====================================================

-- Add removed column to org_group
ALTER TABLE org_group
    ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add removed column to org_department
ALTER TABLE org_department
    ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add removed column to org_personnel
ALTER TABLE org_personnel
    ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add comments
COMMENT ON COLUMN org_group.removed IS 'Logical delete flag';
COMMENT ON COLUMN org_department.removed IS 'Logical delete flag';
COMMENT ON COLUMN org_personnel.removed IS 'Logical delete flag';

-- =====================================================
-- 5. Create indexes for removed column
-- =====================================================

-- Index for filtering active groups
CREATE INDEX IF NOT EXISTS idx_org_group_removed ON org_group(removed);

-- Index for filtering active departments
CREATE INDEX IF NOT EXISTS idx_org_department_removed ON org_department(removed);

-- Index for filtering active personnel
CREATE INDEX IF NOT EXISTS idx_org_personnel_removed ON org_personnel(removed);

-- Composite index for tenant isolation and active status
CREATE INDEX IF NOT EXISTS idx_org_group_tenant_removed ON org_group(tenant_id, removed);
CREATE INDEX IF NOT EXISTS idx_org_department_tenant_removed ON org_department(tenant_id, removed);
CREATE INDEX IF NOT EXISTS idx_org_personnel_tenant_removed ON org_personnel(tenant_id, removed);
