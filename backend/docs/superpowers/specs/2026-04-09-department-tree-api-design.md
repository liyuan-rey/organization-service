# 部门树结构 API 设计文档

**日期**: 2026-04-09
**状态**: 待实现

---

## 1. 概述

实现部门树结构查询 API，支持以分组为根的多用途树结构（如"基础通讯录"、"防汛调度通讯录"等），支持分层加载以应对大规模节点场景（5k+）。

---

## 2. API 设计

### 2.1 端点

```
GET /api/trees/{groupId}?depth={depth}
```

### 2.2 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `groupId` | UUID | 是 | 根分组ID，标识树的用途 |
| `depth` | Integer | 否 | 加载深度，默认=1（直接子节点），-1表示完整加载 |

### 2.3 响应结构

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": "01234567-...",
    "type": "GROUP",
    "name": "基础通讯录",
    "sortOrder": 1,
    "statistics": {
      "subGroupCount": 5,
      "subDepartmentCount": 50,
      "personnelCount": 200
    },
    "children": [
      {
        "id": "01234568-...",
        "type": "DEPARTMENT",
        "name": "办公室",
        "sortOrder": 1,
        "statistics": {
          "subGroupCount": 2,
          "subDepartmentCount": 3,
          "personnelCount": 15
        },
        "children": []
      }
    ]
  }
}
```

---

## 3. 数据结构

### 3.1 节点类型

| 类型 | 枚举值 | 可包含子节点 | 说明 |
|------|--------|-------------|------|
| 分组 | GROUP | GROUP, DEPARTMENT, PERSONNEL | 根节点类型，可挂子分组、部门、人员 |
| 部门 | DEPARTMENT | GROUP, DEPARTMENT, PERSONNEL | 组织单元，可挂子分组、子部门、人员 |
| 人员 | PERSONNEL | 无（叶子节点） | 叶子节点 |

### 3.2 层级关系与数据来源

| 父节点类型 | 子节点类型 | 关联表 |
|-----------|-----------|--------|
| GROUP | GROUP | GroupHierarchy |
| GROUP | DEPARTMENT | GroupDepartment |
| GROUP | PERSONNEL | GroupPersonnel |
| DEPARTMENT | GROUP | **DepartmentGroup（新建）** |
| DEPARTMENT | DEPARTMENT | DepartmentHierarchy |
| DEPARTMENT | PERSONNEL | DepartmentPersonnel |

### 3.3 统计字段

| 字段 | 类型 | 说明 |
|------|------|------|
| subGroupCount | Integer | 子分组数量 |
| subDepartmentCount | Integer | 子部门数量 |
| personnelCount | Integer | 人员数量 |

**来源映射**：

| 父节点类型 | subGroupCount 来源 | subDepartmentCount 来源 | personnelCount 来源 |
|-----------|-------------------|------------------------|--------------------|
| GROUP | GroupHierarchy | GroupDepartment | GroupPersonnel |
| DEPARTMENT | DepartmentGroup | DepartmentHierarchy | DepartmentPersonnel |
| PERSONNEL | - | - | - |

### 3.4 排序规则

节点按以下顺序排序：
1. **类型优先**：GROUP > DEPARTMENT > PERSONNEL
2. **sortOrder 升序**：同类型节点按 sortOrder 字段升序排列

---

## 4. 实现方案

### 4.1 新增组件

#### 4.1.1 实体层

| 组件 | 包路径 | 说明 |
|------|--------|------|
| DepartmentGroupEntity | entity/ | 部门-分组关联实体（新建） |

#### 4.1.2 Repository 层

| 组件 | 包路径 | 说明 |
|------|--------|------|
| DepartmentGroupRepository | repository/ | 部门-分组关联 Repository（新建） |

#### 4.1.3 Service 层

| 组件 | 包路径 | 说明 |
|------|--------|------|
| TreeService | service/ | 树结构服务接口（新建） |
| TreeServiceImpl | service/impl/ | 树结构服务实现（新建） |
| DepartmentGroupService | service/ | 部门-分组服务接口（新建） |
| DepartmentGroupServiceImpl | service/impl/ | 部门-分组服务实现（新建） |

#### 4.1.4 Controller 层

| 组件 | 包路径 | 说明 |
|------|--------|------|
| TreeController | controller/ | 树结构控制器（新建） |
| DepartmentGroupController | controller/ | 部门-分组关联控制器（新建） |

#### 4.1.5 DTO 层

| 组件 | 包路径 | 说明 |
|------|--------|------|
| TreeNodeRsp | dto/response/ | 树节点响应 DTO（新建） |
| TreeStatistics | dto/response/ | 统计信息 DTO（新建） |
| NodeType | dto/ | 节点类型枚举（新建） |
| DepartmentGroupCreateReq | dto/request/ | 部门-分组创建请求 DTO（新建） |
| DepartmentGroupRsp | dto/response/ | 部门-分组响应 DTO（新建） |

### 4.2 DepartmentGroupEntity 设计

```java
@Entity
@Table(name = "org_department_group",
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "group_id"}))
public class DepartmentGroupEntity {
    @Id
    private UUID id;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    // 标准字段：createTime, updateTime, tenantId
}
```

### 4.3 Repository 新增方法

需要在现有 Repository 中添加计数方法：

```java
// GroupHierarchyRepository
long countByParentId(UUID parentId);

// GroupDepartmentRepository
long countByGroupId(UUID groupId);

// GroupPersonnelRepository
long countByGroupId(UUID groupId);

// DepartmentGroupRepository（新建）
long countByDepartmentId(UUID departmentId);
List<DepartmentGroupEntity> findByDepartmentIdOrderBySortOrderAsc(UUID departmentId);
List<DepartmentGroupEntity> findByGroupIdOrderBySortOrderAsc(UUID groupId);

// DepartmentHierarchyRepository
long countByParentId(UUID parentId);

// DepartmentPersonnelRepository
long countByDepartmentId(UUID departmentId);
```

### 4.4 查询逻辑

TreeServiceImpl 核心逻辑：

```
1. 根据 groupId 获取分组信息作为根节点
2. 根据 depth 参数递归查询子节点：
   - depth=1：仅查询直接子节点
   - depth=N：查询到第 N 层
   - depth=-1：完整加载所有层级
3. 为每个节点：
   a. 根据节点类型查询对应的子节点关联表
   b. 计算统计信息（subGroupCount, subDepartmentCount, personnelCount）
4. 按排序规则整理节点顺序（类型优先 + sortOrder）
5. 构建嵌套树结构返回
```

---

## 5. 错误处理

| 场景 | HTTP 状态码 | 错误信息 |
|------|------------|---------|
| groupId 不存在 | 404 | 分组不存在 |
| depth 参数无效（非正整数或-1） | 400 | depth 参数必须为正整数或-1 |

---

## 6. 测试策略

### 6.1 单元测试

- TreeService 各种 depth 参数的查询逻辑
- 统计信息计算逻辑
- 排序逻辑验证
- DepartmentGroupService CRUD 操作

### 6.2 集成测试

- 完整树结构加载验证
- 分层加载验证（depth=1, depth=2, depth=-1）
- 跨层级关系验证（部门下的子分组）
- 大规模数据性能测试（模拟 5k+ 节点）

---

## 7. 后续优化

- [ ] 添加缓存支持，缓存已加载的树节点
- [ ] 添加异步加载支持，应对超大树结构
- [ ] 支持节点搜索功能