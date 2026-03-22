# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Monorepo containing a full-stack Organization Management Service with:
- **Backend**: Spring Boot 4.0 + Java 17 + PostgreSQL (in `backend/`)
- **Frontend**: Vue 3 + Vite + TypeScript + Tailwind CSS + Element Plus (in `frontend/`)

Frontend is based on **Vben Admin v5.6.0** with pnpm monorepo structure.

## Quick Commands

### Backend (run from `backend/` directory)

```bash
# Build
./gradlew clean build

# Run with local profile
./gradlew bootRun -Dspring-boot.run.profiles=local

# Tests
./gradlew test                    # Unit tests
./gradlew testIntegration         # Integration tests (requires Docker)
./gradlew test jacocoTestReport   # Coverage report

# Start database (Docker)
docker-compose up -d
```

### Frontend (run from `frontend/` directory)

```bash
# Install dependencies
pnpm install

# Development
pnpm dev              # Start dev server (interactive app selection)
pnpm dev:ele          # Start web-ele app specifically

# Build
pnpm build            # Build all
pnpm build:ele        # Build web-ele
pnpm preview          # Preview production build

# Code Quality
pnpm lint             # Lint code
pnpm format           # Format code
pnpm check:type       # Type check

# Testing
pnpm test:unit        # Unit tests
```

## Architecture

### Backend Structure (`backend/`)

```
src/main/java/com/reythecoder/organization/
├── controller/       # REST controllers (Department, Personnel, Position, Group + associations)
├── service/          # Business logic (interface + impl/)
├── repository/       # Spring Data JPA repositories
├── entity/           # JPA entities
├── dto/
│   ├── request/      # Request DTOs (*Req.java)
│   └── response/     # Response DTOs (*Rsp.java) + ApiResult
├── mapper/           # MapStruct mappers
├── exception/        # ApiException + GlobalExceptionHandler
└── aspect/           # AOP logging aspect
```

**Key Conventions:**
- DTOs use `Record` type (no Lombok)
- Request DTOs suffixed with `Req`, Response DTOs with `Rsp`
- `ApiResult<T>` returns `{ code, message, data }`
- Primary keys use UUIDv7 via `UUIDv7.randomUUID()`
- All controllers return `ApiResult<T>` wrapped responses

### Frontend Structure (`frontend/`)

Monorepo structure managed by pnpm workspaces:

```
frontend/
├── apps/                    # Applications
│   ├── backend-mock/        # Nitro mock server
│   └── web-ele/             # Element Plus app (main)
│       └── src/
│           ├── api/         # API requests
│           ├── adapter/     # Form/table adapters
│           ├── layouts/     # Layout components
│           ├── router/      # Vue Router config
│           ├── store/       # Pinia stores
│           ├── views/       # Page components
│           └── locales/     # i18n resources
├── packages/                # Shared packages
│   ├── @core/               # Core packages (base, composables, ui-kit)
│   ├── effects/             # Side effects (access, plugins, request)
│   ├── stores/              # Shared Pinia stores
│   ├── types/               # TypeScript types
│   └── utils/               # Utilities
├── internal/                # Internal tooling (lint, vite, tailwind configs)
└── docs/                    # VitePress documentation
```

**Key Conventions:**
- Vue 3 Composition API with `<script setup>`
- PascalCase for component names
- Path alias: `#/*` maps to `./src/*`
- Icons: Use Tailwind CSS classes like `icon-[lucide--search]`
- API base URL proxied to backend at `http://localhost:8080`

## Core Entities

- **Main**: Department, Personnel, Position, Group
- **Associations**: DepartmentPersonnel, PersonnelPosition, DepartmentPosition, GroupDepartment, GroupPersonnel
- **Hierarchies**: DepartmentHierarchy, GroupHierarchy

## Configuration

### Backend
- Multi-profile: `local`, `dev`, `prod`
- `.env` for local database credentials
- PostgreSQL on port `15432` (Docker)

### Frontend
- Vite proxy to backend in dev mode
- No `.env` required for local development

## Development Workflow

1. **Start database**: `cd backend && docker-compose up -d`
2. **Start backend**: `cd backend && ./gradlew bootRun -Dspring-boot.run.profiles=local`
3. **Start frontend**: `cd frontend && pnpm dev:ele`
4. Access: Frontend http://localhost:5555, Backend API http://localhost:8080

## Testing

- Unit tests: `./gradlew test` (excludes `@Tag("integration")`)
- Integration tests: `./gradlew testIntegration` (requires Docker)
- Use Testcontainers for PostgreSQL in integration tests
- Mockito for unit test mocking

## Docs

- [Backend README](backend/README.md)
- [Project Architecture](backend/docs/project-architecture.md)
- [Database Design Guide](backend/docs/database-design-develop-guide-for-postgresql.md)
- [Development Guidelines](backend/docs/development-guidelines.md)
- [API Documentation](backend/docs/API_DOCUMENTATION.md)
- [Frontend CLAUDE.md](frontend/CLAUDE.md) - Detailed frontend development guide
