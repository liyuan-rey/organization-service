# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Monorepo containing a full-stack Organization Management Service with:
- **Backend**: Spring Boot 4.0 + Java 17 + PostgreSQL (in `backend/`)
- **Frontend**: Vue 3 + Vite + TypeScript + Tailwind CSS + Element Plus (in `frontend/`)

## 子项目

| 目录 | 说明 | 开发文档 |
|------|------|---------|
| `backend/` | Spring Boot 4.0 + Java 17 + PostgreSQL | [`backend/CLAUDE.md`](backend/CLAUDE.md) |
| `frontend/` | Vue 3 + Element Plus (Vben Admin v5) | [`frontend/CLAUDE.md`](frontend/CLAUDE.md) |

**开始开发前，请先阅读对应子项目的文档。**

## 快速启动

```bash
# 1. 数据库
cd backend && docker-compose up -d

# 2. 后端 (http://localhost:8080)
cd backend && ./gradlew bootRun -Dspring-boot.run.profiles=dev

# 3. 前端 (http://localhost:5555)
cd frontend && pnpm install && pnpm dev:ele
```

## 基本约定

- 后端命令在 `backend/` 执行，前端命令在 `frontend/` 执行，Git 操作在根目录执行
- 提交信息标注范围：`feat(backend):`, `fix(frontend):`, `docs:`, `ci:` 等
- API 响应格式：`{ code: 0, message: "success", data: T }`

## 详细文档

- 后端开发：[`backend/CLAUDE.md`](backend/CLAUDE.md)、[`backend/docs/`](backend/docs/)
- 前端开发：[`frontend/CLAUDE.md`](frontend/CLAUDE.md)、[`frontend/docs/`](frontend/docs/)
