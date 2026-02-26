# 组织管理前端

组织管理系统的前端界面，采用 Vue 3 + Vite + TypeScript + Tailwind CSS 技术栈。

## 🚀 技术栈

- **框架**: Vue 3.5+ (Composition API)
- **构建工具**: Vite 7+
- **语言**: TypeScript 5.9+
- **样式**: Tailwind CSS 4+
- **路由**: Vue Router 5+
- **状态管理**: Pinia 3+
- **HTTP 客户端**: Axios

## 📦 功能特性

- ✅ 部门管理（增删改查）
- ✅ 人员管理（增删改查）
- ✅ 响应式布局（支持桌面和移动端）
- ✅ Admin 风格界面
- ✅ TypeScript 类型安全
- ✅ RESTful API 集成

## 🛠️ 开发

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 📁 项目结构

```
frontend/
├── src/
│   ├── api/              # API 调用封装
│   │   ├── request.ts    # Axios 实例和拦截器
│   │   ├── department.ts # 部门相关 API
│   │   └── personnel.ts  # 人员相关 API
│   ├── components/       # 可复用组件
│   │   └── common/       # 通用组件（模态框等）
│   ├── layouts/          # 布局组件
│   │   └── MainLayout.vue
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   │   ├── department.ts
│   │   └── personnel.ts
│   ├── types/            # TypeScript 类型定义
│   ├── views/            # 页面组件
│   │   ├── DepartmentList.vue
│   │   └── PersonnelList.vue
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── style.css         # 全局样式
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 🔧 配置

### API 代理

开发环境下，API 请求会代理到后端服务：

- 前端：http://localhost:3000
- 后端：http://localhost:8080

在 `vite.config.ts` 中配置代理。

### 环境变量

创建 `.env` 文件（可选）：

```env
VITE_API_BASE_URL=/api
```

## 📝 开发规范

- 使用 Composition API (`<script setup>`)
- 使用 TypeScript 类型注解
- 组件命名采用 PascalCase
- 文件命名与组件名一致

## 🚧 待办事项

- [ ] 用户认证和登录
- [ ] 权限控制
- [ ] 部门树形结构展示
- [ ] 批量操作
- [ ] 数据导入/导出
- [ ] 搜索和筛选功能
