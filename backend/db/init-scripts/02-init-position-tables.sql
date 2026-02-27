-- Create position table (岗位表)
CREATE TABLE IF NOT EXISTS org_position (
    id UUID PRIMARY KEY, -- Primary key using 128-bit UUID v7 algorithm
    name VARCHAR(100) NOT NULL DEFAULT '', -- Position name
    code VARCHAR(50) NOT NULL DEFAULT '', -- Position code
    description VARCHAR(500) NOT NULL DEFAULT '', -- Position description
    job_level VARCHAR(50) NOT NULL DEFAULT '', -- Job level (e.g., P1, P2, M1, M2)
    job_category VARCHAR(50) NOT NULL DEFAULT '', -- Job category (e.g., Technical, Management, Sales)
    min_salary DECIMAL(12,2), -- Minimum salary
    max_salary DECIMAL(12,2), -- Maximum salary
    status INTEGER NOT NULL DEFAULT 1, -- Status: 1=Active, 0=Inactive
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' -- Tenant identifier for multi-tenant data isolation
);

-- Add indexes on position table
CREATE INDEX IF NOT EXISTS idx_org_position_name ON org_position(name);
CREATE INDEX IF NOT EXISTS idx_org_position_code ON org_position(code);
CREATE INDEX IF NOT EXISTS idx_org_position_job_level ON org_position(job_level);
CREATE INDEX IF NOT EXISTS idx_org_position_job_category ON org_position(job_category);
CREATE INDEX IF NOT EXISTS idx_org_position_status ON org_position(status);
CREATE INDEX IF NOT EXISTS idx_org_position_tenant_id ON org_position(tenant_id);

-- Add comments for position table columns
COMMENT ON COLUMN org_position.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN org_position.name IS 'Position name';
COMMENT ON COLUMN org_position.code IS 'Position code';
COMMENT ON COLUMN org_position.description IS 'Position description';
COMMENT ON COLUMN org_position.job_level IS 'Job level (e.g., P1, P2, M1, M2)';
COMMENT ON COLUMN org_position.job_category IS 'Job category (e.g., Technical, Management, Sales)';
COMMENT ON COLUMN org_position.min_salary IS 'Minimum salary';
COMMENT ON COLUMN org_position.max_salary IS 'Maximum salary';
COMMENT ON COLUMN org_position.status IS 'Status: 1=Active, 0=Inactive';
COMMENT ON COLUMN org_position.create_time IS 'Record creation time';
COMMENT ON COLUMN org_position.update_time IS 'Record last update time';
COMMENT ON COLUMN org_position.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create department-position relation table (部门 - 岗位关联表)
-- A department can have multiple positions
CREATE TABLE IF NOT EXISTS org_department_position (
    id UUID PRIMARY KEY, -- Primary key
    department_id UUID NOT NULL, -- Department ID
    position_id UUID NOT NULL, -- Position ID
    is_primary BOOLEAN NOT NULL DEFAULT FALSE, -- Is this the primary position for this department
    sort_order INTEGER NOT NULL DEFAULT 0, -- Sort order within the department
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- Tenant identifier
    UNIQUE (department_id, position_id) -- Prevent duplicate relations
);

