# Organization Service - Backend

组织管理服务后端 - 基于 Spring Boot 4.0 的 RESTful API 服务

[![CI - Backend](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml/badge.svg)](https://github.com/liyuan-rey/organization-service/actions/workflows/ci-backend.yml)

---

## 📖 项目简介

提供组织和人员管理的核心业务逻辑和 RESTful API，支持组织树、标签库等多维度管理。

**核心功能：**
- ✅ 组织树管理（基于 LexoRank 排序的树形结构 CRUD）
- ✅ 部门管理（CRUD、层级结构）
- ✅ 人员管理（CRUD、部门关联）
- ✅ 职位管理（CRUD、部门/人员关联）
- ✅ 分组管理（CRUD、层级结构、部门/人员关联）
- ✅ 标签库管理（分类、标签树、标签关联）
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
| **主键生成** | UUIDv7 | 趋势递增的全局唯一标识 |
| **日志** | Logback + Logstash | 结构化日志（支持 JSON 格式） |
| **AOP** | AspectJ 1.9.22 | 切面编程（日志切面） |
| **测试** | JUnit 5, Testcontainers 2.0.2 | 单元测试和集成测试 |

---

## 📁 目录结构

```
backend/
├── src/main/java/com/reythecoder/
│   ├── ServiceApplication.java                 # 启动类
│   ├── common/                                 # 共享组件
│   │   ├── aspect/                             # AOP 切面（LoggingAspect）
│   │   ├── dto/
│   │   │   └── ApiResult.java                 # API 响应包装
│   │   ├── exception/
│   │   │   ├── ApiException.java              # 自定义 API 异常
│   │   │   └── GlobalExceptionHandler.java    # 全局异常处理
│   │   └── utils/
│   │       └── LexoRankUtils.java             # LexoRank 排序工具
│   ├── organization/                           # 组织管理核心域
│   │   ├── controller/                         # REST 控制器
│   │   ├── service/                            # 业务逻辑层（接口 + impl/）
│   │   ├── repository/                         # 数据访问层
│   │   ├── entity/                             # JPA 实体
│   │   ├── dto/
│   │   │   ├── request/                        # 请求 DTO（*Req.java）
│   │   │   └── response/                       # 响应 DTO（*Rsp.java）
│   │   └── mapper/                             # MapStruct 映射器
│   ├── taglib/                                 # 标签库管理
│   │   ├── controller/                         # TagCategory, Tag, TagRelation 控制器
│   │   ├── service/                            # 业务逻辑层（接口 + impl/）
│   │   ├── repository/                         # 数据访问层
│   │   ├── entity/                          # TagCategory, Tag, TagRelation 实体
│   │   ├── dto/
│   │   │   ├── request/                     # 请求 DTO
│   │   │   └── response/                    # 响应 DTO
│   │   └── mapper/                          # MapStruct 映射器
│   └── common/                              # 共享工具
│       └── utils/LexoRankUtils.java         # LexoRank 排序工具
├── src/main/resources/
│   ├── application.yml                      # 主配置文件
│   ├── application-dev.yml                  # 开发环境配置
│   ├── application-prod.yml                 # 生产环境配置
│   ├── application-test.yml                 # 测试环境配置
│   ├── logback-spring.xml                   # 日志配置
│   └── openapi.yaml                         # 符合 OpenAPI 3.1 规范的接口描述
├── db/init-scripts/                         # 数据库初始化脚本
├── docs/                                    # 项目文档
├── build.gradle                             # Gradle 构建配置
├── settings.gradle                          # Gradle 设置
├── gradle.properties                        # Gradle 属性（版本管理）
├── docker-compose.yml                       # Docker 配置（PostgreSQL）
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
# 使用 dev 配置启动
./gradlew bootRun -Dspring-boot.run.profiles=dev

# 或指定环境变量
env $(cat .env | xargs) ./gradlew bootRun
```

应用启动后访问：http://localhost:8080

---

## OpenAPI 规范的接口定义文件

本项目使用 OpenAPI 3.1 规范的 YAML 文件来描述 API：

- **位置**: `backend/docs/openapi.yaml`
- **版本**: OpenAPI 3.1

该文件包含了所有 API 端点的完整描述，包括：
- 请求/响应格式
- 参数说明
- 状态码
- 数据结构定义

可以使用以下工具查看和测试 API：
- **Apifox**: 将 YAML 文件导入，可视化编辑和预览
- **Swagger Editor**: 将 YAML 文件导入 https://editor.swagger.io/ 预览
- **Stoplight Studio**: 可视化编辑和预览
- **Redoc**: 生成美观的静态文档

### 主要接口

#### 组织树管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/tree/nodes` | 创建节点 |
| GET | `/api/tree/nodes/{nodeId}` | 获取节点详情 |
| PUT | `/api/tree/nodes/{nodeId}` | 更新节点 |
| POST | `/api/tree/nodes/{nodeId}/remove` | 移除节点 |
| POST | `/api/tree/nodes/{nodeId}/move` | 移动节点 |
| GET | `/api/tree/nodes/{nodeId}/children` | 获取子节点 |
| GET | `/api/tree/nodes/{nodeId}/subtree` | 获取子树 |
| GET | `/api/tree/nodes/{nodeId}/descendants` | 获取所有后代 |
| GET | `/api/tree/nodes/{nodeId}/ancestors` | 获取所有祖先 |
| GET | `/api/tree/nodes/root` | 获取根节点 |
| GET | `/api/tree/nodes/root/children` | 获取根节点的子节点 |
| GET | `/api/trees/{groupId}?depth=N` | 获取树结构（指定深度） |

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

#### 职位管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/positions` | 获取所有职位 |
| GET | `/api/positions/{id}` | 获取职位详情 |
| POST | `/api/positions` | 创建职位 |
| PUT | `/api/positions/{id}` | 更新职位 |
| DELETE | `/api/positions/{id}` | 删除职位 |

#### 分组管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/groups` | 获取所有分组 |
| GET | `/api/groups/{id}` | 获取分组详情 |
| POST | `/api/groups` | 创建分组 |
| PUT | `/api/groups/{id}` | 更新分组 |
| DELETE | `/api/groups/{id}` | 删除分组 |

#### 部门层级

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/department-hierarchy/roots` | 获取根部门 |
| GET | `/api/department-hierarchy/children/{parentId}` | 获取子部门 |
| GET | `/api/department-hierarchy/{childId}` | 获取层级关系 |
| POST | `/api/department-hierarchy` | 创建层级关系 |
| DELETE | `/api/department-hierarchy/{childId}` | 删除层级关系 |

#### 分组层级

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/group-hierarchy/roots` | 获取根分组 |
| GET | `/api/group-hierarchy/children/{parentId}` | 获取子分组 |
| GET | `/api/group-hierarchy/{childId}` | 获取层级关系 |
| POST | `/api/group-hierarchy` | 创建层级关系 |
| DELETE | `/api/group-hierarchy/{childId}` | 删除层级关系 |

#### 关联关系

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/department-personnel/department/{departmentId}` | 获取部门下的人员 |
| GET | `/api/department-personnel/personnel/{personnelId}` | 获取人员所属部门 |
| POST | `/api/department-personnel` | 创建部门-人员关联 |
| DELETE | `/api/department-personnel/{departmentId}/{personnelId}` | 删除部门-人员关联 |
| PUT | `/api/department-personnel/set-primary/{personnelId}/{departmentId}` | 设置主部门 |
| GET | `/api/department-positions` | 获取所有部门-职位关联 |
| GET | `/api/department-positions/department/{departmentId}` | 获取部门下的职位 |
| POST | `/api/department-positions` | 创建部门-职位关联 |
| DELETE | `/api/department-positions/{departmentId}/{positionId}` | 删除部门-职位关联 |
| GET | `/api/personnel-positions` | 获取所有人员-职位关联 |
| GET | `/api/personnel-positions/personnel/{personnelId}` | 获取人员的职位 |
| POST | `/api/personnel-positions` | 创建人员-职位关联 |
| PUT | `/api/personnel-positions/{id}` | 更新人员-职位关联 |
| DELETE | `/api/personnel-positions/{id}` | 删除人员-职位关联 |
| GET | `/api/department-group/department/{departmentId}` | 获取部门所属分组 |
| POST | `/api/department-group` | 创建部门-分组关联 |
| DELETE | `/api/department-group/{departmentId}/{groupId}` | 删除部门-分组关联 |
| GET | `/api/group-department/group/{groupId}` | 获取分组下的部门 |
| POST | `/api/group-department` | 创建分组-部门关联 |
| DELETE | `/api/group-department/{groupId}/{departmentId}` | 删除分组-部门关联 |
| GET | `/api/group-personnel/group/{groupId}` | 获取分组下的人员 |
| POST | `/api/group-personnel` | 创建分组-人员关联 |
| DELETE | `/api/group-personnel/{groupId}/{personnelId}` | 删除分组-人员关联 |

#### 标签库管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/tag-categories` | 获取所有标签分类 |
| GET | `/api/tag-categories/{id}` | 获取分类详情 |
| POST | `/api/tag-categories` | 创建标签分类 |
| PUT | `/api/tag-categories/{id}` | 更新标签分类 |
| DELETE | `/api/tag-categories/{id}` | 删除标签分类 |
| GET | `/api/tags?categoryId={id}` | 获取标签树 |
| GET | `/api/tags/{id}` | 获取标签详情 |
| POST | `/api/tags` | 创建标签 |
| PUT | `/api/tags/{id}` | 更新标签 |
| DELETE | `/api/tags/{id}` | 删除标签（递归） |
| GET | `/api/tag-relations?objectType={type}&objectId={id}` | 按对象查询标签关联 |
| GET | `/api/tag-relations?tagId={id}` | 按标签查询关联对象 |
| POST | `/api/tag-relations/batch` | 批量创建标签关联 |
| DELETE | `/api/tag-relations/{id}` | 删除标签关联 |
| POST | `/api/tag-relations/query` | 多标签组合查询 |

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
| `application.yml` | 默认 | 基础配置（JPA ddl-auto: validate） |
| `application-dev.yml` | dev | 开发环境（DEBUG 日志） |
| `application-prod.yml` | prod | 生产环境（WARN 日志） |
| `application-test.yml` | test | 测试环境（ddl-auto: create-drop，Testcontainers） |

**激活环境：**

```bash
./gradlew bootRun -Dspring-boot.run.profiles=dev
```

---

## 📚 相关文档

- [数据库设计指南](docs/database-design-guide-for-postgresql.md)
- [开发指南](docs/development-guidelines.md)
- [Git 规范](docs/git-instructions.md)
- [CI/CD 配置](docs/ci-cd-setup.md)

---

## 📄 许可证

Apache License 2.0 - 详见根目录 [LICENSE](../LICENSE) 文件

---

## 📞 联系方式

- 项目仓库：https://github.com/liyuan-rey/organization-service
- 问题反馈：GitHub Issues

---

**最后更新：** 2026-04-11
