---
outline: deep
---

# 功能模块开发指南

::: tip 适用对象

本文档适用于：

- **Coding Agent（AI 开发助手）**：按照文档流程自动生成符合规范的代码
- **新入职开发者**：学习项目结构和开发流程，快速上手功能开发

:::

本文档提供完整的功能模块开发流程，涵盖从路由配置、API 层、页面组件到状态管理的全链路开发步骤，并强调 UI 设计规范和资产复用约定。

## 快速入门

### 开发流程概览

开发一个新功能模块需要以下步骤：

```
1. 定义路由 → 2. 编写 API → 3. 开发页面 → 4. 添加组件 → 5. 状态管理（可选）
```

| 步骤     | 文件位置                         | 说明                   |
| -------- | -------------------------------- | ---------------------- |
| 路由配置 | `src/router/routes/modules/*.ts` | 定义页面路由和菜单     |
| API 层   | `src/api/<module>/index.ts`      | 封装接口请求和类型定义 |
| 页面组件 | `src/views/<module>/*.vue`       | 页面 UI 和业务逻辑     |
| 状态管理 | `src/store/*.ts`                 | Pinia store（可选）    |

### 文件结构模板

新增一个模块（如 `user-management`）的标准文件结构：

```
src/
├── router/routes/modules/
│   └── user-management.ts        # 路由配置
├── api/
│   └── user-management/
│       └── index.ts              # API 请求 + 类型定义
├── views/
│   └── user-management/
│       ├── index.vue             # 主页面
│       ├── components/           # 子组件（可选）
│       │   └── user-form.vue     # 表单组件
│       └── hooks/                # 组合式函数（可选）
│           └── use-user.ts
└── store/
    └── user-management.ts        # Pinia store（可选）
```

---

## 开发步骤详解

### 1. 路由配置

路由文件位于 `src/router/routes/modules/` 目录，通过 glob 自动导入。

#### 路由配置模板

```ts
// src/router/routes/modules/user-management.ts
import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:users', // 菜单图标（使用 lucide 图标集）
      order: 100, // 菜单排序（仅一级菜单有效）
      title: $t('page.userManagement.title'), // 国际化标题
    },
    name: 'UserManagement',
    path: '/user-management',
    redirect: '/user-management/list',
    children: [
      {
        name: 'UserList',
        path: '/user-management/list',
        component: () => import('#/views/user-management/index.vue'),
        meta: {
          icon: 'lucide:list',
          title: $t('page.userManagement.list'),
          keepAlive: true, // 开启页面缓存
        },
      },
      {
        name: 'UserDetail',
        path: '/user-management/detail',
        component: () => import('#/views/user-management/detail.vue'),
        meta: {
          icon: 'lucide:user',
          title: $t('page.userManagement.detail'),
          hideInMenu: true, // 隐藏菜单项
          activePath: '/user-management/list', // 激活父级菜单
        },
      },
    ],
  },
];

export default routes;
```

#### 路由 meta 常用字段

| 字段                 | 类型       | 说明                                   |
| -------------------- | ---------- | -------------------------------------- |
| `title`              | `string`   | 页面标题，支持 i18n key                |
| `icon`               | `string`   | 菜单图标，格式 `lucide:xxx` 或图片 URL |
| `order`              | `number`   | 一级菜单排序，数值越小越靠前           |
| `keepAlive`          | `boolean`  | 开启页面缓存（需 tabbar 启用）         |
| `hideInMenu`         | `boolean`  | 隐藏菜单项                             |
| `hideInTab`          | `boolean`  | 隐藏标签页                             |
| `hideChildrenInMenu` | `boolean`  | 隐藏子菜单                             |
| `activePath`         | `string`   | 激活的父级菜单路径                     |
| `authority`          | `string[]` | 角色权限控制                           |
| `affixTab`           | `boolean`  | 固定标签页                             |

::: warning 注意

- 排序仅针对一级菜单有效，二级菜单按代码顺序排列
- 使用 `$t()` 函数支持国际化，key 定义在 `src/locales/langs/zh-CN/page.json`
- **所有标题必须使用 `$t()` 包裹**，禁止硬编码中文文本

:::

#### 国际化配置步骤

1. **定义翻译 key**：在 `src/locales/langs/zh-CN/page.json` 中添加：

```json
{
  "userManagement": {
    "title": "用户管理",
    "list": "用户列表",
    "detail": "用户详情"
  }
}
```

