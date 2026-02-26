# Organization Service (Monorepo)

组织管理服务 - 采用 Monorepo 结构，单一仓库包含前后端工程。

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
│   └── .github/          # CI/CD 配置
│
├── frontend/             # 前端工程（待开发）
│   └── README.md         # 占位说明
│
├── scripts/              # 公共脚本
│
├── .git/                 # Git 仓库
├── .gitattributes
└── LICENSE
```

---

## ⚠️ 重要：目录结构规范

### 后端代码操作

**所有后端相关操作必须在 `backend/` 目录下执行：**

```bash
# ✅ 正确
cd backend
./gradlew build
./gradlew test
./gradlew bootRun

# ❌ 错误 - 不要在根目录执行后端命令
./gradlew build          # 根目录没有 gradlew
```

**后端源码路径：**
- 位置：`backend/src/main/java/com/reythecoder/organization/`
- 新增类文件必须放在 `backend/src/` 下的正确包路径中

**构建产物：**
- 输出：`backend/build/`
- 日志：`backend/logs/`（运行时生成）

### 前端代码操作

**前端开发在 `frontend/` 目录下进行：**

```bash
cd frontend
# 前端命令在此执行（待初始化）
```

### Git 操作

- 在**根目录**执行 Git 命令
- 提交信息应明确标注影响范围：
  - `feat(backend): ...` - 后端功能
  - `feat(frontend): ...` - 前端功能
  - `refactor: ...` - 影响整体结构

---

## 🚀 快速开始

### 后端开发

```bash
# 进入后端目录
cd backend

# 编译构建
./gradlew clean build

# 运行测试
./gradlew test

# 启动开发服务器
./gradlew bootRun -Dspring-boot.run.profiles=local
```

### 前端开发

前端工程待初始化...

---

## 📝 开发规范

1. **严格区分前后端目录** - 不要将后端代码放到 `frontend/`，反之亦然
2. **依赖管理** - 后端依赖在 `backend/build.gradle` 中声明
3. **配置文件** - 环境配置在各自工程目录下（如 `backend/src/main/resources/`）
4. **文档更新** - 修改代码时同步更新对应目录下的文档

---

## 📚 相关文档

- [后端 API 文档](backend/docs/)
- [数据库设计](backend/db/)
- [部署指南](backend/docs/deployment.md)

---

**最后更新：** 2026-02-26
