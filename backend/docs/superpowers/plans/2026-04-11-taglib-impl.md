# Tag Library Management System Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a tag library management system with categories, tree-structured tags, and polymorphic tag-business object relations.

**Architecture:** Three new tables (`taglib_category`, `taglib_tag`, `taglib_tag_relation`) in a new `com.reythecoder.taglib` package. Migrates `LexoRankUtils` to a shared `com.reythecoder.common.utils` package. Full CRUD for categories and tags, batch tagging and multi-tag query for relations.

**Tech Stack:** Spring Boot 4.0, Java 17, Spring Data JPA, MapStruct 1.6.3, PostgreSQL, Lombok, UUIDv7, Mockito + AssertJ for tests.

**Design Spec:** `backend/docs/superpowers/specs/2026-04-11-taglib-design.md`

---

## File Map

### New files (by task)

| Task | File | Responsibility |
|------|------|----------------|
| 1 | `src/main/java/com/reythecoder/common/utils/LexoRankUtils.java` | LexoRank utility (migrated) |
| 1 | `OrganizationServiceApplication.java` (modify) | Add entity/repo scanning for new packages |
| 2 | `db/init-scripts/03-init-taglib-tables.sql` | DDL for 3 taglib tables |
| 2 | `db/init-scripts/04-seed-taglib-data.sql` | Sample seed data |
| 3 | `src/main/java/com/reythecoder/taglib/entity/TagCategoryEntity.java` | Category JPA entity |
| 3 | `src/main/java/com/reythecoder/taglib/entity/TagEntity.java` | Tag JPA entity (tree) |
| 3 | `src/main/java/com/reythecoder/taglib/entity/TagRelationEntity.java` | Tag-object relation JPA entity |
| 3 | `src/main/java/com/reythecoder/taglib/entity/TagObjectType.java` | Enum for object_type |
| 4 | `src/main/java/com/reythecoder/taglib/repository/TagCategoryRepository.java` | Category repository |
| 4 | `src/main/java/com/reythecoder/taglib/repository/TagRepository.java` | Tag repository |
| 4 | `src/main/java/com/reythecoder/taglib/repository/TagRelationRepository.java` | Relation repository |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagCategoryCreateReq.java` | Category create DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagCategoryUpdateReq.java` | Category update DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/response/TagCategoryRsp.java` | Category response DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagCreateReq.java` | Tag create DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagUpdateReq.java` | Tag update DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/response/TagRsp.java` | Tag response DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/response/TagTreeRsp.java` | Tag tree node response DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagRelationReq.java` | Tag relation create DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/request/TagRelationQueryReq.java` | Multi-tag query DTO |
| 5 | `src/main/java/com/reythecoder/taglib/dto/response/TagRelationRsp.java` | Tag relation response DTO |
| 6 | `src/main/java/com/reythecoder/taglib/mapper/TagCategoryMapper.java` | Category MapStruct mapper |
| 6 | `src/main/java/com/reythecoder/taglib/mapper/TagMapper.java` | Tag MapStruct mapper |
| 7 | `src/main/java/com/reythecoder/taglib/service/TagCategoryService.java` | Category service interface |
| 7 | `src/main/java/com/reythecoder/taglib/service/impl/TagCategoryServiceImpl.java` | Category service implementation |
| 8 | `src/test/java/com/reythecoder/taglib/service/TagCategoryServiceTest.java` | Category service unit tests |
| 8 | `src/test/java/com/reythecoder/taglib/controller/TagCategoryControllerTest.java` | Category controller tests |
| 9 | `src/main/java/com/reythecoder/taglib/controller/TagCategoryController.java` | Category REST controller |
| 9 | `src/main/resources/openapi.yaml` (modify) | Category API documentation |
| 10 | `src/main/java/com/reythecoder/taglib/service/TagService.java` | Tag service interface |
| 10 | `src/main/java/com/reythecoder/taglib/service/impl/TagServiceImpl.java` | Tag service implementation |
| 11 | `src/test/java/com/reythecoder/taglib/service/TagServiceTest.java` | Tag service unit tests |
| 11 | `src/test/java/com/reythecoder/taglib/controller/TagControllerTest.java` | Tag controller tests |
| 12 | `src/main/java/com/reythecoder/taglib/controller/TagController.java` | Tag REST controller |
| 12 | `src/main/resources/openapi.yaml` (modify) | Tag API documentation |
| 13 | `src/main/java/com/reythecoder/taglib/service/TagRelationService.java` | Relation service interface |
| 13 | `src/main/java/com/reythecoder/taglib/service/impl/TagRelationServiceImpl.java` | Relation service implementation |
| 14 | `src/test/java/com/reythecoder/taglib/service/TagRelationServiceTest.java` | Relation service unit tests |
| 14 | `src/test/java/com/reythecoder/taglib/controller/TagRelationControllerTest.java` | Relation controller tests |
| 15 | `src/main/java/com/reythecoder/taglib/controller/TagRelationController.java` | Relation REST controller |
| 15 | `src/main/resources/openapi.yaml` (modify) | Relation API documentation |

### Modified existing files

| File | Change |
|------|--------|
| `src/main/java/com/reythecoder/organization/OrganizationServiceApplication.java` | Add `@EntityScan`, `@EnableJpaRepositories` for multi-package scanning |
| `src/main/java/com/reythecoder/organization/service/impl/OrgTreeNodeServiceImpl.java` | Update `LexoRankUtils` import path |

---

## Task 1: Migrate LexoRankUtils to Common Package

**Files:**
- Create: `src/main/java/com/reythecoder/common/utils/LexoRankUtils.java`
- Delete: `src/main/java/com/reythecoder/organization/utils/LexoRankUtils.java`
- Modify: `src/main/java/com/reythecoder/organization/service/impl/OrgTreeNodeServiceImpl.java`
- Modify: `src/main/java/com/reythecoder/organization/OrganizationServiceApplication.java`

- [ ] **Step 1: Create the common package and move LexoRankUtils**

Create directory structure and copy the file with updated package declaration:

```java
// File: src/main/java/com/reythecoder/common/utils/LexoRankUtils.java
package com.reythecoder.common.utils;

/**
 * LexoRank 排序工具类
 *
 * LexoRank 是一种词典序排序算法，支持动态插入而无需重排其他元素。
 * 使用 36 进制字符集：0123456789abcdefghijklmnopqrstuvwxyz
 *
 * 排序值格式：至少 2 个字符，例如 "a0", "b0", "abc123"
 */
public final class LexoRankUtils {

    private static final String CHARSET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 36;

    private LexoRankUtils() {
    }

    public static String initialRank(int index) {
        if (index < 0 || index >= 26) {
            throw new IllegalArgumentException("Index must be between 0 and 25, got: " + index);
        }
        return CHARSET.charAt(index + 10) + "0";
    }

