-- V2__Insert_org_tree_sample_data.sql
-- Flyway migration script for inserting sample organization tree data

-- =====================================================
-- 1. Insert virtual root node
-- =====================================================
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000000',  -- id (virtual root)
    '00000000-0000-0000-0000-000000000000',  -- parent_id (points to itself)
    'ROOT',                                   -- entity_type
    '00000000-0000-0000-0000-000000000000',  -- entity_id
    '虚拟根节点',                              -- alias
    0,                                        -- level
    '{}',                                     -- path (empty array)
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 2. Insert sample Group data
-- =====================================================

-- 总公司 (Headquarters)
INSERT INTO org_group (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '10000000-0000-0000-0000-000000000000',  -- id
    '总公司',                                  -- name
    'Headquarters',                           -- english_name
    'HQ',                                     -- short_name
    'GRP-001',                                -- org_code
    '010-88888888',                           -- phone
    '010-88888889',                           -- fax
    'hq@example.com',                         -- email
    '北京市朝阳区 XX 路 XX 号',                  -- address
    '100000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 华东分公司 (East China Branch)
INSERT INTO org_group (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '20000000-0000-0000-0000-000000000000',  -- id
    '华东分公司',                              -- name
    'East China Branch',                      -- english_name
    'ECB',                                    -- short_name
    'GRP-002',                                -- org_code
    '021-66666666',                           -- phone
    '021-66666667',                           -- fax
    'ecb@example.com',                        -- email
    '上海市浦东新区 XX 路 XX 号',                -- address
    '200000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 华北分公司 (North China Branch)
INSERT INTO org_group (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '30000000-0000-0000-0000-000000000000',  -- id
    '华北分公司',                              -- name
    'North China Branch',                     -- english_name
    'NCB',                                    -- short_name
    'GRP-003',                                -- org_code
    '010-55555555',                           -- phone
    '010-55555556',                           -- fax
    'ncb@example.com',                        -- email
    '北京市海淀区 XX 路 XX 号',                 -- address
    '100080',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 3. Insert sample Department data
-- =====================================================

-- 人力资源部 (HR Department) - under Headquarters
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '40000000-0000-0000-0000-000000000000',  -- id
    '人力资源部',                              -- name
    'Human Resources Department',             -- english_name
    'HR',                                     -- short_name
    'DEPT-001',                               -- org_code
    '010-88888801',                           -- phone
    '010-88888802',                           -- fax
    'hr@example.com',                         -- email
    '北京市朝阳区 XX 路 XX 号',                 -- address
    '100000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 财务部 (Finance Department) - under Headquarters
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '50000000-0000-0000-0000-000000000000',  -- id
    '财务部',                                  -- name
    'Finance Department',                     -- english_name
    'FD',                                     -- short_name
    'DEPT-002',                               -- org_code
    '010-88888803',                           -- phone
    '010-88888804',                           -- fax
    'finance@example.com',                    -- email
    '北京市朝阳区 XX 路 XX 号',                 -- address
    '100000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 技术部 (Technology Department) - under Headquarters
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '60000000-0000-0000-0000-000000000000',  -- id
    '技术部',                                  -- name
    'Technology Department',                  -- english_name
    'TD',                                     -- short_name
    'DEPT-003',                               -- org_code
    '010-88888805',                           -- phone
    '010-88888806',                           -- fax
    'tech@example.com',                       -- email
    '北京市朝阳区 XX 路 XX 号',                 -- address
    '100000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 上海研发中心 (Shanghai R&D Center) - under East China Branch
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '70000000-0000-0000-0000-000000000000',  -- id
    '上海研发中心',                            -- name
    'Shanghai R&D Center',                    -- english_name
    'SH-R&D',                                 -- short_name
    'DEPT-004',                               -- org_code
    '021-66666601',                           -- phone
    '021-66666602',                           -- fax
    'sh-rd@example.com',                      -- email
    '上海市浦东新区 XX 路 XX 号',               -- address
    '200000',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- 北京研发中心 (Beijing R&D Center) - under North China Branch
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, created_at, updated_at, tenant_id, removed)
VALUES (
    '80000000-0000-0000-0000-000000000000',  -- id
    '北京研发中心',                            -- name
    'Beijing R&D Center',                     -- english_name
    'BJ-R&D',                                 -- short_name
    'DEPT-005',                               -- org_code
    '010-55555501',                           -- phone
    '010-55555502',                           -- fax
    'bj-rd@example.com',                      -- email
    '北京市海淀区 XX 路 XX 号',                -- address
    '100080',                                 -- postal_code
    '2000-01-01 00:00:00+00',                 -- created_at
    '2000-01-01 00:00:00+00',                 -- updated_at
    '00000000-0000-0000-0000-000000000000',   -- tenant_id
    FALSE                                     -- removed
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 4. Insert sample Personnel data
-- =====================================================

-- 张三 (Zhang San)
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, photo, created_at, updated_at, tenant_id, removed)
VALUES (
    '91000000-0000-0000-0000-000000000000',  -- id
    '张三',                                   -- name
    'M',                                     -- gender
    '110101199001011234',                    -- id_card
    '13800138001',                           -- mobile
    '010-88888811',                          -- telephone
    '010-88888812',                          -- fax
    'zhangsan@example.com',                  -- email
    NULL,                                    -- photo
    '2000-01-01 00:00:00+00',                -- created_at
    '2000-01-01 00:00:00+00',                -- updated_at
    '00000000-0000-0000-0000-000000000000',  -- tenant_id
    FALSE                                    -- removed
) ON CONFLICT (id) DO NOTHING;

-- 李四 (Li Si)
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, photo, created_at, updated_at, tenant_id, removed)
VALUES (
    '92000000-0000-0000-0000-000000000000',  -- id
    '李四',                                   -- name
    'M',                                     -- gender
    '110101199002022345',                    -- id_card
    '13800138002',                           -- mobile
    '010-88888821',                          -- telephone
    '010-88888822',                          -- fax
    'lisi@example.com',                      -- email
    NULL,                                    -- photo
    '2000-01-01 00:00:00+00',                -- created_at
    '2000-01-01 00:00:00+00',                -- updated_at
    '00000000-0000-0000-0000-000000000000',  -- tenant_id
    FALSE                                    -- removed
) ON CONFLICT (id) DO NOTHING;

-- 王五 (Wang Wu)
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, photo, created_at, updated_at, tenant_id, removed)
VALUES (
    '93000000-0000-0000-0000-000000000000',  -- id
    '王五',                                   -- name
    'F',                                     -- gender
    '110101199003033456',                    -- id_card
    '13800138003',                           -- mobile
    '021-66666631',                          -- telephone
    '021-66666632',                          -- fax
    'wangwu@example.com',                    -- email
    NULL,                                    -- photo
    '2000-01-01 00:00:00+00',                -- created_at
    '2000-01-01 00:00:00+00',                -- updated_at
    '00000000-0000-0000-0000-000000000000',  -- tenant_id
    FALSE                                    -- removed
) ON CONFLICT (id) DO NOTHING;

