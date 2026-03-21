# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Monorepo containing a full-stack Organization Management Service with:
- **Backend**: Spring Boot 4.0 + Java 17 + PostgreSQL (in `backend/`)
- **Frontend**: Vue 3 + Vite + TypeScript + Tailwind CSS + Element Plus + shadcn-vue (in `frontend/`)

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
npm install

# Development
npm run dev            # Start dev server at http://localhost:3000
npm run type-check     # Type check only

# Build
npm run build          # Production build to dist/
npm run preview        # Preview production build

# Screenshots (using Puppeteer)
node screenshot.cjs    # Capture screenshots to frontend/screenshots/
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

```
src/
├── api/              # Axios-based API clients
├── components/
│   ├── common/       # Business modal components
│   └── ui/           # shadcn-vue UI components (Button, Card, Dialog, etc.)
├── layouts/          # Layout components (TopBarLayout.vue)
├── router/           # Vue Router config
├── stores/           # Pinia stores (including theme.ts for dark mode)
├── types/            # TypeScript types
└── views/            # Page components
```

**Technology Stack:**
- **Vue 3.5+**: Composition API with `<script setup>`
- **Element Plus**: Complex components (Table, Form, Select, DatePicker, etc.)
- **shadcn-vue**: Base UI components (Button, Card, Dialog, AlertDialog, Badge)
- **Tailwind CSS 4**: Styling with CSS variables for theming
- **lucide-vue-next**: Primary icon library
- **@element-plus/icons-vue**: Secondary icons (only when lucide doesn't have the icon)

**Key Conventions:**
- Composition API with `<script setup>`
- PascalCase for component names
- API base URL proxied to backend at `http://localhost:8080`
- Dark/Light theme toggle via `useThemeStore()`
- Views support table/card toggle and batch operations
- **Icons**: Prefer lucide-vue-next over @element-plus/icons-vue

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
- Theme preference stored in localStorage

## Development Workflow

1. **Start database**: `cd backend && docker-compose up -d`
2. **Start backend**: `cd backend && ./gradlew bootRun -Dspring-boot.run.profiles=local`
3. **Start frontend**: `cd frontend && npm run dev`
4. Access: Frontend http://localhost:3000, Backend API http://localhost:8080, Swagger http://localhost:8080/swagger-ui.html

## Testing

- Unit tests: `./gradlew test` (excludes `@Tag("integration")`)
- Integration tests: `./gradlew testIntegration` (requires Docker)
- Use Testcontainers for PostgreSQL in integration tests
- Mockito for unit test mocking

## Docs

- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)
- [Project Architecture](backend/docs/project-architecture.md)
- [Database Design Guide](backend/docs/database-design-develop-guide-for-postgresql.md)
- [Development Guidelines](backend/docs/development-guidelines.md)
- [API Documentation](backend/docs/API_DOCUMENTATION.md)

## Screenshot Tool

For UI verification, use Playwright to capture screenshots:

```bash
# Prerequisites
cd frontend
npm install  # Playwright is included in devDependencies

# Install Playwright browsers (use mirror for China if needed)
# China mirror (faster in China):
PLAYWRIGHT_DOWNLOAD_HOST=https://npmmirror.com/mirrors/playwright npx playwright install chromium
# Or use official source:
npx playwright install chromium

# Start dev server first
npm run dev

# In another terminal, capture screenshots
node screenshot.cjs
```

Screenshots are saved to `frontend/screenshots/` directory.
