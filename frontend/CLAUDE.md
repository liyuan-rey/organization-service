# Frontend CLAUDE.md

本文档为前端项目提供开发指导，基于 Vben Admin v5.5.9/v5.6.0。

## 项目概述

前端采用 **pnpm Monorepo** 结构（使用 Turborepo 编排构建），主应用为 `web-ele`，使用 **Element Plus** UI 框架。Node.js >= 20.19.0，pnpm 10.28.2。

### 技术栈

| 分类 | 技术 |
|------|------|
| 包管理器 | pnpm (monorepo workspaces + catalog 协议) |
| 构建编排 | Turborepo |
| 构建工具 | Vite |
| UI 框架 | Element Plus (通过 Adapter 适配到 Vben 通用接口) |
| CSS 框架 | Tailwind CSS v4.x (不兼容 v3.x) |
| 语言 | TypeScript (ESM 模式), Vue 3 (Composition API + `<script setup>`) |
| 状态管理 | Pinia (含 pinia-plugin-persistedstate 持久化) |
| 路由 | Vue Router (支持 hash/history 模式) |
| HTTP 请求 | Axios (封装为 @vben/request) |
| 国际化 | Vue I18n |
| 测试 | Vitest |
| 代码质量 | ESLint, Stylelint, Prettier, Commitlint, Cspell |
| Git Hooks | Lefthook |
| Mock 服务 | Nitro (apps/backend-mock) |

## 常用命令

```bash
# 安装依赖
pnpm install

# 开发
pnpm dev              # 交互式启动 (turbo-run)
pnpm dev:ele          # 单独启动 web-ele 应用

# 构建
pnpm build            # 构建所有 (Turbo 并行)
pnpm build:ele        # 构建 web-ele
pnpm build:analyze    # 构建并分析体积
pnpm preview          # 预览构建结果

# 代码质量
pnpm lint             # ESLint + Stylelint 检查
pnpm format           # Prettier 格式化
pnpm check:type       # TypeScript 类型检查
pnpm check:circular   # 循环依赖检查
pnpm check:dep        # 依赖检查
pnpm check            # 运行所有检查

# 测试
pnpm test:unit        # 单元测试 (Vitest)
pnpm test:e2e         # E2E 测试

# 其他
pnpm reinstall        # 清除 node_modules 并重装
pnpm commit           # 交互式提交 (czg)
pnpm dev:docs         # 启动文档站 (VitePress)
```

## 目录结构

```
frontend/
├── apps/                          # 应用目录
│   ├── backend-mock/              # Mock 后端服务 (Nitro)
│   └── web-ele/                   # 主前端应用 (Element Plus)
├── packages/                      # 共享包 (通过 workspace:* 引用)
│   ├── @core/                     # 核心框架包
│   │   ├── base/                  #   基础: design, icons, shared, typings
│   │   ├── composables/           #   Vue composables
│   │   ├── preferences/           #   偏好设置引擎
│   │   └── ui-kit/                #   UI 组件: form-ui, layout-ui, menu-ui, popup-ui, shadcn-ui, tabs-ui
│   ├── effects/                   # 副作用包
│   │   ├── access/                #   路由/菜单权限控制
│   │   ├── common-ui/             #   通用业务组件 (Page, Authentication, ApiComponent 等)
│   │   ├── hooks/                 #   Vue hooks (useAppConfig, useElementPlusDesignTokens 等)
│   │   ├── layouts/               #   布局组件 (basic, authentication, iframe, widgets)
│   │   ├── plugins/               #   插件集成 (echarts, motion, vxe-table)
│   │   └── request/               #   HTTP 请求封装 (RequestClient + 拦截器)
│   ├── constants/                 # 共享常量
│   ├── icons/                     # 图标库 (Iconify + SVG)
│   ├── locales/                   # 国际化核心 (Vue I18n 封装)
│   ├── preferences/               # 偏好设置公共 API
│   ├── stores/                    # 共享 Pinia 存储 (access, user)
│   ├── styles/                    # 全局样式 (含 Element Plus 集成)
│   ├── types/                     # 共享 TypeScript 类型
│   └── utils/                     # 工具函数 (mergeRouteModules, traverseTreeValues 等)
├── internal/                      # 内部工具
│   ├── lint-configs/              #   代码检查: commitlint, eslint, prettier, stylelint
│   ├── node-utils/                #   Node.js 构建工具
│   ├── tailwind-config/           #   Tailwind CSS 共享配置
│   ├── tsconfig/                  #   TypeScript 配置: base, web-app, node, library
│   └── vite-config/               #   Vite 构建配置工厂 (defineConfig)
├── scripts/                       # 脚本
│   ├── deploy/                    #   部署脚本 (Docker)
│   ├── turbo-run/                 #   Turborepo runner CLI
│   └── vsh/                       #   VSH CLI (lint, 循环依赖检查)
├── docs/                          # 文档站 (VitePress)
├── playground/                    # 演示区
├── package.json                   # 根 monorepo 配置
├── pnpm-workspace.yaml            # workspace 定义 + catalog 版本管理
├── turbo.json                     # Turborepo 管道配置
├── vitest.config.ts               # Vitest 根配置
├── eslint.config.mjs              # ESLint 入口
├── lefthook.yml                   # Git hooks (pre-commit, commit-msg, post-merge)
└── .commitlintrc.js               # Commitlint 配置
```

