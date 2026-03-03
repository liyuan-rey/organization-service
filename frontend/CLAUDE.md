# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Vue 3 frontend for Organization Management Service.

**Tech Stack:**
- Vue 3.5+ (Composition API)
- Vite 7+
- TypeScript 5.9+
- Tailwind CSS 4+
- Vue Router 5+
- Pinia 3+
- Axios

## Commands

```bash
# Install dependencies
npm install

# Development
npm run dev            # Start dev server at http://localhost:3000
npm run type-check     # Type check only

# Build
npm run build          # Production build to dist/
npm run preview        # Preview production build
```

## Architecture

```
src/
├── api/                    # API clients
│   ├── request.ts          # Axios instance + interceptors + generic methods
│   ├── department.ts       # Department CRUD
│   ├── personnel.ts        # Personnel CRUD
│   ├── position.ts         # Position CRUD
│   ├── departmentPosition.ts
│   └── personnelPosition.ts
├── components/
│   └── common/             # Modal components for CRUD forms
├── layouts/
│   └── MainLayout.vue      # Main layout with navigation
├── router/
│   └── index.ts            # Route definitions
├── stores/
│   ├── index.ts            # Pinia setup
│   ├── department.ts
│   ├── personnel.ts
│   ├── position.ts
│   ├── departmentPosition.ts
│   └── personnelPosition.ts
├── types/
│   └── index.ts            # TypeScript interfaces (ApiResult, entities, DTOs)
├── views/                  # Page components
│   ├── DepartmentList.vue
│   ├── PersonnelList.vue
│   ├── PositionList.vue
│   ├── DepartmentPositionList.vue
│   └── PersonnelPositionList.vue
├── App.vue
└── main.ts
```

## Key Conventions

**TypeScript:**
- All types defined in `types/index.ts`
- `ApiResult<T>` matches backend: `{ status, message, data }`
- Path alias: `@` → `src/`

**API Layer:**
- Base URL: `/api` (proxied to `http://localhost:8080`)
- Generic methods: `get<T>`, `post<T>`, `put<T>`, `del<T>`
- Response interceptor rejects on `status !== 200`

**State Management (Pinia):**
- One store per domain entity
- Stores manage local state for list views and modals

**Components:**
- Use `<script setup>` Composition API
- Modal components in `components/common/` for CRUD forms
- PascalCase naming

**Routes:**
- All routes under `MainLayout`
- Default redirect: `/departments`

## Development Workflow

1. **Start backend**: `cd ../backend && ./gradlew bootRun -Dspring-boot.run.profiles=local`
2. **Start frontend**: `npm run dev`
3. Access: http://localhost:3000

API requests are proxied to backend at `http://localhost:8080` (see `vite.config.ts`)

## Type Definitions

Key interfaces in `types/index.ts`:
- `ApiResult<T>` - API response wrapper
- `Department`, `Personnel`, `Position` - Entity types
- `*CreateReq`, `*UpdateReq` - Request DTOs
- `DepartmentPosition`, `PersonnelPosition` - Association types