    public static String between(String lower, String upper) {
        if (lower == null && upper == null) {
            return initialRank(0);
        }
        if (lower != null && upper == null) {
            return after(lower);
        }
        if (lower == null && upper != null) {
            return before(upper);
        }
        if (lower.compareTo(upper) >= 0) {
            throw new IllegalArgumentException("lower must be less than upper, got: " + lower + " >= " + upper);
        }
        long lowerVal = toNumeric(lower);
        long upperVal = toNumeric(upper);
        long midVal = (lowerVal + upperVal) / 2;
        if (midVal == lowerVal) {
            return lower + CHARSET.charAt(0);
        }
        return fromNumeric(midVal);
    }

    public static String before(String current) {
        if (current == null) {
            return initialRank(0);
        }
        if (current.length() < 2) {
            throw new IllegalArgumentException("Invalid rank format: " + current);
        }
        char firstChar = current.charAt(0);
        int firstIndex = CHARSET.indexOf(firstChar);
        if (firstIndex > 0) {
            return CHARSET.charAt(firstIndex - 1) + "0";
        } else {
            return current + CHARSET.charAt(0);
        }
    }

    public static String after(String current) {
        if (current == null) {
            return initialRank(0);
        }
        if (current.length() < 2) {
            throw new IllegalArgumentException("Invalid rank format: " + current);
        }
        return current + CHARSET.charAt(0);
    }

    public static boolean isValidRank(String rank) {
        if (rank == null || rank.length() < 2) {
            return false;
        }
        for (char c : rank.toCharArray()) {
            if (CHARSET.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    private static long toNumeric(String rank) {
        long result = 0;
        for (char c : rank.toCharArray()) {
            result = result * BASE + CHARSET.indexOf(c);
        }
        return result;
    }

    private static String fromNumeric(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative: " + value);
        }
        StringBuilder sb = new StringBuilder();
        long v = value;
        do {
            int remainder = (int) (v % BASE);
            sb.insert(0, CHARSET.charAt(remainder));
            v = v / BASE;
        } while (v > 0);
        return sb.toString();
    }
}
```

- [ ] **Step 2: Delete old LexoRankUtils**

Delete `src/main/java/com/reythecoder/organization/utils/LexoRankUtils.java`.

- [ ] **Step 3: Update import in OrgTreeNodeServiceImpl**

In `src/main/java/com/reythecoder/organization/service/impl/OrgTreeNodeServiceImpl.java`, change:

```java
import com.reythecoder.organization.utils.LexoRankUtils;
```

to:

```java
import com.reythecoder.common.utils.LexoRankUtils;
```

- [ ] **Step 4: Update OrganizationServiceApplication for multi-package scanning**

The new `com.reythecoder.taglib` package is outside the default scan base. Add `@EntityScan` and `@EnableJpaRepositories` to include both packages:

```java
package com.reythecoder.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.reythecoder.organization", "com.reythecoder.taglib", "com.reythecoder.common"})
@EnableJpaRepositories(basePackages = {"com.reythecoder.organization", "com.reythecoder.taglib"})
public class OrganizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationServiceApplication.class, args);
    }
}
```

- [ ] **Step 5: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "refactor: migrate LexoRankUtils to common package and configure multi-package scanning"
```

---

## Task 2: Create Database Scripts

**Files:**
- Create: `backend/db/init-scripts/03-init-taglib-tables.sql`
- Create: `backend/db/init-scripts/04-seed-taglib-data.sql`

- [ ] **Step 1: Write DDL script**

Create `backend/db/init-scripts/03-init-taglib-tables.sql` following the project's SQL conventions from `02-init-position-tables.sql`:

```sql
-- Create tag category table (标签分类表)
CREATE TABLE IF NOT EXISTS taglib_category (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL DEFAULT '',
    description VARCHAR(500) NOT NULL DEFAULT '',
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0',
    removed BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
);

-- Add indexes on tag category table
CREATE INDEX IF NOT EXISTS idx_taglib_category_tenant_id ON taglib_category(tenant_id);

-- Add comments for tag category table columns
COMMENT ON COLUMN taglib_category.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_category.name IS 'Tag category name';
COMMENT ON COLUMN taglib_category.description IS 'Tag category description';
COMMENT ON COLUMN taglib_category.sort_rank IS 'LexoRank sort value for ordering';
COMMENT ON COLUMN taglib_category.removed IS 'Logical delete flag: true=removed';
COMMENT ON COLUMN taglib_category.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_category.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_category.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create tag table (标签表)
CREATE TABLE IF NOT EXISTS taglib_tag (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL DEFAULT '',
    category_id UUID NOT NULL,
    parent_id UUID,
    sort_rank VARCHAR(100) NOT NULL DEFAULT 'a0',
    removed BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (category_id, name)
);

-- Add indexes on tag table
CREATE INDEX IF NOT EXISTS idx_taglib_tag_category_id ON taglib_tag(category_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_parent_id ON taglib_tag(parent_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_tenant_id ON taglib_tag(tenant_id);

-- Add comments for tag table columns
COMMENT ON COLUMN taglib_tag.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_tag.name IS 'Tag name';
COMMENT ON COLUMN taglib_tag.category_id IS 'Belongs to tag category';
COMMENT ON COLUMN taglib_tag.parent_id IS 'Parent tag ID (NULL = top-level)';
COMMENT ON COLUMN taglib_tag.sort_rank IS 'LexoRank sort value for ordering';
COMMENT ON COLUMN taglib_tag.removed IS 'Logical delete flag: true=removed';
COMMENT ON COLUMN taglib_tag.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_tag.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_tag.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Create tag-object relation table (标签关联表)
CREATE TABLE IF NOT EXISTS taglib_tag_relation (
    id UUID PRIMARY KEY,
    object_type VARCHAR(50) NOT NULL DEFAULT '',
    object_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
    tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    UNIQUE (object_type, object_id, tag_id)
);

-- Add indexes on tag relation table
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_object ON taglib_tag_relation(object_type, object_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tag_id ON taglib_tag_relation(tag_id);
CREATE INDEX IF NOT EXISTS idx_taglib_tag_relation_tenant_id ON taglib_tag_relation(tenant_id);

-- Add comments for tag relation table columns
COMMENT ON COLUMN taglib_tag_relation.id IS 'Primary key using 128-bit UUID v7 algorithm';
COMMENT ON COLUMN taglib_tag_relation.object_type IS 'Business object type: DEPARTMENT/PERSONNEL/GROUP';
COMMENT ON COLUMN taglib_tag_relation.object_id IS 'Business object ID';
COMMENT ON COLUMN taglib_tag_relation.tag_id IS 'Tag ID';
COMMENT ON COLUMN taglib_tag_relation.create_time IS 'Record creation time';
COMMENT ON COLUMN taglib_tag_relation.update_time IS 'Record last update time';
COMMENT ON COLUMN taglib_tag_relation.tenant_id IS 'Tenant identifier for multi-tenant data isolation';

-- Add triggers for auto-updating update_time
CREATE TRIGGER update_taglib_category_updated_at BEFORE UPDATE ON taglib_category FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_taglib_tag_updated_at BEFORE UPDATE ON taglib_tag FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
CREATE TRIGGER update_taglib_tag_relation_updated_at BEFORE UPDATE ON taglib_tag_relation FOR EACH ROW EXECUTE PROCEDURE upd_timestamp();
```

