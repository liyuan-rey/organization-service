# Frontend CLAUDE.md

本文档为前端项目提供开发指导，基于 Vben Admin v5.6.0。

## 项目概述

前端采用 **Monorepo** 结构，使用 pnpm workspaces 管理。主应用为 `web-ele`，使用 Element Plus UI 框架。

### 技术栈

| 分类 | 技术 |
|------|------|
| 包管理器 | pnpm (monorepo workspaces) |
| 构建工具 | Vite |
| UI 框架 | Element Plus |
| CSS 框架 | Tailwind CSS v4.x |
| 语言 | TypeScript, Vue 3 (Composition API) |
| 状态管理 | Pinia |
| 测试 | Vitest |
| 代码检查 | ESLint, Stylelint, Prettier, Commitlint |
| 构建编排 |  |
| Git Hooks | Lefthook |

## 常用命令

```bash
# 安装依赖
pnpm install

# 开发
pnpm dev              # 启动开发服务器（交互式选择应用）
pnpm dev:ele          # 单独启动 web-ele 应用
pnpm dev:docs         # 启动文档服务

# 构建
pnpm build            # 构建所有
pnpm build:ele        # 构建 web-ele
pnpm build:analyze    # 构建并分析体积
pnpm preview          # 预览构建结果

# 代码质量
pnpm lint             # 代码检查
pnpm format           # 格式化代码
pnpm check:type       # 类型检查
pnpm check:circular   # 循环引用检查
pnpm check:dep        # 依赖检查
pnpm check            # 运行所有检查

# 测试
pnpm test:unit        # 单元测试
pnpm test:e2e         # E2E 测试

# 其他
pnpm reinstall        # 重新安装依赖
pnpm commit           # 交互式提交
```

## 架构说明

### 目录结构

```
frontend/
├── apps/                    # 应用目录
│   ├── backend-mock/        # Mock 后端服务 (Nitro)
│   └── web-ele/             # Element Plus 前端应用
├── packages/                # 共享包
│   ├── @core/               # 核心包
│   │   ├── base/            # 基础工具 (design, icons, shared, typings)
│   │   ├── composables/     # Vue composables
│   │   ├── preferences/     # 偏好设置处理
│   │   └── ui-kit/          # UI 组件 (layout, menu, shadcn-ui, tabs)
│   ├── effects/             # 副作用包 (access, plugins, common-ui, hooks, layouts, request)
│   ├── constants/           # 常量
│   ├── icons/               # 图标
│   ├── locales/             # 国际化
│   ├── preferences/         # 偏好设置
│   ├── stores/              # Pinia 状态存储
│   ├── styles/              # 样式
│   ├── types/               # TypeScript 类型
│   └── utils/               # 工具函数
├── internal/                # 内部工具
│   ├── lint-configs/        # Lint 配置 (commitlint, eslint, prettier, stylelint)
│   ├── node-utils/          # Node.js 工具
│   ├── tailwind-config/     # Tailwind CSS 配置
│   ├── tsconfig/            # TypeScript 配置
│   └── vite-config/         # Vite 构建配置
├── docs/                    # 文档 (VitePress)
├── scripts/                 # 脚本
│   ├── turbo-run/           # Turbo runner 脚本
│   └── vsh/                 # VSH CLI 脚本
└── playground/              # 示例/演示区域
```

### 应用目录结构 (apps/web-ele/src/)

```
src/
├── adapter/       # 适配器 (表单、表格、组件)
├── api/           # API 请求
├── layouts/       # 布局组件
├── locales/       # 国际化资源
├── router/        # 路由配置
│   └── routes/
│       ├── core/      # 核心路由 (根路由、登录、404 等)
│       ├── static/    # 静态路由
│       └── modules/   # 动态路由模块
├── store/         # Pinia 状态存储
├── views/         # 页面组件
├── app.vue        # 根组件
├── bootstrap.ts   # 应用初始化
├── main.ts        # 入口文件
└── preferences.ts # 偏好设置配置
```

### 路径别名

使用 `#` 开头的路径别名，通过 Node.js subpath imports 实现：

```json
// package.json
{
  "imports": {
    "#/*": "./src/*"
  }
}
```

```json
// tsconfig.json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "#/*": ["src/*"]
    }
  }
}
```

使用示例：
```ts
import { useAuthStore } from '#/store/auth';
```

## 开发规范

### Vue 组件

- 使用 **Vue 3 Composition API** + `<script setup>` 语法
- 组件命名使用 **PascalCase**
- 页面组件放置在 `views/` 目录

```vue
<script setup lang="ts">
import { ref } from 'vue';

const count = ref(0);
</script>

<template>
  <div>{{ count }}</div>
</template>
```

### 样式规范

- 预处理器：使用 **SCSS**
- CSS 框架：**Tailwind CSS v4.x**（注意：不兼容 v3.x）
- 命名规范：推荐使用 `useNamespace` 函数生成 BEM 命名
- 支持 CSS Modules