2. **在路由中使用**：

```ts
import { $t } from '#/locales';

meta: {
  title: $t('page.userManagement.title'),
}
```

::: tip 国际化规范

- **key 命名规则**：`page.<模块名>.<页面名>`
- **禁止硬编码**：所有显示文本必须通过 `$t()` 函数
- **同步更新**：修改标题时同时更新 `zh-CN` 和 `en-US` 两个语言文件

:::

---

### 2. API 层开发

API 文件位于 `src/api/<module>/index.ts`，封装请求和类型定义。

#### API 配置模板

```ts
// src/api/user-management/index.ts
import { requestClient } from '#/api/request';

// 类型定义
export interface User {
  id: string;
  name: string;
  email: string;
  status: 'active' | 'inactive';
  createTime: string;
}

export interface PageResponse<T> {
  items: T[];
  total: number;
}

export interface UserQueryParams {
  page?: number;
  pageSize?: number;
  name?: string;
  status?: string;
}

// 获取用户列表（分页）
export async function getUserListApi(params?: UserQueryParams) {
  return requestClient.get<PageResponse<User>>('/user/list', { params });
}

// 获取用户详情
export async function getUserDetailApi(id: string) {
  return requestClient.get<User>(`/user/${id}`);
}

// 创建用户
export async function createUserApi(data: Partial<User>) {
  return requestClient.post<User>('/user', data);
}

// 更新用户
export async function updateUserApi(id: string, data: Partial<User>) {
  return requestClient.put<User>(`/user/${id}`, data);
}

// 删除用户
export async function deleteUserApi(id: string) {
  return requestClient.delete<boolean>(`/user/${id}`);
}
```

#### 后端响应格式约定

后端 API 必须返回以下格式：

```json
{
  "code": 0,
  "data": T,
  "message": "success"
}
```

- `code: 0` 表示成功，其他值为失败
- `data` 为业务数据
- `message` 为提示信息

`requestClient` 会自动解包 `data` 字段，直接返回业务数据。

::: tip requestClient vs baseRequestClient

- `requestClient`：自动提取 `response.data.data`，常用
- `baseRequestClient`：返回原始响应，用于特殊场景

:::

---

### 3. 页面组件开发

#### Page 组件基础用法

使用 `Page` 组件作为页面根容器，提供标题、描述和内容区域。

```vue
<script lang="ts" setup>
import { Page } from '@vben/common-ui';
</script>

<template>
  <Page title="用户管理" description="管理系统用户信息" auto-content-height>
    <template #extra>
      <!-- 页面头部右侧内容 -->
      <ElButton type="primary">新增用户</ElButton>
    </template>

    <!-- 页面主体内容 -->
    <div class="p-4">
      <!-- 你的业务组件 -->
    </div>
  </Page>
</template>
```

#### Page Props

| 属性                | 类型             | 说明             |
| ------------------- | ---------------- | ---------------- |
| `title`             | `string \| slot` | 页面标题         |
| `description`       | `string \| slot` | 页面描述         |
| `autoContentHeight` | `boolean`        | 自动调整内容高度 |
| `contentClass`      | `string`         | 内容区域 class   |
| `headerClass`       | `string`         | 头部区域 class   |

#### Page Slots

| 插槽          | 说明         |
| ------------- | ------------ |
| `default`     | 页面内容     |
| `title`       | 自定义标题   |
| `description` | 自定义描述   |
| `extra`       | 页面头部右侧 |
| `footer`      | 页面底部     |

::: tip autoContentHeight

启用 `autoContentHeight` 后，内容区域会自动填充剩余高度，适合表格等需要固定高度的组件。

:::

#### 组件拆分最佳实践

当页面逻辑复杂时，应拆分为独立的子组件：

```
views/user-management/
├── index.vue              # 主页面（组装子组件）
├── components/
│   ├── user-form.vue      # 表单组件（用于新增/编辑）
│   ├── user-table.vue     # 表格组件
│   └── user-filter.vue    # 搜索筛选组件
└── hooks/
    └── use-user-list.ts   # 列表数据逻辑（组合式函数）
```

::: warning 组件拆分原则

- **单一职责**：每个组件只负责一个功能模块
- **逻辑抽离**：复用逻辑抽取为 `hooks/*.ts` 组合式函数
- **通信方式**：使用 props/emit 或 provide/inject，避免直接依赖 store
- **避免过度拆分**：简单页面不需要拆分，保持适度