- [ ] **Step 2: Write seed data script**

Create `backend/db/init-scripts/04-seed-taglib-data.sql`:

```sql
-- Insert sample tag categories
INSERT INTO taglib_category (id, name, description, sort_rank) VALUES
    ('fa000000-0000-0000-0000-000000000001', '技能标签', '人员技能相关标签分类', 'a0'),
    ('fa000000-0000-0000-0000-000000000002', '部门属性', '部门属性相关标签分类', 'b0'),
    ('fa000000-0000-0000-0000-000000000003', '组织标签', '组织/分组相关标签分类', 'c0')
ON CONFLICT (id) DO NOTHING;

-- Insert sample tags (技能标签分类下的树形结构)
INSERT INTO taglib_tag (id, name, category_id, parent_id, sort_rank) VALUES
    ('fb000000-0000-0000-0000-000000000001', '编程语言', 'fa000000-0000-0000-0000-000000000001', NULL, 'a0'),
    ('fb000000-0000-0000-0000-000000000002', 'Java', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'a0'),
    ('fb000000-0000-0000-0000-000000000003', 'Python', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'b0'),
    ('fb000000-0000-0000-0000-000000000004', 'Go', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000001', 'c0'),
    ('fb000000-0000-0000-0000-000000000005', '软技能', 'fa000000-0000-0000-0000-000000000001', NULL, 'b0'),
    ('fb000000-0000-0000-0000-000000000006', '沟通能力', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000005', 'a0'),
    ('fb000000-0000-0000-0000-000000000007', '团队协作', 'fa000000-0000-0000-0000-000000000001', 'fb000000-0000-0000-0000-000000000005', 'b0')
ON CONFLICT (id) DO NOTHING;

-- Insert sample tags (部门属性分类)
INSERT INTO taglib_tag (id, name, category_id, parent_id, sort_rank) VALUES
    ('fb000000-0000-0000-0000-000000000010', '核心部门', 'fa000000-0000-0000-0000-000000000002', NULL, 'a0'),
    ('fb000000-0000-0000-0000-000000000011', '支撑部门', 'fa000000-0000-0000-0000-000000000002', NULL, 'b0')
ON CONFLICT (id) DO NOTHING;

-- Insert sample tag relations (为示例人员和部门打标签)
INSERT INTO taglib_tag_relation (id, object_type, object_id, tag_id) VALUES
    ('fc000000-0000-0000-0000-000000000001', 'PERSONNEL', '55555555-5555-5555-5555-555555555555', 'fb000000-0000-0000-0000-000000000002'),
    ('fc000000-0000-0000-0000-000000000002', 'PERSONNEL', '55555555-5555-5555-5555-555555555555', 'fb000000-0000-0000-0000-000000000006'),
    ('fc000000-0000-0000-0000-000000000003', 'DEPARTMENT', '22222222-2222-2222-2222-222222222222', 'fb000000-0000-0000-0000-000000000010')
ON CONFLICT (id) DO NOTHING;
```

- [ ] **Step 3: Commit**

```bash
git add backend/db/init-scripts/03-init-taglib-tables.sql backend/db/init-scripts/04-seed-taglib-data.sql
git commit -m "feat(taglib): add database DDL and seed data scripts"
```

---

## Task 3: Create Entity Layer

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/entity/TagObjectType.java`
- Create: `src/main/java/com/reythecoder/taglib/entity/TagCategoryEntity.java`
- Create: `src/main/java/com/reythecoder/taglib/entity/TagEntity.java`
- Create: `src/main/java/com/reythecoder/taglib/entity/TagRelationEntity.java`

- [ ] **Step 1: Create TagObjectType enum**

```java
// File: src/main/java/com/reythecoder/taglib/entity/TagObjectType.java
package com.reythecoder.taglib.entity;

public enum TagObjectType {
    DEPARTMENT,
    PERSONNEL,
    GROUP
}
```

- [ ] **Step 2: Create TagCategoryEntity**

Following the `GroupEntity` pattern with `removed` field:

```java
// File: src/main/java/com/reythecoder/taglib/entity/TagCategoryEntity.java
package com.reythecoder.taglib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taglib_category")
public class TagCategoryEntity {

    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "sort_rank", nullable = false)
    private String sortRank;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public TagCategoryEntity(String name, String description, String sortRank) {
        this.id = io.github.robsonkades.uuidv7.UUIDv7.randomUUID();
        this.name = name != null ? name : "";
        this.description = description != null ? description : "";
        this.sortRank = sortRank != null ? sortRank : "a0";
        this.removed = false;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
```

- [ ] **Step 3: Create TagEntity**

Following the `OrgTreeNodeEntity` pattern with `parentId` for tree and `removed`:

```java
// File: src/main/java/com/reythecoder/taglib/entity/TagEntity.java
package com.reythecoder.taglib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taglib_tag", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(columnNames = {"category_id", "name"})
})
public class TagEntity {

    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "sort_rank", nullable = false)
    private String sortRank;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public TagEntity(String name, UUID categoryId, UUID parentId, String sortRank) {
        this.id = io.github.robsonkades.uuidv7.UUIDv7.randomUUID();
        this.name = name != null ? name : "";
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.sortRank = sortRank != null ? sortRank : "a0";
        this.removed = false;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
```

- [ ] **Step 4: Create TagRelationEntity**

Following the `GroupDepartmentEntity` pattern for association:

```java
// File: src/main/java/com/reythecoder/taglib/entity/TagRelationEntity.java
package com.reythecoder.taglib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taglib_tag_relation", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(columnNames = {"object_type", "object_id", "tag_id"})
})
public class TagRelationEntity {

    @Column(name = "id")
    private UUID id;

    @Column(name = "object_type", nullable = false)
    private String objectType;

    @Column(name = "object_id", nullable = false)
    private UUID objectId;

