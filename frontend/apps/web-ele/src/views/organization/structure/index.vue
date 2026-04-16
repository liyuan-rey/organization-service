<script lang="ts" setup>
import type { Group } from '#/api/organization';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { ElSplitter, ElSplitterPanel } from 'element-plus';

import { getGroupListApi } from '#/api/organization';

import DepartmentGrid from './components/department-grid.vue';
import GroupList from './components/group-list.vue';

// 分组数据
const groups = ref<Group[]>([]);
const groupLoading = ref(false);
const selectedGroupId = ref<string>('');
const groupSearchKeyword = ref('');

// 加载分组列表
async function loadGroups() {
  groupLoading.value = true;
  try {
    const data = await getGroupListApi();
    groups.value = data;
    // 默认选中第一个
    const firstGroup = data[0];
    if (firstGroup) {
      selectedGroupId.value = firstGroup.id;
    }
  } finally {
    groupLoading.value = false;
  }
}

// 选中分组
function handleSelectGroup(group: Group) {
  selectedGroupId.value = group.id;
}

// 初始化
onMounted(() => {
  loadGroups();
});
</script>

<template>
  <Page
    auto-content-height
    :title="$t('page.organization.structure.title')"
    :description="$t('page.organization.structure.description')"
  >
    <ElSplitter class="h-full">
      <!-- 左栏：分组列表 -->
      <ElSplitterPanel :size="260">
        <GroupList
          :groups="groups"
          :loading="groupLoading"
          :selected-id="selectedGroupId"
          :search-keyword="groupSearchKeyword"
          @select="handleSelectGroup"
          @update:search-keyword="groupSearchKeyword = $event"
        />
      </ElSplitterPanel>

      <!-- 右栏：部门表格 -->
      <ElSplitterPanel>
        <DepartmentGrid :selected-group-id="selectedGroupId" />
      </ElSplitterPanel>
    </ElSplitter>
  </Page>
</template>
