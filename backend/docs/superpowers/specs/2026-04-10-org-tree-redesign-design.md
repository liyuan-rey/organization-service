---
title: 组织结构树重构设计文档
date: 2026-04-10
author: Claude
status: Approved
---

# 组织结构树重构设计文档

## 1. 设计背景与目标

### 1.1 当前问题

现有组织结构管理通过多个关联表实现：

- `org_group_hierarchy`：Group 层级关系
- `org_department_hierarchy`：Department 层级关系
- `org_group_department`：Group-Department 关联
- `org_group_personnel`：Group-Personnel 关联
- `org_department_group`：Department-Group 关联
- `org_department_personnel`：Department-Personnel 关联

**问题：**
- 6 个关联表管理层级关系，结构复杂
- 跨类型层级查询需要多表 JOIN，性能较差
- 代码维护成本高，业务逻辑分散

### 1.2 设计目标

- **简化**：用单表 `org_tree` 统一管理 Group/Department/Personnel 的层级关系
- **灵活**：支持同一实体对应多个树节点（多归属场景）
- **高效**：使用 UUID[] 数组 + GIN 索引优化路径查询
- **兼容**：保留 DepartmentPosition 和 PersonnelPosition 关联表

## 2. 核心设计决策

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 树结构存储 | 父子关系法（parent_id） | 简单直观，递归查询即可，迁移成本低 |
| 实体关联 | 外键引用法（entity_type + entity_id） | 保持原有业务表不变，职责清晰 |
| Position 处理 | 不纳入树结构，保留关联表 | 岗位是角色定义，与人员关联更符合业务语义 |
| 多归属支持 | 同一实体可对应多个树节点 | 每节点单父节点，树结构严格；多节点实现多归属 |
| 排序算法 | LexoRank 字符排序 | 动态插入无需重排其他元素，性能最优 |
| 删除策略 | 逻辑删除（removed 字段） | 支持数据恢复，审计追溯 |
| 路径存储 | UUID[] 数组 + GIN 索引 | PostgreSQL 原生支持，查询效率高 |

## 3. 数据库设计

### 3.1 org_tree 表结构

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| `id` | UUID | NOT NULL | - | 主键，UUIDv7 |
| `parent_id` | UUID | NOT NULL | - | 父节点ID，虚拟根指向自身 |
| `entity_type` | VARCHAR(20) | NOT NULL | 'ROOT' | 实体类型：ROOT / GROUP / DEPARTMENT / PERSONNEL |
| `entity_id` | UUID | NOT NULL | - | 关联的业务实体ID |
| `alias` | VARCHAR(100) | NOT NULL | '' | 别名，空字符串表示无别名。显示逻辑：alias > entity.name |
| `level` | INTEGER | NOT NULL | 0 | 层级深度。虚拟根为 0，业务根节点为 1，依次递增 |
| `path` | UUID[] | NOT NULL | '{}' | 节点路径数组，从根到父节点的 UUID 序列 |
| `sort_rank` | VARCHAR(12) | NOT NULL | 'a0' | LexoRank 排序值，字典序排序 |
| `removed` | BOOLEAN | NOT NULL | FALSE | 逻辑删除标记 |
| `create_time` | TIMESTAMP | NOT NULL | '2000-01-01 00:00:00' | 创建时间 |
| `update_time` | TIMESTAMP | NOT NULL | '2000-01-01 00:00:00' | 更新时间 |
| `tenant_id` | UUID | NOT NULL | - | 租户ID |

### 3.2 索引设计

| 索引名 | 类型 | 字段 | 用途 |
|--------|------|------|------|
| `pk_org_tree` | 主键 | `id` | 主键索引 |
| `idx_org_tree_parent` | B-tree | `(parent_id, sort_rank)` | 查询子节点并排序 |
| `idx_org_tree_entity` | B-tree | `(entity_type, entity_id)` | 反向查询实体节点 |
| `idx_org_tree_path_gin` | GIN | `path` | 高效路径查询（PostgreSQL 数组索引） |
| `uk_org_tree_parent_entity` | 唯一 | `(parent_id, entity_type, entity_id)` WHERE removed=FALSE | 防止同一父节点下重复挂载 |

### 3.3 虚拟根节点设计

