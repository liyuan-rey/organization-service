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
- **UI 组件**:
  - **Element Plus**: 复杂组件 (Table, Form, Select, DatePicker 等)
  - **shadcn-vue**: 基础组件 (Button, Card, Dialog, Badge 等)
- **图标**: lucide-vue-next + @element-plus/icons-vue
- **测试工具**: Puppeteer (截图验证)

## 📦 功能特性

- ✅ 仪表盘首页（统计卡片、快捷入口）
- ✅ 部门管理（增删改查、搜索筛选、批量操作）
- ✅ 人员管理（增删改查、搜索筛选、批量操作）
- ✅ 岗位管理（增删改查、搜索筛选、批量操作）
- ✅ 部门岗位关联管理
- ✅ 人员岗位任职管理
- ✅ 表格/卡片视图切换
- ✅ 亮色/暗色主题切换
- ✅ 响应式布局（支持桌面和移动端）
- ✅ 简约现代风格界面
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

### 截图验证 (Puppeteer)

```bash
# 确保开发服务器正在运行
npm run dev

# 在另一个终端运行截图脚本
node screenshot.cjs
```

截图保存在 `screenshots/` 目录。

## 📁 项目结构

```
frontend/
├── src/
│   ├── api/              # API 调用封装
│   │   ├── request.ts    # Axios 实例和拦截器
│   │   ├── department.ts # 部门相关 API
│   │   ├── personnel.ts  # 人员相关 API
│   │   └── ...
│   ├── components/
│   │   ├── common/       # 业务组件（模态框等）
│   │   └── ui/           # shadcn-vue UI 组件
│   ├── layouts/          # 布局组件
│   │   └── TopBarLayout.vue
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   │   ├── theme.ts      # 主题切换
│   │   ├── department.ts
│   │   └── ...
│   ├── types/            # TypeScript 类型定义
│   ├── views/            # 页面组件
│   │   ├── Dashboard.vue
│   │   ├── DepartmentList.vue
│   │   ├── PersonnelList.vue
│   │   └── ...
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── style.css         # 全局样式 + 主题变量
├── screenshot.cjs        # Puppeteer 截图脚本
├── screenshots/          # 截图输出目录
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
- [ ] 权限控制（RBAC）
- [ ] 部门树形结构展示
- [ ] 数据导入/导出
- [ ] 表格分页
- [ ] 国际化支持