## 应用结构 (apps/web-ele/src/)

```
src/
├── adapter/                  # 适配器层
│   ├── component/index.ts    #   Element Plus 组件适配 (映射到 Vben 通用接口)
│   ├── form.ts               #   表单配置 (验证规则, 字段映射)
│   └── vxe-table.ts          #   VxeTable 网格配置 (分页, 单元格渲染器)
├── api/                      # API 请求层
│   ├── request.ts            #   RequestClient 工厂 (axios 封装 + 拦截器)
│   ├── core/                 #   核心 API
│   │   ├── auth.ts           #     登录/登出/刷新令牌/权限码
│   │   ├── user.ts           #     获取用户信息
│   │   └── menu.ts           #     获取菜单数据
│   └── organization/         #   业务 API
│       └── index.ts          #     分组列表、部门列表
├── layouts/                  # 布局组件
│   ├── basic.vue             #   主布局 (侧边栏/头部/通知/锁屏)
│   └── auth.vue              #   认证页布局
├── locales/                  # 国际化资源
│   └── langs/
│       ├── en-US/            #   英文翻译 (page.json, demos.json)
│       └── zh-CN/            #   中文翻译 (page.json, demos.json)
├── router/                   # 路由配置
│   ├── index.ts              #   Vue Router 实例
│   ├── access.ts             #   动态路由生成 (generateAccess)
│   ├── guard.ts              #   路由守卫 (进度条/认证/令牌刷新)
│   └── routes/
│       ├── index.ts          #   路由合并入口 (glob 导入 modules)
│       ├── core.ts           #   核心路由 (根路由, 认证, 404)
│       └── modules/          #   业务路由模块
│           ├── dashboard.ts  #     仪表盘
│           ├── demos.ts      #     演示页
│           ├── organization.ts #   组织机构
│           └── vben.ts       #     Vben Admin 信息页
├── store/                    # 应用级 Pinia 存储
│   └── auth.ts               #   认证存储 (login/logout/fetchUserInfo)
├── views/                    # 页面组件
│   ├── _core/                #   框架内置页面
│   │   ├── authentication/   #     登录/注册/忘记密码等
│   │   ├── fallback/         #     403/404/500/coming-soon
│   │   ├── about/            #     关于页
│   │   └── profile/          #     个人设置
│   ├── dashboard/            #   仪表盘 (analytics, workspace)
│   ├── demos/                #   演示页 (element, form)
│   └── organization/         #   组织机构业务页
│       ├── address-book/     #     通讯录
│       └── structure/        #     结构维护
├── app.vue                   # 根组件 (ElConfigProvider + RouterView)
├── bootstrap.ts              # 应用初始化 (适配器/i18n/Pinia/指令/插件)
├── main.ts                   # 入口 (初始化偏好设置 → bootstrap)
└── preferences.ts            # 应用偏好覆盖 (当前仅覆盖 app.name)
```

## 路径别名