- 系统初始化时创建虚拟根节点（id = `00000000-0000-0000-0000-000000000000`）
- entity_type = 'ROOT'，parent_id = id（指向自身）
- 所有业务根节点（顶层 Group/Department）的 parent_id 指向虚拟根
- level = 0，path = '{}'

**优点：**
- parent_id 永远非空，递归查询逻辑统一
- 无需处理 NULL parent_id 的特殊情况

### 3.4 唯一约束设计

`(parent_id, entity_type, entity_id)` 组合唯一约束（排除已删除记录）：

- 同一个父节点下，不能有两个子节点指向同一个业务实体
- 支持同一实体在不同父节点下有多个节点（多归属）

### 3.5 LexoRank 排序算法

**核心原理：**
- 使用字符集 `0-9a-z`（36个字符）
- 字典序排序，插入时取两值中间值，无需重排其他元素

**操作示例：**
```
初始状态: a0, b0, c0 (3个节点)
在 a0 和 b0 之间插入 → 计算中间值 "aM"
结果: a0, aM, b0, c0 (其他节点无需更新)
```

**字段长度：VARCHAR(12)** 可支持极细粒度排序。

### 3.6 建库脚本

**脚本文件：**
- `V1__Create_base_tables.sql`：DDL，创建所有表结构、索引、约束
- `V2__Insert_sample_data.sql`：DML，插入虚拟根节点、示例业务数据、树结构

**示例数据包含：**
- 1 个虚拟根节点
- 3 个分组（总公司、华东分公司、华北分公司）
- 5 个部门（人力资源部、财务部、技术部、上海研发中心、北京研发中心）
- 5 个人员（张三、李四、王五、赵六、钱七）
- 5 个岗位
- 完整组织树结构（演示多归属：王五同时在上海和北京研发中心）

## 4. Java 代码设计

### 4.1 Entity 实体类

**OrgTreeNodeEntity.java：**

```java
@Entity
@Table(name = "org_tree",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_org_tree_parent_entity",
           columnNames = {"parent_id", "entity_type", "entity_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgTreeNodeEntity {

    @Id
    private UUID id;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(name = "entity_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "alias", length = 100, nullable = false)
    private String alias;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "path", nullable = false, columnDefinition = "UUID[]")
    private UUID[] path;

    @Column(name = "sort_rank", length = 12, nullable = false)
    private String sortRank;

    @Column(name = "removed", nullable = false)
    private Boolean removed;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}
```

**EntityType.java：**

```java
public enum EntityType {
    ROOT,
    GROUP,
    DEPARTMENT,
    PERSONNEL
}
```

### 4.2 Repository 接口

**OrgTreeNodeRepository.java：**

```java
public interface OrgTreeNodeRepository extends JpaRepository<OrgTreeNodeEntity, UUID> {

    // ===== 方法命名约定 =====

    List<OrgTreeNodeEntity> findByParentIdOrderBySortOrderAsc(UUID parentId);

    List<OrgTreeNodeEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    long countByParentId(UUID parentId);

    Optional<OrgTreeNodeEntity> findByParentIdAndEntityTypeAndEntityId(
        UUID parentId, EntityType entityType, UUID entityId);

    // ===== @Query - JPQL =====

    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.entityType = :entityType ORDER BY n.level, n.sortRank")
    List<OrgTreeNodeEntity> findByEntityType(@Param("entityType") EntityType entityType);

    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.level = :level ORDER BY n.sortRank")
    List<OrgTreeNodeEntity> findByLevel(@Param("level") Integer level);

    // ===== @Query - 原生 SQL（数组操作） =====

    @Query(value = """
        SELECT * FROM org_tree
        WHERE (:nodeId = ANY(path) OR id = :nodeId) AND removed = false
        ORDER BY level, sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findAllDescendantsNotRemoved(@Param("nodeId") UUID nodeId);

    @Query(value = """
        SELECT * FROM org_tree
        WHERE parent_id = :parentId AND removed = false
        ORDER BY sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentId(@Param("parentId") UUID parentId);

    @Query(value = """
        SELECT * FROM org_tree WHERE entity_type = 'ROOT' LIMIT 1
        """, nativeQuery = true)
    Optional<OrgTreeNodeEntity> findRootNode();

    @Query(value = """
        SELECT * FROM org_tree
        WHERE parent_id IN (:parentIds) AND removed = false
        ORDER BY parent_id, sort_rank
        """, nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentIds(@Param("parentIds") List<UUID> parentIds);

    // ===== 统计查询 =====

    @Query("SELECT n.entityType, COUNT(n) FROM OrgTreeNodeEntity n WHERE n.parentId = :parentId AND n.removed = false GROUP BY n.entityType")
    List<Object[]> countChildrenByType(@Param("parentId") UUID parentId);

    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.id = :nodeId AND n.removed = false")
    Optional<OrgTreeNodeEntity> findByIdNotRemoved(@Param("nodeId") UUID nodeId);

    @Query("SELECT COUNT(n) FROM OrgTreeNodeEntity n WHERE n.parentId = :parentId AND n.removed = false")
    long countChildrenByParentId(@Param("parentId") UUID parentId);
}
```