    @Column(name = "tag_id", nullable = false)
    private UUID tagId;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public TagRelationEntity(String objectType, UUID objectId, UUID tagId) {
        this.id = io.github.robsonkades.uuidv7.UUIDv7.randomUUID();
        this.objectType = objectType;
        this.objectId = objectId;
        this.tagId = tagId;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
```

- [ ] **Step 5: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/entity/
git commit -m "feat(taglib): add JPA entities for tag category, tag, and tag relation"
```

---

## Task 4: Create Repository Layer

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/repository/TagCategoryRepository.java`
- Create: `src/main/java/com/reythecoder/taglib/repository/TagRepository.java`
- Create: `src/main/java/com/reythecoder/taglib/repository/TagRelationRepository.java`

- [ ] **Step 1: Create TagCategoryRepository**

```java
// File: src/main/java/com/reythecoder/taglib/repository/TagCategoryRepository.java
package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategoryEntity, UUID> {

    List<TagCategoryEntity> findByRemovedFalseOrderBySortRankAsc();

    boolean existsByNameAndRemovedFalse(String name);
}
```

- [ ] **Step 2: Create TagRepository**

```java
// File: src/main/java/com/reythecoder/taglib/repository/TagRepository.java
package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {

    List<TagEntity> findByCategoryIdAndRemovedFalseOrderBySortRankAsc(UUID categoryId);

    List<TagEntity> findByParentIdAndRemovedFalseOrderBySortRankAsc(UUID parentId);

    Optional<TagEntity> findByIdAndRemovedFalse(UUID id);

    boolean existsByCategoryIdAndNameAndRemovedFalse(UUID categoryId, String name);

    List<TagEntity> findByParentIdAndRemovedFalse(UUID parentId);
}
```

- [ ] **Step 3: Create TagRelationRepository**

```java
// File: src/main/java/com/reythecoder/taglib/repository/TagRelationRepository.java
package com.reythecoder.taglib.repository;

import com.reythecoder.taglib.entity.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRelationRepository extends JpaRepository<TagRelationEntity, UUID> {

    List<TagRelationEntity> findByObjectTypeAndObjectId(String objectType, UUID objectId);

    List<TagRelationEntity> findByTagId(UUID tagId);

    boolean existsByObjectTypeAndObjectIdAndTagId(String objectType, UUID objectId, UUID tagId);

    void deleteByObjectTypeAndObjectIdAndTagId(String objectType, UUID objectId, UUID tagId);

    @Query("SELECT r FROM TagRelationEntity r WHERE r.objectType = :objectType AND r.tagId IN :tagIds GROUP BY r.objectId HAVING COUNT(DISTINCT r.tagId) = :tagCount")
    List<TagRelationEntity> findByObjectTypeAndTagIdsWithAllMatch(@Param("objectType") String objectType, @Param("tagIds") List<UUID> tagIds, @Param("tagCount") long tagCount);
}
```

- [ ] **Step 4: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/repository/
git commit -m "feat(taglib): add Spring Data JPA repositories for tag category, tag, and tag relation"
```

---

## Task 5: Create DTO Layer

**Files:**
- Create all 8 DTO files in `src/main/java/com/reythecoder/taglib/dto/request/` and `dto/response/`

- [ ] **Step 1: Create request DTOs**

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagCategoryCreateReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCategoryCreateReq {
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "分类描述不能超过500个字符")
    private String description;

    private String sortRank;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagCategoryUpdateReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCategoryUpdateReq {
    @Size(max = 100, message = "分类名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "分类描述不能超过500个字符")
    private String description;

    private String sortRank;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagCreateReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateReq {
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 100, message = "标签名称不能超过100个字符")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private UUID categoryId;

    private UUID parentId;

    private String sortRank;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagUpdateReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateReq {
    @Size(max = 100, message = "标签名称不能超过100个字符")
    private String name;

    private UUID parentId;

    private String sortRank;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagRelationReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationReq {
    @NotNull(message = "业务对象类型不能为空")
    private String objectType;

    @NotNull(message = "业务对象ID不能为空")
    private UUID objectId;

    @NotNull(message = "标签ID列表不能为空")
    private List<UUID> tagIds;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/request/TagRelationQueryReq.java
package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationQueryReq {
    @NotNull(message = "业务对象类型不能为空")
    private String objectType;

    @NotEmpty(message = "标签ID列表不能为空")
    private List<UUID> tagIds;
}
```

- [ ] **Step 2: Create response DTOs**

```java
// File: src/main/java/com/reythecoder/taglib/dto/response/TagCategoryRsp.java
package com.reythecoder.taglib.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCategoryRsp {
    private UUID id;
    private String name;
    private String description;
    private String sortRank;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/response/TagRsp.java
package com.reythecoder.taglib.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRsp {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private UUID parentId;
    private String sortRank;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/response/TagTreeRsp.java
package com.reythecoder.taglib.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagTreeRsp {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private UUID parentId;
    private String sortRank;
    private List<TagTreeRsp> children;
}
```

```java
// File: src/main/java/com/reythecoder/taglib/dto/response/TagRelationRsp.java
package com.reythecoder.taglib.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationRsp {
    private UUID id;
    private String objectType;
    private UUID objectId;
    private String objectName;
    private UUID tagId;
    private String tagName;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
```

- [ ] **Step 3: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/dto/
git commit -m "feat(taglib): add request and response DTOs"
```

---

## Task 6: Create MapStruct Mappers

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/mapper/TagCategoryMapper.java`
- Create: `src/main/java/com/reythecoder/taglib/mapper/TagMapper.java`

- [ ] **Step 1: Create TagCategoryMapper**

```java
// File: src/main/java/com/reythecoder/taglib/mapper/TagCategoryMapper.java
package com.reythecoder.taglib.mapper;

import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagCategoryMapper {

    TagCategoryMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(TagCategoryMapper.class);

    TagCategoryEntity toEntity(TagCategoryCreateReq req);

    void updateEntity(TagCategoryUpdateReq req, @MappingTarget TagCategoryEntity entity);

    TagCategoryRsp toRsp(TagCategoryEntity entity);
}
```

- [ ] **Step 2: Create TagMapper**

```java
// File: src/main/java/com/reythecoder/taglib/mapper/TagMapper.java
package com.reythecoder.taglib.mapper;

import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {

    TagMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(TagMapper.class);

    TagEntity toEntity(TagCreateReq req);

    void updateEntity(TagUpdateReq req, @MappingTarget TagEntity entity);

    TagRsp toRsp(TagEntity entity);
}
```

- [ ] **Step 3: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/mapper/
git commit -m "feat(taglib): add MapStruct mappers for tag category and tag"
```

---

## Task 7: Create TagCategory Service

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/service/TagCategoryService.java`
- Create: `src/main/java/com/reythecoder/taglib/service/impl/TagCategoryServiceImpl.java`

- [ ] **Step 1: Write TagCategoryService interface**

```java
// File: src/main/java/com/reythecoder/taglib/service/TagCategoryService.java
package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagCategoryService {

    List<TagCategoryRsp> getAllCategories();

    TagCategoryRsp getById(@NotNull UUID id);

    TagCategoryRsp create(@Valid @NotNull TagCategoryCreateReq req);

    TagCategoryRsp update(@NotNull UUID id, @Valid @NotNull TagCategoryUpdateReq req);

    void delete(@NotNull UUID id);
}
```

- [ ] **Step 2: Write TagCategoryServiceImpl**

```java
// File: src/main/java/com/reythecoder/taglib/service/impl/TagCategoryServiceImpl.java
package com.reythecoder.taglib.service.impl;

import com.reythecoder.common.utils.LexoRankUtils;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.mapper.TagCategoryMapper;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.service.TagCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TagCategoryServiceImpl implements TagCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(TagCategoryServiceImpl.class);

    private final TagCategoryRepository tagCategoryRepository;
    private final TagCategoryMapper tagCategoryMapper;

    public TagCategoryServiceImpl(TagCategoryRepository tagCategoryRepository) {
        this.tagCategoryRepository = tagCategoryRepository;
        this.tagCategoryMapper = TagCategoryMapper.INSTANCE;
    }

    @Override
    public List<TagCategoryRsp> getAllCategories() {
        logger.info("获取所有标签分类");
        return tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()
                .stream()
                .map(tagCategoryMapper::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public TagCategoryRsp getById(UUID id) {
        logger.info("获取标签分类: {}", id);
        var entity = tagCategoryRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));
        if (entity.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }
        return tagCategoryMapper.toRsp(entity);
    }

    @Override
    public TagCategoryRsp create(TagCategoryCreateReq req) {
        logger.info("创建标签分类: {}", req.getName());

        if (tagCategoryRepository.existsByNameAndRemovedFalse(req.getName())) {
            throw new ApiException(400, "标签分类名称已存在: " + req.getName());
        }

        String sortRank = req.getSortRank();
        if (sortRank == null || sortRank.isBlank()) {
            var lastCategory = tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()
                    .stream()
                    .reduce((first, second) -> second)
                    .orElse(null);
            sortRank = lastCategory != null
                    ? LexoRankUtils.after(lastCategory.getSortRank())
                    : LexoRankUtils.initialRank(0);
        }

        var entity = new TagCategoryEntity(req.getName(), req.getDescription(), sortRank);
        var saved = tagCategoryRepository.save(entity);
        return tagCategoryMapper.toRsp(saved);
    }

    @Override
    public TagCategoryRsp update(UUID id, TagCategoryUpdateReq req) {
        logger.info("更新标签分类: {}", id);
        var entity = tagCategoryRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));

        if (entity.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }

        tagCategoryMapper.updateEntity(req, entity);
        var updated = tagCategoryRepository.save(entity);
        return tagCategoryMapper.toRsp(updated);
    }

    @Override
    public void delete(UUID id) {
        logger.info("删除标签分类: {}", id);
        var entity = tagCategoryRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));

        if (entity.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }

        entity.setRemoved(true);
        tagCategoryRepository.save(entity);
    }
}
```

- [ ] **Step 3: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/service/
git commit -m "feat(taglib): add tag category service interface and implementation"
```

---

## Task 8: TagCategory Service & Controller Tests

**Files:**
- Create: `src/test/java/com/reythecoder/taglib/service/TagCategoryServiceTest.java`
- Create: `src/test/java/com/reythecoder/taglib/controller/TagCategoryControllerTest.java`

- [ ] **Step 1: Write TagCategoryServiceTest**

```java
// File: src/test/java/com/reythecoder/taglib/service/TagCategoryServiceTest.java
package com.reythecoder.taglib.service;

import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.service.impl.TagCategoryServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagCategoryServiceTest {

    @Mock
    private TagCategoryRepository tagCategoryRepository;

    @InjectMocks
    private TagCategoryServiceImpl tagCategoryService;

    private UUID categoryId;
    private TagCategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        categoryEntity = new TagCategoryEntity("技能标签", "技能相关分类", "a0");
        categoryEntity.setId(categoryId);
        categoryEntity.setCreateTime(now);
        categoryEntity.setUpdateTime(now);
    }

    @Test
    void getAllCategories_shouldReturnActiveCategories() {
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(List.of(categoryEntity));

        List<TagCategoryRsp> result = tagCategoryService.getAllCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("技能标签");
        verify(tagCategoryRepository).findByRemovedFalseOrderBySortRankAsc();
    }

    @Test
    void getAllCategories_shouldReturnEmptyList() {
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(Collections.emptyList());

        List<TagCategoryRsp> result = tagCategoryService.getAllCategories();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_shouldReturnCategoryWhenExists() {
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        TagCategoryRsp result = tagCategoryService.getById(categoryId);

        assertThat(result.getName()).isEqualTo("技能标签");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagCategoryService.getById(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
    }

    @Test
    void getById_shouldThrowWhenRemoved() {
        categoryEntity.setRemoved(true);
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        assertThatThrownBy(() -> tagCategoryService.getById(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
    }

    @Test
    void create_shouldReturnCreatedCategory() {
        var req = TagCategoryCreateReq.builder().name("新分类").description("描述").build();
        when(tagCategoryRepository.existsByNameAndRemovedFalse("新分类")).thenReturn(false);
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(Collections.emptyList());
        when(tagCategoryRepository.save(any(TagCategoryEntity.class))).thenReturn(categoryEntity);

        TagCategoryRsp result = tagCategoryService.create(req);

        assertThat(result).isNotNull();
        verify(tagCategoryRepository).save(any(TagCategoryEntity.class));
    }

    @Test
    void create_shouldThrowWhenNameExists() {
        var req = TagCategoryCreateReq.builder().name("技能标签").build();
        when(tagCategoryRepository.existsByNameAndRemovedFalse("技能标签")).thenReturn(true);

        assertThatThrownBy(() -> tagCategoryService.create(req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("标签分类名称已存在");
        verify(tagCategoryRepository, never()).save(any());
    }

    @Test
    void update_shouldReturnUpdatedCategory() {
        var req = TagCategoryUpdateReq.builder().name("更新分类").build();
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(tagCategoryRepository.save(any(TagCategoryEntity.class))).thenReturn(categoryEntity);

        TagCategoryRsp result = tagCategoryService.update(categoryId, req);

        assertThat(result).isNotNull();
        verify(tagCategoryRepository).save(any(TagCategoryEntity.class));
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        var req = TagCategoryUpdateReq.builder().name("更新分类").build();
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagCategoryService.update(categoryId, req))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
    }

    @Test
    void delete_shouldMarkAsRemoved() {
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        tagCategoryService.delete(categoryId);

        assertThat(categoryEntity.isRemoved()).isTrue();
        verify(tagCategoryRepository).save(categoryEntity);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagCategoryService.delete(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
    }
}
```

- [ ] **Step 2: Run service test**

Run: `cd backend && ./gradlew test --tests "*TagCategoryServiceTest" -i`
Expected: All tests PASS

- [ ] **Step 3: Write TagCategoryControllerTest**

```java
// File: src/test/java/com/reythecoder/taglib/controller/TagCategoryControllerTest.java
package com.reythecoder.taglib.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.service.TagCategoryService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagCategoryController.class)
class TagCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagCategoryService tagCategoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private TagCategoryRsp categoryRsp;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        categoryRsp = TagCategoryRsp.builder()
                .id(categoryId)
                .name("技能标签")
                .description("技能相关分类")
                .sortRank("a0")
                .createTime(now)
                .updateTime(now)
                .build();
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        when(tagCategoryService.getAllCategories()).thenReturn(List.of(categoryRsp));

        mockMvc.perform(get("/api/tag-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("技能标签"));
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        when(tagCategoryService.getById(categoryId)).thenReturn(categoryRsp);

        mockMvc.perform(get("/api/tag-categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("技能标签"));
    }

    @Test
    void createCategory_shouldReturnCreated() throws Exception {
        var req = TagCategoryCreateReq.builder().name("新分类").description("描述").build();
        when(tagCategoryService.create(any())).thenReturn(categoryRsp);

        mockMvc.perform(post("/api/tag-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateCategory_shouldReturnUpdated() throws Exception {
        var req = TagCategoryUpdateReq.builder().name("更新分类").build();
        when(tagCategoryService.update(eq(categoryId), any())).thenReturn(categoryRsp);

        mockMvc.perform(put("/api/tag-categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        doNothing().when(tagCategoryService).delete(categoryId);

        mockMvc.perform(delete("/api/tag-categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }
}
```

- [ ] **Step 4: Run controller test**

Note: This test will fail because the controller doesn't exist yet (Task 9). That's expected for TDD flow — the test proves what the controller must do.

Run: `cd backend && ./gradlew test --tests "*TagCategoryControllerTest" -i`
Expected: FAIL (controller class not found)

- [ ] **Step 5: Commit tests**

```bash
git add src/test/java/com/reythecoder/taglib/
git commit -m "test(taglib): add tag category service and controller tests"
```

---

## Task 9: Create TagCategory Controller + OpenAPI

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/controller/TagCategoryController.java`
- Modify: `src/main/resources/openapi.yaml`

- [ ] **Step 1: Write TagCategoryController**

```java
// File: src/main/java/com/reythecoder/taglib/controller/TagCategoryController.java
package com.reythecoder.taglib.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.service.TagCategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tag-categories")
public class TagCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(TagCategoryController.class);

    private final TagCategoryService tagCategoryService;

    public TagCategoryController(TagCategoryService tagCategoryService) {
        this.tagCategoryService = tagCategoryService;
    }

    @GetMapping
    public ApiResult<List<TagCategoryRsp>> getAllCategories() {
        logger.info("获取所有标签分类");
        return ApiResult.success(tagCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ApiResult<TagCategoryRsp> getCategoryById(@PathVariable UUID id) {
        logger.info("获取标签分类: {}", id);
        return ApiResult.success(tagCategoryService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<TagCategoryRsp> createCategory(@Valid @RequestBody TagCategoryCreateReq req) {
        logger.info("创建标签分类: {}", req.getName());
        return ApiResult.success("标签分类创建成功", tagCategoryService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<TagCategoryRsp> updateCategory(@PathVariable UUID id, @Valid @RequestBody TagCategoryUpdateReq req) {
        logger.info("更新标签分类: {}", id);
        return ApiResult.success("标签分类更新成功", tagCategoryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deleteCategory(@PathVariable UUID id) {
        logger.info("删除标签分类: {}", id);
        tagCategoryService.delete(id);
        return ApiResult.success("标签分类删除成功", null);
    }
}
```

- [ ] **Step 2: Run all tag category tests**

Run: `cd backend && ./gradlew test --tests "*TagCategory*" -i`
Expected: All tests PASS

- [ ] **Step 3: Add OpenAPI schemas and paths for tag categories**

Append to `src/main/resources/openapi.yaml` — add schemas in `components.schemas` section and paths in `paths` section. Follow the existing OpenAPI 3.1.0 patterns already in the file. Add:

In `components.schemas`:
- `TagCategory`, `TagCategoryCreateRequest`, `TagCategoryUpdateRequest`
- `Tag`, `TagCreateRequest`, `TagUpdateRequest`, `TagTree`
- `TagRelation`, `TagRelationCreateRequest`, `TagRelationQueryRequest`
- `TagObjectType` enum

In `paths`:
- `/tag-categories`, `/tag-categories/{id}`
- `/tags`, `/tags/{id}`
- `/tag-relations`, `/tag-relations/{id}`, `/tag-relations/batch`, `/tag-relations/query`

(The full YAML content for this step is large but follows the exact same pattern as existing department/personnel endpoints — same `allOf` with `ApiResult`, same response structure.)

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/controller/ src/main/resources/openapi.yaml
git commit -m "feat(taglib): add tag category controller and OpenAPI documentation"
```

---

## Task 10: Create Tag Service

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/service/TagService.java`
- Create: `src/main/java/com/reythecoder/taglib/service/impl/TagServiceImpl.java`

- [ ] **Step 1: Write TagService interface**

```java
// File: src/main/java/com/reythecoder/taglib/service/TagService.java
package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagService {

    List<TagTreeRsp> getTagTreeByCategory(@NotNull UUID categoryId);

    TagRsp getById(@NotNull UUID id);

    TagRsp create(@Valid @NotNull TagCreateReq req);

    TagRsp update(@NotNull UUID id, @Valid @NotNull TagUpdateReq req);

    void delete(@NotNull UUID id);
}
```

- [ ] **Step 2: Write TagServiceImpl**

```java
// File: src/main/java/com/reythecoder/taglib/service/impl/TagServiceImpl.java
package com.reythecoder.taglib.service.impl;

import com.reythecoder.common.utils.LexoRankUtils;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.mapper.TagMapper;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;
    private final TagCategoryRepository tagCategoryRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagCategoryRepository tagCategoryRepository) {
        this.tagRepository = tagRepository;
        this.tagCategoryRepository = tagCategoryRepository;
        this.tagMapper = TagMapper.INSTANCE;
    }

    @Override
    public List<TagTreeRsp> getTagTreeByCategory(UUID categoryId) {
        logger.info("获取分类 {} 下的标签树", categoryId);

        tagCategoryRepository.findById(java.util.Objects.requireNonNull(categoryId))
                .filter(c -> !c.isRemoved())
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));

        List<TagEntity> allTags = tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId);
        return buildTree(allTags, categoryId);
    }

    @Override
    public TagRsp getById(UUID id) {
        logger.info("获取标签: {}", id);
        var entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));
        return enrichTagRsp(entity);
    }

    @Override
    public TagRsp create(TagCreateReq req) {
        logger.info("创建标签: {}", req.getName());

        tagCategoryRepository.findById(java.util.Objects.requireNonNull(req.getCategoryId()))
                .filter(c -> !c.isRemoved())
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));

        if (req.getParentId() != null) {
            tagRepository.findByIdAndRemovedFalse(req.getParentId())
                    .orElseThrow(() -> new ApiException(404, "父标签不存在"));
        }

        if (tagRepository.existsByCategoryIdAndNameAndRemovedFalse(req.getCategoryId(), req.getName())) {
            throw new ApiException(400, "该分类下已存在同名标签: " + req.getName());
        }

        String sortRank = req.getSortRank();
        if (sortRank == null || sortRank.isBlank()) {
            List<TagEntity> siblings = req.getParentId() != null
                    ? tagRepository.findByParentIdAndRemovedFalseOrderBySortRankAsc(req.getParentId())
                    : tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(req.getCategoryId())
                        .stream()
                        .filter(t -> t.getParentId() == null)
                        .toList();
            var lastSibling = siblings.stream().reduce((first, second) -> second).orElse(null);
            sortRank = lastSibling != null
                    ? LexoRankUtils.after(lastSibling.getSortRank())
                    : LexoRankUtils.initialRank(0);
        }

        var entity = new TagEntity(req.getName(), req.getCategoryId(), req.getParentId(), sortRank);
        var saved = tagRepository.save(entity);
        return enrichTagRsp(saved);
    }

    @Override
    public TagRsp update(UUID id, TagUpdateReq req) {
        logger.info("更新标签: {}", id);
        var entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));

        tagMapper.updateEntity(req, entity);
        var updated = tagRepository.save(entity);
        return enrichTagRsp(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        logger.info("删除标签: {}", id);
        var entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));

