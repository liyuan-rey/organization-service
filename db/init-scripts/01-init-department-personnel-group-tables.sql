-- Create department table
CREATE TABLE IF NOT EXISTS b_org_department (
    id UUID PRIMARY KEY, -- Primary key using UUID v7 algorithm
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

-- Add indexes on department table
CREATE INDEX IF NOT EXISTS idx_b_org_department_name ON b_org_department(name);
CREATE INDEX IF NOT EXISTS idx_b_org_department_org_code ON b_org_department(org_code);
CREATE INDEX IF NOT EXISTS idx_b_org_department_tenant_id ON b_org_department(tenant_id);

-- Create personnel table
CREATE TABLE IF NOT EXISTS b_org_personnel (
    id UUID PRIMARY KEY, -- Primary key using UUID v7 algorithm
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

-- Add indexes on personnel table
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_name ON b_org_personnel(name);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_id_card ON b_org_personnel(id_card);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_mobile ON b_org_personnel(mobile);
CREATE INDEX IF NOT EXISTS idx_b_org_personnel_tenant_id ON b_org_personnel(tenant_id);

-- Create group table
CREATE TABLE IF NOT EXISTS b_org_group (
    id UUID PRIMARY KEY, -- Primary key using UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Group name
    description TEXT NOT NULL DEFAULT '', -- Group description
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record creation time
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00', -- Record last update time
    tenant_id UUID NOT NULL DEFAULT 0 -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on group table
CREATE INDEX IF NOT EXISTS idx_b_org_group_name ON b_org_group(name);
CREATE INDEX IF NOT EXISTS idx_b_org_group_tenant_id ON b_org_group(tenant_id);

-- Create universal entity relation table
CREATE TABLE IF NOT EXISTS r_org_entity_relation (
    id UUID PRIMARY KEY, -- Primary key using UUID v7 algorithm
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

-- Add indexes on entity relation table
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_source ON r_org_entity_relation(source_type, source_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_target ON r_org_entity_relation(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_type ON r_org_entity_relation(relation_type);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_tenant ON r_org_entity_relation(tenant_id);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_path ON r_org_entity_relation(path);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_level ON r_org_entity_relation(level);
CREATE INDEX IF NOT EXISTS idx_r_org_entity_relation_composite ON r_org_entity_relation(source_type, target_type, relation_type);

-- Insert sample data for department
-- Note: In a real application, UUIDs would be generated by the application
INSERT INTO b_org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code) VALUES 
    ('10000000-0000-0000-0000-000000000001', 'Human Resources Department', 'Human Resources Department', 'HR', 'ORG-HR-001', '+1-555-0101', '+1-555-0102', 'hr@company.com', '123 Main Street, New York, NY 10001', '10001'),
    ('10000000-0000-0000-0000-000000000002', 'Technology Department', 'Technology Department', 'Tech', 'ORG-TECH-001', '+1-555-0103', '+1-555-0104', 'tech@company.com', '456 Innovation Blvd, San Francisco, CA 94102', '94102'),
    ('10000000-0000-0000-0000-000000000003', 'Development Division', 'Development Division', 'Dev', 'ORG-DEV-001', '+1-555-0105', '+1-555-0106', 'dev@company.com', '789 Code Street, San Francisco, CA 94103', '94103')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for personnel
INSERT INTO b_org_personnel (id, name, gender, id_card, mobile, telephone, fax, email) VALUES 
    ('20000000-0000-0000-0000-000000000001', 'John Smith', 'M', 'S123456789', '+1-555-0105', '+1-555-0106', '+1-555-0107', 'john.smith@company.com'),
    ('20000000-0000-0000-0000-000000000002', 'Jane Doe', 'F', 'D987654321', '+1-555-0108', '+1-555-0109', '+1-555-0110', 'jane.doe@company.com'),
    ('20000000-0000-0000-0000-000000000003', 'Bob Johnson', 'M', 'J456789123', '+1-555-0111', '+1-555-0112', '+1-555-0113', 'bob.johnson@company.com')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for group
INSERT INTO b_org_group (id, name, description) VALUES 
    ('30000000-0000-0000-0000-000000000001', 'Development Team', 'Responsible for software development'),
    ('30000000-0000-0000-0000-000000000002', 'QA Team', 'Responsible for software testing'),
    ('30000000-0000-0000-0000-000000000003', 'DevOps Team', 'Responsible for infrastructure and deployment')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for entity relations
-- Department hierarchy relationships
INSERT INTO r_org_entity_relation (id, source_type, source_id, target_type, target_id, relation_type, level, path, sort_order, is_direct) VALUES
    ('40000000-0000-0000-0000-000000000001', 'department', '10000000-0000-0000-0000-000000000001', 'department', '10000000-0000-0000-0000-000000000002', 'parent_of', 1, '1', 1, true), -- HR department is parent of Tech department
    ('40000000-0000-0000-0000-000000000002', 'department', '10000000-0000-0000-0000-000000000002', 'department', '10000000-0000-0000-0000-000000000003', 'parent_of', 2, '1,2', 1, true); -- Tech department is parent of Dev division

-- Personnel-department relationships
INSERT INTO r_org_entity_relation (id, source_type, source_id, target_type, target_id, relation_type, attributes) VALUES
    ('40000000-0000-0000-0000-000000000003', 'personnel', '20000000-0000-0000-0000-000000000001', 'department', '10000000-0000-0000-0000-000000000001', 'belongs_to', '{"position": "HR Manager", "is_primary": true}'),
    ('40000000-0000-0000-0000-000000000004', 'personnel', '20000000-0000-0000-0000-000000000002', 'department', '10000000-0000-0000-0000-000000000002', 'belongs_to', '{"position": "Tech Lead", "is_primary": true}'),
    ('40000000-0000-0000-0000-000000000005', 'personnel', '20000000-0000-0000-0000-000000000003', 'department', '10000000-0000-0000-0000-000000000003', 'belongs_to', '{"position": "Developer", "is_primary": true}');

-- Personnel-group relationships
INSERT INTO r_org_entity_relation (id, source_type, source_id, target_type, target_id, relation_type, attributes) VALUES
    ('40000000-0000-0000-0000-000000000006', 'personnel', '20000000-0000-0000-0000-000000000002', 'group', '30000000-0000-0000-0000-000000000001', 'member_of', '{"role": "Team Lead"}'),
    ('40000000-0000-0000-0000-000000000007', 'personnel', '20000000-0000-0000-0000-000000000003', 'group', '30000000-0000-0000-0000-000000000001', 'member_of', '{"role": "Developer"}'),
    ('40000000-0000-0000-0000-000000000008', 'personnel', '20000000-0000-0000-0000-000000000003', 'group', '30000000-0000-0000-0000-000000000003', 'member_of', '{"role": "DevOps Engineer"}');

-- Department-group relationships
INSERT INTO r_org_entity_relation (id, source_type, source_id, target_type, target_id, relation_type, attributes) VALUES
    ('40000000-0000-0000-0000-000000000009', 'department', '10000000-0000-0000-0000-000000000002', 'group', '30000000-0000-0000-0000-000000000001', 'contains', '{"function": "Software Development"}'),
    ('40000000-0000-0000-0000-000000000010', 'department', '10000000-0000-0000-0000-000000000002', 'group', '30000000-0000-0000-0000-000000000002', 'contains', '{"function": "Quality Assurance"}'),
    ('40000000-0000-0000-0000-000000000011', 'department', '10000000-0000-0000-0000-000000000003', 'group', '30000000-0000-0000-0000-000000000003', 'contains', '{"function": "Infrastructure"}');