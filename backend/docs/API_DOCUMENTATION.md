# 组织服务 API 文档

## 概述

组织服务提供部门管理、人员管理和用户组管理的 RESTful API。

- **基础 URL**: `/api`
- **版本**: v1
- **响应格式**: JSON
- **认证**: 暂未实现（后续添加）

## 响应格式

所有 API 调用返回统一的响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 资源创建成功 |
| 204 | 资源删除成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 部门管理 API

### 获取所有部门

**GET** `/api/departments`

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "11111111-1111-1111-1111-111111111111",
      "name": "人力资源部",
      "englishName": "Human Resources Department",
      "shortName": "HR",
      "orgCode": "ORG-HR-001",
      "phone": "+1-555-0101",
      "fax": "+1-555-0102",
      "email": "hr@company.com",
      "address": "北京市朝阳区某某街道123号",
      "postalCode": "100000",
      "createTime": "2026-02-22T10:00:00+08:00",
      "updateTime": "2026-02-22T10:00:00+08:00",
      "tenantId": "00000000-0000-0000-0000-000000000000"
    }
  ]
}
```

### 根据ID获取部门

**GET** `/api/departments/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 部门ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "11111111-1111-1111-1111-111111111111",
    "name": "人力资源部",
    "englishName": "Human Resources Department",
    "shortName": "HR",
    "orgCode": "ORG-HR-001",
    "phone": "+1-555-0101",
    "fax": "+1-555-0102",
    "email": "hr@company.com",
    "address": "北京市朝阳区某某街道123号",
    "postalCode": "100000",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:00:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 创建部门

**POST** `/api/departments`

#### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 部门名称 |
| englishName | string | 否 | 部门英文名称 |
| shortName | string | 否 | 部门简称 |
| orgCode | string | 否 | 组织代码 |
| phone | string | 否 | 联系电话 |
| fax | string | 否 | 传真 |
| email | string | 否 | 邮箱 |
| address | string | 否 | 地址 |
| postalCode | string | 否 | 邮政编码 |

#### 请求示例

```json
{
  "name": "技术部",
  "englishName": "Technology Department",
  "shortName": "Tech",
  "orgCode": "ORG-TECH-001",
  "phone": "+1-555-0103",
  "fax": "+1-555-0104",
  "email": "tech@company.com",
  "address": "北京市海淀区某某科技园区456号",
  "postalCode": "100001"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "部门创建成功",
  "data": {
    "id": "22222222-2222-2222-2222-222222222222",
    "name": "技术部",
    "englishName": "Technology Department",
    "shortName": "Tech",
    "orgCode": "ORG-TECH-001",
    "phone": "+1-555-0103",
    "fax": "+1-555-0104",
    "email": "tech@company.com",
    "address": "北京市海淀区某某科技园区456号",
    "postalCode": "100001",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:00:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 更新部门

**PUT** `/api/departments/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 部门ID |

#### 请求体

同创建部门的请求体，所有字段都是可选的。

#### 响应示例

```json
{
  "code": 200,
  "message": "部门更新成功",
  "data": {
    "id": "22222222-2222-2222-2222-222222222222",
    "name": "开发部",
    "englishName": "Development Department",
    "shortName": "Dev",
    "orgCode": "ORG-DEV-001",
    "phone": "+1-555-0105",
    "fax": "+1-555-0106",
    "email": "dev@company.com",
    "address": "北京市海淀区某某科技园区789号",
    "postalCode": "100002",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:05:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 删除部门

**DELETE** `/api/departments/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 部门ID |

#### 响应

- 状态码: 204 No Content
- 无响应体

## 人员管理 API

### 获取所有人员

**GET** `/api/personnel`

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "44444444-4444-4444-4444-444444444444",
      "name": "张三",
      "gender": "M",
      "idCard": "110101199001011234",
      "mobile": "+1-555-0105",
      "telephone": "+1-555-0106",
      "fax": "+1-555-0107",
      "email": "zhangsan@company.com",
      "createTime": "2026-02-22T10:00:00+08:00",
      "updateTime": "2026-02-22T10:00:00+08:00",
      "tenantId": "00000000-0000-0000-0000-000000000000"
    }
  ]
}
```

### 根据ID获取人员

**GET** `/api/personnel/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 人员ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "44444444-4444-4444-4444-444444444444",
    "name": "张三",
    "gender": "M",
    "idCard": "110101199001011234",
    "mobile": "+1-555-0105",
    "telephone": "+1-555-0106",
    "fax": "+1-555-0107",
    "email": "zhangsan@company.com",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:00:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 创建人员

**POST** `/api/personnel`

#### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 姓名 |
| gender | string | 是 | 性别 (M/F) |
| idCard | string | 是 | 身份证号 |
| mobile | string | 是 | 手机号 |
| telephone | string | 否 | 电话 |
| fax | string | 否 | 传真 |
| email | string | 否 | 邮箱 |

#### 请求示例

```json
{
  "name": "李四",
  "gender": "F",
  "idCard": "110101199001015678",
  "mobile": "+1-555-0108",
  "telephone": "+1-555-0109",
  "fax": "+1-555-0110",
  "email": "lisi@company.com"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "人员创建成功",
  "data": {
    "id": "55555555-5555-5555-5555-555555555555",
    "name": "李四",
    "gender": "F",
    "idCard": "110101199001015678",
    "mobile": "+1-555-0108",
    "telephone": "+1-555-0109",
    "fax": "+1-555-0110",
    "email": "lisi@company.com",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:00:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 更新人员

**PUT** `/api/personnel/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 人员ID |

#### 请求体

同创建人员的请求体，所有字段都是可选的。

#### 响应示例

```json
{
  "code": 200,
  "message": "人员更新成功",
  "data": {
    "id": "55555555-5555-5555-5555-555555555555",
    "name": "王五",
    "gender": "M",
    "idCard": "110101199001019012",
    "mobile": "+1-555-0111",
    "telephone": "+1-555-0112",
    "fax": "+1-555-0113",
    "email": "wangwu@company.com",
    "createTime": "2026-02-22T10:00:00+08:00",
    "updateTime": "2026-02-22T10:05:00+08:00",
    "tenantId": "00000000-0000-0000-0000-000000000000"
  }
}
```

### 删除人员

**DELETE** `/api/personnel/{id}`

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | UUID | 是 | 人员ID |

#### 响应

- 状态码: 204 No Content
- 无响应体

## OpenAPI 规范文件

本项目使用 OpenAPI 3.0 规范的 YAML 文件来描述 API：

- **位置**: `src/main/resources/openapi.yaml`
- **版本**: OpenAPI 3.0.3

该文件包含了所有 API 端点的完整描述，包括：
- 请求/响应格式
- 参数说明
- 状态码
- 数据结构定义

可以使用以下工具查看和测试 API：
- **Swagger Editor**: 将 YAML 文件导入 https://editor.swagger.io/
- **Stoplight Studio**: 可视化编辑和预览
- **Redoc**: 生成美观的静态文档

## 错误处理

### 常见错误响应

#### 部门不存在 (404)

```json
{
  "code": 404,
  "message": "部门不存在",
  "data": null
}
```

#### 请求参数验证失败 (400)

```json
{
  "code": 400,
  "message": "部门名称不能为空",
  "data": null
}
```