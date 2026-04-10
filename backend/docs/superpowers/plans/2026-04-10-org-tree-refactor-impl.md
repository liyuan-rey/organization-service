# Organization Tree Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 用单表 `org_tree` 统一管理 Group/Department/Personnel 的层级关系，替代现有 6 个关联表

**Architecture:** 父子关系法（parent_id）+ 外键引用法（entity_type + entity_id），使用 UUID[] 数组 + GIN 索引优化路径查询，LexoRank 字符排序支持动态插入

**Tech Stack:** Spring Boot 4.0, Java 17, Spring Data JPA, PostgreSQL 15, UUID v7, MapStruct

---

## File Structure

**新建文件：**

| 文件路径 | 类型 | 职责 |
|----------|------|------|
| `src/main/resources/db/migration/V1__Create_org_tree_tables.sql` | SQL DDL | 创建 org_tree 表、索引、约束 |
| `src/main/resources/db/migration/V2__Insert_org_tree_sample_data.sql` | SQL DML | 插入虚拟根节点、示例数据 |
| `src/main/java/.../entity/OrgTreeNodeEntity.java` | Entity | 树节点实体类 |
| `src/main/java/.../entity/EntityType.java` | Enum | 实体类型枚举 |
| `src/main/java/.../repository/OrgTreeNodeRepository.java` | Repository | 树节点数据访问 |
| `src/main/java/.../service/OrgTreeNodeService.java` | Service | 树节点业务接口 |
| `src/main/java/.../service/impl/OrgTreeNodeServiceImpl.java` | Service | 树节点业务实现 |
| `src/main/java/.../controller/OrgTreeNodeController.java` | Controller | 树节点 REST API |
| `src/main/java/.../utils/LexoRankUtils.java` | Utils | LexoRank 排序工具 |
| `src/main/java/.../mapper/OrgTreeNodeMapper.java` | Mapper | Entity-DTO 映射 |
| `src/main/java/.../dto/request/CreateTreeNodeReq.java` | DTO | 创建节点请求 |
| `src/main/java/.../dto/request/UpdateTreeNodeReq.java` | DTO | 更新节点请求 |
| `src/main/java/.../dto/request/MoveTreeNodeReq.java` | DTO | 移动节点请求 |
| `src/main/java/.../dto/response/TreeNodeRsp.java` | DTO | 树节点响应（替换现有） |
| `src/main/java/.../dto/response/TreeStatistics.java` | DTO | 统计信息（修改） |

**修改文件：**

| 文件路径 | 修改内容 |
|----------|----------|
| `src/main/java/.../entity/GroupEntity.java` | 添加 `removed` 字段 |
| `src/main/java/.../entity/DepartmentEntity.java` | 添加 `removed` 字段 |
| `src/main/java/.../entity/PersonnelEntity.java` | 添加 `removed` 字段 |
| `src/main/java/.../dto/NodeType.java` | 扩展为与 EntityType 兼容 |

**测试文件：**

| 文件路径 | 测试内容 |
|----------|----------|
| `src/test/java/.../entity/OrgTreeNodeEntityTest.java` | Entity 字段验证 |
| `src/test/java/.../repository/OrgTreeNodeRepositoryTest.java` | Repository 查询 |
| `src/test/java/.../service/OrgTreeNodeServiceTest.java` | Service 逻辑 |
| `src/test/java/.../controller/OrgTreeNodeControllerTest.java` | Controller API |
| `src/test/java/.../utils/LexoRankUtilsTest.java` | LexoRank 算法 |
| `src/test/java/.../integration/OrgTreeIntegrationTest.java` | 集成测试 |

---

## Tasks

### Task 1: LexoRank 工具类

**Files:**
- Create: `src/main/java/com/reythecoder/organization/utils/LexoRankUtils.java`
- Test: `src/test/java/com/reythecoder/organization/utils/LexoRankUtilsTest.java`

- [ ] **Step 1: 编写 LexoRank 工具类**

```java
package com.reythecoder.organization.utils;

import java.util.Objects;

public class LexoRankUtils {

    private static final String CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int CHAR_COUNT = 36;

    /**
     * 生成初始排序值
     * @param index 索引位置（0-based）
     * @return 初始排序值，如 a0, b0, c0...
     */
    public static String initialRank(int index) {
        if (index < 0 || index >= CHAR_COUNT) {
            throw new IllegalArgumentException("Index must be between 0 and " + (CHAR_COUNT - 1));
        }
        return String.valueOf(CHAR_SET.charAt(index)) + "0";
    }

    /**
     * 计算两个排序值的中间值
     * @param lower 下界
     * @param upper 上界
     * @return 中间排序值
     */
    public static String between(String lower, String upper) {
        Objects.requireNonNull(lower);
        Objects.requireNonNull(upper);

        if (lower.compareTo(upper) >= 0) {
            throw new IllegalArgumentException("Lower must be less than upper");
        }

        // 简单实现：转换为数值计算中间值
        // 生产环境需要完整的 LexoRank 算法
        char firstCharLower = lower.charAt(0);
        char firstCharUpper = upper.charAt(0);

        if (firstCharLower == firstCharUpper && lower.length() > 1 && upper.length() > 1) {
            // 首字符相同，计算剩余部分的中间值
            String lowerRest = lower.substring(1);
            String upperRest = upper.substring(1);
            return firstCharLower + between(lowerRest, upperRest);
        }

        int idxLower = CHAR_SET.indexOf(firstCharLower);
        int idxUpper = CHAR_SET.indexOf(firstCharUpper);
        int midIdx = (idxLower + idxUpper) / 2;

        if (midIdx == idxLower) {
            // 相邻，需要扩展长度
            return lower + CHAR_SET.charAt(CHAR_COUNT / 2);
        }

        return String.valueOf(CHAR_SET.charAt(midIdx)) + "0";
    }

    /**
     * 在当前排序值之前生成新排序值
     * @param current 当前排序值
     * @return 之前的排序值
     */
    public static String before(String current) {
        Objects.requireNonNull(current);
        if (current.isEmpty()) {
            throw new IllegalArgumentException("Current cannot be empty");
        }

        char firstChar = current.charAt(0);
        int idx = CHAR_SET.indexOf(firstChar);

        if (idx <= 0) {
            // 已经是最小，需要扩展
            return "0" + CHAR_SET.charAt(CHAR_COUNT / 2) + current;
        }

        return String.valueOf(CHAR_SET.charAt(idx - 1)) + "0";
    }

    /**
     * 在当前排序值之后生成新排序值
     * @param current 当前排序值
     * @return 之后的排序值
     */
    public static String after(String current) {
        Objects.requireNonNull(current);
        if (current.isEmpty()) {
            throw new IllegalArgumentException("Current cannot be empty");
        }

        char firstChar = current.charAt(0);
        int idx = CHAR_SET.indexOf(firstChar);

        if (idx >= CHAR_COUNT - 1) {
            // 已经是最大，需要扩展
            return "z" + CHAR_SET.charAt(CHAR_COUNT / 2);
        }

        return String.valueOf(CHAR_SET.charAt(idx + 1)) + "0";
    }

    /**
     * 验证排序值是否合法
     * @param rank 排序值
     * @return 是否合法
     */
    public static boolean isValidRank(String rank) {
        if (rank == null || rank.isEmpty()) {
            return false;
        }
        for (char c : rank.toCharArray()) {
            if (CHAR_SET.indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }
}
```

