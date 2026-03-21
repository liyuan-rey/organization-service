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
- **Element Plus** - Complex UI components (Table, Form, Select, DatePicker, etc.)
- **shadcn-vue** - Base UI components (Button, Card, Dialog, AlertDialog, Badge)
- **lucide-vue-next** + **@element-plus/icons-vue** - Icon libraries

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

# Screenshots (using Puppeteer)
node screenshot.cjs    # Capture screenshots to screenshots/
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
│   ├── common/             # Modal components for CRUD forms
│   └── ui/                 # shadcn-vue UI components (Button, Card, Dialog, etc.)
├── layouts/
│   └── TopBarLayout.vue    # Top navigation layout with theme toggle
├── router/
│   └── index.ts            # Route definitions
├── stores/
│   ├── index.ts            # Pinia setup
│   ├── theme.ts            # Dark/Light theme management
│   ├── department.ts
│   ├── personnel.ts
│   ├── position.ts
│   ├── departmentPosition.ts
│   └── personnelPosition.ts
├── types/
│   └── index.ts            # TypeScript interfaces (ApiResult, entities, DTOs)
├── views/                  # Page components
│   ├── Dashboard.vue       # Dashboard with statistics
│   ├── DepartmentList.vue
│   ├── PersonnelList.vue
│   ├── PositionList.vue
│   ├── DepartmentPositionList.vue
│   └── PersonnelPositionList.vue
├── App.vue
├── main.ts                 # Entry point with Element Plus setup
└── style.css               # Tailwind CSS + theme variables
```

## Key Conventions

**TypeScript:**
- All types defined in `types/index.ts`
- `ApiResult<T>` matches backend: `{ code, message, data }`
- Path alias: `@` → `src/`

**API Layer:**
- Base URL: `/api` (proxied to `http://localhost:8080`)
- Generic methods: `get<T>`, `post<T>`, `put<T>`, `del<T>`
- Response interceptor rejects on `code !== 200`

**State Management (Pinia):**
- One store per domain entity
- Stores manage local state for list views and modals
- `theme.ts` manages dark/light mode with localStorage persistence

**Components:**
- Use `<script setup>` Composition API
- Modal components in `components/common/` for CRUD forms
- PascalCase naming
- **Element Plus** for complex components (el-table, el-form, el-dialog, etc.)
- **shadcn-vue** for base components (Button, Card, Badge, etc.)

**Icons:**
- **Prefer lucide-vue-next** for all icons (Plus, Search, Edit, Trash, MoreVertical, etc.)
- Only use `@element-plus/icons-vue` when lucide-vue-next doesn't have the icon
- Common lucide icons: `Plus`, `Search`, `Edit`, `Trash`, `List`, `Grid`, `MoreVertical`, `X`, `Menu`, `Sun`, `Moon`

**Views:**
- Support table/card view toggle
- Include search and filter functionality
- Support batch selection and deletion
- Dark/Light theme compatible

**Routes:**
- All routes under `TopBarLayout`
- Default redirect: `/dashboard`

**Theming:**
- CSS variables for colors (see `style.css`)
- Dark mode via `.dark` class on `<html>`
- Theme toggle via `useThemeStore().toggleTheme()`

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