使用 `#` 前缀别名，通过 Node.js subpath imports 实现（运行时和类型均生效）：

```jsonc
// apps/web-ele/package.json
{ "imports": { "#/*": "./src/*" } }

// apps/web-ele/tsconfig.json
{ "compilerOptions": { "paths": { "#/*": ["src/*"] } } }
```

使用示例：
```ts
import { useAuthStore } from '#/store/auth';
```

## 开发规范

### Vue 组件

- 使用 **Vue 3 Composition API** + `<script setup>` 语法
- 组件命名使用 **PascalCase**
- 页面组件放置在 `views/` 目录，路由模块放在 `router/routes/modules/`

### 样式

- CSS 框架：**Tailwind CSS v4.x**（不兼容 v3.x，务必检查版本）
- 预处理器：**SCSS**
- BEM 命名：使用 `useNamespace` 函数或全局 SCSS mixin (`b()`, `e()`, `is()`)
- 也支持 CSS Modules（`<style module>`）

### 图标

三种方式，均在 `@vben/icons` 统一管理：

1. **Iconify 图标**（推荐）：`import { createIconifyIcon } from '@vben/icons'`
2. **SVG 图标**（推荐）：放入 `packages/icons/src/svg/icons/`，自动注册
3. **Tailwind CSS 图标**：类名 `icon-[mdi--xxx]`（无需导入）

### Git 提交规范

使用 Angular 风格（Commitlint 强制）：

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

### 代码检查

| 工具 | 配置位置 | 用途 |
|------|---------|------|
| ESLint | `eslint.config.mjs` → `internal/lint-configs/eslint-config` | JS/TS 代码质量 |
| Stylelint | `stylelint.config.mjs` → `internal/lint-configs/stylelint-config` | CSS/Vue 样式验证 |
| Prettier | `.prettierrc.mjs` → `internal/lint-configs/prettier-config` | 代码格式化 |
| Commitlint | `.commitlintrc.js` → `internal/lint-configs/commitlint-config` | 提交信息格式 |
| Cspell | `cspell.json` | 拼写检查 |

Git Hooks（Lefthook, `lefthook.yml`）：
- `pre-commit`: 并行执行格式化和 lint 检查
- `commit-msg`: Commitlint 验证
- `post-merge`: 自动 `pnpm install`

## 路由

### 路由类型

| 类型 | 位置 | 说明 |
|------|------|------|
| Core | `routes/core.ts` | 框架内置 (根路由, 认证页, 404) |
| Static | `routes/index.ts` | 静态路由 (需手动开启) |
| Dynamic | `routes/modules/*.ts` | 业务路由 (通过 glob 自动导入) |

### 路由 meta 字段

```ts
meta: {
  title: '页面标题',           // 支持 i18n key
  icon: 'lucide:building-2',   // 菜单图标
  order: 100,                  // 菜单排序 (仅一级)
  authority: ['admin'],        // 角色权限
  keepAlive: true,             // 页面缓存 (需 tabbar 启用)
  hideInMenu: false,           // 隐藏菜单项
  hideInTab: false,            // 隐藏标签页
  hideChildrenInMenu: false,   // 隐藏子菜单
  affixTab: false,             // 固定标签页
  iframeSrc: '',               // 内嵌 iframe
  link: '',                    // 外部链接 (新窗口打开)
  activePath: '',              // 激活的菜单路径
  menuVisibleWithForbidden: false, // 菜单可见但访问重定向到 403
}
```

### 当前路由模块

| 模块 | 路径 | 子路由 |
|------|------|--------|
| Dashboard | `/dashboard` | Analytics (固定), Workspace |
| Organization | `/organization` | Address Book, Structure |
| Demos | `/demos` | Element Plus, Form |
| Vben Admin | `/vben-admin` | Document, GitHub, About, Profile (含 iframe) |

### 标签页

- Tab key 优先级：`query.pageKey` > `fullPath` > `path`
- 支持固定标签页 (`affixTab`)、最大数量限制 (`maxNumOfOpenTab`)、KeepAlive 缓存
- 刷新页面使用 `useRefresh()` from `@vben/hooks`

## API 与请求

### 请求封装