**何时拆分**：

- 页面代码超过 100 行
- 存在多个独立功能区域（如左侧列表 + 右侧表格）
- 有可复用的子功能（如表单弹窗）

:::

---

### 4. 表单场景

使用 `useVbenForm` 创建表单，支持表单验证、联动、自定义组件等。

#### 基础表单模板

```vue
<script lang="ts" setup>
import { useVbenForm, z } from '#/adapter/form';
import { Page } from '@vben/common-ui';
import { ElCard } from 'element-plus';

const [BaseForm, baseFormApi] = useVbenForm({
  commonConfig: {
    colon: true, // label 后显示冒号
    componentProps: {
      class: 'w-full', // 组件宽度
    },
  },
  layout: 'horizontal', // 水平布局
  schema: [
    {
      component: 'Input',
      fieldName: 'name',
      label: '用户名',
      rules: 'required', // 必填验证
      componentProps: {
        placeholder: '请输入用户名',
      },
    },
    {
      component: 'Input',
      fieldName: 'email',
      label: '邮箱',
      rules: z.string().email({ message: '请输入有效的邮箱地址' }),
    },
    {
      component: 'Select',
      fieldName: 'status',
      label: '状态',
      componentProps: {
        options: [
          { label: '启用', value: 'active' },
          { label: '禁用', value: 'inactive' },
        ],
        placeholder: '请选择状态',
      },
    },
  ],
  handleSubmit: (values) => {
    console.log('提交数据:', values);
    // 调用 API 提交
  },
});

// 设置表单值
function handleSetValues() {
  baseFormApi.setValues({
    name: '张三',
    email: 'zhangsan@example.com',
    status: 'active',
  });
}
</script>

<template>
  <Page title="用户表单" auto-content-height>
    <ElCard title="基础表单">
      <template #extra>
        <ElButton type="primary" @click="handleSetValues">设置表单值</ElButton>
      </template>
      <BaseForm />
    </ElCard>
  </Page>
</template>
```

#### 表单 Schema 字段

| 字段             | 类型                  | 说明                 |
| ---------------- | --------------------- | -------------------- |
| `component`      | `string`              | 组件类型，见下方列表 |
| `fieldName`      | `string`              | 字段名，也作为插槽名 |
| `label`          | `string \| VNode`     | 表单标签             |
| `rules`          | `string \| ZodSchema` | 验证规则             |
| `componentProps` | `object \| Function`  | 组件参数             |
| `defaultValue`   | `any`                 | 默认值               |
| `dependencies`   | `object`              | 表单联动配置         |
| `hide`           | `boolean`             | 隐藏字段             |
| `help`           | `string \| VNode`     | 帮助信息             |
| `suffix`         | `string \| VNode`     | 后缀内容             |

#### 可用组件类型

| 组件            | 说明         |
| --------------- | ------------ |
| `Input`         | 文本输入框   |
| `InputNumber`   | 数字输入框   |
| `InputPassword` | 密码输入框   |
| `Select`        | 下拉选择     |
| `ApiSelect`     | 远程数据下拉 |
| `ApiTreeSelect` | 远程树形选择 |
| `TreeSelect`    | 树形选择     |
| `DatePicker`    | 日期选择     |
| `TimePicker`    | 时间选择     |
| `Checkbox`      | 复选框       |
| `CheckboxGroup` | 复选框组     |
| `RadioGroup`    | 单选框组     |
| `Switch`        | 开关         |
| `Upload`        | 文件上传     |
| `IconPicker`    | 图标选择器   |
| `Divider`       | 分隔线       |

#### 表单 API

| 方法                          | 说明           |
| ----------------------------- | -------------- |
| `setValues(values)`           | 设置表单值     |
| `setFieldValue(field, value)` | 设置单个字段值 |
| `getValues()`                 | 获取表单值     |
| `validate()`                  | 校验表单       |
| `resetForm()`                 | 重置表单       |
| `submitForm()`                | 提交表单       |
| `updateSchema(schema)`        | 更新 schema    |
| `setState(state)`             | 设置组件状态   |

#### 表单联动示例

