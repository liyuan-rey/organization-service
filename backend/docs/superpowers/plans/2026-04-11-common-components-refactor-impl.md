# 后端共享组件重构实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 organization 包中的共享组件移动到 common 包，并重命名主入口类为 ServiceApplication。

**Architecture:** 纯代码重构，无功能变更。移动 ApiResult、ApiException、GlobalExceptionHandler、LoggingAspect 到 common 包，创建 ServiceApplication.java 在顶层包。

**Tech Stack:** Spring Boot 4.0 + Java 17 + Gradle

---

## File Structure

### 创建文件
- `src/main/java/com/reythecoder/ServiceApplication.java`
- `src/main/java/com/reythecoder/common/aspect/LoggingAspect.java`
- `src/main/java/com/reythecoder/common/dto/ApiResult.java`
- `src/main/java/com/reythecoder/common/exception/ApiException.java`
- `src/main/java/com/reythecoder/common/exception/GlobalExceptionHandler.java`

### 删除文件
- `src/main/java/com/reythecoder/organization/OrganizationServiceApplication.java`
- `src/main/java/com/reythecoder/organization/aspect/LoggingAspect.java`
- `src/main/java/com/reythecoder/organization/dto/response/ApiResult.java`
- `src/main/java/com/reythecoder/organization/exception/ApiException.java`
- `src/main/java/com/reythecoder/organization/exception/GlobalExceptionHandler.java`

### 修改文件（import 更新）
**Controllers (11 files):**
- `src/main/java/com/reythecoder/organization/controller/*.java`
- `src/main/java/com/reythecoder/taglib/controller/*.java`

**Services (10 files):**
- `src/main/java/com/reythecoder/organization/service/impl/*.java`
- `src/main/java/com/reythecoder/taglib/service/impl/*.java`

**Tests (10 files):**
- `src/test/java/com/reythecoder/organization/service/*.java`
- `src/test/java/com/reythecoder/taglib/service/*.java`

---

### Task 1: 创建目标包结构

**Files:**
- Create directories: `common/aspect/`, `common/dto/`, `common/exception/`

- [ ] **Step 1: 创建目录结构**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder && \
mkdir -p common/aspect common/dto common/exception && \
ls -la common/
```

Expected:
```
total 12
drwxr-xr-x 2 sam sam 4096 Apr 11 23:00 aspect
drwxr-xr-x 2 sam sam 4096 Apr 11 23:00 dto
drwxr-xr-x 2 sam sam 4096 Apr 11 23:00 exception
```

---

### Task 2: 创建 ApiResult.java 在 common/dto

**Files:**
- Create: `src/main/java/com/reythecoder/common/dto/ApiResult.java`

- [ ] **Step 1: 创建 ApiResult.java**

```java
package com.reythecoder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResult<T> error(int code, String message, T data) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run:
```bash
cat /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/common/dto/ApiResult.java | head -5
```

Expected:
```
package com.reythecoder.common.dto;
```

---

### Task 3: 创建 ApiException.java 在 common/exception

**Files:**
- Create: `src/main/java/com/reythecoder/common/exception/ApiException.java`

- [ ] **Step 1: 创建 ApiException.java**

```java
package com.reythecoder.common.exception;

public class ApiException extends RuntimeException {
    private final int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run:
```bash
cat /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/common/exception/ApiException.java | head -5
```

Expected:
```
package com.reythecoder.common.exception;
```

---

### Task 4: 创建 GlobalExceptionHandler.java 在 common/exception

**Files:**
- Create: `src/main/java/com/reythecoder/common/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: 创建 GlobalExceptionHandler.java**

```java
package com.reythecoder.common.exception;

import com.reythecoder.common.dto.ApiResult;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ApiResult<Object> handleApiException(ApiException e) {
        logger.error("API异常: {}", e.getMessage(), e);
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证异常: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Object> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("约束违反异常: {}", e.getMessage(), e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("约束违反");
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("参数类型不匹配异常: {}", e.getMessage(), e);
        var expectedType = e.getRequiredType();
        String typeName = expectedType != null ? expectedType.getSimpleName() : "未知";
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s", e.getName(), typeName);
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("HTTP消息不可读异常: {}", e.getMessage(), e);
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误");
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run:
```bash
cat /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/common/exception/GlobalExceptionHandler.java | head -10
```

Expected:
```
package com.reythecoder.common.exception;

