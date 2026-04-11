# Tag Library Management System Design

## Overview

Implement a tag library management system for the backend, supporting:
- Tag categories (CRUD)
- Tags organized in tree structure within categories (unlimited depth, CRUD)
- Tagging multiple business object types (Department, Personnel, Group initially, extensible)
- Optimized query capabilities for common access patterns

## Design Decisions

### Chosen Approach: Two-Entity Model (Category + Tag)

Separate `taglib_category` and `taglib_tag` tables, with tags supporting tree structure via adjacency list (`parent_id`).

**Rationale**: Clear separation of concerns, consistent with existing project patterns, intuitive querying.

**Rejected alternative**: Single unified `tag_node` table with type discriminator — mixes category/tag semantics, requires extra filtering.

### Polymorphic Association for Tag-Business Object Relations

Single `taglib_tag_relation` table with `object_type` + `object_id` to support multiple business object types.

**Rationale**: Extensible without schema changes. Adding a new business object type only requires a new enum value and potentially a new repository lookup.

### LexoRank Sorting

Uses existing `LexoRankUtils` migrated to `com.reythecoder.common.utils` (from `com.reythecoder.organization.utils`). Sort fields use `sort_rank VARCHAR(100)` with LexoRank string values. No new utility class needed.

### Package Structure

Tag library code lives in an independent package `com.reythecoder.taglib`, separate from `com.reythecoder.organization`.

## Database Model

### Table: `taglib_category`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | UUIDv7 |
| name | VARCHAR(100) | NOT NULL DEFAULT '' | Category name |
| description | VARCHAR(500) | NOT NULL DEFAULT '' | Category description |
| sort_rank | VARCHAR(100) | NOT NULL DEFAULT 'a0' | LexoRank sort value |
| removed | BOOLEAN | NOT NULL DEFAULT FALSE | Logical delete flag |
| create_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Creation timestamp |
| update_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Update timestamp |
| tenant_id | UUID | NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' | Tenant ID |

### Table: `taglib_tag`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | UUIDv7 |
| name | VARCHAR(100) | NOT NULL DEFAULT '' | Tag name |
| category_id | UUID | NOT NULL | Belongs to category |
| parent_id | UUID | NULL | Parent tag (NULL = top-level) |
| sort_rank | VARCHAR(100) | NOT NULL DEFAULT 'a0' | LexoRank sort value |
| removed | BOOLEAN | NOT NULL DEFAULT FALSE | Logical delete flag |
| create_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Creation timestamp |
| update_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Update timestamp |
| tenant_id | UUID | NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' | Tenant ID |

**Unique constraint**: `uk_taglib_tag_category_name(category_id, name)` — tag names must be unique within a category.

**Indexes**:
- `idx_taglib_tag_category_id` on `category_id`
- `idx_taglib_tag_parent_id` on `parent_id`

### Table: `taglib_tag_relation`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | UUIDv7 |
| object_type | VARCHAR(50) | NOT NULL DEFAULT '' | DEPARTMENT / PERSONNEL / GROUP |
| object_id | UUID | NOT NULL | Business object ID |
| tag_id | UUID | NOT NULL | Tag ID |
| create_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Creation timestamp |
| update_time | TIMESTAMPTZ | NOT NULL DEFAULT now() | Update timestamp |
| tenant_id | UUID | NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000' | Tenant ID |

**Unique constraint**: `uk_taglib_tag_relation_unique(object_type, object_id, tag_id)` — prevents duplicate tagging.

**Indexes**:
- `idx_taglib_tag_relation_object` on `(object_type, object_id)` — optimize "object -> tags" queries
- `idx_taglib_tag_relation_tag_id` on `tag_id` — optimize "tag -> objects" queries

### Key Design Points

- Tags use adjacency list model (`parent_id`) for tree structure. No materialized path needed — tag trees are typically shallow (2-3 levels).
- Logical delete (`removed`) on category and tag tables; physical delete on relation table (tag/un-tag is an explicit operation).
- All columns have NOT NULL with defaults (per project database design standards).
- `upd_timestamp()` trigger on all three tables for auto-updating `update_time`.

## API Design

### Tag Category API (`/api/tag-categories`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/tag-categories` | List all categories |
| GET | `/api/tag-categories/{id}` | Get category by ID |
| POST | `/api/tag-categories` | Create category |
| PUT | `/api/tag-categories/{id}` | Update category |
| DELETE | `/api/tag-categories/{id}` | Delete category (logical) |

### Tag API (`/api/tags`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/tags?categoryId={id}` | Get tag tree by category |
| GET | `/api/tags/{id}` | Get tag by ID |
| POST | `/api/tags` | Create tag |
| PUT | `/api/tags/{id}` | Update tag |
| DELETE | `/api/tags/{id}` | Delete tag (logical, cascades children) |

### Tag Relation API (`/api/tag-relations`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/tag-relations?objectType={type}&objectId={id}` | Get all tags for an object |
| GET | `/api/tag-relations?tagId={id}` | Get all objects with a tag |
| POST | `/api/tag-relations/batch` | Batch tag objects |
| DELETE | `/api/tag-relations/{id}` | Remove tag association |
| POST | `/api/tag-relations/query` | Multi-tag filter (AND/OR logic) |

## Query Optimization

### 1. Object -> Tags

```sql
SELECT t.*, tc.name as category_name
FROM taglib_tag_relation r
JOIN taglib_tag t ON t.id = r.tag_id AND t.removed = false
JOIN taglib_category tc ON tc.id = t.category_id AND tc.removed = false
WHERE r.object_type = ? AND r.object_id = ?
ORDER BY tc.sort_rank, t.sort_rank
```

