# Organization Service - Backend

组织管理服务后端 - 基于 Spring Boot 4.0 的 RESTful API 服务

[![CI - Backend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml)

---

## 📖 项目简介

提供组织和人员管理的核心业务逻辑和 RESTful API，支持部门和人员的增删改查操作。

**核心功能：**
- ✅ 部门管理（CRUD、层级结构）
- ✅ 人员管理（CRUD、部门关联）
- ✅ 自定义 API 响应结构（`ApiResult<T>`）
- ✅ 全局异常处理
- ✅ AOP 日志切面
- ✅ 集成测试支持

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **框架** | Spring Boot 4.0.3 | Web 应用框架 |
| **语言** | Java 17 | 编程语言 |
| **构建工具** | Gradle 9+ | 构建和依赖管理 |
| **数据库** | PostgreSQL 15 | 关系型数据库 |
| **ORM** | Spring Data JPA | 数据访问层 |
| **验证** | Hibernate Validator | Bean 验证 |
| **工具库** | Lombok | 简化代码（@Data, @Builder 等） |
| **对象映射** | MapStruct 1.6.3 | DTO ↔ Entity 映射 |
| **日志** | Logback + Logstash | 结构化日志（支持 JSON 格式） |
| **AOP** | AspectJ 1.9.22 | 切面编程（日志切面） |
| **测试** | JUnit 5, Testcontainers | 单元测试和集成测试 |

---

## 📁 目录结构

```
backend/
├── src/main/java/com/reythecoder/organization/
│   ├── OrganizationServiceApplication.java  # 启动类
│   ├── aspect/                              # AOP 切面
│   │   └── LoggingAspect.java               # 日志切面
│   ├── config/                              # 配置类
│   ├── controller/                          # REST 控制器
│   │   ├── DepartmentController.java        # 部门控制器
│   │   └── PersonnelController.java         # 人员控制器
│   ├── service/                             # 业务逻辑层
│   │   ├── DepartmentService.java
│   │   ├── PersonnelService.java
│   │   └── impl/
│   ├── repository/                          # 数据访问层
│   │   ├── DepartmentRepository.java
│   │   └── PersonnelRepository.java
│   ├── entity/                              # 实体类
│   │   ├── DepartmentEntity.java
│   │   └── PersonnelEntity.java
│   ├── dto/                                 # 数据传输对象
│   │   ├── request/                         # 请求 DTO
│   │   ├── response/                        # 响应 DTO
│   │   └── ApiResult.java                   # 统一响应结构
│   ├── mapper/                              # MapStruct 映射器
│   │   ├── DepartmentMapper.java
│   │   └── PersonnelMapper.java
│   └── exception/                           # 异常处理
│       ├── ApiException.java                # 自定义异常
│       └── GlobalExceptionHandler.java      # 全局异常处理器
├── src/main/resources/
│   ├── application.yml                      # 主配置文件
│   ├── application-local.yml                # 本地环境配置
│   ├── application-dev.yml                  # 开发环境配置
│   ├── application-prod.yml                 # 生产环境配置
│   ├── logback-spring.xml                   # 日志配置
│   └── db/init-scripts/                     # 数据库初始化脚本
├── db/                                      # 数据库相关
├── docs/                                    # 文档
├── build.gradle                             # Gradle 构建配置
├── settings.gradle                          # Gradle 设置
├── gradle.properties                        # Gradle 属性
├── docker-compose.yml                       # Docker 配置
├── Dockerfile                               # Docker 镜像
└── .env.example                             # 环境变量示例
```

---

## 🚀 快速开始

### 环境要求

- Java 17+
- Gradle 9+（或使用 Gradle Wrapper）
- PostgreSQL 15+（或使用 Docker）
- Docker（可选，用于本地开发环境）

### 1. 克隆项目

```bash
# 从根目录克隆
git clone https://github.com/liyuan-rey/organization-service.git
cd organization-service/backend
```

### 2. 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env` 文件，配置数据库连接等信息：

```env
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/organization_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# 服务器端口
SERVER_PORT=8080

# 日志级别
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_REYTHECODER_ORGANIZATION=DEBUG
```

### 3. 启动数据库（使用 Docker）

```bash
docker-compose up -d
```

数据库将在 `localhost:5432` 启动，自动执行初始化脚本。

### 4. 编译构建

```bash
./gradlew clean build
```

### 5. 启动应用

```bash
# 使用本地配置启动
./gradlew bootRun -Dspring-boot.run.profiles=local

# 或指定环境变量
env $(cat .env | xargs) ./gradlew bootRun
```

应用启动后访问：http://localhost:8080

---

## 📡 API 文档

### Swagger/OpenAPI

启动应用后访问：http://localhost:8080/swagger-ui.html

### 主要接口

#### 部门管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/departments` | 获取所有部门 |
| GET | `/api/departments/{id}` | 获取部门详情 |
| POST | `/api/departments` | 创建部门 |
| PUT | `/api/departments/{id}` | 更新部门 |
| DELETE | `/api/departments/{id}` | 删除部门 |

#### 人员管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/personnel` | 获取所有人员 |
| GET | `/api/personnel/{id}` | 获取人员详情 |
| POST | `/api/personnel` | 创建人员 |
| PUT | `/api/personnel/{id}` | 更新人员 |
| DELETE | `/api/personnel/{id}` | 删除人员 |

### API 响应格式

所有接口返回统一的数据结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

**错误响应：**

```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null
}
```

---

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行集成测试（需要 Docker）
./gradlew testIntegration

# 运行特定测试类
./gradlew test --tests *DepartmentControllerTest

# 生成测试报告
./gradlew test jacocoTestReport
```

### 测试覆盖率

```bash
# 查看覆盖率报告
open build/reports/jacoco/test/html/index.html
```

---

## 📦 部署

### Docker 部署

```bash
# 构建镜像
docker build -t organization-service:latest .

# 运行容器
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/organization_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  organization-service:latest
```

### 生产环境构建

```bash
# 构建生产 JAR（跳过测试）
./gradlew build -x test

# 输出位置
ls -lh build/libs/organization-service-0.0.1-SNAPSHOT.jar

# 运行
java -jar build/libs/organization-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

---

## 🔧 开发指南

### 添加新接口

1. 在 `controller/` 创建控制器类
2. 在 `service/` 定义接口和实现
3. 在 `dto/` 定义请求和响应 DTO
4. 在 `mapper/` 添加 MapStruct 映射器
5. 编写单元测试

### 日志切面

自动记录 Controller 和 Service 公共方法的执行日志：

```java
// 日志输出示例
2026-02-26 16:00:00.123 [http-nio-8080-exec-1] INFO  c.r.o.aspect.LoggingAspect - 开始执行方法：com.reythecoder.organization.controller.DepartmentController.getAllDepartments
2026-02-26 16:00:00.150 [http-nio-8080-exec-1] INFO  c.r.o.aspect.LoggingAspect - 方法执行成功：com.reythecoder.organization.controller.DepartmentController.getAllDepartments | 耗时：27ms
```

### 配置说明

**多环境配置：**

| 文件 | 环境 | 说明 |
|------|------|------|
| `application.yml` | 默认 | 基础配置 |
| `application-local.yml` | local | 本地开发（DEBUG 日志） |
| `application-dev.yml` | dev | 开发环境（INFO 日志） |
| `application-prod.yml` | prod | 生产环境（WARN 日志） |

**激活环境：**

```bash
./gradlew bootRun -Dspring-boot.run.profiles=dev
```

---

## 📚 相关文档

- [API 文档](docs/API_DOCUMENTATION.md)
- [数据库设计](docs/database-structure-design.md)
- [项目架构](docs/project-architecture.md)
- [开发指南](docs/development-guidelines.md)
- [本地开发](docs/local-development.md)
- [CI/CD 配置](docs/ci-cd-setup.md)

---

## 🚧 待办事项

- [ ] 实现部门树形结构查询接口
- [ ] 添加批量导入/导出功能
- [ ] 实现软删除
- [ ] 添加缓存支持（Redis）
- [ ] 添加分页查询支持
- [ ] 实现审计字段（创建人、更新人）

---

## 📄 许可证

Apache License 2.0 - 详见根目录 [LICENSE](../LICENSE) 文件

---

## 📞 联系方式

- 项目仓库：https://github.com/liyuan-rey/organization-service
- 问题反馈：GitHub Issues

---

**最后更新：** 2026-02-26