import com.reythecoder.common.dto.ApiResult;
```

---

### Task 5: 创建 LoggingAspect.java 在 common/aspect

**Files:**
- Create: `src/main/java/com/reythecoder/common/aspect/LoggingAspect.java`

- [ ] **Step 1: 创建 LoggingAspect.java**

```java
package com.reythecoder.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public void controllerAndServicePointcut() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethodPointcut() {
    }

    @Around("controllerAndServicePointcut() && publicMethodPointcut()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String methodSignature = className + "." + methodName;

        logger.info("开始执行方法：{}", methodSignature);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("方法执行成功：{} | 耗时：{}ms", methodSignature, duration);

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("方法执行异常：{} | 耗时：{}ms | 异常：{}", methodSignature, duration, e.getMessage(), e);

            throw e;
        }
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run:
```bash
cat /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/common/aspect/LoggingAspect.java | head -5
```

Expected:
```
package com.reythecoder.common.aspect;
```

---

### Task 6: 创建 ServiceApplication.java 在顶层包

**Files:**
- Create: `src/main/java/com/reythecoder/ServiceApplication.java`

- [ ] **Step 1: 创建 ServiceApplication.java**

```java
package com.reythecoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run:
```bash
cat /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/ServiceApplication.java
```

Expected:
```
package com.reythecoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

---

### Task 7: 更新 organization controllers 的 ApiResult import

**Files:**
- Modify: `src/main/java/com/reythecoder/organization/controller/*.java` (11 files)

