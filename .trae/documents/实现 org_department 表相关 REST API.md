# 实现 org_department 表相关 REST API

## 1. 项目目录结构
首先创建完整的项目目录结构，包括以下包：
- `com.reythecoder.organization.entity` - 实体类
- `com.reythecoder.organization.dto` - 请求和响应类型
- `com.reythecoder.organization.mapper` - 类型映射器
- `com.reythecoder.organization.repository` - 仓库接口
- `com.reythecoder.organization.service` - 服务层
- `com.reythecoder.organization.controller` - 控制器
- `com.reythecoder.organization.config` - 配置类
- `com.reythecoder.organization.exception` - 异常处理
- `com.reythecoder.organization.util` - 工具类

## 2. 实现实体类
使用 Record 实现 `Department` 实体类，对应 `org_department` 表结构：
```java
package com.reythecoder.organization.entity;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "org_department")
public record Department(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id,
    String name,
    @Column(name = "english_name")
    String englishName,
    @Column(name = "short_name")
    String shortName,
    @Column(name = "org_code")
    String orgCode,
    String phone,
    String fax,
    String email,
    String address,
    @Column(name = "postal_code")
    String postalCode,
    @Column(name = "create_time")
    OffsetDateTime createTime,
    @Column(name = "update_time")
    OffsetDateTime updateTime,
    @Column(name = "tenant_id")
    UUID tenantId
) {
}
```

## 3. 实现请求和响应类型
创建以下 DTO 类：
- `DepartmentReq` - 创建和更新部门的请求类型
- `DepartmentRsp` - 部门响应类型
- `PageReq` - 分页请求类型
- `PageRsp` - 分页响应类型
- `ApiResponse` - 统一 API 响应格式

## 4. 实现类型映射器
使用 MapStruct 实现 `DepartmentMapper` 接口，用于在实体类和 DTO 之间进行转换：
```java
package com.reythecoder.organization.mapper;

import com.reythecoder.organization.entity.Department;
import com.reythecoder.organization.dto.DepartmentReq;
import com.reythecoder.organization.dto.DepartmentRsp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentReq req);
    void updateEntity(DepartmentReq req, @MappingTarget Department entity);
    DepartmentRsp toRsp(Department entity);
}
```

## 5. 实现仓库接口
创建 `DepartmentRepository` 接口，继承 `JpaRepository`：
```java
package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.Department;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}
```

## 6. 实现服务层
创建 `DepartmentService` 接口和 `DepartmentServiceImpl` 实现类，实现业务逻辑：
```java
package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.DepartmentReq;
import com.reythecoder.organization.dto.DepartmentRsp;
import com.reythecoder.organization.dto.PageReq;
import com.reythecoder.organization.dto.PageRsp;
import java.util.UUID;

public interface DepartmentService {
    DepartmentRsp createDepartment(DepartmentReq req);
    DepartmentRsp updateDepartment(UUID id, DepartmentReq req);
    void deleteDepartment(UUID id);
    DepartmentRsp getDepartment(UUID id);
    PageRsp<DepartmentRsp> listDepartments(PageReq pageReq);
}
```

## 7. 实现控制器
创建 `DepartmentController` 类，处理 HTTP 请求：
```java
package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.DepartmentReq;
import com.reythecoder.organization.dto.DepartmentRsp;
import com.reythecoder.organization.dto.PageReq;
import com.reythecoder.organization.dto.PageRsp;
import com.reythecoder.organization.service.DepartmentService;
import com.reythecoder.organization.util.ApiResponse;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    
    // 构造函数注入
    
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentRsp>> createDepartment(@RequestBody DepartmentReq req) {
        // 实现创建部门逻辑
    }
    
    // 其他 HTTP 方法实现
}
```

## 8. 实现全局异常处理
创建 `GlobalExceptionHandler` 类，处理各种异常情况：
```java
package com.reythecoder.organization.exception;

import com.reythecoder.organization.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        // 实现异常处理逻辑
    }
    
    // 其他异常处理方法
}
```

## 9. 实现统一响应格式
创建 `ApiResponse` 类，定义统一的 JSON 响应格式：
```java
package com.reythecoder.organization.util;

public record ApiResponse<T>(
    int status,
    String message,
    T data
) {
    // 静态工厂方法
}
```

## 10. 配置日志处理
确保日志配置正确，使用 logback-spring.xml 文件配置日志格式和输出位置。

## 11. 实现工具类
创建必要的工具类，如 `UUIDUtil` 用于生成 UUID v7 等。

## 12. 测试
实现单元测试和集成测试，确保 API 功能正常。

## 13. 运行项目
使用 Gradle 命令运行项目，确保项目能够正常启动和运行。

## 实现顺序
1. 创建项目目录结构
2. 实现实体类
3. 实现请求和响应类型
4. 实现类型映射器
5. 实现仓库接口
6. 实现服务层
7. 实现控制器
8. 实现全局异常处理
9. 实现统一响应格式
10. 实现工具类
11. 编写测试
12. 运行项目

## 技术栈
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- MapStruct
- UUID v7
- JUnit 5
- TestContainers

## 注意事项
- 使用 Record 而不是 Lombok 实现实体类
- 请求和响应类型使用 ...Req 和 ...Rsp 作为后缀
- 使用 MapStruct 进行类型映射
- 统一的 JSON 响应格式，包含 status、message 和 data 字段
- 优雅的全局异常处理
- 优雅的日志处理
- 遵循 RESTful API 设计规范
- 实现分页查询功能
- 实现基本的 CRUD 操作