<script lang="ts" setup>
import type { Department, Group } from '#/api/organization';

import { computed, onMounted, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElButtonGroup,
  ElCard,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElInput,
  ElPagination,
  ElSplitter,
  ElSplitterPanel,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { getDepartmentListApi, getGroupListApi } from '#/api/organization';

// 分组数据
const groups = ref<Group[]>([]);
const groupLoading = ref(false);
const selectedGroupId = ref<string>('');
const groupSearchKeyword = ref('');

// 视图模式：table | tree
const viewMode = ref<'table' | 'tree'>('table');

// 筛选后的分组列表
const filteredGroups = computed(() => {
  if (!groupSearchKeyword.value.trim()) {
    return groups.value;
  }
  const keyword = groupSearchKeyword.value.toLowerCase();
  return groups.value.filter(
    (group) =>
      group.name.toLowerCase().includes(keyword) ||
      group.description?.toLowerCase().includes(keyword),
  );
});

// 部门数据
const departments = ref<Department[]>([]);
const departmentLoading = ref(false);
const departmentSearchKeyword = ref('');

// 分页
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0,
});

// 加载分组列表
async function loadGroups() {
  groupLoading.value = true;
  try {
    const data = await getGroupListApi();
    groups.value = data;
    // 默认选中第一个
    if (data.length > 0) {
      selectedGroupId.value = data[0].id;
    }
  } catch {
    // 错误已由全局拦截器处理
  } finally {
    groupLoading.value = false;
  }
}

// 加载部门列表
async function loadDepartments() {
  departmentLoading.value = true;
  try {
    const data = await getDepartmentListApi({
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      name: departmentSearchKeyword.value || undefined,
    });
    departments.value = data.items;
    pagination.value.total = data.total;
  } catch {
    // 错误已由全局拦截器处理
  } finally {
    departmentLoading.value = false;
  }
}

// 选中分组
function selectGroup(group: Group) {
  selectedGroupId.value = group.id;
}

// 分页改变
function handlePageChange(page: number) {
  pagination.value.page = page;
  loadDepartments();
}

function handleSizeChange(size: number) {
  pagination.value.pageSize = size;
  pagination.value.page = 1;
  loadDepartments();
}

// 搜索部门
function handleSearchDepartment() {
  pagination.value.page = 1;
  loadDepartments();
}

// 表格排序
function handleSortChange({ prop, order }: { order: string; prop: string }) {
  if (prop && order) {
    const sortDirection = order === 'ascending' ? 1 : -1;
    departments.value.sort((a: Record<string, any>, b: Record<string, any>) => {
      const aVal = a[prop] || '';
      const bVal = b[prop] || '';
      return aVal.localeCompare(bVal) * sortDirection;
    });
  }
}

// 监听选中分组变化
watch(selectedGroupId, () => {
  // 切换分组时重新加载部门数据
  pagination.value.page = 1;
  loadDepartments();
});

// 初始化
onMounted(() => {
  loadGroups();
  loadDepartments();
});
</script>

