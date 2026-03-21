# Organization Service (Monorepo)

组织管理服务 - 采用 Monorepo 结构，单一仓库包含前后端完整工程。

[![CI - Backend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml)
[![CI - Frontend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-frontend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-frontend.yml)

---

## 📖 项目简介

组织管理系统提供部门和人员的完整管理功能，采用前后端分离架构，支持 RESTful API 和现代化的 Web 界面。

**核心功能：**
- 部门管理（增删改查、层级结构）
- 人员管理（增删改查、部门关联）
- 自定义 API 响应结构
- 全局异常处理
- AOP 日志切面

---

## 📁 目录结构

```
organization-service/
├── backend/              # 后端工程（Spring Boot + Java）
│   ├── src/              # 源代码
│   │   └── main/java/com/reythecoder/organization/
│   │       ├── aspect/       # AOP 切面（日志等）
│   │       ├── config/       # 配置类
│   │       ├── controller/   # REST 控制器
│   │       ├── service/      # 业务逻辑层
│   │       ├── repository/   # 数据访问层
│   │       ├── entity/       # 实体类
│   │       ├── dto/          # 数据传输对象
│   │       ├── mapper/       # MapStruct 映射器
│   │       └── exception/    # 异常处理
│   ├── build.gradle      # Gradle 构建配置
│   ├── settings.gradle   # Gradle 设置
│   ├── gradle.properties # Gradle 属性
│   ├── gradlew           # Gradle Wrapper (Unix)
│   ├── gradlew.bat       # Gradle Wrapper (Windows)
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── db/               # 数据库迁移脚本
│   ├── docs/             # 后端文档
│   └── .github/          # CI/CD 配置（已迁移到根目录）
│
├── frontend/             # 前端工程（Vue3 + Vite + TypeScript）
│   ├── src/              # 源代码
│   │   ├── api/              # API 调用封装
│   │   ├── components/       # 可复用组件
│   │   ├── layouts/          # 布局组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── types/            # TypeScript 类型
│   │   └── views/            # 页面组件
│   ├── package.json      # NPM 依赖配置
│   ├── vite.config.ts    # Vite 构建配置
│   ├── tsconfig.json     # TypeScript 配置
│   └── README.md         # 前端文档
│
├── scripts/              # 公共脚本
├── .github/workflows/    # GitHub Actions 工作流
│   ├── ci-backend.yml    # 后端 CI（backend/ 变化时触发）
│   ├── ci-frontend.yml   # 前端 CI（frontend/ 变化时触发）
│   ├── dependency-check.yml  # 依赖检查
│   └── release.yml       # 发布工作流
├── .git/                 # Git 仓库
├── .gitattributes
└── LICENSE
```

---

## 🛠️ 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| **框架** | Spring Boot 4.0.3 | Web 应用框架 |
| **语言** | Java 17 | 编程语言 |
| **构建工具** | Gradle 9+ | 构建和依赖管理 |
| **数据库** | PostgreSQL 15 | 关系型数据库 |
| **ORM** | Spring Data JPA | 数据访问层 |
| **工具库** | Lombok, MapStruct | 代码简化、对象映射 |
| **日志** | Logback + Logstash | 结构化日志 |
| **AOP** | AspectJ | 切面编程 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| **框架** | Vue 3.5+ | Composition API |
| **构建工具** | Vite 7+ | 快速构建 |
| **语言** | TypeScript 5.9+ | 类型安全 |
| **样式** | Tailwind CSS 4+ | 原子化 CSS |
| **路由** | Vue Router 5+ | 单页路由 |
| **状态管理** | Pinia 3+ | 状态管理 |
| **HTTP** | Axios | 请求库 |
| **UI 组件** | Element Plus + shadcn-vue | 复杂组件 + 基础组件 |
| **图标** | lucide-vue-next | 图标库 |

---

## 🚀 快速开始

### 环境要求

- **后端**: Java 17+, Docker (可选，用于本地数据库)
- **前端**: Node.js 18+, npm 9+

### 后端启动

```bash
# 1. 进入后端目录
cd backend

# 2. 复制环境配置
cp .env.example .env

# 3. 启动 PostgreSQL（使用 Docker）
docker-compose up -d

# 4. 编译构建
./gradlew clean build

# 5. 启动开发服务器
./gradlew bootRun -Dspring-boot.run.profiles=local
```

后端服务运行在 http://localhost:8080

API 文档：http://localhost:8080/swagger-ui.html

### 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器（自动代理到后端）
npm run dev
```

前端服务运行在 http://localhost:3000

---

## 📝 开发规范

### 目录操作规范

**后端代码操作必须在 `backend/` 目录下执行：**

```bash
# ✅ 正确
cd backend
./gradlew build

# ❌ 错误 - 根目录没有 gradlew
./gradlew build
```

**前端代码操作必须在 `frontend/` 目录下执行：**

```bash
# ✅ 正确
cd frontend
npm run build

# ❌ 错误 - 根目录没有 package.json
npm run build
```

### Git 提交规范

在**根目录**执行 Git 命令，提交信息标注影响范围：

```bash
feat(backend): 添加部门导出功能
feat(frontend): 实现人员搜索功能
fix(backend): 修复空指针异常
docs: 更新 README 文档
refactor: 调整目录结构
```

### 代码规范

**后端：**
- 遵循 Spring Boot 最佳实践
- Controller 返回统一使用 `ApiResult<T>`
- 使用 Lombok 简化代码
- 使用 MapStruct 进行对象映射
- 异常通过 `GlobalExceptionHandler` 统一处理

**前端：**
- 使用 Composition API (`<script setup>`)
- 使用 TypeScript 类型注解
- 组件命名采用 PascalCase
- API 调用统一封装在 `src/api/` 目录

---

## 🧪 测试

### 后端测试

```bash
# 运行单元测试
./gradlew test

# 运行集成测试（需要 Docker）
./gradlew testIntegration

# 生成测试报告
./gradlew test jacocoTestReport
```

### 前端测试

```bash
# 类型检查
npm run build

# 代码规范检查（待配置）
npm run lint
```

---

## 📦 部署

### Docker 部署

```bash
# 构建 Docker 镜像
cd backend
docker build -t organization-service:latest .

# 运行容器
docker run -p 8080:8080 organization-service:latest
```

### 生产环境构建

```bash
# 后端
cd backend
./gradlew build -x test
# 输出：build/libs/organization-service-0.0.1-SNAPSHOT.jar

# 前端
cd frontend
npm run build
# 输出：dist/
```

---

## 🔧 CI/CD

项目使用 GitHub Actions 进行持续集成：

| 工作流 | 触发条件 | 说明 |
|--------|---------|------|
| **CI - Backend** | `backend/**` 变化 | 编译、测试、安全扫描 |
| **CI - Frontend** | `frontend/**` 变化 | 类型检查、构建 |
| **Dependency Check** | 每周一或 `build.gradle` 变化 | 依赖更新和漏洞检查 |
| **Release** | 推送 `v*.*.*` 标签 | 构建 JAR 和 Docker 镜像 |

---

## 📚 文档

- [后端 API 文档](backend/docs/)
- [数据库设计](backend/db/)
- [项目架构](backend/docs/project-architecture.md)
- [前端文档](frontend/README.md)

---

## 🚧 待办事项

### 后端
- [ ] 实现部门树形结构查询接口
- [ ] 添加批量导入/导出功能
- [ ] 实现软删除
- [ ] 添加缓存支持（Redis）

### 前端
- [x] 用户认证和登录
- [x] 顶部导航布局
- [x] 亮色/暗色主题切换
- [x] 仪表盘首页
- [x] 搜索和筛选功能
- [x] 表格/卡片视图切换
- [x] 批量操作
- [ ] 权限控制（RBAC）
- [ ] 部门树形结构展示
- [ ] 数据导入/导出

---

## 📄 许可证

Apache License 2.0 - 详见 [LICENSE](LICENSE) 文件

---

## 📞 联系方式

- 项目仓库：https://github.com/liyuan-rey/organization-service
- 问题反馈：GitHub Issues

---

**最后更新：** 2026-03-21