### 4.3 Service 接口

**OrgTreeNodeService.java：**

```java
public interface OrgTreeNodeService {

    // ===== 树节点 CRUD =====

    OrgTreeNodeEntity createNode(UUID parentId, EntityType entityType, UUID entityId, String alias);

    OrgTreeNodeEntity createNodeAfter(UUID parentId, EntityType entityType, UUID entityId,
                                       String alias, UUID afterNodeId);

    OrgTreeNodeEntity updateNode(UUID nodeId, String alias, String sortRank);

    OrgTreeNodeEntity moveNode(UUID nodeId, UUID newParentId);

    OrgTreeNodeEntity moveNodeAfter(UUID nodeId, UUID newParentId, UUID afterNodeId);

    void removeNode(UUID nodeId);

    // ===== 树结构查询 =====

    OrgTreeNodeEntity getNode(UUID nodeId);

    List<OrgTreeNodeEntity> getChildren(UUID parentId);

    TreeNodeRsp getSubTree(UUID nodeId, Integer depth);

    List<OrgTreeNodeEntity> getAllDescendants(UUID nodeId);

    List<OrgTreeNodeEntity> getAllAncestors(UUID nodeId);

    // ===== 实体关联查询 =====

    List<OrgTreeNodeEntity> getNodesByEntity(EntityType entityType, UUID entityId);

    // ===== 统计 =====

    long countChildren(UUID parentId);
}
```

### 4.4 DTO 设计