-- Add indexes on department-position relation table
CREATE INDEX IF NOT EXISTS idx_org_dept_position_dept_id ON org_department_position(department_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_position_position_id ON org_department_position(position_id);
CREATE INDEX IF NOT EXISTS idx_org_dept_position_tenant_id ON org_department_position(tenant_id);

-- Add comments for department-position relation table columns
COMMENT ON COLUMN org_department_position.id IS 'Primary key';
COMMENT ON COLUMN org_department_position.department_id IS 'Department ID';
COMMENT ON COLUMN org_department_position.position_id IS 'Position ID';
COMMENT ON COLUMN org_department_position.is_primary IS 'Is this the primary position for this department';
COMMENT ON COLUMN org_department_position.sort_order IS 'Sort order within the department';
COMMENT ON COLUMN org_department_position.create_time IS 'Record creation time';
COMMENT ON COLUMN org_department_position.update_time IS 'Record last update time';
COMMENT ON COLUMN org_department_position.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create personnel-position relation table (人员 - 岗位关联表)
-- A person can have multiple positions, a position can be held by multiple people
CREATE TABLE IF NOT EXISTS org_personnel_position (
    id UUID PRIMARY KEY, -- Primary key
    personnel_id UUID NOT NULL, -- Personnel ID
    position_id UUID NOT NULL, -- Position ID
    department_id UUID, -- Department ID (the department where this position is held)
    is_primary BOOLEAN NOT NULL DEFAULT FALSE, -- Is this the primary position for this person
    start_date DATE, -- Start date of holding this position
    end_date DATE, -- End date of holding this position (null means current)
    status INTEGER NOT NULL DEFAULT 1, -- Status: 1=Active, 0=Inactive
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record creation time
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp, -- Record last update time
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000', -- Tenant identifier
    UNIQUE (personnel_id, position_id, department_id) -- Prevent duplicate relations
);

-- Add indexes on personnel-position relation table
CREATE INDEX IF NOT EXISTS idx_org_person_position_person_id ON org_personnel_position(personnel_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_position_id ON org_personnel_position(position_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_dept_id ON org_personnel_position(department_id);
CREATE INDEX IF NOT EXISTS idx_org_person_position_status ON org_personnel_position(status);
CREATE INDEX IF NOT EXISTS idx_org_person_position_tenant_id ON org_personnel_position(tenant_id);

-- Add comments for personnel-position relation table columns
COMMENT ON COLUMN org_personnel_position.id IS 'Primary key';
COMMENT ON COLUMN org_personnel_position.personnel_id IS 'Personnel ID';
COMMENT ON COLUMN org_personnel_position.position_id IS 'Position ID';
COMMENT ON COLUMN org_personnel_position.department_id IS 'Department ID (the department where this position is held)';
COMMENT ON COLUMN org_personnel_position.is_primary IS 'Is this the primary position for this person';
COMMENT ON COLUMN org_personnel_position.start_date IS 'Start date of holding this position';
COMMENT ON COLUMN org_personnel_position.end_date IS 'End date of holding this position (null means current)';
COMMENT ON COLUMN org_personnel_position.status IS 'Status: 1=Active, 0=Inactive';
COMMENT ON COLUMN org_personnel_position.create_time IS 'Record creation time';
COMMENT ON COLUMN org_personnel_position.update_time IS 'Record last update time';
COMMENT ON COLUMN org_personnel_position.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Add trigger for position table
CREATE TRIGGER update_org_position_updated_at BEFORE UPDATE ON org_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_department_position_updated_at BEFORE UPDATE ON org_department_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_org_personnel_position_updated_at BEFORE UPDATE ON org_personnel_position FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();

-- Insert sample data for position
INSERT INTO org_position (id, name, code, description, job_level, job_category, min_salary, max_salary, status) VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '人力资源经理', 'POS-HR-MGR-001', '负责人力资源管理工作', 'M2', 'Management', 15000.00, 25000.00, 1),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '软件工程师', 'POS-DEV-ENG-001', '负责软件开发工作', 'P2', 'Technical', 10000.00, 20000.00, 1),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '高级软件工程师', 'POS-DEV-SENR-001', '负责高级软件开发和架构设计', 'P3', 'Technical', 20000.00, 35000.00, 1),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '技术总监', 'POS-TECH-DIR-001', '负责技术团队管理和架构决策', 'M3', 'Management', 30000.00, 50000.00, 1),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '测试工程师', 'POS-QA-ENG-001', '负责软件测试和质量保证', 'P2', 'Technical', 8000.00, 15000.00, 1)
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for department-position relations
-- HR department has HR Manager position
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES 
    ('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', TRUE, 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- Technology department has Software Engineer, Senior Software Engineer, and Tech Director positions
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES 
    ('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, 1, '00000000-0000-0000-0000-000000000000'),
    ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', FALSE, 2, '00000000-0000-0000-0000-000000000000'),
    ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd', TRUE, 0, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- Development department has Software Engineer and QA Engineer positions
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES 
    ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, 1, '00000000-0000-0000-0000-000000000000'),
    ('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', TRUE, 0, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- Insert sample data for personnel-position relations
-- Zhang San is HR Manager in HR department
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES 
    ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- Li Si is Software Engineer in Technology department
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES 
    ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- Wang Wu is Senior Software Engineer and also QA Engineer in Development department
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES 
    ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab', '66666666-6666-6666-6666-666666666666', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', FALSE, '2024-03-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;