### 图标使用

支持三种方式：

1. **Iconify 图标**（推荐）：在 `packages/icons/src/iconify` 目录管理
2. **SVG 图标**（推荐）：在 `packages/icons/src/svg/icons` 存放 svg 文件
3. **Tailwind CSS 图标**：使用 `icon-[mdi--xxx]` 类名

### Git 提交规范

使用 Angular 风格：

| 类型 | 描述 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `style` | 代码格式（不影响运行） |
| `perf` | 性能优化 |
| `refactor` | 重构 |
| `revert` | 回滚 |
| `test` | 测试相关 |
| `docs` | 文档 |
| `chore` | 构建/工具变动 |
| `workflow` | 工作流改进 |
| `ci` | CI/CD 变动 |
| `types` | 类型定义变动 |

示例：
```
feat: 添加用户管理模块
fix: 修复登录页面样式问题
```

## 配置说明

### 环境变量

在 `apps/web-ele/.env.development` 或 `.env.production` 中配置：

```bash
# 接口地址
VITE_GLOB_API_URL=/api

# 是否开启 Mock
VITE_NITRO_MOCK=true

# 是否开启 devtools
VITE_DEVTOOLS=true

# 是否注入全局 loading
VITE_INJECT_APP_LOADING=true

# 压缩方式：none, brotli, gzip
VITE_COMPRESS=gzip

# 是否开启 PWA
VITE_PWA=false

# 路由模式：history 或 hash
VITE_ROUTER_HISTORY=hash
```

以 `VITE_GLOB_*` 开头的变量会注入到 `_app.config.js`，生产环境可动态修改。

### 偏好设置

在 `src/preferences.ts` 中配置：

```ts
import { defineOverridesPreferences } from '@vben/preferences';

export const overridesPreferences = defineOverridesPreferences({
  // 布局配置
  header: { enable: true },
  sidebar: { collapsed: false },
  tabbar: { enable: true },
  footer: { enable: false },

  // 主题配置
  theme: { mode: 'light' },

  // 功能配置
  watermark: { enable: false },
  lockScreen: { enable: false },
});
```

### API 代理配置

开发环境通过 Vite proxy 处理跨域，在 `vite.config.mts` 中配置：

```ts
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

## 路由

### 路由定义

```ts
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:home',
      title: $t('page.home.title'),  // 国际化
      order: 1,
      authority: ['admin'],          // 权限控制
      keepAlive: true,               // 缓存
    },
    name: 'Home',
    path: '/home',
    children: [
      {
        name: 'HomeIndex',
        path: '/home/index',
        component: () => import('#/views/home/index.vue'),
      },
    ],
  },
];
```

### 权限控制

通过 `meta` 字段控制：

| 字段 | 说明 |
|------|------|
| `authority` | 需要的角色标识数组 |
| `ignoreAccess` | 忽略权限检查 |
| `menuVisibleWithForbidden` | 菜单可见但访问重定向到 403 |

### 标签页管理

- Tab key 优先级：`query.pageKey` > `fullPath` > `path`
- 支持固定标签页 (`affixTab`)
- 支持最大打开数量限制 (`maxNumOfOpenTab`)
- 支持 KeepAlive 缓存

## API 与状态管理

### 请求封装

基于 axios 封装，核心由 `@vben/request` 提供。在 `src/api/request.ts` 中配置。

### Mock 服务

使用 Nitro 作为本地 Mock 服务器，位于 `apps/backend-mock`。

开启 Mock：
```bash
VITE_NITRO_MOCK=true
```

### Pinia 存储

共享存储位于 `packages/stores/`，应用级存储位于 `apps/web-ele/src/store/`。

## 测试与质量

### 单元测试

使用 Vitest：
```bash
pnpm test:unit
```

测试文件使用 `.test.ts` 后缀或放置在 `__tests__` 目录。

### 代码检查

| 工具 | 用途 |
|------|------|
| ESLint | JavaScript/TypeScript 代码质量 |
| Stylelint | CSS/Vue 样式验证 |
| Prettier | 代码格式化 |
| Commitlint | Git 提交信息验证 |
| Cspell | 拼写检查 |

### Git Hooks (Lefthook)

- `pre-commit`: 运行代码格式化和检查
- `commit-msg`: 验证提交信息格式
- `post-merge`: 自动安装依赖

## 开发工作流

1. **启动依赖服务**（如后端 API、数据库）
2. **安装依赖**: `pnpm install`
3. **启动开发服务器**: `pnpm dev:ele`
4. **访问应用**: http://localhost:5555（默认端口）
5. **开发完成后提交**: `pnpm commit`

## 文档参考Turborepo

- [项目目录结构](docs/src/guide/project/dir.md)
- [开发规范](docs/src/guide/project/standard.md)
- [开发入门](docs/src/guide/essentials/development.md)
- [路由配置](docs/src/guide/essentials/route.md)
- [服务端交互](docs/src/guide/essentials/server.md)