        markRemovedRecursive(entity);
    }

    private void markRemovedRecursive(TagEntity entity) {
        entity.setRemoved(true);
        tagRepository.save(entity);

        List<TagEntity> children = tagRepository.findByParentIdAndRemovedFalse(entity.getId());
        for (TagEntity child : children) {
            markRemovedRecursive(child);
        }
    }

    private List<TagTreeRsp> buildTree(List<TagEntity> allTags, UUID categoryId) {
        var categoryName = tagCategoryRepository.findById(categoryId)
                .map(c -> c.getName()).orElse("");

        Map<UUID, List<TagEntity>> byParent = allTags.stream()
                .collect(Collectors.groupingBy(t -> t.getParentId() != null ? t.getParentId() : new UUID(0, 0)));

        List<TagEntity> roots = byParent.getOrDefault(new UUID(0, 0), Collections.emptyList());

        return roots.stream()
                .map(tag -> toTreeRsp(tag, byParent, categoryName))
                .collect(Collectors.toList());
    }

    private TagTreeRsp toTreeRsp(TagEntity entity, Map<UUID, List<TagEntity>> byParent, String categoryName) {
        List<TagEntity> children = byParent.getOrDefault(entity.getId(), Collections.emptyList());
        return TagTreeRsp.builder()
                .id(entity.getId())
                .name(entity.getName())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryName)
                .parentId(entity.getParentId())
                .sortRank(entity.getSortRank())
                .children(children.stream()
                        .map(child -> toTreeRsp(child, byParent, categoryName))
                        .collect(Collectors.toList()))
                .build();
    }

    private TagRsp enrichTagRsp(TagEntity entity) {
        var rsp = tagMapper.toRsp(entity);
        tagCategoryRepository.findById(entity.getCategoryId())
                .ifPresent(cat -> rsp.setCategoryName(cat.getName()));
        return rsp;
    }
}
```

- [ ] **Step 3: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/service/TagService.java src/main/java/com/reythecoder/taglib/service/impl/TagServiceImpl.java
git commit -m "feat(taglib): add tag service with tree building and recursive delete"
```

