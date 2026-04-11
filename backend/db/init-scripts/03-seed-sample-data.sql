-- ============================================================================
-- 示例数据初始化脚本
-- 包含：组织机构、组织树、职位关联、标签库的示例数据
-- 所有 INSERT 使用 ON CONFLICT DO NOTHING 确保幂等性
-- ============================================================================

-- ============================================================================
-- 一、组织机构基础数据
-- ============================================================================

-- 部门数据
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code) VALUES
    ('11111111-1111-1111-1111-111111111111', '人力资源部', 'Human Resources Department', 'HR', 'ORG-HR-001', '+1-555-0101', '+1-555-0102', 'hr@company.com', '北京市朝阳区某某街道123号', '100000'),
    ('22222222-2222-2222-2222-222222222222', '技术部', 'Technology Department', 'Tech', 'ORG-TECH-001', '+1-555-0103', '+1-555-0104', 'tech@company.com', '北京市海淀区某某科技园区456号', '100001'),
    ('33333333-3333-3333-3333-333333333333', '开发部', 'Development Department', 'Dev', 'ORG-DEV-001', '+1-555-0105', '+1-555-0106', 'dev@company.com', '北京市海淀区某某科技园区789号', '100002')
ON CONFLICT (id) DO NOTHING;

-- 人员数据
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email) VALUES
    ('44444444-4444-4444-4444-444444444444', '张三', 'M', '110101199001011234', '+1-555-0105', '+1-555-0106', '+1-555-0107', 'zhangsan@company.com'),
    ('55555555-5555-5555-5555-555555555555', '李四', 'F', '110101199001015678', '+1-555-0108', '+1-555-0109', '+1-555-0110', 'lisi@company.com'),
    ('66666666-6666-6666-6666-666666666666', '王五', 'M', '110101199001019012', '+1-555-0111', '+1-555-0112', '+1-555-0113', 'wangwu@company.com')
ON CONFLICT (id) DO NOTHING;

-- 分组数据
INSERT INTO org_group (id, name, description) VALUES
    ('77777777-7777-7777-7777-777777777777', '开发组', '负责软件开发工作'),
    ('88888888-8888-8888-8888-888888888888', '测试组', '负责软件测试工作'),
    ('99999999-9999-9999-9999-999999999999', '运维组', '负责基础设施和部署工作')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 二、职位与关联数据
-- ============================================================================

-- 职位数据
INSERT INTO org_position (id, name, code, description, job_level, job_category, min_salary, max_salary, status) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '人力资源经理', 'POS-HR-MGR-001', '负责人力资源管理工作', 'M2', 'Management', 15000.00, 25000.00, 1),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '软件工程师', 'POS-DEV-ENG-001', '负责软件开发工作', 'P2', 'Technical', 10000.00, 20000.00, 1),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '高级软件工程师', 'POS-DEV-SENR-001', '负责高级软件开发和架构设计', 'P3', 'Technical', 20000.00, 35000.00, 1),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '技术总监', 'POS-TECH-DIR-001', '负责技术团队管理和架构决策', 'M3', 'Management', 30000.00, 50000.00, 1),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '测试工程师', 'POS-QA-ENG-001', '负责软件测试和质量保证', 'P2', 'Technical', 8000.00, 15000.00, 1)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 三、关联关系数据
-- ============================================================================

-- 部门职位关联
-- 人力资源部 -> 人力资源经理
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES
    ('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', TRUE, 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 技术部 -> 软件工程师、高级软件工程师、技术总监
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES
    ('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, 1, '00000000-0000-0000-0000-000000000000'),
    ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', FALSE, 2, '00000000-0000-0000-0000-000000000000'),
    ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd', TRUE, 0, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 开发部 -> 软件工程师、测试工程师
INSERT INTO org_department_position (id, department_id, position_id, is_primary, sort_order, tenant_id) VALUES
    ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, 1, '00000000-0000-0000-0000-000000000000'),
    ('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', TRUE, 0, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 人员职位关联
-- 张三：人力资源经理（人力资源部）
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES
    ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 李四：软件工程师（技术部）
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES
    ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 王五：高级软件工程师 + 测试工程师（开发部）
INSERT INTO org_personnel_position (id, personnel_id, position_id, department_id, is_primary, start_date, status, tenant_id) VALUES
    ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', TRUE, '2024-01-01', 1, '00000000-0000-0000-0000-000000000000'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab', '66666666-6666-6666-6666-666666666666', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', FALSE, '2024-03-01', 1, '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 四、组织树数据
-- 基于 LexoRank 排序的统一组织树结构
-- ============================================================================

-- 虚拟根节点（指向自身）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000000',
    '00000000-0000-0000-0000-000000000000',
    'ROOT',
    '00000000-0000-0000-0000-000000000000',
    '虚拟根节点',
    0,
    '{}',
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (id) DO NOTHING;

-- Level 1：分组节点（挂载在虚拟根下）
-- 开发组
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'a0000000-0000-0000-0000-000000000000',
    '00000000-0000-0000-0000-000000000000',
    'GROUP',
    '77777777-7777-7777-7777-777777777777',
    '',
    1,
    ARRAY['a0000000-0000-0000-0000-000000000000']::UUID[],
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 测试组
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'b0000000-0000-0000-0000-000000000000',
    '00000000-0000-0000-0000-000000000000',
    'GROUP',
    '88888888-8888-8888-8888-888888888888',
    '',
    1,
    ARRAY['b0000000-0000-0000-0000-000000000000']::UUID[],
    'b0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 运维组
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'c0000000-0000-0000-0000-000000000000',
    '00000000-0000-0000-0000-000000000000',
    'GROUP',
    '99999999-9999-9999-9999-999999999999',
    '',
    1,
    ARRAY['c0000000-0000-0000-0000-000000000000']::UUID[],
    'c0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- Level 2：部门节点（挂载在分组下）
-- 人力资源部（开发组下）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'd0000000-0000-0000-0000-000000000000',
    'a0000000-0000-0000-0000-000000000000',
    'DEPARTMENT',
    '11111111-1111-1111-1111-111111111111',
    '人事部',
    2,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000000']::UUID[],
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 技术部（开发组下）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'e0000000-0000-0000-0000-000000000000',
    'a0000000-0000-0000-0000-000000000000',
    'DEPARTMENT',
    '22222222-2222-2222-2222-222222222222',
    '',
    2,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'e0000000-0000-0000-0000-000000000000']::UUID[],
    'b0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 开发部（开发组下）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    'f0000000-0000-0000-0000-000000000000',
    'a0000000-0000-0000-0000-000000000000',
    'DEPARTMENT',
    '33333333-3333-3333-3333-333333333333',
    '',
    2,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'f0000000-0000-0000-0000-000000000000']::UUID[],
    'c0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- Level 3：人员节点（挂载在部门下）