-- 赵六 (Zhao Liu)
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, photo, created_at, updated_at, tenant_id, removed)
VALUES (
    '94000000-0000-0000-0000-000000000000',  -- id
    '赵六',                                   -- name
    'M',                                     -- gender
    '110101199004044567',                    -- id_card
    '13800138004',                           -- mobile
    '021-66666641',                          -- telephone
    '021-66666642',                          -- fax
    'zhaoliu@example.com',                   -- email
    NULL,                                    -- photo
    '2000-01-01 00:00:00+00',                -- created_at
    '2000-01-01 00:00:00+00',                -- updated_at
    '00000000-0000-0000-0000-000000000000',  -- tenant_id
    FALSE                                    -- removed
) ON CONFLICT (id) DO NOTHING;

-- 钱七 (Qian Qi)
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, photo, created_at, updated_at, tenant_id, removed)
VALUES (
    '95000000-0000-0000-0000-000000000000',  -- id
    '钱七',                                   -- name
    'F',                                     -- gender
    '110101199005055678',                    -- id_card
    '13800138005',                           -- mobile
    '021-66666651',                          -- telephone
    '021-66666652',                          -- fax
    'qianqi@example.com',                    -- email
    NULL,                                    -- photo
    '2000-01-01 00:00:00+00',                -- created_at
    '2000-01-01 00:00:00+00',                -- updated_at
    '00000000-0000-0000-0000-000000000000',  -- tenant_id
    FALSE                                    -- removed
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 5. Insert tree structure data
-- =====================================================

-- Level 1: Groups under virtual root
-- 总公司 (Headquarters)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'a0000000-0000-0000-0000-000000000000',  -- id (tree node id)
    '00000000-0000-0000-0000-000000000000',  -- parent_id (virtual root)
    'GROUP',                                  -- entity_type
    '10000000-0000-0000-0000-000000000000',  -- entity_id (Headquarters)
    '',                                       -- alias
    1,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 华东分公司 (East China Branch)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'b0000000-0000-0000-0000-000000000000',  -- id
    '00000000-0000-0000-0000-000000000000',  -- parent_id (virtual root)
    'GROUP',                                  -- entity_type
    '20000000-0000-0000-0000-000000000000',  -- entity_id (East China Branch)
    '',                                       -- alias
    1,                                        -- level
    ARRAY['b00000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'b0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 华北分公司 (North China Branch)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'c0000000-0000-0000-0000-000000000000',  -- id
    '00000000-0000-0000-0000-000000000000',  -- parent_id (virtual root)
    'GROUP',                                  -- entity_type
    '30000000-0000-0000-0000-000000000000',  -- entity_id (North China Branch)
    '',                                       -- alias
    1,                                        -- level
    ARRAY['c00000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'c0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- Level 2: Departments under Headquarters
-- 人力资源部 (HR Department)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'd0000000-0000-0000-0000-000000000000',  -- id
    'a0000000-0000-0000-0000-000000000000',  -- parent_id (Headquarters)
    'DEPARTMENT',                             -- entity_type
    '40000000-0000-0000-0000-000000000000',  -- entity_id (HR Department)
    '人事部',                                 -- alias
    2,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 财务部 (Finance Department)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'e0000000-0000-0000-0000-000000000000',  -- id
    'a0000000-0000-0000-0000-000000000000',  -- parent_id (Headquarters)
    'DEPARTMENT',                             -- entity_type
    '50000000-0000-0000-0000-000000000000',  -- entity_id (Finance Department)
    '',                                       -- alias
    2,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000', 'e0000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'b0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 技术部 (Technology Department)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'f0000000-0000-0000-0000-000000000000',  -- id
    'a0000000-0000-0000-0000-000000000000',  -- parent_id (Headquarters)
    'DEPARTMENT',                             -- entity_type
    '60000000-0000-0000-0000-000000000000',  -- entity_id (Technology Department)
    '',                                       -- alias
    2,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000', 'f0000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'c0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- Level 2: Departments under branches
-- 上海研发中心 (Shanghai R&D Center) under East China Branch
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '11000000-0000-0000-0000-000000000000',  -- id
    'b0000000-0000-0000-0000-000000000000',  -- parent_id (East China Branch)
    'DEPARTMENT',                             -- entity_type
    '70000000-0000-0000-0000-000000000000',  -- entity_id (Shanghai R&D Center)
    '',                                       -- alias
    2,                                        -- level
    ARRAY['b00000000-0000-0000-0000-000000000000', '11000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 北京研发中心 (Beijing R&D Center) under North China Branch
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '12000000-0000-0000-0000-000000000000',  -- id
    'c0000000-0000-0000-0000-000000000000',  -- parent_id (North China Branch)
    'DEPARTMENT',                             -- entity_type
    '80000000-0000-0000-0000-000000000000',  -- entity_id (Beijing R&D Center)
    '',                                       -- alias
    2,                                        -- level
    ARRAY['c00000000-0000-0000-0000-000000000000', '12000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- Level 3: Personnel under departments
-- 张三 (Zhang San) under HR Department
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '13000000-0000-0000-0000-000000000000',  -- id
    'd0000000-0000-0000-0000-000000000000',  -- parent_id (HR Department)
    'PERSONNEL',                              -- entity_type
    '91000000-0000-0000-0000-000000000000',  -- entity_id (Zhang San)
    '',                                       -- alias
    3,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000000', '13000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 李四 (Li Si) under Technology Department
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '14000000-0000-0000-0000-000000000000',  -- id
    'f0000000-0000-0000-0000-000000000000',  -- parent_id (Technology Department)
    'PERSONNEL',                              -- entity_type
    '92000000-0000-0000-0000-000000000000',  -- entity_id (Li Si)
    '',                                       -- alias
    3,                                        -- level
    ARRAY['a00000000-0000-0000-0000-000000000000', 'f0000000-0000-0000-0000-000000000000', '14000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 王五 (Wang Wu) under Shanghai R&D Center
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '15000000-0000-0000-0000-000000000000',  -- id
    '11000000-0000-0000-0000-000000000000',  -- parent_id (Shanghai R&D Center)
    'PERSONNEL',                              -- entity_type
    '93000000-0000-0000-0000-000000000000',  -- entity_id (Wang Wu)
    '上海负责人',                              -- alias
    3,                                        -- level
    ARRAY['b00000000-0000-0000-0000-000000000000', '11000000-0000-0000-0000-000000000000', '15000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 王五 (Wang Wu) under Beijing R&D Center (Multi-assignment example)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '16000000-0000-0000-0000-000000000000',  -- id
    '12000000-0000-0000-0000-000000000000',  -- parent_id (Beijing R&D Center)
    'PERSONNEL',                              -- entity_type
    '93000000-0000-0000-0000-000000000000',  -- entity_id (Wang Wu - same as above)
    '北京项目负责人',                          -- alias
    3,                                        -- level
    ARRAY['c00000000-0000-0000-0000-000000000000', '12000000-0000-0000-0000-000000000000', '16000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'a0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 赵六 (Zhao Liu) under Shanghai R&D Center
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '17000000-0000-0000-0000-000000000000',  -- id
    '11000000-0000-0000-0000-000000000000',  -- parent_id (Shanghai R&D Center)
    'PERSONNEL',                              -- entity_type
    '94000000-0000-0000-0000-000000000000',  -- entity_id (Zhao Liu)
    '',                                       -- alias
    3,                                        -- level
    ARRAY['b00000000-0000-0000-0000-000000000000', '11000000-0000-0000-0000-000000000000', '17000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'b0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 钱七 (Qian Qi) under Shanghai R&D Center
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '18000000-0000-0000-0000-000000000000',  -- id
    '11000000-0000-0000-0000-000000000000',  -- parent_id (Shanghai R&D Center)
    'PERSONNEL',                              -- entity_type
    '95000000-0000-0000-0000-000000000000',  -- entity_id (Qian Qi)
    '',                                       -- alias
    3,                                        -- level
    ARRAY['b00000000-0000-0000-0000-000000000000', '11000000-0000-0000-0000-000000000000', '18000000-0000-0000-0000-000000000000']::UUID[],  -- path
    'c0',                                     -- sort_rank
    '2000-01-01 00:00:00+00',                 -- create_time
    '2000-01-01 00:00:00+00',                 -- update_time
    '00000000-0000-0000-0000-000000000000'   -- tenant_id
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;