基于 axios 封装为 `RequestClient`（`@vben/request`），在 `src/api/request.ts` 配置：

- **两个导出客户端**：
  - `requestClient`：自动提取 `response.data.data`（常用）
  - `baseRequestClient`：返回原始响应
- **请求拦截器**：注入 `Authorization: Bearer <token>` 和 `Accept-Language` 头
- **响应拦截器**：
  1. `defaultResponseInterceptor` — 解包 `{ code: 0, data, message }` 响应格式
  2. `authenticateResponseInterceptor` — 处理 401、自动刷新令牌
  3. `errorMessageResponseInterceptor` — `ElMessage.error()` 错误提示

### 后端响应格式

后端 API 必须返回：
```json
{ "code": 0, "data": T, "message": "success" }
```
`code: 0` 表示成功，其他值为失败。

### 核心 API

| 模块 | 端点 | 说明 |
|------|------|------|
| Auth | `POST /auth/login` | 登录，返回 `{ accessToken }` |
| Auth | `POST /auth/refresh` | 刷新令牌 |
| Auth | `POST /auth/logout` | 登出 |
| Auth | `GET /auth/codes` | 获取权限码数组 |
| User | `GET /user/info` | 用户信息 (`{ roles, realName }`) |
| Menu | `GET /menu/all` | 菜单数据 |
| Organization | `GET /organization/group/list` | 分组列表 |
| Organization | `GET /organization/department/list` | 部门列表 (分页) |

### Mock 服务

- 位于 `apps/backend-mock/`，使用 Nitro
- 通过 `VITE_NITRO_MOCK=true` 开启
- 仅用于开发环境

## 权限控制

三种模式（通过 `preferences.ts` 的 `accessMode` 设置）：

1. **frontend**：路由 `meta.authority` 硬编码角色，前端匹配
2. **backend**：菜单/路由从 API 获取，后端动态返回
3. **mixed**：前端权限 + 后端菜单合并

按钮级权限：
- 权限码：`<AccessControl :codes="['AC_100100']" type="code">` 或 `v-access:code="'AC_100100'"`
- 角色：`<AccessControl :codes="['super']">` 或 `v-access:role="'super'"`
- API 判断：`hasAccessByCodes()` / `hasAccessByRoles()`

## 状态管理

- **共享存储**：`packages/stores/` — `useAccessStore`, `useUserStore` 等
- **应用存储**：`apps/web-ele/src/store/` — `useAuthStore` (login/logout/fetchUserInfo)
- 使用 `pinia-plugin-persistedstate` 自动持久化

## 适配器模式 (Adapter)

Element Plus 组件通过适配器映射到 Vben 通用接口：

- **component adapter** (`adapter/component`): 将 ElInput, ElSelect 等映射为 Vben 表单可用的组件类型
- **form adapter** (`adapter/form`): 配置表单验证规则和字段映射
- **vxe-table adapter** (`adapter/vxe-table`): VxeTable 网格全局配置、分页映射、自定义单元格渲染器

所有 Element Plus 组件使用 `defineAsyncComponent` 按需加载。`withDefaultPlaceholder` HOC 自动添加占位符文本。

## 国际化 (i18n)

- 默认语言：中文 (zh-CN) 和英文 (en-US)
- 配置：`preferences.ts` 中 `app.locale`
- 翻译文件：`src/locales/langs/{locale}/*.json`
- 使用：模板/脚本中 `$t('key')`（from `@vben/locales`）
- 第三方语言包：Element Plus 和 dayjs 的 locale 在 `src/locales/index.ts` 中处理
- 新增语言需更新 `SUPPORT_LANGUAGES` 和 `SupportedLanguagesType`
- 请求自动携带 `Accept-Language` 头

## 主题系统

- 基于 **CSS 变量**（HSL 格式）+ **shadcn-vue** + **Tailwind CSS**
- 16 个内置主题，支持自定义主题
- 配置：`preferences.ts` 中 `theme.colorPrimary`（HSL 格式）
- 暗色模式：`theme.mode: 'dark'`
- 半暗侧边栏/头部：`semiDarkSidebar`, `semiDarkHeader`
- Element Plus 主题通过 `useElementPlusDesignTokens()` 自动同步 CSS 变量
- 更改颜色后需清空浏览器缓存