---

## Task 11: Tag Service & Controller Tests

**Files:**
- Create: `src/test/java/com/reythecoder/taglib/service/TagServiceTest.java`
- Create: `src/test/java/com/reythecoder/taglib/controller/TagControllerTest.java`

- [ ] **Step 1: Write TagServiceTest**

Test key behaviors: tree building, create with parent, duplicate name check, recursive delete. Follow the same `@ExtendWith(MockitoExtension.class)` + `@Mock`/`@InjectMocks` pattern from `TagCategoryServiceTest`.

- [ ] **Step 2: Run service test**

Run: `cd backend && ./gradlew test --tests "*TagServiceTest" -i`
Expected: All tests PASS

- [ ] **Step 3: Write TagControllerTest**

Test all 5 endpoints. Follow `@WebMvcTest(TagController.class)` + `@MockitoBean` pattern.

- [ ] **Step 4: Run controller test**

Run: `cd backend && ./gradlew test --tests "*TagControllerTest" -i`
Expected: FAIL (controller not yet created)

- [ ] **Step 5: Commit tests**

```bash
git add src/test/java/com/reythecoder/taglib/
git commit -m "test(taglib): add tag service and controller tests"
```

---

## Task 12: Create Tag Controller

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/controller/TagController.java`

- [ ] **Step 1: Write TagController**

```java
// File: src/main/java/com/reythecoder/taglib/controller/TagController.java
package com.reythecoder.taglib.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.service.TagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResult<List<TagTreeRsp>> getTagTreeByCategory(@RequestParam UUID categoryId) {
        logger.info("获取分类 {} 下的标签树", categoryId);
        return ApiResult.success(tagService.getTagTreeByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ApiResult<TagRsp> getTagById(@PathVariable UUID id) {
        logger.info("获取标签: {}", id);
        return ApiResult.success(tagService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<TagRsp> createTag(@Valid @RequestBody TagCreateReq req) {
        logger.info("创建标签: {}", req.getName());
        return ApiResult.success("标签创建成功", tagService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<TagRsp> updateTag(@PathVariable UUID id, @Valid @RequestBody TagUpdateReq req) {
        logger.info("更新标签: {}", id);
        return ApiResult.success("标签更新成功", tagService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> deleteTag(@PathVariable UUID id) {
        logger.info("删除标签: {}", id);
        tagService.delete(id);
        return ApiResult.success("标签删除成功", null);
    }
}
```

- [ ] **Step 2: Run all tag tests**

Run: `cd backend && ./gradlew test --tests "*Tag*" -i`
Expected: All tests PASS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/controller/TagController.java
git commit -m "feat(taglib): add tag controller with tree query support"
```

---

## Task 13: Create TagRelation Service

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/service/TagRelationService.java`
- Create: `src/main/java/com/reythecoder/taglib/service/impl/TagRelationServiceImpl.java`

- [ ] **Step 1: Write TagRelationService interface**

```java
// File: src/main/java/com/reythecoder/taglib/service/TagRelationService.java
package com.reythecoder.taglib.service;

import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TagRelationService {

    List<TagRelationRsp> getByObject(@NotNull String objectType, @NotNull UUID objectId);

    List<TagRelationRsp> getByTag(@NotNull UUID tagId);

    List<TagRelationRsp> batchCreate(@Valid @NotNull TagRelationReq req);

    void delete(@NotNull UUID id);

    List<TagRelationRsp> queryByMultipleTags(@Valid @NotNull TagRelationQueryReq req);
}
```

- [ ] **Step 2: Write TagRelationServiceImpl**

```java
// File: src/main/java/com/reythecoder/taglib/service/impl/TagRelationServiceImpl.java
package com.reythecoder.taglib.service.impl;

import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.entity.TagRelationEntity;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.repository.TagRelationRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.TagRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TagRelationServiceImpl implements TagRelationService {

    private static final Logger logger = LoggerFactory.getLogger(TagRelationServiceImpl.class);

    private final TagRelationRepository tagRelationRepository;
    private final TagRepository tagRepository;

    public TagRelationServiceImpl(TagRelationRepository tagRelationRepository, TagRepository tagRepository) {
        this.tagRelationRepository = tagRelationRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagRelationRsp> getByObject(String objectType, UUID objectId) {
        logger.info("查询对象 {}:{} 的标签", objectType, objectId);
        List<TagRelationEntity> relations = tagRelationRepository.findByObjectTypeAndObjectId(objectType, objectId);
        return relations.stream().map(this::toRsp).collect(Collectors.toList());
    }

    @Override
    public List<TagRelationRsp> getByTag(UUID tagId) {
        logger.info("查询标签 {} 关联的对象", tagId);
        List<TagRelationEntity> relations = tagRelationRepository.findByTagId(tagId);
        return relations.stream().map(this::toRsp).collect(Collectors.toList());
    }

    @Override
    public List<TagRelationRsp> batchCreate(TagRelationReq req) {
        logger.info("为对象 {}:{} 批量打标签", req.getObjectType(), req.getObjectId());

        return req.getTagIds().stream()
                .map(tagId -> createSingle(req.getObjectType(), req.getObjectId(), tagId))
                .collect(Collectors.toList());
    }

    private TagRelationRsp createSingle(String objectType, UUID objectId, UUID tagId) {
        tagRepository.findByIdAndRemovedFalse(tagId)
                .orElseThrow(() -> new ApiException(404, "标签不存在: " + tagId));

        if (tagRelationRepository.existsByObjectTypeAndObjectIdAndTagId(objectType, objectId, tagId)) {
            logger.warn("标签关联已存在: {}:{} <-> {}", objectType, objectId, tagId);
            return tagRelationRepository.findByObjectTypeAndObjectId(objectType, objectId)
                    .stream()
                    .filter(r -> r.getTagId().equals(tagId))
                    .findFirst()
                    .map(this::toRsp)
                    .orElse(null);
        }

        var entity = new TagRelationEntity(objectType, objectId, tagId);
        var saved = tagRelationRepository.save(entity);
        return toRsp(saved);
    }

    @Override
    public void delete(UUID id) {
        logger.info("删除标签关联: {}", id);
        var entity = tagRelationRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(404, "标签关联不存在"));
        tagRelationRepository.delete(entity);
    }

    @Override
    public List<TagRelationRsp> queryByMultipleTags(TagRelationQueryReq req) {
        logger.info("多标签筛选: objectType={}, tagIds={}", req.getObjectType(), req.getTagIds());

        List<TagRelationEntity> results = tagRelationRepository.findByObjectTypeAndTagIdsWithAllMatch(
                req.getObjectType(), req.getTagIds(), req.getTagIds().size());

        return results.stream().map(this::toRsp).collect(Collectors.toList());
    }

    private TagRelationRsp toRsp(TagRelationEntity entity) {
        String tagName = tagRepository.findById(entity.getTagId())
                .map(TagEntity::getName).orElse("");

        return TagRelationRsp.builder()
                .id(entity.getId())
                .objectType(entity.getObjectType())
                .objectId(entity.getObjectId())
                .tagId(entity.getTagId())
                .tagName(tagName)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
```

- [ ] **Step 3: Build and verify**

Run: `cd backend && ./gradlew clean compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/service/TagRelationService.java src/main/java/com/reythecoder/taglib/service/impl/TagRelationServiceImpl.java
git commit -m "feat(taglib): add tag relation service with batch create and multi-tag query"
```

---

## Task 14: TagRelation Service & Controller Tests

**Files:**
- Create: `src/test/java/com/reythecoder/taglib/service/TagRelationServiceTest.java`
- Create: `src/test/java/com/reythecoder/taglib/controller/TagRelationControllerTest.java`

- [ ] **Step 1: Write TagRelationServiceTest**

Test: getByObject, getByTag, batchCreate (success + duplicate skip), delete, multi-tag query. Follow `@ExtendWith(MockitoExtension.class)` pattern.

- [ ] **Step 2: Run service test**

Run: `cd backend && ./gradlew test --tests "*TagRelationServiceTest" -i`
Expected: All tests PASS

- [ ] **Step 3: Write TagRelationControllerTest**

Test all 5 endpoints including batch and query. Follow `@WebMvcTest(TagRelationController.class)` pattern.

- [ ] **Step 4: Run controller test**

Run: `cd backend && ./gradlew test --tests "*TagRelationControllerTest" -i`
Expected: FAIL (controller not yet created)

- [ ] **Step 5: Commit tests**

```bash
git add src/test/java/com/reythecoder/taglib/
git commit -m "test(taglib): add tag relation service and controller tests"
```

---

## Task 15: Create TagRelation Controller

**Files:**
- Create: `src/main/java/com/reythecoder/taglib/controller/TagRelationController.java`

- [ ] **Step 1: Write TagRelationController**

```java
// File: src/main/java/com/reythecoder/taglib/controller/TagRelationController.java
package com.reythecoder.taglib.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.service.TagRelationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tag-relations")
public class TagRelationController {

    private static final Logger logger = LoggerFactory.getLogger(TagRelationController.class);

    private final TagRelationService tagRelationService;

    public TagRelationController(TagRelationService tagRelationService) {
        this.tagRelationService = tagRelationService;
    }

    @GetMapping
    public ApiResult<List<TagRelationRsp>> getByObject(
            @RequestParam String objectType,
            @RequestParam UUID objectId) {
        logger.info("查询对象 {}:{} 的标签", objectType, objectId);
        return ApiResult.success(tagRelationService.getByObject(objectType, objectId));
    }

    @GetMapping(params = "tagId")
    public ApiResult<List<TagRelationRsp>> getByTag(@RequestParam UUID tagId) {
        logger.info("查询标签 {} 关联的对象", tagId);
        return ApiResult.success(tagRelationService.getByTag(tagId));
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<List<TagRelationRsp>> batchCreate(@Valid @RequestBody TagRelationReq req) {
        logger.info("为对象 {}:{} 批量打标签", req.getObjectType(), req.getObjectId());
        return ApiResult.success("标签关联创建成功", tagRelationService.batchCreate(req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> delete(@PathVariable UUID id) {
        logger.info("删除标签关联: {}", id);
        tagRelationService.delete(id);
        return ApiResult.success("标签关联删除成功", null);
    }

    @PostMapping("/query")
    public ApiResult<List<TagRelationRsp>> queryByMultipleTags(@Valid @RequestBody TagRelationQueryReq req) {
        logger.info("多标签筛选: objectType={}", req.getObjectType());
        return ApiResult.success(tagRelationService.queryByMultipleTags(req));
    }
}
```

- [ ] **Step 2: Run all tests**

Run: `cd backend && ./gradlew test -i`
Expected: All tests PASS (both organization and taglib)

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/reythecoder/taglib/controller/TagRelationController.java
git commit -m "feat(taglib): add tag relation controller with batch and multi-tag query"
```

---

## Task 16: Final Verification

- [ ] **Step 1: Run full test suite**

Run: `cd backend && ./gradlew clean test -i`
Expected: All tests PASS

- [ ] **Step 2: Run full build**

Run: `cd backend && ./gradlew clean build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Verify all files exist**

Run: `find src/main/java/com/reythecoder/taglib -name "*.java" | sort`
Expected: 17 Java files listed

Run: `find src/test/java/com/reythecoder/taglib -name "*.java" | sort`
Expected: 6 test files listed

- [ ] **Step 4: Final commit (if any remaining changes)**

```bash
git add -A
git commit -m "feat(taglib): complete tag library management system implementation"
```
