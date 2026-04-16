# Git 使用规范

本文档包含两部分内容：

- [分支约定](#分支约定) — 分支命名、创建与合并流程
- [提交信息规范](#提交信息规范) — Conventional Commits 格式要求

---

## 分支约定

### 分支类型

| 分支 | 命名格式 | 用途 |
|------|---------|------|
| `main` | `main` | 稳定分支，始终保持可部署状态 |
| `feature` | `feat/<简短描述>` | 新功能开发，从 `main` 创建 |

### 工作流程

1. 从 `main` 创建 feature 分支：`git checkout -b feat/add-tag-import`
2. 在分支上开发，遵循 [提交信息规范](#提交信息规范)
3. 推送到远程：`git push -u origin feat/add-tag-import`
4. 创建 Pull Request，目标为 `main`
5. 代码审查通过后合并，合并后删除分支

### 规则

- **禁止直接向 `main` 推送**，所有变更通过 PR 合并
- PR 合并前至少需要一人审查（Review）
- feature 分支保持短生命周期，合并后及时删除
- 合并方式优先使用 **Squash Merge**，保持 `main` 历史整洁
- PR 描述需关联相关 Issue（如 `Closes #123`）
- 合并前确保 CI 通过、无冲突

---

## 提交信息规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范。

### 格式

```plain
<type>(<scope>): <subject>

<body>

<footer>
```

### 类型 `<type>`

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复缺陷 |
| `docs` | 文档变更 |
| `style` | 代码格式调整（不影响逻辑） |
| `refactor` | 重构（既非新功能也非修复） |
| `perf` | 性能优化 |
| `test` | 新增或修改测试 |
| `chore` | 构建、工具、依赖等维护性变更 |

### 简要描述 `<subject>`

- 使用祈使语气：「添加功能」而非「添加了功能」
- 不超过 50 个字符
- 首字母大写，末尾不加句号
- 可附带范围：`feat(controller): 添加组织机构接口`

### 正文 `<body>`

- 每行不超过 72 个字符
- 说明 **做了什么** 和 **为什么**，不需要说明怎么做
- 多项变更使用列表形式
- 包含必要的背景和动机

### 破坏性变更

- 在页脚添加 `BREAKING CHANGE:` 说明
- 或在类型后加 `!`：`feat(api)!: 移除已废弃的接口`

### 页脚 `<footer>`

- 关联 Issue：`Closes #123`、`Fixes #456`
- 标注破坏性变更说明
- 协作者署名
- 按要求添加 Sign-off

### 示例

**好的示例：**

```plain
feat(service): 添加组织机构创建功能

- 实现带验证的 createOrganization 方法
- 添加重名检查的业务逻辑
- 对接数据库仓库层

Closes #123
```

**不好的示例：**

```plain
添加了组织机构创建
修了一些bug
更新了依赖
```

### 工作流程

1. 暂存变更：`git add`
2. 提交并附带规范信息
3. 验证提交记录：`git log --oneline -5`
4. 准备就绪后推送到远程
