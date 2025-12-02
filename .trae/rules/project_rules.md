# 项目规则

## 技术栈

- **数据库**: PostgreSQL 15.x
- **Java 版本**: 21
- **构建工具**: Gradle 8.x
- **框架**: Spring Boot 3.x
- **数据访问层**: Spring Data JPA
- **日志**: Logback, Logstash
- **测试**: JUnit 5, TestContainers
- **其他**: MapStruct, UUIDv7

## 项目目录结构

```plain
src/main/java/com/reythecoder/organization/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/             # 数据传输对象
│   ├── request/     # 请求DTO
│   └── response/    # 响应DTO
├── entity/          # 实体类
├── exception/       # 异常处理
├── mapper/          # MapStruct映射器
├── repository/      # 数据访问层
└── service/         # 业务逻辑层
    └── impl/        # 业务逻辑实现
```

以下是一个示例：

```plain
src/main/java/com/reythecoder/organization/
├── OrganizationServiceApplication.java         # Main application class
├── config/                                     # Configuration classes
│   ├── DatabaseConfig.java                     # Database configuration
│   ├── SecurityConfig.java                     # Security configuration
│   └── TestcontainersConfig.java               # Testcontainers configuration
├── controller/                                 # controllers
│   ├── OrganizationController.java             # Organization endpoints
├── dto/                                        # DTO classes
│   ├── request/                                # Request DTOs
│   │   ├── OrganizationCreateReq.java          # Create organization request
│   │   └── OrganizationUpdateReq.java          # Update organization request
│   └── response/                               # Response DTOs
│       ├── OrganizationRsp.java                # Response class
│       └── ApiResponse.java                    # Generic API response
├── entity/                                     # JPA entities
│   └── OrganizationEntity.java                 # Entity class
├── exception/                                  # Custom exceptions
│   ├── ApiException.java                       # Custom API exception
│   └── GlobalExceptionHandler.java             # Global exception handler
├── mapper/                                     # MapStruct mappers
│   └── OrganizationMapper.java                 # Entity-DTO mapper
├── repository/                                 # Data access layer
│   └── OrganizationRepository.java             # JPA repository
├── service/                                    # Business logic layer
│   ├── OrganizationService.java                # Service interface
│   └── impl/                                   # Service implementations
│       └── OrganizationServiceImpl.java
└── utils/                                      # Utility classes
    └── DateUtils.java

src/test/java/com/reythecoder/organization/
├── controller/                                 # Controller tests
│   └── OrganizationControllerTest.java
├── service/                                    # Service tests
│   └── OrganizationServiceTest.java
├── repository/                                 # Repository tests
│   └── OrganizationRepositoryTest.java
└── integration/                                # Integration tests
    └── OrganizationIntegrationTest.java

src/main/resources/
├── application.yml                    # Main configuration with common settings
├── application-test.yml               # Test profile with environment-specific settings
├── application-dev.yml                # Development profile with environment-specific settings
└── application-prod.yml               # Production profile with environment-specific settings

db/
├── data/                                     # Database data files
├── init-scripts/                             # Database initialization scripts
└── migration/                                # Database migration files
    └── V1__Create_organization_table.sql

./
├── .env                               # Environment variables for local development (not committed)
└── .env.example                       # Template for environment variables
```

## 技术规范

- 数据传输对象（DTO）采用 Record，不使用 Lombok
- 使用 MapStruct 进行类型映射
- 请求参数类型以`Req`为后缀
- 返回参数类型以`Rsp`为后缀
- 遵循 RESTful API 设计规范
  - 分页查询参数：page, size, sort
  - 排序参数：sort=field,asc|desc
- 统一返回JSON结构：{status: integer, message: string, data: Object | Array<any>}
  - ApiResponse 类：统一封装 API 响应，包含 status、message 和 data 字段
  - status：是 HTTP 状态码的超集，包含了更多的自定义状态码，例如 40000 表示业务逻辑错误
  - message：对状态码的描述
  - data：实际返回的数据，根据不同的 API 接口而变化
    - 当请求成功时，data 字段包含实际返回的数据
    - 当请求失败时，data 字段为空对象 {} 或空数组 []，具体根据业务逻辑而定
- 优雅的实现全局异常处理：
  - 自定义异常类 ApiException，继承自 RuntimeException
  - 全局异常处理器类 GlobalExceptionHandler，捕获自定义异常和其他异常
- 优雅的实现日志处理：
  - Logback 记录日志，
  - Logstash 输出 JSON 日志
