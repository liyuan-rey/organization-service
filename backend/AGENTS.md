# AGENTS.md

AI Agent 开发指南 - Organization Service Backend

## 项目概述

组织管理服务后端，基于 Spring Boot 4.0 的 RESTful API 服务，提供部门、人员、分组、职位的增删改查功能。

**核心业务场景**：
- 部门层级管理（树形结构）
- 人员多部门归属（多对多）
- 分组管理及跨部门协作
- 职位与人员关联

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| 框架 | Spring Boot 4.0.3 | Web 应用框架 |
| 语言 | Java 17 | 编程语言 |
| 构建工具 | Gradle 9+ | 构建和依赖管理 |
| 数据库 | PostgreSQL 15 | 关系型数据库 |
| ORM | Spring Data JPA | 数据访问层 |
| 对象映射 | MapStruct 1.6.3 | DTO ↔ Entity 映射 |
| 工具库 | Lombok | 简化代码 |
| 日志 | Logback + Logstash | 结构化日志 |
| 测试 | JUnit 5, Testcontainers | 单元测试和集成测试 |
| UUID | uuidv7 1.0.1 | UUID v7 主键生成 |

## 常用命令

### 构建与运行

```bash
./gradlew clean build

./gradlew bootRun -Dspring-boot.run.profiles=local

./gradlew bootRun -Dspring-boot.run.profiles=dev
```

### 测试

```bash
./gradlew test

./gradlew testIntegration

./gradlew test --tests *DepartmentControllerTest
```

### 数据库

```bash
docker-compose up -d
```

## 项目结构

```
src/main/java/com/reythecoder/organization/
├── OrganizationServiceApplication.java  # 启动类
├── aspect/                              # AOP 切面
├── config/                              # 配置类
├── controller/                          # REST 控制器
├── service/                             # 业务逻辑层
│   └── impl/                            # 服务实现
├── repository/                          # 数据访问层
├── entity/                              # JPA 实体
├── dto/                                 # 数据传输对象
│   ├── request/                         # 请求 DTO (*Req.java)
│   └── response/                        # 响应 DTO (*Rsp.java)
├── mapper/                              # MapStruct 映射器
└── exception/                           # 异常处理
```

## 代码规范

### 实体和仓库

- Entity 类放在 `entity` 包，使用 Lombok 注解
- Repository 接口放在 `repository` 包，继承 `JpaRepository`
- 实体名使用单数，仓库名使用复数

### DTO 和映射

- 请求 DTO 放在 `dto/request` 包，命名格式：`*Req.java`
- 响应 DTO 放在 `dto/response` 包，命名格式：`*Rsp.java`
- 使用 MapStruct 进行 Entity-DTO 映射，映射器放在 `mapper` 包

### 服务层

- 服务接口放在 `service` 包
- 服务实现放在 `service/impl` 包，使用 `@Service` 注解
- 使用构造器注入依赖

### 控制器

- REST 控制器放在 `controller` 包，使用 `@RestController` 注解
- 所有接口返回统一的 `ApiResult<T>` 结构

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

## 数据库设计规范

### 核心设计：节点抽象关系模型

项目采用**节点抽象 + 通用关联表**设计，将部门、人员、分组统一抽象为"节点"，通过 `org_entity_relation` 表建立关联。

**业务关系支持**：
- 部门 → 子部门（树形层级）
- 部门 → 人员（多对多，支持主次标识）
- 部门 → 分组（部门下设分组）
- 分组 → 子分组（树形层级）
- 分组 → 部门（跨部门协作）
- 分组 → 人员（多对多，支持角色差异）

详细设计文档：[docs/database-design-node-relation-model.md](docs/database-design-node-relation-model.md)

### 主键策略

- 所有表主键使用 UUID v7 算法生成（128位）
- 使用 `io.github.robsonkades:uuidv7` 库

### 必需字段

所有表必须包含：
- `id`: 主键
- `create_time`: 创建时间
- `update_time`: 更新时间
- `tenant_id`: 租户标识（多租户隔离）

### NULL 值处理

不允许 NULL 值，必须提供默认值：
- 时间类型: `2000-01-01 00:00:00`
- 字符串类型: 空字符串 `""`
- 数值类型: `0`

### 表命名规范

- 表名前缀: `org_`
- 使用小写字母、数字、下划线
- 使用英文单数形式

## Git 提交规范

遵循 Conventional Commits 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### 提交类型

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档变更
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具

### 示例

```
feat(service): Add organization creation functionality

- Implement createOrganization method with validation
- Add business logic for duplicate name checking

Closes #123
```

## 配置文件

| 文件 | 环境 |
|------|------|
| `application.yml` | 基础配置 |
| `application-local.yml` | 本地开发 |
| `application-dev.yml` | 开发环境 |
| `application-prod.yml` | 生产环境 |
| `application-test.yml` | 测试环境 |

## 日志规范

### 日志级别

- **ERROR**: 需要立即处理的严重错误
- **WARN**: 不影响运行的异常情况
- **INFO**: 重要业务事件和应用生命周期
- **DEBUG**: 详细调试信息

### 日志使用

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SomeServiceImpl {
    
    public void someMethod(String param) {
        log.info("Processing: {}", param);
        try {
            log.debug("Operation completed successfully");
        } catch (Exception e) {
            log.error("Failed to process: {}", param, e);
            throw e;
        }
    }
}
```

## 测试规范

### 测试类型

- **单元测试**: 使用 `@WebMvcTest`, `@DataJpaTest`
- **集成测试**: 使用 `@SpringBootTest` + Testcontainers，标记 `@Tag("integration")`

### 测试命令

```bash
./gradlew test

./gradlew testIntegration
```

## 注意事项

1. **禁止提交敏感信息**: 数据库密码、API 密钥等不要提交到版本控制
2. **使用环境变量**: 敏感配置通过环境变量或 `.env` 文件管理
3. **测试覆盖**: 新功能需要编写单元测试和必要的集成测试
4. **代码审查**: 提交前确保代码通过所有测试和检查
5. **不添加注释**: 代码中不要添加注释，除非用户明确要求

## 相关文档

- [数据库节点关系模型设计](docs/database-design-node-relation-model.md)
- [数据库设计开发指南](docs/database-design-develop-guide-for-postgresql.md)
- [开发指南](docs/development-guidelines.md)
- [Git 提交规范](docs/git-instructions.md)