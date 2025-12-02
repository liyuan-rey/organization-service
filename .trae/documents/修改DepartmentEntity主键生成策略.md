# 修改DepartmentEntity主键生成策略

## 修改内容

1. **移除@GeneratedValue注解**：不再使用数据库自动生成主键
2. **添加UUIDv7导入**：导入io.github.robsonkades.uuidv7.UUIDv7类
3. **修改ID生成逻辑**：在默认构造函数中使用UUIDv7.randomUUID()生成ID

## 实现步骤

1. 编辑`DepartmentEntity.java`文件
2. 移除第13行的`@GeneratedValue(strategy = GenerationType.AUTO)`注解
3. 添加UUIDv7类的导入语句
4. 修改默认构造函数中的ID生成逻辑，将`UUID.randomUUID()`替换为`UUIDv7.randomUUID()`
5. 运行编译命令验证修改是否成功

## 技术规范

- 使用UUIDv7.randomUUID()生成主键
- 保持其他代码不变
- 确保编译通过