<template>
  <Page
    auto-content-height
    description="管理组织机构的分组和部门信息"
    title="结构维护"
  >
    <ElSplitter class="h-full">
      <!-- 左栏：分组列表 -->
      <ElSplitterPanel :size="260">
        <ElCard
          class="h-full rounded-lg"
          :body-style="{
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            padding: '12px',
          }"
        >
          <!-- 搜索框 -->
          <div class="mb-3">
            <ElInput
              v-model="groupSearchKeyword"
              clearable
              placeholder="搜索分组名称"
            >
              <template #prefix>
                <span class="icon-[lucide--search] text-base"></span>
              </template>
            </ElInput>
          </div>

          <!-- 分组列表 -->
          <div class="flex-1 overflow-y-auto">
            <ElEmpty
              v-if="filteredGroups.length === 0 && !groupLoading"
              description="暂无分组数据"
            />
            <div v-else class="flex flex-col gap-2">
              <ElCard
                v-for="group in filteredGroups"
                :key="group.id"
                :body-style="{ padding: '12px' }"
                class="cursor-pointer transition-all hover:shadow-md"
                :class="[
                  selectedGroupId === group.id
                    ? 'border-primary ring-1 ring-primary'
                    : '',
                ]"
                shadow="hover"
                @click="selectGroup(group)"
              >
                <div class="flex items-start gap-3">
                  <div
                    class="flex size-10 flex-shrink-0 items-center justify-center rounded bg-primary/10"
                  >
                    <span
                      class="icon-[lucide--folder] text-lg text-primary"
                    ></span>
                  </div>
                  <div class="min-w-0 flex-1">
                    <div class="truncate font-medium">{{ group.name }}</div>
                    <div
                      class="mt-1 truncate text-sm text-muted-foreground"
                      :title="group.description"
                    >
                      {{ group.description || '暂无描述' }}
                    </div>
                  </div>
                </div>
              </ElCard>
            </div>
          </div>
        </ElCard>
      </ElSplitterPanel>

      <!-- 右栏：部门表格 -->
      <ElSplitterPanel>
        <ElCard
          class="h-full rounded-lg"
          :body-style="{
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            padding: '12px',
          }"
        >
          <!-- 搜索表单和视图切换 -->
          <div class="mb-3 flex items-center justify-between">
            <!-- 左侧：搜索表单 -->
            <ElForm :inline="true">
              <ElFormItem label="部门名称">
                <ElInput
                  v-model="departmentSearchKeyword"
                  clearable
                  placeholder="请输入部门名称"
                  style="width: 200px"
                  @keyup.enter="handleSearchDepartment"
                />
              </ElFormItem>
            </ElForm>

            <!-- 右侧：模式切换按钮组 -->
            <ElButtonGroup>
              <ElButton
                :type="viewMode === 'tree' ? 'primary' : 'default'"
                @click="viewMode = 'tree'"
              >
                <span class="icon-[lucide--git-branch] mr-1 text-base"></span>
                树型展示
              </ElButton>
              <ElButton
                :type="viewMode === 'table' ? 'primary' : 'default'"
                @click="viewMode = 'table'"
              >
                <span class="icon-[lucide--table] mr-1 text-base"></span>
                表格展示
              </ElButton>
            </ElButtonGroup>
          </div>

          <!-- 表格视图 -->
          <template v-if="viewMode === 'table'">
            <div class="flex-1 overflow-hidden">
              <ElTable
                v-loading="departmentLoading"
                :data="departments"
                :header-cell-style="{
                  backgroundColor: 'var(--el-fill-color-light)',
                }"
                border
                height="100%"
                stripe
                @sort-change="handleSortChange"
              >
                <ElTableColumn
                  label="名称"
                  prop="name"
                  sortable="custom"
                  width="150"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="英文名"
                  prop="englishName"
                  sortable="custom"
                  width="180"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="简称"
                  prop="shortName"
                  sortable="custom"
                  width="100"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="组织编码"
                  prop="orgCode"
                  sortable="custom"
                  width="130"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="电话"
                  prop="phone"
                  width="140"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="邮箱"
                  prop="email"
                  width="180"
                  show-overflow-tooltip
                />
                <ElTableColumn
                  label="地址"
                  prop="address"
                  min-width="200"
                  show-overflow-tooltip
                />
              </ElTable>
            </div>

            <!-- 分页 -->
            <div class="mt-3 flex justify-end">
              <ElPagination
                v-model:current-page="pagination.page"
                v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                background
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handlePageChange"
              />
            </div>
          </template>

          <!-- 树型视图（占位） -->
          <div
            v-else
            class="flex flex-1 items-center justify-center overflow-hidden"
          >
            <span class="text-muted-foreground">树型展示（开发中）</span>
          </div>
        </ElCard>
      </ElSplitterPanel>
    </ElSplitter>
  </Page>
</template>