```ts
schema: [
  {
    component: 'Select',
    fieldName: 'type',
    label: '类型',
    componentProps: {
      options: [
        { label: '个人', value: 'personal' },
        { label: '企业', value: 'enterprise' },
      ],
    },
  },
  {
    component: 'Input',
    fieldName: 'companyName',
    label: '企业名称',
    dependencies: {
      triggerFields: ['type'],
      // 仅当 type 为 enterprise 时显示
      show: (values) => values.type === 'enterprise',
      // 动态必填
      required: (values) => values.type === 'enterprise',
    },
  },
],
```

---

### 5. 表格场景

使用 `useVbenVxeGrid` 创建数据表格，支持分页、排序、搜索表单等。

#### 基础表格模板

```vue
<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { Page } from '@vben/common-ui';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getUserListApi, type User } from '#/api/user-management';

const [Grid, gridApi] = useVbenVxeGrid<User>({
  gridOptions: {
    columns: [
      { type: 'seq', title: '序号', width: 50 },
      { field: 'name', title: '用户名', sortable: true },
      { field: 'email', title: '邮箱' },
      { field: 'status', title: '状态' },
      { field: 'createTime', title: '创建时间' },
    ],
    proxyConfig: {
      ajax: {
        query: async ({ page }) => {
          const res = await getUserListApi({
            page: page.currentPage,
            pageSize: page.pageSize,
          });
          return {
            items: res.items,
            total: res.total,
          };
        },
      },
    },
    pagerConfig: {
      enabled: true,
      pageSize: 10,
      pageSizes: [10, 20, 50, 100],
    },
  },
  formOptions: {
    schema: [
      {
        component: 'Input',
        fieldName: 'name',
        label: '用户名',
      },
      {
        component: 'Select',
        fieldName: 'status',
        label: '状态',
        componentProps: {
          options: [
            { label: '启用', value: 'active' },
            { label: '禁用', value: 'inactive' },
          ],
        },
      },
    ],
  },
});

// 刷新表格
function handleRefresh() {
  gridApi.reload();
}
</script>

<template>
  <Page title="用户列表" auto-content-height>
    <Grid table-title="用户数据">
      <template #toolbar-tools>
        <ElButton type="primary" @click="handleRefresh">刷新</ElButton>
      </template>
    </Grid>
  </Page>
</template>
```

#### 表格列配置

| 字段           | 说明                         |
| -------------- | ---------------------------- |
| `field`        | 数据字段名                   |
| `title`        | 列标题                       |
| `width`        | 列宽度                       |
| `sortable`     | 是否可排序                   |
| `fixed`        | 固定列 `'left' \| 'right'`   |
| `showOverflow` | 溢出隐藏并 tooltip           |
| `type`         | 特殊列类型 `seq \| checkbox` |

#### 表格 API

| 方法                      | 说明                 |
| ------------------------- | -------------------- |
| `reload()`                | 重载表格（重新请求） |
| `query()`                 | 查询（保留当前分页） |
| `setLoading(loading)`     | 设置 loading 状态    |
| `setGridOptions(options)` | 设置表格配置         |
| `grid`                    | vxe-table 实例       |
| `formApi`                 | 搜索表单 API         |

#### 表格 Slots

| 插槽              | 说明       |
| ----------------- | ---------- |
| `toolbar-actions` | 工具栏左侧 |
| `toolbar-tools`   | 工具栏右侧 |
| `table-title`     | 表格标题   |

---

### 6. 弹窗与抽屉

使用 `useVbenModal` 和 `useVbenDrawer` 创建弹窗和抽屉。

#### 弹窗模板

```vue
<script lang="ts" setup>
import { useVbenModal } from '@vben/common-ui';
import { useVbenForm } from '#/adapter/form';
import { ElMessage } from 'element-plus';

// 表单
const [Form, formApi] = useVbenForm({
  schema: [
    {
      component: 'Input',
      fieldName: 'name',
      label: '用户名',
      rules: 'required',
    },
    { component: 'Input', fieldName: 'email', label: '邮箱' },
  ],
});

// 弹窗
const [Modal, modalApi] = useVbenModal({
  onConfirm: async () => {
    const values = await formApi.validate();
    // 提交数据
    modalApi.lock(); // 锁定弹窗防止重复提交
    try {
      await createUserApi(values);
      ElMessage.success('创建成功');
      modalApi.close();
    } finally {
      modalApi.unlock();
    }
  },
  onCancel: () => modalApi.close(),
});

// 打开弹窗并传入数据
function handleOpen(data?: User) {
  if (data) {
    formApi.setValues(data);
  }
  modalApi.open();
}
</script>

<template>
  <Modal class="w-[600px]" title="新增用户" :footer="true">
    <Form />
  </Modal>
</template>
```