**请求 DTO：**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreeNodeReq {
    @NotNull(message = "父节点ID不能为空")
    private UUID parentId;

    @NotNull(message = "实体类型不能为空")
    private EntityType entityType;

    @NotNull(message = "实体ID不能为空")
    private UUID entityId;

    private String alias;

    private UUID afterNodeId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTreeNodeReq {
    private String alias;

    private String sortRank;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveTreeNodeReq {
    @NotNull(message = "新父节点ID不能为空")
    private UUID newParentId;

    private UUID afterNodeId;
}
```

**响应 DTO：**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRsp {
    private UUID id;
    private UUID entityId;
    private EntityType entityType;
    private String name;
    private String alias;
    private Integer level;
    private String sortRank;
    private TreeStatistics statistics;
    private List<TreeNodeRsp> children;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeStatistics {
    private Integer subGroupCount;
    private Integer subDepartmentCount;
    private Integer personnelCount;
}
```

### 4.5 Controller 接口

**OrgTreeNodeController.java：**

```java
@RestController
@RequestMapping("/api/tree")
public class OrgTreeNodeController {

    @PostMapping("/nodes")
    ApiResult<TreeNodeRsp> createNode(@Valid @RequestBody CreateTreeNodeReq req);

    @GetMapping("/nodes/{nodeId}")
    ApiResult<TreeNodeRsp> getNode(@PathVariable UUID nodeId);

    @PutMapping("/nodes/{nodeId}")
    ApiResult<TreeNodeRsp> updateNode(@PathVariable UUID nodeId, @Valid @RequestBody UpdateTreeNodeReq req);

    @PostMapping("/nodes/{nodeId}/remove")
    ApiResult<Void> removeNode(@PathVariable UUID nodeId);

    @PostMapping("/nodes/{nodeId}/move")
    ApiResult<TreeNodeRsp> moveNode(@PathVariable UUID nodeId, @Valid @RequestBody MoveTreeNodeReq req);

    @GetMapping("/nodes/{nodeId}/children")
    ApiResult<List<TreeNodeRsp>> getChildren(@PathVariable UUID nodeId);

    @GetMapping("/nodes/{nodeId}/subtree")
    ApiResult<TreeNodeRsp> getSubTree(@PathVariable UUID nodeId, @RequestParam(defaultValue = "1") Integer depth);

    @GetMapping("/nodes/{nodeId}/descendants")
    ApiResult<List<TreeNodeRsp>> getAllDescendants(@PathVariable UUID nodeId);

    @GetMapping("/nodes/{nodeId}/ancestors")
    ApiResult<List<TreeNodeRsp>> getAllAncestors(@PathVariable UUID nodeId);

    @GetMapping("/root")
    ApiResult<TreeNodeRsp> getRootNode();

    @GetMapping("/root/children")
    ApiResult<List<TreeNodeRsp>> getRootChildren();
}
```

### 4.6 LexoRank 工具类

**LexoRankUtils.java：**

```java
public class LexoRankUtils {

    private static final String CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int CHAR_COUNT = 36;

    public static String initialRank(int index);

    public static String between(String lower, String upper);

    public static String before(String current);

    public static String after(String current);

    public static boolean isValidRank(String rank);
}
```

### 4.7 Mapper 映射器

**OrgTreeNodeMapper.java：**

```java
@Mapper(componentModel = "spring")
public interface OrgTreeNodeMapper {

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(target = "children", ignore = true)
    TreeNodeRsp toTreeNodeRsp(OrgTreeNodeEntity entity);

    List<TreeNodeRsp> toTreeNodeRspList(List<OrgTreeNodeEntity> entities);
}
```

### 4.8 异常处理

使用现有 `ApiException` 类，不新增异常类：

| 场景 | code | message |
|------|------|---------|
| 父节点不存在 | 404 | 父节点不存在: {parentId} |
| 节点不存在 | 404 | 节点不存在: {nodeId} |
| 业务实体不存在 | 404 | 业务实体不存在: {entityType} - {entityId} |
| 重复挂载 | 400 | 该父节点下已存在相同实体的树节点 |
| 循环引用 | 400 | 不能将节点移动到自身或其子孙节点下 |
| 删除虚拟根 | 400 | 虚拟根节点不允许删除 |

## 5. 数据库开发约定（新增）

### 5.1 Repository 查询方法优先级

1. **方法命名约定**：适用于简单的单表查询
2. **@Query - JPQL**：适用于标准 JPA 操作，类型安全
3. **@Query - 原生 SQL**：适用于 PostgreSQL 特定功能（数组操作、GIN 索引）
4. **Specification**：适用于动态条件组合

### 5.2 避免使用的方式

- 自定义 Repository 实现类（RepositoryCustom + Impl），仅在极端复杂场景使用

### 5.3 PostgreSQL 数组操作约定

涉及 UUID[] 数组查询时，使用原生 SQL：

```java
@Query(value = "SELECT * FROM org_tree WHERE :nodeId = ANY(path)", nativeQuery = true)
List<OrgTreeNodeEntity> findByPathContains(@Param("nodeId") UUID nodeId);
```

### 5.4 逻辑删除查询约定

所有查询必须排除已删除记录（`removed = false`）。

## 6. 测试策略

### 6.1 单元测试

| 测试类 | 测试内容 |
|--------|---------|
| `OrgTreeNodeEntityTest` | Entity 字段验证 |
| `OrgTreeNodeRepositoryTest` | Repository 查询方法 |
| `OrgTreeNodeServiceTest` | Service 业务逻辑（Mock Repository） |
| `OrgTreeNodeControllerTest` | Controller API |
| `LexoRankUtilsTest` | LexoRank 算法 |

### 6.2 集成测试

| 测试类 | 测试内容 |
|--------|---------|
| `OrgTreeNodeIntegrationTest` | 完整树操作流程 |
| `OrgTreeQueryIntegrationTest` | 复杂查询（子孙节点、祖先节点） |

## 7. 影响范围

### 7.1 需修改的文件

| 文件类型 | 文件 | 修改内容 |
|----------|------|---------|
| SQL | 新增 V1__Create_base_tables.sql | 建库脚本 |
| SQL | 新增 V2__Insert_sample_data.sql | 示例数据 |
| Entity | 新增 OrgTreeNodeEntity.java | 树节点实体 |
| Entity | 新增 EntityType.java | 实体类型枚举 |
| Repository | 新增 OrgTreeNodeRepository.java | 树节点 Repository |
| Service | 新增 OrgTreeNodeService.java | 树节点 Service 接口 |
| Service | 新增 OrgTreeNodeServiceImpl.java | 树节点 Service 实现 |
| Controller | 新增 OrgTreeNodeController.java | 树节点 Controller |
| DTO | 新增 CreateTreeNodeReq.java | 创建请求 DTO |
| DTO | 新增 UpdateTreeNodeReq.java | 更新请求 DTO |
| DTO | 新增 MoveTreeNodeReq.java | 移动请求 DTO |
| DTO | 修改 TreeNodeRsp.java | 树节点响应 DTO |
| DTO | 修改 TreeStatistics.java | 统计信息 DTO |
| Mapper | 新增 OrgTreeNodeMapper.java | 树节点 Mapper |
| Utils | 新增 LexoRankUtils.java | LexoRank 工具类 |
| OpenAPI | 更新 openapi.yaml | API 文档 |

### 7.2 可删除的文件（可选，建议保留用于回滚）

| 文件类型 | 文件 | 说明 |
|----------|------|------|
| Entity | GroupHierarchyEntity.java | 原 Group 层级实体 |
| Entity | DepartmentHierarchyEntity.java | 原 Department 层级实体 |
| Entity | GroupDepartmentEntity.java | 原 Group-Department 关联 |
| Entity | GroupPersonnelEntity.java | 原 Group-Personnel 关联 |
| Entity | DepartmentGroupEntity.java | 原 Department-Group 关联 |
| Entity | DepartmentPersonnelEntity.java | 原 Department-Personnel 关联 |
| Repository | 对应 6 个 Repository | 原关联表 Repository |
| Service | TreeService.java | 原树查询 Service |
| Service | TreeServiceImpl.java | 原树查询 Service 实现 |
| Controller | TreeController.java | 原树查询 Controller |

### 7.3 保留的文件

| 文件类型 | 文件 | 说明 |
|----------|------|------|
| Entity | DepartmentPositionEntity.java | 部门-岗位关联 |
| Entity | PersonnelPositionEntity.java | 人员-岗位关联 |
| Repository | DepartmentPositionRepository.java | 部门-岗位 Repository |
| Repository | PersonnelPositionRepository.java | 人员-岗位 Repository |

## 8. 验收标准

1. 所有表结构、索引正确创建
2. 示例数据正确初始化（虚拟根节点、业务数据、树结构）
3. 所有 API 接口功能正常（创建、查询、更新、移动、删除）
4. 多归属场景正确支持（同一实体可出现在多个父节点下）
5. LexoRank 排序正常工作（插入、移动无需重排其他元素）
6. 路径查询高效（GIN 索引生效）
7. 逻辑删除正确（removed=true 的记录不出现在查询结果中）
8. 单元测试覆盖率 ≥ 80%
9. 集成测试通过
10. openapi.yaml 文档同步更新

## 9. 附录

### 9.1 示例数据树结构

```
ROOT (虚拟根)
├── 总公司 (GROUP)
│   ├── 华东分公司 (GROUP)
│   │   └── 上海研发中心 (DEPARTMENT)
│   │       ├── 王五 (PERSONNEL)
│   │       ├── 赵六 (PERSONNEL)
│   │       └── 钱七 (PERSONNEL)
│   ├── 华北分公司 (GROUP)
│   │   └── 北京研发中心 (DEPARTMENT)
│   │       └── 王五 (PERSONNEL, alias="北京项目负责人") [多归属示例]
│   ├── 人事部 (DEPARTMENT, alias="人事部" 原"人力资源部")
│   │   └── 张三 (PERSONNEL)
│   ├── 财务部 (DEPARTMENT)
│   └── 技术部 (DEPARTMENT)
│       └── 李四 (PERSONNEL)
```

### 9.2 LexoRank 算法参考

- 字符集：`0123456789abcdefghijklmnopqrstuvwxyz`
- 初始值：`a0`、`b0`、`c0`...
- 中间值计算：字典序中间值
- 长度扩展：当单字符无法区分时，增加字符长度

---

**文档版本：** 1.0
**最后更新：** 2026-04-10