Index: `idx_taglib_tag_relation_object(object_type, object_id)`

### 2. Tag -> Objects

```sql
SELECT r.object_type, r.object_id
FROM taglib_tag_relation r
WHERE r.tag_id = ?
```

Index: `idx_taglib_tag_relation_tag_id(tag_id)`

### 3. Multi-tag Filter (AND logic)

```sql
SELECT r.object_id, r.object_type
FROM taglib_tag_relation r
WHERE r.tag_id IN (?tag1, ?tag2, ?tag3)
  AND r.object_type = ?
GROUP BY r.object_id
HAVING COUNT(DISTINCT r.tag_id) = 3
```

Optimized via `(object_type, object_id)` composite index + `tag_id` index.

### 4. Category -> Tag Tree

```sql
SELECT * FROM taglib_tag
WHERE category_id = ? AND removed = false
ORDER BY sort_rank
```

Index: `idx_taglib_tag_category_id(category_id)`. Tree structure assembled in Java.

## Database Development Conventions

### Repository Query Method Priority

Use the following priority order for implementing repository queries:

1. **Method name derivation** — for simple single-table queries:
   ```java
   List<TagEntity> findByCategoryIdAndRemovedFalse(UUID categoryId);
   ```

2. **@Query JPQL** — for standard JPA operations, type-safe:
   ```java
   @Query("SELECT t FROM TagEntity t WHERE t.categoryId = :categoryId AND t.removed = false ORDER BY t.sortRank")
   List<TagEntity> findActiveTagsByCategory(@Param("categoryId") UUID categoryId);
   ```

3. **@Query native SQL** — for PostgreSQL-specific features (array operations, GIN indexes):
   ```java
   @Query(value = "SELECT * FROM taglib_tag_relation WHERE :tagId = ANY(tag_ids)", nativeQuery = true)
   List<TagRelationEntity> findByTagIdInArray(@Param("tagId") UUID tagId);
   ```

4. **Specification** — for dynamic condition composition (multi-criteria search).

### Avoid

- Custom Repository implementation classes (`RepositoryCustom` + `Impl`) — only use in extreme complexity scenarios.

### PostgreSQL Array Operation Convention

When querying UUID[] arrays, use native SQL:

```java
@Query(value = "SELECT * FROM org_tree WHERE :nodeId = ANY(path)", nativeQuery = true)
List<OrgTreeNodeEntity> findByPathContains(@Param("nodeId") UUID nodeId);
```

## File Structure

### New Package: `com.reythecoder.taglib`

```
src/main/java/com/reythecoder/taglib/
├── controller/
│   ├── TagCategoryController.java
│   ├── TagController.java
│   └── TagRelationController.java
├── entity/
│   ├── TagCategoryEntity.java
│   ├── TagEntity.java
│   └── TagRelationEntity.java
├── repository/
│   ├── TagCategoryRepository.java
│   ├── TagRepository.java
│   └── TagRelationRepository.java
├── dto/
│   ├── request/
│   │   ├── TagCategoryCreateReq.java
│   │   ├── TagCategoryUpdateReq.java
│   │   ├── TagCreateReq.java
│   │   ├── TagUpdateReq.java
│   │   ├── TagRelationReq.java
│   │   └── TagRelationQueryReq.java
│   └── response/
│       ├── TagCategoryRsp.java
│       ├── TagRsp.java
│       ├── TagTreeRsp.java
│       └── TagRelationRsp.java
├── mapper/
│   ├── TagCategoryMapper.java
│   └── TagMapper.java
├── service/
│   ├── TagCategoryService.java
│   ├── TagService.java
│   └── TagRelationService.java
└── service/impl/
    ├── TagCategoryServiceImpl.java
    ├── TagServiceImpl.java
    └── TagRelationServiceImpl.java
```

### Common Package Migration

```
src/main/java/com/reythecoder/common/
└── utils/
    └── LexoRankUtils.java       # Migrated from com.reythecoder.organization.utils
```

All existing references in `com.reythecoder.organization` and new references in `com.reythecoder.taglib` will import from `com.reythecoder.common.utils`.

### Database Scripts

| File | Content |
|------|---------|
| `backend/db/init-scripts/03-init-taglib-tables.sql` | DDL: CREATE TABLE + indexes + constraints + triggers + comments |
| `backend/db/init-scripts/04-seed-taglib-data.sql` | DML: Sample data INSERT statements |

### API Documentation

| File | Content |
|------|---------|
| `backend/docs/openapi.yaml` | Append tag category, tag, and tag relation schemas + paths |

## Implementation Patterns

Follow existing project conventions:

- **Entities**: UUIDv7 IDs, `@Data` + `@NoArgsConstructor` + `@AllArgsConstructor`, raw UUID references (no JPA `@ManyToOne`)
- **DTOs**: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`, `@NotBlank`/`@NotNull`/`@Size` validation
- **Mappers**: MapStruct with `INSTANCE` singleton, `NullValuePropertyMappingStrategy.IGNORE`
- **Services**: Interface + `@Service @Validated` impl, constructor injection, `ApiException` for errors
- **Controllers**: `@RestController`, `ApiResult<T>` wrapper, `@ResponseStatus` on POST/DELETE
- **Repositories**: Extend `JpaRepository<Entity, UUID>`, derived query methods
- **Database**: `upd_timestamp()` trigger on all tables, `tenant_id` on all tables