#### Modal Props

| 属性         | 说明                         |
| ------------ | ---------------------------- |
| `title`      | 弹窗标题                     |
| `class`      | 弹窗 class（宽度通过此配置） |
| `footer`     | 是否显示底部                 |
| `draggable`  | 是否可拖拽                   |
| `fullscreen` | 是否全屏                     |
| `closable`   | 是否显示关闭按钮             |
| `loading`    | loading 状态                 |

#### Modal API

| 方法              | 说明                   |
| ----------------- | ---------------------- |
| `open()`          | 打开弹窗               |
| `close()`         | 关闭弹窗               |
| `setState(state)` | 设置状态               |
| `setData(data)`   | 设置共享数据           |
| `getData()`       | 获取共享数据           |
| `lock()`          | 锁定弹窗（提交时使用） |
| `unlock()`        | 解锁弹窗               |

#### 抽屉用法

抽屉与弹窗用法类似，使用 `useVbenDrawer`：

```ts
const [Drawer, drawerApi] = useVbenDrawer({
  onConfirm: () => {},
  onCancel: () => drawerApi.close(),
});
```

---

### 7. 状态管理（可选）

使用 Pinia 进行状态管理，支持持久化。

#### Store 模板

```ts
// src/store/user-management.ts
import { defineStore } from 'pinia';
import { getUserListApi, type User } from '#/api/user-management';

export const useUserManagementStore = defineStore('user-management', {
  state: () => ({
    users: [] as User[],
    loading: false,
    selectedUserId: '',
  }),
  actions: {
    async fetchUsers(params?: UserQueryParams) {
      this.loading = true;
      try {
        const res = await getUserListApi(params);
        this.users = res.items;
      } finally {
        this.loading = false;
      }
    },
    setSelectedUser(id: string) {
      this.selectedUserId = id;
    },
  },
  // 启用持久化
  persist: {
    pick: ['selectedUserId'], // 仅持久化部分字段
  },
});
```

#### 使用 Store

```vue
<script lang="ts" setup>
import { useUserManagementStore } from '#/store/user-management';

const store = useUserManagementStore();

// 获取数据
store.fetchUsers();

// 访问状态
const users = computed(() => store.users);
</script>
```

::: tip 持久化配置

在 `pinia-plugin-persistedstate` 中配置 `persist` 选项启用持久化。

:::

---

## UI 设计规范

### 1. 颜色系统与 CSS 变量

项目基于 shadcn-vue 和 Tailwind CSS，使用 CSS 变量定义颜色。

#### 常用颜色变量

| 变量            | 用途     | Tailwind 类名                         |
| --------------- | -------- | ------------------------------------- |
| `--primary`     | 主题色   | `text-primary` / `bg-primary`         |
| `--background`  | 背景色   | `bg-background`                       |
| `--foreground`  | 文字色   | `text-foreground`                     |
| `--card`        | 卡片背景 | `bg-card`                             |
| `--border`      | 边框色   | `border-border`                       |
| `--muted`       | 柔和背景 | `bg-muted`                            |
| `--destructive` | 错误色   | `text-destructive` / `bg-destructive` |
| `--success`     | 成功色   | `text-success`                        |
| `--warning`     | 警告色   | `text-warning`                        |

#### 颜色使用规范

```vue
<!-- 推荐：使用语义化颜色类名 -->
<div class="bg-background text-foreground">
  <span class="text-muted-foreground">次要文字</span>
  <ElButton type="primary">主题按钮</ElButton>
</div>

<!-- 避免：硬编码颜色 -->
<div style="color: #333">不推荐</div>
```

::: tip 主题一致性

使用 CSS 变量确保主题切换时颜色自动适配，避免硬编码颜色值。

:::

#### 样式最佳实践对比

