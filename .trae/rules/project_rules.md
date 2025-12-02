# 项目规则

## 语言

- 使用中文
- 所有英文技术术语（如 Java、Spring Boot、RESTful API、DTO、JPA、MapStruct 等等）保持英文不变
- 中文和英文混排时确保留有一个空格

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

以下是项目的目录结构约定，包含示例文件：

```plain
src/main/java/com/reythecoder/organization/
├── OrganizationServiceApplication.java         # 主应用类
├── config/                                     # 配置类目录
│   ├── DatabaseConfig.java                     # 数据库配置类
│   ├── SecurityConfig.java                     # 安全配置类
│   └── TestcontainersConfig.java               # Testcontainers 配置类
├── controller/                                 # 控制器层目录
│   └── OrganizationController.java             # RESTful API 端点类
├── dto/                                        # DTO 类目录
│   ├── request/                                # 请求 DTO 类目录
│   │   ├── OrganizationCreateReq.java          # 创建请求 DTO 类
│   │   └── OrganizationUpdateReq.java          # 更新请求 DTO 类
│   └── response/                               # 响应 DTO 类目录
│       ├── OrganizationRsp.java                # 响应 DTO 类
│       └── ApiResponse.java                    # 通用 API 响应类
├── entity/                                     # JPA 实体目录
│   └── OrganizationEntity.java                 # 实体类
├── exception/                                  # 异常处理目录
│   ├── ApiException.java                       # 自定义 API 异常类
│   └── GlobalExceptionHandler.java             # 全局异常处理器类
├── mapper/                                     # MapStruct 映射器目录
│   └── OrganizationMapper.java                 # Entity-DTO 映射器类
├── repository/                                 # 数据访问层目录
│   └── OrganizationRepository.java             # JPA 仓库类
├── service/                                    # 业务逻辑层目录
│   ├── OrganizationService.java                # 服务接口类
│   └── impl/                                   # 服务实现类目录
│       └── OrganizationServiceImpl.java        # 服务实现类
└── utils/                                      # 工具类目录
    └── DateUtils.java                          # 日期工具类

src/test/java/com/reythecoder/organization/
├── controller/                                 # 控制器层测试目录
│   └── OrganizationControllerTest.java         # 控制器测试类
├── service/                                    # 业务逻辑层测试目录
│   └── OrganizationServiceTest.java            # 服务测试类
├── repository/                                 # 数据访问层测试目录
│   └── OrganizationRepositoryTest.java         # Repository 测试类
└── integration/                                # 集成测试目录
    └── OrganizationIntegrationTest.java        # 集成测试类

src/main/resources/
├── application.yml                    # 包含通用设置的主配置文件
├── application-test.yml               # 测试环境特定设置的配置文件
├── application-dev.yml                # 开发环境特定设置的配置文件
└── application-prod.yml               # 生产环境特定设置的配置文件

db/
├── data/                                       # 数据库数据文件目录
├── init-scripts/                               # 数据库初始化脚本目录
└── migration/                                  # 数据库迁移文件目录
    └── V1__Create_organization_table.sql

./
├── .env                               # 本地开发的环境变量文件（不提交）
└── .env.example                       # 环境变量模板文件
```

## 技术规范

- 数据传输对象（DTO）采用 Record，不使用 Lombok
- 使用 MapStruct 进行类型映射
- 请求参数类型以`Req`为后缀
- 返回参数类型以`Rsp`为后缀
- 遵循 RESTful API 设计规范
  - 分页查询参数：page, size, sort
  - 排序参数：sort=field,asc|desc
- 统一返回JSON结构：`{status: integer, message: string, data: Object | Array<any>}`
  - ApiResponse 类：统一封装 API 响应，包含 status、message 和 data 字段
  - status：是 HTTP 状态码的超集，包含了更多的自定义状态码，例如 40000 表示业务逻辑错误
  - message：对应状态码，对返回情况进行描述，例如 "成功"、"失败"、"参数错误" 等
  - data：实际返回的数据，根据不同的 API 接口而变化
    - 当请求成功时，data 字段包含实际返回的数据
    - 当请求失败时，data 字段为空对象 {} 或空数组 []，具体根据业务逻辑而定
- 优雅的实现全局异常处理：
  - 自定义异常类 ApiException，继承自 RuntimeException
  - 全局异常处理器类 GlobalExceptionHandler，捕获自定义异常和其他异常
- 优雅的实现日志处理：
  - Logback 记录日志，
  - Logstash 输出 JSON 日志

## Git 提交规范

参考 [git-instructions.md](../../docs/git-instructions.md)
