# 后端共享组件重构设计

## 背景

当前 `organization` 包中存在多个被其他模块共享使用的组件：

- `ApiResult.java` - 被 `organization` 和 `taglib` 的所有控制器使用
- `ApiException.java` - 被 `organization` 和 `taglib` 的服务层使用
- `GlobalExceptionHandler.java` - 全局异常处理
- `LoggingAspect.java` - AOP 日志切面（切面覆盖所有 RestController 和 Service）
- `OrganizationServiceApplication.java` - 主入口类

这些组件应移至共享位置，避免跨模块引用 `organization` 包。

## 目标

1. **改善代码组织** - 明确划分共享组件与领域模块
2. **为未来扩展做准备** - 新增领域模块可直接使用共享组件
3. **遵循 Spring Boot 最佳实践** - 主入口类置于顶层包

## 设计

### 目标包结构

```
com.reythecoder/
├── ServiceApplication.java              # 主入口类（重命名并移动）
├── common/                               # 共享模块
│   ├── aspect/
│   │   └── LoggingAspect.java           # AOP 日志切面
│   ├── dto/
│   │   └── ApiResult.java               # API 响应包装类
│   ├── exception/
│   │   ├── ApiException.java            # 自定义 API 异常
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   └── utils/
│       └── LexoRankUtils.java           # 已存在
├── organization/                         # 组织管理域
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   │   └── EntityType.java              # 领域枚举（保留）
│   ├── dto/
│   │   ├── NodeType.java                # 领域枚举（保留）
│   │   ├── request/
│   │   └── response/                    # 移除 ApiResult.java
│   ├── mapper/
└── taglib/                               # 标签库域（结构不变）
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    │   └── TagObjectType.java           # 领域枚举（保留）
    ├── dto/
    ├── mapper/
```

### 组件移动清单

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `organization/OrganizationServiceApplication.java` | `ServiceApplication.java` | 重命名并移至顶层 |
| `organization/aspect/LoggingAspect.java` | `common/aspect/LoggingAspect.java` | AOP 日志切面 |
| `organization/dto/response/ApiResult.java` | `common/dto/ApiResult.java` | API 响应包装 |
| `organization/exception/ApiException.java` | `common/exception/ApiException.java` | 自定义异常 |
| `organization/exception/GlobalExceptionHandler.java` | `common/exception/GlobalExceptionHandler.java` | 全局异常处理 |

### 组件设计（保持现有设计不变）

#### ApiResult.java

保持现有设计，仅更新包路径：

```java
package com.reythecoder.common.dto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) { ... }
    public static <T> ApiResult<T> success(String message, T data) { ... }
    public static <T> ApiResult<T> error(int code, String message) { ... }
    public static <T> ApiResult<T> error(int code, String message, T data) { ... }
}
```

#### ApiException.java

保持现有设计，仅更新包路径：

```java
package com.reythecoder.common.exception;

public class ApiException extends RuntimeException {
    private final int code;

    public ApiException(int code, String message) { ... }
    public ApiException(int code, String message, Throwable cause) { ... }
    public int getCode() { ... }
}
```

#### GlobalExceptionHandler.java

保持现有设计，更新内部 import 路径：

```java
package com.reythecoder.common.exception;

import com.reythecoder.common.dto.ApiResult;
// ... 其他 import

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 保持现有异常处理方法不变
}
```

#### LoggingAspect.java

保持现有设计，仅更新包路径：

```java
package com.reythecoder.common.aspect;

@Aspect
@Component
public class LoggingAspect {
    // 保持现有切面逻辑不变
}
```

#### ServiceApplication.java

重命名并移至顶层包：

```java
package com.reythecoder;

@SpringBootApplication
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

### 需更新的引用

移动组件后，需更新以下 import 语句：

| 组件 | 受影响文件 |
|------|-----------|
| ApiResult | organization/controller/* (11个), taglib/controller/* (3个) |
| ApiException | organization/service/impl/* (7个), taglib/service/impl/* (3个) |

**Import 更新示例：**

```java
// 旧 import
import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.exception.ApiException;

// 新 import
import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.common.exception.ApiException;
```

## 实施步骤

1. 创建目标包结构（`common/aspect`, `common/dto`, `common/exception`）
2. 移动 `ApiResult.java` 至 `common/dto`
3. 移动 `ApiException.java` 至 `common/exception`
4. 移动 `GlobalExceptionHandler.java` 至 `common/exception`（更新内部 import）
5. 移动 `LoggingAspect.java` 至 `common/aspect`
6. 创建 `ServiceApplication.java` 在顶层包（复制 OrganizationServiceApplication.java 内容）
7. 更新所有受影响文件的 import 语句
8. 删除旧文件（organization/OrganizationServiceApplication.java, organization/exception/*, organization/aspect/*）
9. 更新文档（CLAUDE.md, README.md）
10. 运行测试验证

## 测试验证

- 运行单元测试：`./gradlew test`
- 运行集成测试：`./gradlew testIntegration`
- 验证应用启动：`./gradlew bootRun -Dspring-boot.run.profiles=dev`

## 影响范围

- Java 文件：约 25 个文件需更新 import
- 文档文件：CLAUDE.md, README.md 需更新包结构描述
- 无数据库变更
- 无 API 变更

## 风险

- **低风险**：纯代码重构，无功能变更
- **测试覆盖**：现有测试应能验证重构正确性