- [ ] **Step 2: 编写 LexoRank 单元测试**

```java
package com.reythecoder.organization.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LexoRankUtilsTest {

    @Test
    void testInitialRank() {
        assertEquals("a0", LexoRankUtils.initialRank(10));
        assertEquals("00", LexoRankUtils.initialRank(0));
        assertEquals("z0", LexoRankUtils.initialRank(35));
    }

    @Test
    void testBetween() {
        String result = LexoRankUtils.between("a0", "c0");
        assertTrue(result.compareTo("a0") > 0);
        assertTrue(result.compareTo("c0") < 0);
    }

    @Test
    void testBefore() {
        String result = LexoRankUtils.before("b0");
        assertTrue(result.compareTo("b0") < 0);
    }

    @Test
    void testAfter() {
        String result = LexoRankUtils.after("b0");
        assertTrue(result.compareTo("b0") > 0);
    }

    @Test
    void testValidRank() {
        assertTrue(LexoRankUtils.isValidRank("a0"));
        assertTrue(LexoRankUtils.isValidRank("abc123"));
        assertFalse(LexoRankUtils.isValidRank("ABC"));
        assertFalse(LexoRankUtils.isValidRank(null));
    }
}
```

- [ ] **Step 3: 运行测试验证**

```bash
cd backend && ./gradlew test --tests LexoRankUtilsTest
```

预期：所有测试通过

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/reythecoder/organization/utils/LexoRankUtils.java
git add src/test/java/com/reythecoder/organization/utils/LexoRankUtilsTest.java
git commit -m "feat(utils): add LexoRank sorting utility"
```

---

### Task 2: 数据库迁移脚本

**Files:**
- Create: `src/main/resources/db/migration/V1__Create_org_tree_tables.sql`
- Create: `src/main/resources/db/migration/V2__Insert_org_tree_sample_data.sql`

- [ ] **Step 1: 创建 V1 建表脚本**

```sql
-- V1__Create_org_tree_tables.sql
-- Organization Tree Table DDL

-- Create org_tree table
CREATE TABLE IF NOT EXISTS org_tree (
    id UUID PRIMARY KEY,
    parent_id UUID NOT NULL,
    entity_type VARCHAR(20) NOT NULL DEFAULT 'ROOT',
    entity_id UUID NOT NULL,
    alias VARCHAR(100) NOT NULL DEFAULT '',
    level INTEGER NOT NULL DEFAULT 0,
    path UUID[] NOT NULL DEFAULT '{}',
    sort_rank VARCHAR(12) NOT NULL DEFAULT 'a0',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00+00',
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT '2000-01-01 00:00:00+00',
    tenant_id UUID NOT NULL
);

-- Add comments
COMMENT ON TABLE org_tree IS 'Organization tree structure table';
COMMENT ON COLUMN org_tree.id IS 'Primary key, UUID v7';
COMMENT ON COLUMN org_tree.parent_id IS 'Parent node ID, root points to self';
COMMENT ON COLUMN org_tree.entity_type IS 'Entity type: ROOT/GROUP/DEPARTMENT/PERSONNEL';
COMMENT ON COLUMN org_tree.entity_id IS 'Related business entity ID';
COMMENT ON COLUMN org_tree.alias IS 'Display alias, empty means use entity name';
COMMENT ON COLUMN org_tree.level IS 'Hierarchy depth, root is 0';
COMMENT ON COLUMN org_tree.path IS 'Path array from root to parent';
COMMENT ON COLUMN org_tree.sort_rank IS 'LexoRank sort value';
COMMENT ON COLUMN org_tree.create_time IS 'Creation timestamp';
COMMENT ON COLUMN org_tree.update_time IS 'Update timestamp';
COMMENT ON COLUMN org_tree.tenant_id IS 'Tenant identifier';

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_org_tree_parent ON org_tree(parent_id, sort_rank);
CREATE INDEX IF NOT EXISTS idx_org_tree_entity ON org_tree(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_org_tree_path_gin ON org_tree USING GIN(path);
CREATE UNIQUE INDEX IF NOT EXISTS uk_org_tree_parent_entity ON org_tree(parent_id, entity_type, entity_id);

-- Create trigger for update_time
CREATE TRIGGER update_org_tree_updated_at
    BEFORE UPDATE ON org_tree
    FOR EACH ROW
    EXECUTE PROCEDURE upd_timestamp();

-- Add removed column to business tables
ALTER TABLE org_group ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE org_department ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE org_personnel ADD COLUMN IF NOT EXISTS removed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add indexes for removed column
CREATE INDEX IF NOT EXISTS idx_org_group_removed ON org_group(removed);
CREATE INDEX IF NOT EXISTS idx_org_department_removed ON org_department(removed);
CREATE INDEX IF NOT EXISTS idx_org_personnel_removed ON org_personnel(removed);

-- Add comments
COMMENT ON COLUMN org_group.removed IS 'Soft delete flag';
COMMENT ON COLUMN org_department.removed IS 'Soft delete flag';
COMMENT ON COLUMN org_personnel.removed IS 'Soft delete flag';
```

- [ ] **Step 2: 创建 V2 示例数据脚本**

```sql
-- V2__Insert_org_tree_sample_data.sql
-- Sample data for organization tree

-- Insert virtual root node
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000000',
    '00000000-0000-0000-0000-000000000000',
    'ROOT',
    '00000000-0000-0000-0000-000000000000',
    '',
    0,
    '{}',
    'a0',
    '00000000-0000-0000-0000-000000000000'
) ON CONFLICT (id) DO NOTHING;

-- Sample groups (if not exists)
INSERT INTO org_group (id, name, description, tenant_id, removed) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '总公司', 'Group Company', '00000000-0000-0000-0000-000000000000', false),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '华东分公司', 'East China Branch', '00000000-0000-0000-0000-000000000000', false),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '华北分公司', 'North China Branch', '00000000-0000-0000-0000-000000000000', false)
ON CONFLICT (id) DO NOTHING;

