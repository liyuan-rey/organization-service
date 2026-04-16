<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import type { Department } from '#/api/organization';

import { ref, watch } from 'vue';

import { LucideGitBranch, LucideRefresh, LucideTable } from '@vben/icons';
import { $t } from '@vben/locales';

import { ElButton, ElButtonGroup, ElCard } from 'element-plus';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getDepartmentListApi } from '#/api/organization';

interface Props {
  selectedGroupId?: string;
}

interface Emits {
  (e: 'viewModeChange', mode: 'table' | 'tree'): void;
}

interface PageParams {
  currentPage: number;
  pageSize: number;
}

const props = withDefaults(defineProps<Props>(), {
  selectedGroupId: '',
});

const emit = defineEmits<Emits>();

// 视图模式：table | tree
const viewMode = ref<'table' | 'tree'>('table');

// 表格配置
const [Grid, gridApi] = useVbenVxeGrid<Department>({
  gridOptions: {
    columns: [
      {
        type: 'seq',
        title: $t('page.organization.structure.serialNumber'),
        width: 50,
      },
      {
        field: 'name',
        title: $t('page.organization.structure.departmentName'),
        sortable: true,
        width: 150,
      },
      {
        field: 'englishName',
        title: $t('page.organization.structure.englishName'),
        sortable: true,
        width: 180,
      },
      {
        field: 'shortName',
        title: $t('page.organization.structure.shortName'),
        sortable: true,
        width: 100,
      },
      {
        field: 'orgCode',
        title: $t('page.organization.structure.orgCode'),
        sortable: true,
        width: 130,
      },
      {
        field: 'phone',
        title: $t('page.organization.structure.phone'),
        width: 140,
      },
      {
        field: 'email',
        title: $t('page.organization.structure.email'),
        width: 180,
      },
      {
        field: 'address',
        title: $t('page.organization.structure.address'),
        minWidth: 200,
      },
    ],
    proxyConfig: {
      ajax: {
        query: async (
          params: { page: PageParams },
          formValues: Recordable<any>,
        ) => {
          const res = await getDepartmentListApi({
            page: params.page.currentPage,
            pageSize: params.page.pageSize,
            name: formValues?.name || undefined,
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
    toolbarConfig: {
      search: true,
    },
  },
  formOptions: {
    schema: [
      {
        component: 'Input',
        fieldName: 'name',
        label: $t('page.organization.structure.departmentName'),
        componentProps: {
          placeholder: $t('page.organization.structure.searchDepartment'),
        },
      },
    ],
  },
});

// 监听选中分组变化
watch(
  () => props.selectedGroupId,
  () => {
    gridApi.reload();
  },
);

// 刷新表格
function handleRefresh() {
  gridApi.reload();
}

// 切换视图模式
function handleViewModeChange(mode: 'table' | 'tree') {
  viewMode.value = mode;
  emit('viewModeChange', mode);
}
</script>

<template>
  <ElCard class="h-full rounded-lg">
    <div class="flex h-full flex-col p-4">
      <!-- 视图切换按钮 -->
      <div class="mb-4 flex items-center justify-end">
        <ElButtonGroup>
          <ElButton
            :type="viewMode === 'tree' ? 'primary' : 'default'"
            @click="handleViewModeChange('tree')"
          >
            <LucideGitBranch class="mr-2 size-4" />
            {{ $t('page.organization.structure.treeView') }}
          </ElButton>
          <ElButton
            :type="viewMode === 'table' ? 'primary' : 'default'"
            @click="handleViewModeChange('table')"
          >
            <LucideTable class="mr-2 size-4" />
            {{ $t('page.organization.structure.tableView') }}
          </ElButton>
        </ElButtonGroup>
      </div>

      <!-- 表格视图 -->
      <template v-if="viewMode === 'table'">
        <Grid table-title="">
          <template #toolbar-tools>
            <ElButton type="primary" @click="handleRefresh">
              <LucideRefresh class="mr-2 size-4" />
              {{ $t('common.refresh') }}
            </ElButton>
          </template>
        </Grid>
      </template>

      <!-- 树型视图（占位） -->
      <div
        v-else
        class="flex flex-1 items-center justify-center overflow-hidden"
      >
        <span class="text-muted-foreground">
          {{ $t('page.organization.structure.treeViewPlaceholder') }}
        </span>
      </div>
    </div>
  </ElCard>
</template>