-- 张三（人力资源部）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '13000000-0000-0000-0000-000000000000',
    'd0000000-0000-0000-0000-000000000000',
    'PERSONNEL',
    '44444444-4444-4444-4444-444444444444',
    '',
    3,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000000', '13000000-0000-0000-0000-000000000000']::UUID[],
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 李四（技术部）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '14000000-0000-0000-0000-000000000000',
    'e0000000-0000-0000-0000-000000000000',
    'PERSONNEL',
    '55555555-5555-5555-5555-555555555555',
    '',
    3,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'e0000000-0000-0000-0000-000000000000', '14000000-0000-0000-0000-000000000000']::UUID[],
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- 王五（开发部）
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, create_time, update_time, tenant_id)
VALUES (
    '15000000-0000-0000-0000-000000000000',
    'f0000000-0000-0000-0000-000000000000',
    'PERSONNEL',
    '66666666-6666-6666-6666-666666666666',
    '',
    3,
    ARRAY['a0000000-0000-0000-0000-000000000000', 'f0000000-0000-0000-0000-000000000000', '15000000-0000-0000-0000-000000000000']::UUID[],
    'a0',
    '2000-01-01 00:00:00+00',
    '2000-01-01 00:00:00+00',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (parent_id, entity_type, entity_id) DO NOTHING;

-- ============================================================================
-- 五、标签库示例数据
-- ============================================================================

-- 标签分类
INSERT INTO taglib_category (id, name, description, sort_rank) VALUES
    ('fa000000-0000-0000-0000-000000000001', '技能标签', '人员技能相关标签分类', 'a0'),
    ('fa000000-0000-0000-0000-000000000002', '部门属性', '部门属性相关标签分类', 'b0'),
    ('fa000000-0000-0000-0000-000000000003', '组织标签', '组织/分组相关标签分类', 'c0')
ON CONFLICT (id) DO NOTHING;

-- 标签（技能标签分类下的树形结构）
INSERT INTO taglib_tag (id, name, category_id, parent_id, sort_rank) VALUES
    ('fb000000-0000-0000-0000-000000000001', '编程语言', 'fa000000-0000-0000-0000-000000000001', NULL, 'a0'),
    ('fb000000-0000-0000-0000-000000000002', 'Java', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'a0'),
    ('fb000000-0000-0000-0000-000000000003', 'Python', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'b0'),
    ('fb000000-0000-0000-0000-000000000004', 'Go', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'c0'),
    ('fb000000-0000-0000-0000-000000000005', '软技能', 'fa000000-0000-0000-0000-000000000001', NULL, 'b0'),
    ('fb000000-0000-0000-0000-000000000006', '沟通能力', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000005', 'a0'),
    ('fb000000-0000-0000-0000-000000000007', '团队协作', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000005', 'b0')
ON CONFLICT (id) DO NOTHING;

-- 标签（部门属性分类）
INSERT INTO taglib_tag (id, name, category_id, parent_id, sort_rank) VALUES
    ('fb000000-0000-0000-0000-000000000010', '核心部门', 'fa000000-0000-0000-0000-000000000002', NULL, 'a0'),
    ('fb000000-0000-0000-0000-000000000011', '支撑部门', 'fa000000-0000-0000-0000-000000000002', NULL, 'b0')
ON CONFLICT (id) DO NOTHING;

-- 标签关联
INSERT INTO taglib_tag_relation (id, object_type, object_id, tag_id) VALUES
    ('fc000000-0000-0000-0000-000000000001', 'PERSONNEL', '55555555-5555-5555-5555-555555555555', 'fb000000-0000-0000-0000-000000000002'),
    ('fc000000-0000-0000-0000-000000000002', 'PERSONNEL', '55555555-5555-5555-5555-555555555555', 'fb000000-0000-0000-0000-000000000006'),
    ('fc000000-0000-0000-0000-000000000003', 'DEPARTMENT', '22222222-2222-2222-2222-222222222222', 'fb000000-0000-0000-0000-000000000010')
ON CONFLICT (id) DO NOTHING;