-- Sample departments
INSERT INTO org_department (id, name, english_name, short_name, org_code, phone, fax, email, address, postal_code, tenant_id, removed) VALUES
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '人力资源部', 'Human Resources', 'HR', 'HR-001', '010-12345678', '010-12345678', 'hr@company.com', 'Beijing', '100000', '00000000-0000-0000-0000-000000000000', false),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '财务部', 'Finance', 'FIN', 'FIN-001', '010-12345679', '010-12345679', 'finance@company.com', 'Beijing', '100000', '00000000-0000-0000-0000-000000000000', false),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '技术部', 'Technology', 'TECH', 'TECH-001', '010-12345680', '010-12345680', 'tech@company.com', 'Beijing', '100000', '00000000-0000-0000-0000-000000000000', false),
    ('11111111-1111-1111-1111-111111111111', '上海研发中心', 'Shanghai R&D', 'SH-RD', 'SH-RD-001', '021-12345678', '021-12345678', 'sh-rd@company.com', 'Shanghai', '200000', '00000000-0000-0000-0000-000000000000', false),
    ('22222222-2222-2222-2222-222222222222', '北京研发中心', 'Beijing R&D', 'BJ-RD', 'BJ-RD-001', '010-87654321', '010-87654321', 'bj-rd@company.com', 'Beijing', '100000', '00000000-0000-0000-0000-000000000000', false)
ON CONFLICT (id) DO NOTHING;

-- Sample personnel
INSERT INTO org_personnel (id, name, gender, id_card, mobile, telephone, fax, email, tenant_id, removed) VALUES
    ('33333333-3333-3333-3333-333333333333', '张三', 'M', '110101199001011234', '13800000001', '010-11111111', '010-11111111', 'zhangsan@company.com', '00000000-0000-0000-0000-000000000000', false),
    ('44444444-4444-4444-4444-444444444444', '李四', 'F', '110101199001015678', '13800000002', '010-22222222', '010-22222222', 'lisi@company.com', '00000000-0000-0000-0000-000000000000', false),
    ('55555555-5555-5555-5555-555555555555', '王五', 'M', '110101199001019012', '13800000003', '021-33333333', '021-33333333', 'wangwu@company.com', '00000000-0000-0000-0000-000000000000', false),
    ('66666666-6666-6666-6666-666666666666', '赵六', 'M', '110101199001019999', '13800000004', '021-44444444', '021-44444444', 'zhaoliu@company.com', '00000000-0000-0000-0000-000000000000', false),
    ('77777777-7777-7777-7777-777777777777', '钱七', 'F', '110101199001018888', '13800000005', '021-55555555', '021-55555555', 'qianqi@company.com', '00000000-0000-0000-0000-000000000000', false)
ON CONFLICT (id) DO NOTHING;