- [ ] **Step 1: 批量更新 import 语句**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/controller && \
sed -i 's/import com\.reythecoder\.organization\.dto\.response\.ApiResult;/import com.reythecoder.common.dto.ApiResult;/g' *.java && \
grep -l "import com.reythecoder.common.dto.ApiResult" *.java | wc -l
```

Expected: `11` (11 controller files updated)

- [ ] **Step 2: 验证 import 更新正确**

Run:
```bash
grep "import.*ApiResult" /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/controller/DepartmentController.java
```

Expected:
```
import com.reythecoder.common.dto.ApiResult;
```

---

### Task 8: 更新 taglib controllers 的 ApiResult import

**Files:**
- Modify: `src/main/java/com/reythecoder/taglib/controller/*.java` (3 files)

- [ ] **Step 1: 批量更新 import 语句**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/taglib/controller && \
sed -i 's/import com\.reythecoder\.organization\.dto\.response\.ApiResult;/import com.reythecoder.common.dto.ApiResult;/g' *.java && \
grep -l "import com.reythecoder.common.dto.ApiResult" *.java | wc -l
```

Expected: `3` (3 controller files updated)

- [ ] **Step 2: 验证 import 更新正确**

Run:
```bash
grep "import.*ApiResult" /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/taglib/controller/TagController.java
```

Expected:
```
import com.reythecoder.common.dto.ApiResult;
```

---

### Task 9: 更新 organization service impl 的 ApiException import

**Files:**
- Modify: `src/main/java/com/reythecoder/organization/service/impl/*.java` (7 files)

- [ ] **Step 1: 批量更新 import 语句**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/service/impl && \
sed -i 's/import com\.reythecoder\.organization\.exception\.ApiException;/import com.reythecoder.common.exception.ApiException;/g' *.java && \
grep -l "import com.reythecoder.common.exception.ApiException" *.java | wc -l
```

Expected: `7` (7 service impl files updated)

- [ ] **Step 2: 验证 import 更新正确**

Run:
```bash
grep "import.*ApiException" /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/service/impl/DepartmentServiceImpl.java
```

Expected:
```
import com.reythecoder.common.exception.ApiException;
```

---

### Task 10: 更新 taglib service impl 的 ApiException import

**Files:**
- Modify: `src/main/java/com/reythecoder/taglib/service/impl/*.java` (3 files)

- [ ] **Step 1: 批量更新 import 语句**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/taglib/service/impl && \
sed -i 's/import com\.reythecoder\.organization\.exception\.ApiException;/import com.reythecoder.common.exception.ApiException;/g' *.java && \
grep -l "import com.reythecoder.common.exception.ApiException" *.java | wc -l
```

Expected: `3` (3 service impl files updated)

- [ ] **Step 2: 验证 import 更新正确**

Run:
```bash
grep "import.*ApiException" /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/taglib/service/impl/TagServiceImpl.java
```

Expected:
```
import com.reythecoder.common.exception.ApiException;
```

---

### Task 11: 更新测试文件的 import

**Files:**
- Modify: `src/test/java/com/reythecoder/organization/service/*.java` (7 files)
- Modify: `src/test/java/com/reythecoder/taglib/service/*.java` (3 files)

- [ ] **Step 1: 更新 organization 测试文件 import**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/test/java/com/reythecoder/organization/service && \
sed -i 's/import com\.reythecoder\.organization\.exception\.ApiException;/import com.reythecoder.common.exception.ApiException;/g' *.java && \
grep -l "import com.reythecoder.common.exception.ApiException" *.java | wc -l
```

Expected: `7`

- [ ] **Step 2: 更新 taglib 测试文件 import**

Run:
```bash
cd /home/sam/codes/organization-service/backend/src/test/java/com/reythecoder/taglib/service && \
sed -i 's/import com\.reythecoder\.organization\.exception\.ApiException;/import com.reythecoder.common.exception.ApiException;/g' *.java && \
grep -l "import com.reythecoder.common.exception.ApiException" *.java | wc -l
```

Expected: `3`

---

### Task 12: 删除旧文件

**Files:**
- Delete: organization/OrganizationServiceApplication.java
- Delete: organization/aspect/LoggingAspect.java
- Delete: organization/dto/response/ApiResult.java
- Delete: organization/exception/ApiException.java
- Delete: organization/exception/GlobalExceptionHandler.java

- [ ] **Step 1: 删除旧的主入口类**

Run:
```bash
rm /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/OrganizationServiceApplication.java
```

- [ ] **Step 2: 删除旧的 aspect 目录**

Run:
```bash
rm -r /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/aspect
```

- [ ] **Step 3: 删除旧的 ApiResult.java**

Run:
```bash
rm /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/dto/response/ApiResult.java
```

- [ ] **Step 4: 删除旧的 exception 目录**

Run:
```bash
rm -r /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/exception
```

- [ ] **Step 5: 验证旧文件已删除**

Run:
```bash
ls /home/sam/codes/organization-service/backend/src/main/java/com/reythecoder/organization/ | grep -E "OrganizationServiceApplication|aspect|exception" || echo "旧文件已删除"
```

Expected: `旧文件已删除`

---

### Task 13: 运行单元测试验证重构正确性

**Files:**
- None (validation only)

- [ ] **Step 1: 运行单元测试**

Run:
```bash
cd /home/sam/codes/organization-service/backend && ./gradlew test --no-daemon
```

Expected: All tests pass

- [ ] **Step 2: 检查测试结果**

Run:
```bash
cd /home/sam/codes/organization-service/backend && cat build/reports/tests/test/index.html | grep -o "tests.*passed" | head -1
```

Expected: Contains "passed" with test count

---

### Task 14: 更新 CLAUDE.md 文档

**Files:**
- Modify: `backend/CLAUDE.md`

- [ ] **Step 1: 更新项目架构描述**

找到 "## Project Architecture" 部分，更新包结构描述：

```markdown
## Project Architecture

Three top-level packages under `com.reythecoder`:

```
src/main/java/com/reythecoder/
├── ServiceApplication.java                 # Main entry point
├── common/                                 # Shared components
│   ├── aspect/
│   │   └── LoggingAspect.java             # AOP logging aspect
│   ├── dto/
│   │   └── ApiResult.java                 # API response wrapper
│   ├── exception/
│   │   ├── ApiException.java              # Custom API exception
│   │   └── GlobalExceptionHandler.java    # Global exception handler
│   └── utils/
│       └── LexoRankUtils.java             # LexoRank sorting utility
├── organization/                           # Core organization domain
│   ├── controller/                         # 14 REST controllers
│   ├── service/                            # 14 service interfaces + impl/
│   ├── repository/                         # 13 Spring Data JPA repositories
│   ├── entity/                             # 14 JPA entities + EntityType enum
│   ├── dto/
│   │   ├── NodeType.java                   # Enum for tree node types
│   │   ├── request/                        # 19 Request DTOs (*Req.java)
│   │   └── response/                       # 13 Response DTOs (*Rsp.java)
│   ├── mapper/                             # 5 MapStruct mappers
├── taglib/                                 # Tag library management system
│   ├── controller/                         # 3 controllers (TagCategory, Tag, TagRelation)
│   ├── service/                            # 3 service interfaces + impl/
│   ├── repository/                         # 3 repositories
│   ├── entity/                             # 3 entities (TagCategory, Tag, TagRelation) + TagObjectType enum
│   ├── dto/
│   │   ├── request/                        # 6 Request DTOs
│   │   └── response/                       # 4 Response DTOs (TagCategoryRsp, TagRsp, TagTreeRsp, TagRelationRsp)
│   └── mapper/                             # 2 MapStruct mappers
```
```

- [ ] **Step 2: 更新 Key Conventions 部分**

找到 "**Key Conventions:**" 部分，更新 ApiResult 描述：

```markdown
**Key Conventions:**
- DTOs use `Record` type (no Lombok on DTOs; Lombok used on entities)
- Request DTOs suffixed with `Req`, Response DTOs with `Rsp`
- `ApiResult<T>` in `common.dto` returns `{ code, message, data }` — all controllers use this wrapper
- Primary keys use UUIDv7 via `UUIDv7.randomUUID()`
- Service layer: interface in `service/`, implementation in `service/impl/`
- MapStruct mappers use `componentModel = "spring"`
- 前后端交互遵循 `backend/docs/openapi.yaml`，这是符合 OpenAPI 3.1 规范的接口描述
```

---

### Task 15: 更新 README.md 文档

**Files:**
- Modify: `backend/README.md`

- [ ] **Step 1: 更新目录结构描述**

找到 "## 📁 目录结构" 部分，更新为：

```markdown
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
│   │   │   └── LexoRankUtils.java             # LexoRank 排序工具
│   ├── organization/                           # 组织管理核心域
│   │   ├── controller/                         # REST 控制器
│   │   ├── service/                            # 业务逻辑层（接口 + impl/）
│   │   ├── repository/                         # 数据访问层
│   │   ├── entity/                             # JPA 实体
│   │   ├── dto/
│   │   │   ├── request/                        # 请求 DTO（*Req.java）
│   │   │   └── response/                       # 响应 DTO（*Rsp.java）
│   │   ├── mapper/                             # MapStruct 映射器
│   ├── taglib/                                 # 标签库管理
│   │   ├── controller/                         # TagCategory, Tag, TagRelation 控制器
│   │   ├── service/                            # 业务逻辑层（接口 + impl/）
│   │   ├── repository/                         # 数据访问层
│   │   ├── entity/                             # TagCategory, Tag, TagRelation 实体
│   │   ├── dto/
│   │   │   ├── request/                        # 请求 DTO
│   │   │   └── response/                       # 响应 DTO
│   │   └── mapper/                             # MapStruct 映射器
```
```

---

### Task 16: 提交重构更改

**Files:**
- None (git commit)

- [ ] **Step 1: 查看更改状态**

Run:
```bash
cd /home/sam/codes/organization-service/backend && git status
```

Expected: Shows all modified and new files

- [ ] **Step 2: 添加所有更改到暂存区**

Run:
```bash
cd /home/sam/codes/organization-service/backend && \
git add src/main/java/com/reythecoder/ServiceApplication.java \
        src/main/java/com/reythecoder/common/ \
        src/main/java/com/reythecoder/organization/controller/ \
        src/main/java/com/reythecoder/organization/service/ \
        src/main/java/com/reythecoder/organization/dto/response/ \
        src/main/java/com/reythecoder/organization/ \
        src/main/java/com/reythecoder/taglib/controller/ \
        src/main/java/com/reythecoder/taglib/service/ \
        src/test/java/com/reythecoder/ \
        CLAUDE.md README.md docs/superpowers/specs/2026-04-11-common-components-refactor-design.md
```

- [ ] **Step 3: 提交更改**

Run:
```bash
cd /home/sam/codes/organization-service/backend && \
git commit -m "$(cat <<'EOF'
refactor(backend): move shared components to common package

- Move ApiResult to common.dto
- Move ApiException to common.exception
- Move GlobalExceptionHandler to common.exception
- Move LoggingAspect to common.aspect
- Rename OrganizationServiceApplication to ServiceApplication
- Update all import references in controllers, services, and tests
- Update CLAUDE.md and README.md documentation
EOF
)"
```

Expected: Commit successful

- [ ] **Step 4: 验证提交**

Run:
```bash
cd /home/sam/codes/organization-service/backend && git log -1 --oneline
```

Expected: Shows the new commit

---

## Self-Review

**1. Spec coverage:**
- ✅ Task 1-6: 创建目标包结构和文件（覆盖设计文档"组件移动清单"）
- ✅ Task 7-11: 更新 import 引用（覆盖设计文档"需更新的引用"）
- ✅ Task 12: 删除旧文件（覆盖设计文档"删除文件"）
- ✅ Task 13: 测试验证（覆盖设计文档"测试验证"）
- ✅ Task 14-15: 更新文档（覆盖设计文档"影响范围"）
- ✅ Task 16: 提交更改（覆盖设计文档"实施步骤"）

**2. Placeholder scan:**
- ✅ 无 TBD、TODO、implement later
- ✅ 无 "add appropriate error handling"
- ✅ 无 "write tests for above"
- ✅ 所有步骤包含完整代码或命令

**3. Type consistency:**
- ✅ ApiResult 包路径一致：`com.reythecoder.common.dto.ApiResult`
- ✅ ApiException 包路径一致：`com.reythecoder.common.exception.ApiException`
- ✅ 类名保持不变，仅移动包路径