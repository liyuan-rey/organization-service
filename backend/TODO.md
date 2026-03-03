# TODO: 重构节点抽象关系模型

## 阶段一：数据库设计

- [x] 1.1 创建数据库迁移脚本，删除 org_entity_relation 表
- [x] 1.2 创建 org_department_hierarchy 表（部门层级）
- [x] 1.3 创建 org_department_personnel 表（部门人员）
- [x] 1.4 创建 org_group_hierarchy 表（分组层级）
- [x] 1.5 创建 org_group_department 表（分组部门）
- [x] 1.6 创建 org_group_personnel 表（分组人员）
- [x] 1.7 更新索引策略
- [x] 1.8 更新示例数据

## 阶段二：实体层实现

- [x] 2.1 创建 GroupEntity.java
- [x] 2.2 创建 DepartmentHierarchyEntity.java
- [x] 2.3 创建 DepartmentPersonnelEntity.java
- [x] 2.4 创建 GroupHierarchyEntity.java
- [x] 2.5 创建 GroupDepartmentEntity.java
- [x] 2.6 创建 GroupPersonnelEntity.java

## 阶段三：仓储层实现

- [x] 3.1 创建 GroupRepository.java
- [x] 3.2 创建 DepartmentHierarchyRepository.java
- [x] 3.3 创建 DepartmentPersonnelRepository.java
- [x] 3.4 创建 GroupHierarchyRepository.java
- [x] 3.5 创建 GroupDepartmentRepository.java
- [x] 3.6 创建 GroupPersonnelRepository.java

## 阶段四：DTO 和映射层

- [x] 4.1 创建分组相关 DTO
- [x] 4.2 创建部门层级相关 DTO
- [x] 4.3 创建部门人员相关 DTO
- [x] 4.4 创建分组层级相关 DTO
- [x] 4.5 创建分组部门相关 DTO
- [x] 4.6 创建分组人员相关 DTO
- [x] 4.7 更新 GroupMapper.java
- [x] 4.8 创建其他关联表 Mapper

## 阶段五：服务层实现

- [x] 5.1 创建 GroupService 接口
- [x] 5.2 创建 GroupServiceImpl 实现
- [x] 5.3 创建 DepartmentHierarchyService
- [x] 5.4 创建 DepartmentPersonnelService
- [x] 5.5 创建 GroupHierarchyService
- [x] 5.6 创建 GroupDepartmentService
- [x] 5.7 创建 GroupPersonnelService

## 阶段六：控制器层实现

- [x] 6.1 创建 GroupController.java
- [x] 6.2 创建 DepartmentHierarchyController.java
- [x] 6.3 创建 DepartmentPersonnelController.java
- [x] 6.4 创建 GroupHierarchyController.java
- [x] 6.5 创建 GroupDepartmentController.java
- [x] 6.6 创建 GroupPersonnelController.java

## 阶段七：测试编写

- [x] 7.1 编写分组模块单元测试
- [x] 7.2 编写部门层级关系测试
- [x] 7.3 编写部门人员关联测试
- [x] 7.4 编写分组层级关系测试
- [x] 7.5 编写分组部门关联测试
- [x] 7.6 编写分组人员关联测试
- [ ] 7.7 编写集成测试

## 阶段八：文档更新

- [x] 8.1 更新数据库设计文档
- [x] 8.2 更新 AGENTS.md 项目说明
- [x] 8.3 创建新数据库设计文档