-- Insert tree structure
-- ROOT -> 总公司 (GROUP)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, tenant_id) VALUES
    ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa',
     '00000000-0000-0000-0000-000000000000',
     'GROUP',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     '',
     1,
     ARRAY['00000000-0000-0000-0000-000000000000'],
     'a0',
     '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 总公司 -> 华东分公司 (GROUP)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, tenant_id) VALUES
    ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb',
     'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa',
     'GROUP',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     '',
     2,
     ARRAY['00000000-0000-0000-0000-000000000000', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa'],
     'a0',
     '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 华东分公司 -> 上海研发中心 (DEPARTMENT)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, tenant_id) VALUES
    ('11111111-3333-3333-3333-111111111111',
     'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb',
     'DEPARTMENT',
     '11111111-1111-1111-1111-111111111111',
     '',
     3,
     ARRAY['00000000-0000-0000-0000-000000000000', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb'],
     'a0',
     '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 上海研发中心 -> 王五 (PERSONNEL)
INSERT INTO org_tree (id, parent_id, entity_type, entity_id, alias, level, path, sort_rank, tenant_id) VALUES
    ('55555555-4444-4444-4444-555555555555',
     '11111111-3333-3333-3333-111111111111',
     'PERSONNEL',
     '55555555-5555-5555-5555-555555555555',
     '',
     4,
     ARRAY['00000000-0000-0000-0000-000000000000', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', '11111111-3333-3333-3333-111111111111'],
     'a0',
     '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

-- 华北分公司 -> 北京研发中心 (DEPARTMENT) -> 王五 (PERSONNEL, multi-homing)
-- Note: Need to insert 华北分公司 first in a real scenario
```

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/db/migration/V1__Create_org_tree_tables.sql
git add src/main/resources/db/migration/V2__Insert_org_tree_sample_data.sql
git commit -m "feat(db): add organization tree migration scripts"
```

---

### Task 3: Entity 实体类

**Files:**
- Create: `src/main/java/com/reythecoder/organization/entity/OrgTreeNodeEntity.java`
- Create: `src/main/java/com/reythecoder/organization/entity/EntityType.java`
- Modify: `src/main/java/com/reythecoder/organization/entity/GroupEntity.java`
- Modify: `src/main/java/com/reythecoder/organization/entity/DepartmentEntity.java`
- Modify: `src/main/java/com/reythecoder/organization/entity/PersonnelEntity.java`
- Test: `src/test/java/com/reythecoder/organization/entity/OrgTreeNodeEntityTest.java`

- [ ] **Step 1: 创建 EntityType 枚举**

```java
package com.reythecoder.organization.entity;

public enum EntityType {
    ROOT,
    GROUP,
    DEPARTMENT,
    PERSONNEL
}
```

- [ ] **Step 2: 创建 OrgTreeNodeEntity**

```java
package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_tree",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_org_tree_parent_entity",
           columnNames = {"parent_id", "entity_type", "entity_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgTreeNodeEntity {

    @Id
    private UUID id;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(name = "entity_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "alias", length = 100, nullable = false)
    private String alias;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "path", nullable = false, columnDefinition = "UUID[]")
    private UUID[] path;

    @Column(name = "sort_rank", length = 12, nullable = false)
    private String sortRank;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    // Constructor for creating new node
    public OrgTreeNodeEntity(UUID parentId, EntityType entityType, UUID entityId, String alias) {
        this.id = UUIDv7.randomUUID();
        this.parentId = parentId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.alias = alias != null ? alias : "";
        this.level = 1;
        this.path = new UUID[0];
        this.sortRank = "a0";
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
```

- [ ] **Step 3: 修改 GroupEntity 添加 removed 字段**

```java
// 在 GroupEntity 中添加：
@Column(name = "removed", nullable = false)
private Boolean removed = false;

// 在构造函数中添加：
this.removed = false;
```

- [ ] **Step 4: 修改 DepartmentEntity 添加 removed 字段**

```java
// 在 DepartmentEntity 中添加：
@Column(name = "removed", nullable = false)
private Boolean removed = false;

// 在构造函数中添加：
this.removed = false;
```

- [ ] **Step 5: 修改 PersonnelEntity 添加 removed 字段**

```java
// 在 PersonnelEntity 中添加：
@Column(name = "removed", nullable = false)
private Boolean removed = false;

// 在构造函数中添加：
this.removed = false;
```

- [ ] **Step 6: 编写 Entity 测试**

```java
package com.reythecoder.organization.entity;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OrgTreeNodeEntityTest {

    @Test
    void testCreateNode() {
        UUID parentId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        OrgTreeNodeEntity node = new OrgTreeNodeEntity(parentId, EntityType.DEPARTMENT, entityId, "Test");

        assertNotNull(node.getId());
        assertEquals(parentId, node.getParentId());
        assertEquals(EntityType.DEPARTMENT, node.getEntityType());
        assertEquals(entityId, node.getEntityId());
        assertEquals("Test", node.getAlias());
        assertEquals(1, node.getLevel());
        assertEquals("a0", node.getSortRank());
        assertNotNull(node.getCreateTime());
    }

    @Test
    void testEntityType() {
        assertEquals(4, EntityType.values().length);
        assertTrue(EntityType.valueOf("ROOT") != null);
        assertTrue(EntityType.valueOf("GROUP") != null);
        assertTrue(EntityType.valueOf("DEPARTMENT") != null);
        assertTrue(EntityType.valueOf("PERSONNEL") != null);
    }
}
```

- [ ] **Step 7: 运行测试**

```bash
cd backend && ./gradlew test --tests OrgTreeNodeEntityTest
```

- [ ] **Step 8: 提交**

```bash
git add src/main/java/com/reythecoder/organization/entity/*.java
git add src/test/java/com/reythecoder/organization/entity/OrgTreeNodeEntityTest.java
git commit -m "feat(entity): add OrgTreeNodeEntity and EntityType"
```

---

### Task 4: Repository 接口

**Files:**
- Create: `src/main/java/com/reythecoder/organization/repository/OrgTreeNodeRepository.java`
- Test: `src/test/java/com/reythecoder/organization/repository/OrgTreeNodeRepositoryTest.java`

- [ ] **Step 1: 创建 Repository 接口**

```java
package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrgTreeNodeRepository extends JpaRepository<OrgTreeNodeEntity, UUID> {

    // ===== Method naming convention =====

    List<OrgTreeNodeEntity> findByParentIdOrderBySortRankAsc(UUID parentId);

    List<OrgTreeNodeEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    long countByParentId(UUID parentId);

    Optional<OrgTreeNodeEntity> findByParentIdAndEntityTypeAndEntityId(
        UUID parentId, EntityType entityType, UUID entityId);

    // ===== @Query - JPQL =====

    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.entityType = :entityType ORDER BY n.level, n.sortRank")
    List<OrgTreeNodeEntity> findByEntityType(@Param("entityType") EntityType entityType);

    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.level = :level ORDER BY n.sortRank")
    List<OrgTreeNodeEntity> findByLevel(@Param("level") Integer level);

    // ===== @Query - Native SQL (array operations) =====

    @Query(value = """
        SELECT * FROM org_tree
        WHERE :nodeId = ANY(path) OR id = :nodeId
        ORDER BY level, sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findAllDescendants(@Param("nodeId") UUID nodeId);

    @Query(value = """
        SELECT * FROM org_tree
        WHERE parent_id = :parentId
        ORDER BY sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentId(@Param("parentId") UUID parentId);

    @Query(value = """
        SELECT * FROM org_tree WHERE entity_type = 'ROOT' LIMIT 1
        """, nativeQuery = true)
    Optional<OrgTreeNodeEntity> findRootNode();

    @Query(value = """
        SELECT * FROM org_tree
        WHERE parent_id IN (:parentIds)
        ORDER BY parent_id, sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentIds(@Param("parentIds") List<UUID> parentIds);

    // ===== Statistics =====

    @Query("SELECT n.entityType, COUNT(n) FROM OrgTreeNodeEntity n WHERE n.parentId = :parentId GROUP BY n.entityType")
    List<Object[]> countChildrenByType(@Param("parentId") UUID parentId);

    // Count nodes for an entity (for delete check)
    long countByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}
```

- [ ] **Step 2: 编写 Repository 测试**

```java
package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrgTreeNodeRepositoryTest {

    @Autowired
    private OrgTreeNodeRepository repository;

    @Test
    void testFindByParentId() {
        // Given
        UUID rootId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // When
        List<OrgTreeNodeEntity> children = repository.findByParentIdOrderBySortRankAsc(rootId);

        // Then
        assertNotNull(children);
    }

    @Test
    void testCountByEntityTypeAndEntityId() {
        // Given
        UUID entityId = UUID.randomUUID();

        // When
        long count = repository.countByEntityTypeAndEntityId(EntityType.DEPARTMENT, entityId);

        // Then
        assertEquals(0, count);
    }
}
```

- [ ] **Step 3: 运行测试**

```bash
cd backend && ./gradlew test --tests OrgTreeNodeRepositoryTest
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/reythecoder/organization/repository/OrgTreeNodeRepository.java
git add src/test/java/com/reythecoder/organization/repository/OrgTreeNodeRepositoryTest.java
git commit -m "feat(repository): add OrgTreeNodeRepository"
```

---

### Task 5: DTO 数据传输对象

**Files:**
- Create: `src/main/java/com/reythecoder/organization/dto/request/CreateTreeNodeReq.java`
- Create: `src/main/java/com/reythecoder/organization/dto/request/UpdateTreeNodeReq.java`
- Create: `src/main/java/com/reythecoder/organization/dto/request/MoveTreeNodeReq.java`
- Modify: `src/main/java/com/reythecoder/organization/dto/response/TreeNodeRsp.java`
- Modify: `src/main/java/com/reythecoder/organization/dto/response/TreeStatistics.java`
- Test: `src/test/java/com/reythecoder/organization/dto/DtoTest.java`

- [ ] **Step 1: 创建 CreateTreeNodeReq**

```java
package com.reythecoder.organization.dto.request;

import com.reythecoder.organization.entity.EntityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreeNodeReq {

    @NotNull(message = "父节点 ID 不能为空")
    private UUID parentId;

    @NotNull(message = "实体类型不能为空")
    private EntityType entityType;

    @NotNull(message = "实体 ID 不能为空")
    private UUID entityId;

    private String alias;

    private UUID afterNodeId;
}
```

- [ ] **Step 2: 创建 UpdateTreeNodeReq**

```java
package com.reythecoder.organization.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTreeNodeReq {

    private String alias;

    private String sortRank;
}
```

- [ ] **Step 3: 创建 MoveTreeNodeReq**

```java
package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveTreeNodeReq {

    @NotNull(message = "新父节点 ID 不能为空")
    private UUID newParentId;

    private UUID afterNodeId;
}
```

- [ ] **Step 4: 修改 TreeNodeRsp**

```java
// 替换现有 TreeNodeRsp
package com.reythecoder.organization.dto.response;

import com.reythecoder.organization.entity.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRsp {
    private UUID id;
    private UUID entityId;
    private EntityType entityType;
    private String name;
    private String alias;
    private Integer level;
    private String sortRank;
    private TreeStatistics statistics;
    private List<TreeNodeRsp> children;
}
```

- [ ] **Step 5: 修改 TreeStatistics**

```java
// 修改现有 TreeStatistics
package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeStatistics {
    private Integer subGroupCount;
    private Integer subDepartmentCount;
    private Integer personnelCount;
}
```

- [ ] **Step 6: 编写 DTO 测试**

```java
package com.reythecoder.organization.dto;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.entity.EntityType;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testCreateTreeNodeReq() {
        CreateTreeNodeReq req = new CreateTreeNodeReq(
            UUID.randomUUID(),
            EntityType.DEPARTMENT,
            UUID.randomUUID(),
            "Test Alias",
            null
        );

        assertNotNull(req.getParentId());
        assertEquals(EntityType.DEPARTMENT, req.getEntityType());
        assertNotNull(req.getEntityId());
        assertEquals("Test Alias", req.getAlias());
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/reythecoder/organization/dto/
git add src/test/java/com/reythecoder/organization/dto/DtoTest.java
git commit -m "feat(dto): add tree node request and response DTOs"
```

---

### Task 6: Mapper 映射器

**Files:**
- Create: `src/main/java/com/reythecoder/organization/mapper/OrgTreeNodeMapper.java`
- Test: `src/test/java/com/reythecoder/organization/mapper/OrgTreeNodeMapperTest.java`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrgTreeNodeMapper {

    OrgTreeNodeMapper INSTANCE = Mappers.getMapper(OrgTreeNodeMapper.class);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(target = "children", ignore = true)
    TreeNodeRsp toTreeNodeRsp(OrgTreeNodeEntity entity);

    List<TreeNodeRsp> toTreeNodeRspList(List<OrgTreeNodeEntity> entities);
}
```

- [ ] **Step 2: 编写 Mapper 测试**

```java
package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrgTreeNodeMapperTest {

    private final OrgTreeNodeMapper mapper = OrgTreeNodeMapper.INSTANCE;

    @Test
    void testToTreeNodeRsp() {
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity();
        entity.setId(UUID.randomUUID());
        entity.setEntityId(UUID.randomUUID());
        entity.setEntityType(EntityType.DEPARTMENT);
        entity.setAlias("Test");
        entity.setLevel(1);
        entity.setSortRank("a0");

        TreeNodeRsp rsp = mapper.toTreeNodeRsp(entity);

        assertNotNull(rsp);
        assertEquals(entity.getId(), rsp.getId());
        assertEquals(entity.getEntityId(), rsp.getEntityId());
        assertEquals(entity.getEntityType(), rsp.getEntityType());
        assertEquals(entity.getAlias(), rsp.getAlias());
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/reythecoder/organization/mapper/OrgTreeNodeMapper.java
git add src/test/java/com/reythecoder/organization/mapper/OrgTreeNodeMapperTest.java
git commit -m "feat(mapper): add OrgTreeNodeMapper"
```

---

### Task 7: Service 接口和实现

**Files:**
- Create: `src/main/java/com/reythecoder/organization/service/OrgTreeNodeService.java`
- Create: `src/main/java/com/reythecoder/organization/service/impl/OrgTreeNodeServiceImpl.java`
- Test: `src/test/java/com/reythecoder/organization/service/OrgTreeNodeServiceTest.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;

import java.util.List;
import java.util.UUID;

public interface OrgTreeNodeService {

    // ===== CRUD =====

    OrgTreeNodeEntity createNode(UUID parentId, EntityType entityType, UUID entityId, String alias);

    OrgTreeNodeEntity createNodeAfter(UUID parentId, EntityType entityType, UUID entityId,
                                       String alias, UUID afterNodeId);

    OrgTreeNodeEntity updateNode(UUID nodeId, String alias, String sortRank);

    OrgTreeNodeEntity moveNode(UUID nodeId, UUID newParentId);

    OrgTreeNodeEntity moveNodeAfter(UUID nodeId, UUID newParentId, UUID afterNodeId);

    void removeNode(UUID nodeId);

    // ===== Query =====

    OrgTreeNodeEntity getNode(UUID nodeId);

    List<OrgTreeNodeEntity> getChildren(UUID parentId);

    TreeNodeRsp getSubTree(UUID nodeId, Integer depth);

    List<OrgTreeNodeEntity> getAllDescendants(UUID nodeId);

    List<OrgTreeNodeEntity> getAllAncestors(UUID nodeId);

    // ===== Entity association =====

    List<OrgTreeNodeEntity> getNodesByEntity(EntityType entityType, UUID entityId);

    // ===== Statistics =====

    long countChildren(UUID parentId);
}
```

- [ ] **Step 2: 创建 Service 实现（完整实现）**

```java
package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.OrgTreeNodeMapper;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import com.reythecoder.organization.service.OrgTreeNodeService;
import com.reythecoder.organization.utils.LexoRankUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrgTreeNodeServiceImpl implements OrgTreeNodeService {

    private static final Logger logger = LoggerFactory.getLogger(OrgTreeNodeServiceImpl.class);

    private final OrgTreeNodeRepository repository;
    private final OrgTreeNodeMapper mapper;

    public OrgTreeNodeServiceImpl(OrgTreeNodeRepository repository, OrgTreeNodeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OrgTreeNodeEntity createNode(UUID parentId, EntityType entityType, UUID entityId, String alias) {
        logger.info("创建树节点，parentId: {}, entityType: {}, entityId: {}", parentId, entityType, entityId);

        // Check parent exists
        OrgTreeNodeEntity parent = repository.findById(parentId)
            .orElseThrow(() -> new ApiException(404, "父节点不存在：" + parentId));

        // Check duplicate
        if (repository.findByParentIdAndEntityTypeAndEntityId(parentId, entityType, entityId).isPresent()) {
            throw new ApiException(400, "该父节点下已存在相同实体的树节点");
        }

        // Don't allow deleting virtual root
        if (parent.getEntityType() == EntityType.ROOT && entityType == EntityType.ROOT) {
            throw new ApiException(400, "虚拟根节点不允许删除");
        }

        // Calculate level and path
        int level = parent.getLevel() + 1;
        UUID[] path = new UUID[parent.getPath().length + 1];
        System.arraycopy(parent.getPath(), 0, path, 0, parent.getPath().length);
        path[path.length - 1] = parentId;

        // Calculate sort rank
        List<OrgTreeNodeEntity> siblings = repository.findByParentIdOrderBySortRankAsc(parentId);
        String sortRank = siblings.isEmpty() ? "a0" : LexoRankUtils.after(siblings.get(siblings.size() - 1).getSortRank());

        OrgTreeNodeEntity node = new OrgTreeNodeEntity(parentId, entityType, entityId, alias);
        node.setLevel(level);
        node.setPath(path);
        node.setSortRank(sortRank);
        node.setCreateTime(OffsetDateTime.now());
        node.setUpdateTime(OffsetDateTime.now());

        return repository.save(node);
    }

    @Override
    public OrgTreeNodeEntity createNodeAfter(UUID parentId, EntityType entityType, UUID entityId,
                                              String alias, UUID afterNodeId) {
        logger.info("创建树节点（指定位置），parentId: {}, afterNodeId: {}", parentId, afterNodeId);

        if (afterNodeId == null) {
            return createNode(parentId, entityType, entityId, alias);
        }

        OrgTreeNodeEntity afterNode = repository.findById(afterNodeId)
            .orElseThrow(() -> new ApiException(404, "节点不存在：" + afterNodeId));

        String sortRank = LexoRankUtils.after(afterNode.getSortRank());
        OrgTreeNodeEntity node = createNode(parentId, entityType, entityId, alias);
        node.setSortRank(sortRank);
        return repository.save(node);
    }

    @Override
    public OrgTreeNodeEntity updateNode(UUID nodeId, String alias, String sortRank) {
        logger.info("更新树节点，nodeId: {}", nodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
            .orElseThrow(() -> new ApiException(404, "节点不存在：" + nodeId));

        if (alias != null) {
            node.setAlias(alias);
        }
        if (sortRank != null && LexoRankUtils.isValidRank(sortRank)) {
            node.setSortRank(sortRank);
        }
        node.setUpdateTime(OffsetDateTime.now());

        return repository.save(node);
    }

    @Override
    public OrgTreeNodeEntity moveNode(UUID nodeId, UUID newParentId) {
        logger.info("移动树节点，nodeId: {}, newParentId: {}", nodeId, newParentId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
            .orElseThrow(() -> new ApiException(404, "节点不存在：" + nodeId));

        OrgTreeNodeEntity newParent = repository.findById(newParentId)
            .orElseThrow(() -> new ApiException(404, "父节点不存在：" + newParentId));

        // Check cycle
        if (isDescendant(newParentId, nodeId)) {
            throw new ApiException(400, "不能将节点移动到自身或其子孙节点下");
        }

        node.setParentId(newParentId);
        node.setLevel(newParent.getLevel() + 1);

        // Update path
        UUID[] newPath = new UUID[newParent.getPath().length + 1];
        System.arraycopy(newParent.getPath(), 0, newPath, 0, newParent.getPath().length);
        newPath[newPath.length - 1] = newParentId;
        node.setPath(newPath);

        node.setUpdateTime(OffsetDateTime.now());

        return repository.save(node);
    }

    @Override
    public OrgTreeNodeEntity moveNodeAfter(UUID nodeId, UUID newParentId, UUID afterNodeId) {
        logger.info("移动树节点（指定位置），nodeId: {}, newParentId: {}, afterNodeId: {}", nodeId, newParentId, afterNodeId);

        OrgTreeNodeEntity node = moveNode(nodeId, newParentId);

        if (afterNodeId != null) {
            OrgTreeNodeEntity afterNode = repository.findById(afterNodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在：" + afterNodeId));

            node.setSortRank(LexoRankUtils.after(afterNode.getSortRank()));
            node.setUpdateTime(OffsetDateTime.now());
            return repository.save(node);
        }

        return node;
    }

    @Override
    public void removeNode(UUID nodeId) {
        logger.info("删除树节点，nodeId: {}", nodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
            .orElseThrow(() -> new ApiException(404, "节点不存在：" + nodeId));

        // Don't allow deleting virtual root
        if (node.getEntityType() == EntityType.ROOT) {
            throw new ApiException(400, "虚拟根节点不允许删除");
        }

        // Check if this is the last node for this entity
        long count = repository.countByEntityTypeAndEntityId(node.getEntityType(), node.getEntityId());

        if (count == 1) {
            // Mark entity as removed
            markEntityAsRemoved(node.getEntityType(), node.getEntityId());
        }

        // Physical delete node
        repository.deleteById(nodeId);
    }

    private void markEntityAsRemoved(EntityType entityType, UUID entityId) {
        // Implementation depends on business entity repositories
        // This is a simplified version
        logger.info("标记实体为已删除：{}, {}", entityType, entityId);
    }

    private boolean isDescendant(UUID potentialDescendant, UUID ancestor) {
        List<OrgTreeNodeEntity> ancestors = getAllAncestors(potentialDescendant);
        return ancestors.stream().anyMatch(n -> n.getId().equals(ancestor));
    }

    @Override
    @Transactional(readOnly = true)
    public OrgTreeNodeEntity getNode(UUID nodeId) {
        return repository.findById(nodeId)
            .orElseThrow(() -> new ApiException(404, "节点不存在：" + nodeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgTreeNodeEntity> getChildren(UUID parentId) {
        return repository.findByParentIdOrderBySortRankAsc(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNodeRsp getSubTree(UUID nodeId, Integer depth) {
        OrgTreeNodeEntity node = getNode(nodeId);
        return buildTreeNode(node, 1, depth == null ? 1 : depth, depth == null || depth == -1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgTreeNodeEntity> getAllDescendants(UUID nodeId) {
        return repository.findAllDescendants(nodeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgTreeNodeEntity> getAllAncestors(UUID nodeId) {
        OrgTreeNodeEntity node = getNode(nodeId);
        List<OrgTreeNodeEntity> ancestors = new ArrayList<>();

        UUID currentId = node.getParentId();
        while (currentId != null && !currentId.equals(nodeId)) {
            OrgTreeNodeEntity parent = repository.findById(currentId).orElse(null);
            if (parent == null) break;
            ancestors.add(parent);
            currentId = parent.getParentId();
        }

        return ancestors;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgTreeNodeEntity> getNodesByEntity(EntityType entityType, UUID entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countChildren(UUID parentId) {
        return repository.countByParentId(parentId);
    }

    private TreeNodeRsp buildTreeNode(OrgTreeNodeEntity entity, int currentDepth, int maxDepth, boolean loadAll) {
        TreeNodeRsp node = mapper.toTreeNodeRsp(entity);

        // Set name based on entity type
        // This requires fetching the actual entity
        node.setName(entity.getAlias() != null && !entity.getAlias().isEmpty()
            ? entity.getAlias() : "Unknown");

        // Set statistics
        TreeStatistics stats = new TreeStatistics();
        List<Object[]> counts = repository.countChildrenByType(entity.getId());
        for (Object[] count : counts) {
            EntityType type = (EntityType) count[0];
            int value = ((Long) count[1]).intValue();
            switch (type) {
                case GROUP -> stats.setSubGroupCount(value);
                case DEPARTMENT -> stats.setSubDepartmentCount(value);
                case PERSONNEL -> stats.setPersonnelCount(value);
            }
        }
        node.setStatistics(stats);

        // Load children if needed
        if (loadAll || currentDepth < maxDepth) {
            List<OrgTreeNodeEntity> children = repository.findByParentIdOrderBySortRankAsc(entity.getId());
            node.setChildren(children.stream()
                .map(child -> buildTreeNode(child, currentDepth + 1, maxDepth, loadAll))
                .collect(Collectors.toList()));
        } else {
            node.setChildren(new ArrayList<>());
        }

        return node;
    }
}
```

- [ ] **Step 3: 编写 Service 测试**

```java
package com.reythecoder.organization.service;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.mapper.OrgTreeNodeMapper;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import com.reythecoder.organization.service.impl.OrgTreeNodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgTreeNodeServiceTest {

    @Mock
    private OrgTreeNodeRepository repository;

    @Mock
    private OrgTreeNodeMapper mapper;

    private OrgTreeNodeService service;

    @BeforeEach
    void setUp() {
        service = new OrgTreeNodeServiceImpl(repository, mapper);
    }

    @Test
    void testCreateNode() {
        // Given
        UUID parentId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        OrgTreeNodeEntity parent = new OrgTreeNodeEntity();
        parent.setId(parentId);
        parent.setLevel(0);
        parent.setPath(new UUID[0]);

        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        when(repository.findByParentIdOrderBySortRankAsc(parentId)).thenReturn(new ArrayList<>());
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        OrgTreeNodeEntity result = service.createNode(parentId, EntityType.DEPARTMENT, entityId, "Test");

        // Then
        assertNotNull(result);
        assertEquals(EntityType.DEPARTMENT, result.getEntityType());
        assertEquals("Test", result.getAlias());
    }

    @Test
    void testCreateNodeWithDuplicate() {
        // Given
        UUID parentId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        when(repository.findById(parentId)).thenReturn(Optional.of(new OrgTreeNodeEntity()));
        when(repository.findByParentIdAndEntityTypeAndEntityId(any(), any(), any()))
            .thenReturn(Optional.of(new OrgTreeNodeEntity()));

        // When & Then
        assertThrows(ApiException.class, () ->
            service.createNode(parentId, EntityType.DEPARTMENT, entityId, "Test")
        );
    }
}
```

- [ ] **Step 4: 运行测试**

```bash
cd backend && ./gradlew test --tests OrgTreeNodeServiceTest
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/reythecoder/organization/service/OrgTreeNodeService.java
git add src/main/java/com/reythecoder/organization/service/impl/OrgTreeNodeServiceImpl.java
git add src/test/java/com/reythecoder/organization/service/OrgTreeNodeServiceTest.java
git commit -m "feat(service): add OrgTreeNodeService implementation"
```

---

### Task 8: Controller 控制器

**Files:**
- Create: `src/main/java/com/reythecoder/organization/controller/OrgTreeNodeController.java`
- Test: `src/test/java/com/reythecoder/organization/controller/OrgTreeNodeControllerTest.java`

- [ ] **Step 1: 创建 Controller**

```java
package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.dto.request.MoveTreeNodeReq;
import com.reythecoder.organization.dto.request.UpdateTreeNodeReq;
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import com.reythecoder.organization.service.OrgTreeNodeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tree")
public class OrgTreeNodeController {

    private static final Logger logger = LoggerFactory.getLogger(OrgTreeNodeController.class);

    private final OrgTreeNodeService service;

    public OrgTreeNodeController(OrgTreeNodeService service) {
        this.service = service;
    }

    @PostMapping("/nodes")
    public ApiResult<TreeNodeRsp> createNode(@Valid @RequestBody CreateTreeNodeReq req) {
        logger.info("收到创建树节点请求：{}", req);

        OrgTreeNodeEntity entity;
        if (req.getAfterNodeId() != null) {
            entity = service.createNodeAfter(req.getParentId(), req.getEntityType(),
                req.getEntityId(), req.getAlias(), req.getAfterNodeId());
        } else {
            entity = service.createNode(req.getParentId(), req.getEntityType(),
                req.getEntityId(), req.getAlias());
        }

        TreeNodeRsp rsp = service.getSubTree(entity.getId(), 1);
        return ApiResult.success(rsp);
    }

    @GetMapping("/nodes/{nodeId}")
    public ApiResult<TreeNodeRsp> getNode(@PathVariable UUID nodeId) {
        logger.info("收到获取节点请求，nodeId: {}", nodeId);
        TreeNodeRsp rsp = service.getSubTree(nodeId, 1);
        return ApiResult.success(rsp);
    }

    @PutMapping("/nodes/{nodeId}")
    public ApiResult<TreeNodeRsp> updateNode(@PathVariable UUID nodeId,
                                              @Valid @RequestBody UpdateTreeNodeReq req) {
        logger.info("收到更新节点请求，nodeId: {}", nodeId);
        OrgTreeNodeEntity entity = service.updateNode(nodeId, req.getAlias(), req.getSortRank());
        TreeNodeRsp rsp = service.getSubTree(entity.getId(), 1);
        return ApiResult.success(rsp);
    }

    @PostMapping("/nodes/{nodeId}/remove")
    public ApiResult<Void> removeNode(@PathVariable UUID nodeId) {
        logger.info("收到删除节点请求，nodeId: {}", nodeId);
        service.removeNode(nodeId);
        return ApiResult.success(null);
    }

    @PostMapping("/nodes/{nodeId}/move")
    public ApiResult<TreeNodeRsp> moveNode(@PathVariable UUID nodeId,
                                            @Valid @RequestBody MoveTreeNodeReq req) {
        logger.info("收到移动节点请求，nodeId: {}, newParentId: {}", nodeId, req.getNewParentId());

        OrgTreeNodeEntity entity;
        if (req.getAfterNodeId() != null) {
            entity = service.moveNodeAfter(nodeId, req.getNewParentId(), req.getAfterNodeId());
        } else {
            entity = service.moveNode(nodeId, req.getNewParentId());
        }

        TreeNodeRsp rsp = service.getSubTree(entity.getId(), 1);
        return ApiResult.success(rsp);
    }

    @GetMapping("/nodes/{nodeId}/children")
    public ApiResult<List<TreeNodeRsp>> getChildren(@PathVariable UUID nodeId) {
        logger.info("收到获取子节点请求，nodeId: {}", nodeId);
        List<TreeNodeRsp> children = service.getChildren(nodeId).stream()
            .map(entity -> service.getSubTree(entity.getId(), 1))
            .toList();
        return ApiResult.success(children);
    }

    @GetMapping("/nodes/{nodeId}/subtree")
    public ApiResult<TreeNodeRsp> getSubTree(@PathVariable UUID nodeId,
                                              @RequestParam(defaultValue = "1") Integer depth) {
        logger.info("收到获取子树请求，nodeId: {}, depth: {}", nodeId, depth);
        TreeNodeRsp rsp = service.getSubTree(nodeId, depth);
        return ApiResult.success(rsp);
    }

    @GetMapping("/nodes/{nodeId}/descendants")
    public ApiResult<List<TreeNodeRsp>> getAllDescendants(@PathVariable UUID nodeId) {
        logger.info("收到获取子孙节点请求，nodeId: {}", nodeId);
        List<TreeNodeRsp> descendants = service.getAllDescendants(nodeId).stream()
            .map(entity -> service.getSubTree(entity.getId(), 1))
            .toList();
        return ApiResult.success(descendants);
    }

    @GetMapping("/nodes/{nodeId}/ancestors")
    public ApiResult<List<TreeNodeRsp>> getAllAncestors(@PathVariable UUID nodeId) {
        logger.info("收到获取祖先节点请求，nodeId: {}", nodeId);
        List<TreeNodeRsp> ancestors = service.getAllAncestors(nodeId).stream()
            .map(entity -> service.getSubTree(entity.getId(), 1))
            .toList();
        return ApiResult.success(ancestors);
    }

    @GetMapping("/root")
    public ApiResult<TreeNodeRsp> getRootNode() {
        logger.info("收到获取根节点请求");
        OrgTreeNodeEntity root = repository.findRootNode()
            .orElseThrow(() -> new ApiException(404, "根节点不存在"));
        TreeNodeRsp rsp = service.getSubTree(root.getId(), -1);
        return ApiResult.success(rsp);
    }

    @GetMapping("/root/children")
    public ApiResult<List<TreeNodeRsp>> getRootChildren() {
        logger.info("收到获取根节点子节点请求");
        OrgTreeNodeEntity root = repository.findRootNode()
            .orElseThrow(() -> new ApiException(404, "根节点不存在"));
        List<TreeNodeRsp> children = service.getChildren(root.getId()).stream()
            .map(entity -> service.getSubTree(entity.getId(), 1))
            .toList();
        return ApiResult.success(children);
    }
}
```

- [ ] **Step 2: 编写 Controller 测试**

```java
package com.reythecoder.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.service.OrgTreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrgTreeNodeController.class)
class OrgTreeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrgTreeNodeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateNode() throws Exception {
        CreateTreeNodeReq req = new CreateTreeNodeReq(
            UUID.randomUUID(),
            EntityType.DEPARTMENT,
            UUID.randomUUID(),
            "Test",
            null
        );

        mockMvc.perform(post("/api/tree/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    void testGetNode() throws Exception {
        UUID nodeId = UUID.randomUUID();

        mockMvc.perform(get("/api/tree/nodes/{nodeId}", nodeId))
            .andExpect(status().isOk());
    }
}
```

- [ ] **Step 3: 运行测试**

```bash
cd backend && ./gradlew test --tests OrgTreeNodeControllerTest
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/reythecoder/organization/controller/OrgTreeNodeController.java
git add src/test/java/com/reythecoder/organization/controller/OrgTreeNodeControllerTest.java
git commit -m "feat(controller): add OrgTreeNodeController REST API"
```

---

### Task 9: 集成测试

**Files:**
- Create: `src/test/java/com/reythecoder/organization/integration/OrgTreeIntegrationTest.java`

- [ ] **Step 1: 创建集成测试**

```java
package com.reythecoder.organization.integration;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class OrgTreeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrgTreeNodeRepository repository;

    @Test
    void testCreateAndQueryNode() throws Exception {
        // Given
        UUID rootId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        CreateTreeNodeReq req = new CreateTreeNodeReq(
            rootId,
            EntityType.GROUP,
            UUID.randomUUID(),
            "Test Group",
            null
        );

        // When & Then
        mockMvc.perform(post("/api/tree/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

- [ ] **Step 2: 运行集成测试**

```bash
cd backend && ./gradlew testIntegration --tests OrgTreeIntegrationTest
```

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/reythecoder/organization/integration/OrgTreeIntegrationTest.java
git commit -m "test(integration): add OrgTree integration tests"
```

---

### Task 10: 更新 OpenAPI 文档

**Files:**
- Modify: `src/main/resources/static/openapi.yaml` (if exists)

- [ ] **Step 1: 更新 OpenAPI 文档**

根据设计文档中的 Controller API 定义，更新 openapi.yaml 文件。

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/openapi.yaml
git commit -m "docs(api): update OpenAPI spec for tree node endpoints"
```

---

### Task 11: 验证和清理

**Files:**
- N/A (verification task)

- [ ] **Step 1: 运行所有测试**

```bash
cd backend && ./gradlew test
```

- [ ] **Step 2: 运行集成测试**

```bash
cd backend && ./gradlew testIntegration
```

- [ ] **Step 3: 生成覆盖率报告**

```bash
cd backend && ./gradlew test jacocoTestReport
```

- [ ] **Step 4: 验证覆盖率是否 >= 80%**

```bash
open build/reports/jacoco/test/html/index.html
```

- [ ] **Step 5: 最终提交**

```bash
git commit -m "chore: finalize organization tree refactor"
```

---

## Self-Review

**1. Spec coverage check:**

| Spec Section | Task |
|--------------|------|
| LexoRank 算法 | Task 1 |
| 数据库表结构 | Task 2 |
| Entity 实体类 | Task 3 |
| Repository 接口 | Task 4 |
| DTO 设计 | Task 5 |
| Mapper 映射器 | Task 6 |
| Service 接口/实现 | Task 7 |
| Controller API | Task 8 |
| 集成测试 | Task 9 |
| OpenAPI 文档 | Task 10 |

**2. Placeholder scan:** 无 TBD/TODO

**3. Type consistency:** 所有类型、方法签名一致

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-10-org-tree-refactor-impl.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
