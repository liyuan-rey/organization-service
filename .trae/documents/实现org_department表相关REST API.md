# 实现org_department表相关REST API

## 目录结构设计

```
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

## 实现步骤

1. **创建实体类**：使用Record创建DepartmentEntity.java，映射org_department表
2. **创建Repository接口**：DepartmentRepository.java，继承JpaRepository
3. **创建请求DTO**：
   - DepartmentCreateReq.java：创建部门请求
   - DepartmentUpdateReq.java：更新部门请求
4. **创建响应DTO**：
   - DepartmentRsp.java：部门响应
   - ApiResponse.java：统一返回结构
5. **创建MapStruct映射器**：DepartmentMapper.java，实现Entity与DTO之间的映射
6. **创建Service接口**：DepartmentService.java，定义业务逻辑方法
7. **创建Service实现**：DepartmentServiceImpl.java，实现业务逻辑
8. **创建Controller**：DepartmentController.java，定义REST API端点
9. **创建全局异常处理**：GlobalExceptionHandler.java
10. **创建异常类**：ApiException.java

## 技术规范

- Entity类使用Record，不使用Lombok
- 请求参数类型以...Req为后缀
- 返回参数类型以...Rsp为后缀
- 使用MapStruct进行类型映射
- 统一返回JSON结构：{status: integer, message: string, data: Object | Array<any>}
- 实现优雅的全局异常处理
- 实现优雅的日志处理

## API端点设计

| HTTP方法 | 路径 | 功能 |
|----------|------|------|
| GET | /api/departments | 获取所有部门 |
| GET | /api/departments/{id} | 根据ID获取部门 |
| POST | /api/departments | 创建部门 |
| PUT | /api/departments/{id} | 更新部门 |
| DELETE | /api/departments/{id} | 删除部门 |