```vue
<!-- ❌ 不推荐示例 -->
<template>
  <div>
    <!-- 硬编码颜色 -->
    <span style="color: #333">文字</span>

    <!-- 内联样式控制布局 -->
    <ElCard :body-style="{ padding: '12px', height: '100%' }">
      <!-- 硬编码宽度 -->
      <ElInput style="width: 200px" />
    </ElCard>

    <!-- 非推荐间距 -->
    <div class="mb-3 mt-3 gap-3">内容</div>
  </div>
</template>

<!-- ✅ 推荐示例 -->
<template>
  <div class="bg-background text-foreground">
    <!-- 语义化颜色 -->
    <span class="text-muted-foreground">文字</span>

    <!-- Tailwind CSS 控制布局 -->
    <ElCard class="h-full">
      <div class="flex h-full flex-col p-4">
        <!-- Tailwind 宽度 -->
        <ElInput class="w-[200px]" />
      </div>
    </ElCard>

    <!-- 推荐间距 -->
    <div class="mb-4 mt-4 gap-4">内容</div>
  </div>
</template>
```

---

### 2. Tailwind CSS 使用规范

#### 常用布局类

| 类名              | 说明        |
| ----------------- | ----------- |
| `flex`            | Flex 布局   |
| `flex-col`        | 纵向 Flex   |
| `items-center`    | 垂直居中    |
| `justify-between` | 水平分布    |
| `gap-4`           | 间距 16px   |
| `p-4`             | 内边距 16px |
| `m-4`             | 外边距 16px |
| `w-full`          | 宽度 100%   |
| `h-full`          | 高度 100%   |

#### 常用样式类

| 类名          | 说明     |
| ------------- | -------- |
| `rounded-lg`  | 圆角 8px |
| `border`      | 边框     |
| `shadow-md`   | 中等阴影 |
| `text-sm`     | 小号文字 |
| `text-lg`     | 大号文字 |
| `font-medium` | 中等字重 |

#### 响应式类

```vue
<!-- 大屏 3 列，中屏 2 列，小屏 1 列 -->
<div class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
  <ElCard>卡片 1</ElCard>
  <ElCard>卡片 2</ElCard>
  <ElCard>卡片 3</ElCard>
</div>
```

#### 间距规范

使用 Tailwind 间距单位，保持一致性：

| 类名            | 间距 | 推荐                         |
| --------------- | ---- | ---------------------------- |
| `gap-1` / `p-1` | 4px  | ⚠️ 最小间距，谨慎使用        |
| `gap-2` / `p-2` | 8px  | ✅ 推荐用于紧凑布局          |
| `gap-3` / `p-3` | 12px | ❌ 不推荐，使用 `gap-4` 替代 |
| `gap-4` / `p-4` | 16px | ✅ **默认间距，推荐使用**    |
| `gap-6` / `p-6` | 24px | ✅ 推荐用于模块间隔          |

::: warning 间距规范要点

- **优先使用偶数单位**：`2`, `4`, `6`（8px, 16px, 24px）
- **避免使用 `gap-3` / `p-3` / `m-3`**（12px），保持视觉一致性
- **页面内元素间距**：使用 `gap-4` / `p-4`（16px）
- **模块间间距**：使用 `gap-6` / `mb-6`（24px）
- **紧凑场景**：使用 `gap-2`（8px）

:::

---

### 3. 布局模式规范

#### 页面容器布局

```vue
<!-- 标准页面布局 -->
<Page auto-content-height>
  <!-- 搜索区域 -->
  <ElCard class="mb-4">
    <!-- 搜索表单 -->
  </ElCard>

  <!-- 内容区域 -->
  <ElCard>
    <!-- 表格或内容 -->
  </ElCard>
</Page>
```

#### 分栏布局

使用 `ElSplitter` 实现左右分栏：

```vue
<ElSplitter class="h-full">
  <!-- 左栏 -->
  <ElSplitterPanel :size="260">
    <ElCard class="h-full">左侧列表</ElCard>
  </ElSplitterPanel>

  <!-- 右栏 -->
  <ElSplitterPanel>
    <ElCard class="h-full">右侧内容</ElCard>
  </ElSplitterPanel>
</ElSplitter>
```

#### 卡片容器样式规范

::: warning 避免使用内联 body-style

Element Plus 的 `ElCard` 默认 padding 为 20px，项目中推荐使用 Tailwind CSS 控制：

```vue
<!-- ❌ 不推荐：使用内联 body-style -->
<ElCard :body-style="{ padding: '12px', height: '100%' }">
  ...
</ElCard>

<!-- ✅ 推荐：使用 Tailwind CSS -->
<ElCard class="h-full p-4">
  ...
</ElCard>

<!-- ✅ 推荐：使用内容容器控制布局 -->
<ElCard class="h-full">
  <div class="h-full flex flex-col p-4">
    ...
  </div>
</ElCard>
```

