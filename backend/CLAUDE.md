# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for Organization Management Service, named "organization-service".

- **Framework**: Spring Boot 4.0.3 + Java 17
- **Database**: PostgreSQL 15 with Spring Data JPA
- **Build**: Gradle 9+ with parallel/daemon/caching enabled
- **ORM**: Spring Data JPA with Lombok + MapStruct 1.6.3
- **Primary Keys**: UUIDv7 via `UUIDv7.randomUUID()`

Reference: @./README.md

## Project Architecture

Three top-level packages under `com.reythecoder`:

```
src/main/java/com/reythecoder/
├── organization/                          # Core organization domain
│   ├── OrganizationServiceApplication.java
│   ├── controller/       # 14 REST controllers
│   ├── service/          # 14 service interfaces + impl/
│   ├── repository/       # 13 Spring Data JPA repositories
│   ├── entity/           # 14 JPA entities + EntityType enum
│   ├── dto/
│   │   ├── NodeType.java # Enum for tree node types
│   │   ├── request/      # 19 Request DTOs (*Req.java)
│   │   └── response/     # 14 Response DTOs (*Rsp.java) + ApiResult<T>
│   ├── mapper/           # 5 MapStruct mappers
│   ├── exception/        # ApiException + GlobalExceptionHandler
│   └── aspect/           # AOP logging aspect (LoggingAspect)
├── taglib/                                # Tag library management system
│   ├── controller/       # 3 controllers (TagCategory, Tag, TagRelation)
│   ├── service/          # 3 service interfaces + impl/
│   ├── repository/       # 3 repositories
│   ├── entity/           # 3 entities (TagCategory, Tag, TagRelation) + TagObjectType enum
│   ├── dto/
│   │   ├── request/      # 6 Request DTOs
│   │   └── response/     # 4 Response DTOs (TagCategoryRsp, TagRsp, TagTreeRsp, TagRelationRsp)
│   └── mapper/           # 2 MapStruct mappers
└── common/                                # Shared utilities
    └── utils/
        └── LexoRankUtils.java            # LexoRank sorting utility
```

## Key Conventions

- DTOs use `Record` type (no Lombok on DTOs; Lombok used on entities)
- Request DTOs suffixed with `Req`, Response DTOs with `Rsp`
- `ApiResult<T>` returns `{ code, message, data }` — all controllers use this wrapper
- Primary keys use UUIDv7 via `UUIDv7.randomUUID()`
- Service layer: interface in `service/`, implementation in `service/impl/`
- MapStruct mappers use `componentModel = "spring"`
- 前后端交互遵循 `backend/docs/openapi.yaml`，这是符合 OpenAPI 3.1 规范的接口描述

## Controllers

**Organization domain** (14 controllers):
- `DepartmentController`, `PersonnelController`, `PositionController`, `GroupController`
- `DepartmentPersonnelController`, `DepartmentPositionController`, `PersonnelPositionController`
- `DepartmentGroupController`, `GroupDepartmentController`, `GroupPersonnelController`
- `DepartmentHierarchyController`, `GroupHierarchyController`
- `OrgTreeNodeController` — org tree node CRUD with LexoRank ordering
- `TreeController` — generic tree operations

**Taglib domain** (3 controllers):
- `TagCategoryController`, `TagController`, `TagRelationController`

## Entities

**Core**: `DepartmentEntity`, `PersonnelEntity`, `PositionEntity`, `GroupEntity`, `OrgTreeNodeEntity`
**Associations**: `DepartmentPersonnelEntity`, `DepartmentPositionEntity`, `PersonnelPositionEntity`, `DepartmentGroupEntity`, `GroupDepartmentEntity`, `GroupPersonnelEntity`, `DepartmentHierarchyEntity`, `GroupHierarchyEntity`
**Enums**: `EntityType`, `NodeType`
**Taglib**: `TagCategoryEntity`, `TagEntity`, `TagRelationEntity`, `TagObjectType` (enum)

## Configuration

- **Profiles**: `dev`, `prod`, `test` (no `local` profile — use `dev` for local development)
- **Test profile**: `ddl-auto: create-drop`, `show-sql: true`, Testcontainers reuse
- **JPA**: `ddl-auto: validate` in non-test profiles
- `.env` for database credentials (see `.env.example`)
- PostgreSQL on port `5432` (Docker via `docker-compose.yml`)

## Logging

Uses Logback with `logback-spring.xml` configuration:

- Console and file logging enabled
- Log files stored in `logs/` directory
- Rolling policy: 10MB max file size, 30 days retention
- JSON logging via Logstash encoder for centralized log management
- Profile-specific levels: `dev` = DEBUG, `prod` = WARN, `test` = INFO

## Database Scripts (`db/init-scripts/`)

- `01-init-organization-tables.sql` — 组织机构全部库表 DDL（部门、人员、职位、分组、层级、关联、组织树）
- `02-init-taglib-tables.sql` — 标签库全部库表 DDL（分类、标签、关联）
- `03-seed-sample-data.sql` — 全部示例数据（组织机构 + 组织树 + 职位关联 + 标签库）

## Database Design Conventions

See: @./docs/database-design-guide-for-postgresql.md

## Development Guidelines

See: @./docs/development-guidelines.md

## Testing

- Unit tests: `./gradlew test` (excludes `@Tag("integration")`)
- Integration tests: `./gradlew testIntegration` (requires Docker)
- Testcontainers for PostgreSQL in integration tests
- Mockito for unit test mocking
- Tests mirror source structure under `src/test/java/com/reythecoder/`

## Additional Instructions

Git instructions: @./docs/git-instructions.md