## 偏好设置 (Preferences)

在 `src/preferences.ts` 中覆盖默认配置：

```ts
export const overridesPreferences = defineOverridesPreferences({
  app: {
    name: import.meta.env.VITE_APP_TITLE,
  },
});
```

可配置项包括：app (名称/布局/水印/锁屏)、header、sidebar、tabbar、footer、breadcrumb、navigation、theme (颜色/模式/圆角)、transition、widget、shortcutKeys 等。**不要修改默认配置文件**，只覆盖需要的项。更改后清空缓存。

## 配置说明

### 环境变量

`.env` 文件位于 `apps/web-ele/`：

| 变量 | 说明 | 示例 |
|------|------|------|
| `VITE_APP_TITLE` | 应用标题 | - |
| `VITE_APP_NAMESPACE` | 命名空间 (用于缓存隔离) | - |
| `VITE_PORT` | 开发端口 | `5555` |
| `VITE_GLOB_API_URL` | API 基础路径 | `/api` |
| `VITE_NITRO_MOCK` | 是否开启 Mock | `true` |
| `VITE_DEVTOOLS` | 是否开启 Vue DevTools | `true` |
| `VITE_INJECT_APP_LOADING` | 全局 loading 动画 | `true` |
| `VITE_COMPRESS` | 压缩方式 (none/gzip/brotli) | `gzip` |
| `VITE_PWA` | 是否开启 PWA | `false` |
| `VITE_ROUTER_HISTORY` | 路由模式 (history/hash) | `hash` |

`VITE_GLOB_*` 变量会注入到 `dist/_app.config.js`，生产环境可动态修改。添加新的动态配置需同时更新：env 变量 → `packages/types/global.d.ts` 类型 → `packages/effects/hooks/src/use-app-config.ts` 映射。

### API 代理配置

开发环境通过 Vite proxy 处理跨域（`apps/web-ele/vite.config.mts`）：

```ts
proxy: {
  '/api': {
    target: 'http://localhost:5320/api',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, ''),
  },
}
```

### Vite 配置

应用使用 `@vben/vite-config` 的 `defineConfig`，区分 `application`（应用）和 `library`（包）模式。

### Turborepo

`turbo.json` 定义构建管道，根 `package.json` 中 `build` 命令分配 8GB 内存。

## 测试

- 测试框架：Vitest
- 测试文件：`.test.ts` 后缀或 `__tests__/` 目录
- 运行：`pnpm test:unit`
- 根配置：`vitest.config.ts`

## 开发工作流

1. **安装依赖**：`cd frontend && pnpm install`
2. **启动开发服务器**：`pnpm dev:ele`
3. **访问应用**：http://localhost:5555
4. **提交代码**：`pnpm commit`（交互式，自动格式校验）

## 项目业务模块

当前已实现的业务模块：

### 组织机构 (Organization)

- **通讯录** (`/organization/address-book`)：开发中占位页
- **结构维护** (`/organization/structure`)：分组列表 + 部门表格/树形展示，含搜索、排序、分页

API 类型定义位于 `src/api/organization/index.ts`：
- `Group`: `{ id, name, description }`
- `Department`: `{ id, name, englishName, shortName, orgCode, phone, fax, email, address, postalCode, createTime, updateTime }`
- `PageResponse<T>`: `{ items: T[], total: number }`

## 参考文档

- [Vben Admin 官方文档](https://doc.vben.pro/)
- [项目目录结构](docs/src/guide/project/dir.md)
- [编码规范](docs/src/guide/project/standard.md)
- [开发入门](docs/src/guide/essentials/development.md)
- [路由配置](docs/src/guide/essentials/route.md)
- [服务端交互](docs/src/guide/essentials/server.md)
- [图标使用](docs/src/guide/essentials/icons.md)
- [权限控制](docs/src/guide/in-depth/access.md)
- [国际化](docs/src/guide/in-depth/locale.md)
- [主题系统](docs/src/guide/in-depth/theme.md)
- [登录系统](docs/src/guide/in-depth/login.md)