**原因**：

- 内联样式不利于主题切换
- Tailwind CSS 类名更易维护和复用
- 保持样式一致性

:::

#### 宽度与尺寸规范

```vue
<!-- ❌ 不推荐：硬编码宽度 -->
<ElInput style="width: 200px" />

<!-- ✅ 推荐：使用 Tailwind CSS -->
<ElInput class="w-[200px]" />

<!-- ✅ 推荐：使用语义化宽度 -->
<ElInput class="w-full" />
<ElInput class="w-auto" />
```

---

### 4. 响应式设计

#### 响应式断点

| 断点 | Tailwind 前缀 | 屏幕宽度 |
| ---- | ------------- | -------- |
| sm   | `sm:`         | ≥ 640px  |
| md   | `md:`         | ≥ 768px  |
| lg   | `lg:`         | ≥ 1024px |
| xl   | `xl:`         | ≥ 1280px |

#### 响应式示例

```vue
<!-- 响应式隐藏 -->
<div class="hidden md:block">中屏以上显示</div>

<!-- 响应式布局 -->
<div class="flex-col md:flex-row">
  <!-- 小屏纵向，中屏横向 -->
</div>

<!-- 响应式表格 -->
<ElTable :data="users">
  <!-- 小屏隐藏部分列 -->
  <ElTableColumn prop="name" label="名称" />
  <ElTableColumn prop="email" label="邮箱" class-name="hidden lg:table-cell" />
</ElTable>
```

---

## 资产复用

### 1. 图标使用指南

项目支持三种图标使用方式，统一在 `@vben/icons` 管理。

#### Iconify 图标（推荐）

```vue
<script lang="ts" setup>
// 新增图标：packages/icons/src/iconify/index.ts
// export const MdiKeyboardEsc = createIconifyIcon('mdi:keyboard-esc');
import { MdiKeyboardEsc } from '@vben/icons';
</script>

<template>
  <!-- 使用组件 -->
  <MdiKeyboardEsc class="size-5" />
</template>
```

#### Tailwind CSS 图标

```vue
<template>
  <!-- 直接使用类名 -->
  <span class="icon-[lucide--search] text-base"></span>
  <span class="icon-[mdi--home]"></span>
</template>
```

#### 常用图标集

| 图标集   | 格式                                 |
| -------- | ------------------------------------ |
| Lucide   | `lucide:xxx` 或 `icon-[lucide--xxx]` |
| MDI      | `mdi:xxx` 或 `icon-[mdi--xxx]`       |
| IconPark | `icon-park:xxx`                      |

::: tip 推荐使用 Lucide

Lucide 图标集图标丰富、风格统一，推荐作为首选。

:::

#### 图标图标库

