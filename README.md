# Organization Service

组织管理服务 - 前后端分离 Monorepo，单一仓库包含前后端完整工程。

[![CI - Backend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml)
[![CI - Frontend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-frontend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-frontend.yml)

## 📖 项目简介

组织管理系统提供部门和人员的完整管理功能，以及标签库功能。采用前后端分离架构，支持 RESTful API 和现代化的 Web 界面。

## 项目结构

```
organization-service/
├── backend/       # Spring Boot 4.0 + Java 17 + PostgreSQL  →  [详细文档](backend/README.md)
├── frontend/      # Vue 3 + Element Plus (Vben Admin v5)    →  [详细文档](frontend/README.md)
└── scripts/       # 公共脚本
```

## 技术栈

|        | 后端 (`backend/`) | 前端 (`frontend/`)               |
| ------ | ----------------- | -------------------------------- |
| 语言   | Java 17           | TypeScript                       |
| 框架   | Spring Boot 4.0   | Vue 3 (Vben Admin v5)            |
| UI     | -                 | Element Plus + Tailwind CSS v4   |
| 构建   | Gradle            | Vite + Turborepo (pnpm monorepo) |
| 数据库 | PostgreSQL 15     | -                                |
| 端口   | 8080              | 5555                             |

## 快速开始

**环境要求**: Java 17+, Node.js 20.19+, pnpm 10+, Docker (可选)

```bash
# 后端
cd backend && docker-compose up -d          # 启动数据库
cd backend && ./gradlew bootRun -Dspring-boot.run.profiles=dev

# 前端
cd frontend && pnpm install && pnpm dev:ele
```

## 开发规范

- 后端操作在 `backend/` 目录执行，前端操作在 `frontend/` 目录执行
- Git 操作在根目录执行，提交信息标注范围：`feat(backend):`, `fix(frontend):` 等
- 后端 API 统一返回 `ApiResult<T>` (`{ code, message, data }`)

## CI/CD

GitHub Actions，按 `backend/` 和 `frontend/` 目录变化分别触发。详见 `.github/workflows/`。

## 文档

- **后端**: [README](backend/README.md) | [开发指南](backend/CLAUDE.md)
- **前端**: [README](frontend/README.md) | [开发指南](frontend/CLAUDE.md)

## 许可证

Apache License 2.0 - 详见 [LICENSE](LICENSE) 文件

---

**最后更新：** 2026-04-12