- [Lucide Icons](https://lucide.dev/)
- [Material Design Icons](https://pictogrammers.com/library/mdi/)
- [IconPark](https://iconpark.oceanengine.com/)

---

### 2. 组件适配器扩展

当需要新增表单组件类型时，扩展适配器。

#### 新增组件步骤

1. 在 `src/adapter/component/index.ts` 注册组件：

```ts
// 导入组件
const ElRate = defineAsyncComponent(() =>
  Promise.all([
    import('element-plus/es/components/rate/index'),
    import('element-plus/es/components/rate/style/css'),
  ]).then(([res]) => res.ElRate),
);

// 在 components 对象中添加
const components: Partial<Record<ComponentType, Component>> = {
  // ...其他组件
  Rate: ElRate, // 新增评分组件
};

// 在 ComponentType 类型中添加
export type ComponentType =
  | 'Rate'
  | ...其他类型;
```

2. 在表单 schema 中使用：

```ts
schema: [
  {
    component: 'Rate',
    fieldName: 'score',
    label: '评分',
    componentProps: {
      max: 5,
    },
  },
],
```

---

### 3. 样式复用

#### 全局样式引入

```vue
<style lang="scss" scoped>
// 引入全局样式（应用内已自动引入，可省略）
@use '@vben/styles/global' as *;
</style>
```

#### BEM 命名规范

使用 `useNamespace` 生成命名空间：

```vue
<script lang="ts" setup>
import { useNamespace } from '@vben/hooks';

const { b, e, is } = useNamespace('user-card');
</script>

<template>
  <div :class="[b()]">
    <div :class="[e('title')]">标题</div>
    <div :class="[e('content'), is('active', true)]">内容</div>
  </div>
</template>

<style lang="scss" scoped>
@include b('user-card') {
  @include e('title') {
    font-weight: bold;
  }
  @include e('content') {
    @include is('active') {
      color: primary;
    }
  }
}
</style>
```

#### CSS 变量覆盖

```css
/* 覆盖卡片背景色 */
:root {
  --card: 0 0% 30%;
}

/* 暗色模式覆盖 */
.dark {
  --card: 222.34deg 10.43% 12.27%;
}
```

---

## 最佳实践清单

### 开发 Checklist

#### 路由配置

- [ ] 路由文件放在 `src/router/routes/modules/` 目录
- [ ] 使用 `$t()` 包裹标题，支持国际化
- [ ] 图标使用 `lucide:xxx` 格式
- [ ] 一级菜单设置 `order` 排序
- [ ] 详情页设置 `hideInMenu: true` 和 `activePath`

#### API 层

- [ ] API 文件放在 `src/api/<module>/index.ts`
- [ ] 定义 TypeScript 类型接口
- [ ] 使用 `requestClient` 封装请求
- [ ] 后端响应格式 `{ code: 0, data, message }`

#### 页面组件

- [ ] 使用 `Page` 组件作为页面根容器
- [ ] 表格页面启用 `autoContentHeight`
- [ ] 使用 Tailwind CSS 类名，避免硬编码样式
- [ ] 组件命名使用 PascalCase
- [ ] 复杂页面拆分子组件（超过 100 行）

#### 样式规范

- [ ] 间距使用偶数单位（`gap-2`, `gap-4`, `gap-6`）
- [ ] 避免使用 `gap-3` / `p-3` / `m-3`（12px）
- [ ] 禁止 `body-style` 内联样式，使用 Tailwind 替代
- [ ] 禁止 `style="width: xxx"`，使用 `class="w-[xxx]"`
- [ ] 禁止硬编码颜色，使用 CSS 变量（`text-primary` 等）

#### 表单

- [ ] 使用 `useVbenForm` 创建表单
- [ ] 必填字段使用 `rules: 'required'`
- [ ] 设置 `commonConfig.componentProps.class: 'w-full'`
- [ ] 使用 zod 进行复杂验证

#### 表格

- [ ] 使用 `useVbenVxeGrid` 创建表格（推荐）
- [ ] 配置 `proxyConfig.ajax.query` 远程加载
- [ ] 配置 `pagerConfig` 分页
- [ ] 搜索表单使用 `formOptions.schema`
- [ ] 避免直接使用 `ElTable`（简单场景可接受）

#### 弹窗/抽屉

- [ ] 使用 `useVbenModal` / `useVbenDrawer`
- [ ] 提交时使用 `modalApi.lock()` 防止重复提交
- [ ] 宽度通过 `class="w-[600px]"` 配置

#### UI 规范

- [ ] 使用 CSS 变量颜色，避免硬编码
- [ ] 间距使用 Tailwind 单位（gap-4, p-4 等）
- [ ] 图标使用 Lucide 图标集
- [ ] 响应式使用 `md:` `lg:` 前缀

#### Git 提交

- [ ] 提交信息使用 Angular 风格
- [ ] 格式：`feat(frontend): 新增用户管理模块`
- [ ] 类型：`feat` `fix` `perf` `refactor` `docs` `chore`

---

## 参考资源

### 项目文档

- [Vben Admin 官方文档](https://doc.vben.pro/)
- [项目目录结构](/guide/project/dir)
- [编码规范](/guide/project/standard)
- [路由配置](/guide/essentials/route)
- [服务端交互](/guide/essentials/server)
- [图标使用](/guide/essentials/icons)
- [权限控制](/guide/in-depth/access)
- [国际化](/guide/in-depth/locale)
- [主题系统](/guide/in-depth/theme)

### 组件文档

- [Vben Form 表单](/components/common-ui/vben-form)
- [Vben Modal 弹窗](/components/common-ui/vben-modal)
- [Vben Drawer 抽屉](/components/common-ui/vben-drawer)
- [Vben Vxe Table 表格](/components/common-ui/vben-vxe-table)
- [Page 页面组件](/components/layout-ui/page)

### 外部资源

- [Element Plus 文档](https://element-plus.org/)
- [Tailwind CSS 文档](https://tailwindcss.com/)
- [Vue 3 文档](https://vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
