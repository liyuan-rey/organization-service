<script lang="ts" setup>
import type { Group } from '#/api/organization';

import { computed } from 'vue';

import { LucideFolder, LucideSearch } from '@vben/icons';
import { $t } from '@vben/locales';

import { ElCard, ElEmpty, ElInput } from 'element-plus';

interface Props {
  groups: Group[];
  loading?: boolean;
  selectedId?: string;
  searchKeyword?: string;
}

interface Emits {
  (e: 'select', group: Group): void;
  (e: 'update:searchKeyword', value: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  selectedId: '',
  searchKeyword: '',
});

const emit = defineEmits<Emits>();

// 篮选后的分组列表
const filteredGroups = computed(() => {
  if (!props.searchKeyword.trim()) {
    return props.groups;
  }
  const keyword = props.searchKeyword.toLowerCase();
  return props.groups.filter(
    (group) =>
      group.name.toLowerCase().includes(keyword) ||
      group.description?.toLowerCase().includes(keyword),
  );
});

function handleSelect(group: Group) {
  emit('select', group);
}

function handleSearchChange(value: string) {
  emit('update:searchKeyword', value);
}
</script>

<template>
  <ElCard class="h-full rounded-lg">
    <div class="flex h-full flex-col p-4">
      <!-- 搜索框 -->
      <div class="mb-4">
        <ElInput
          :model-value="searchKeyword"
          clearable
          :placeholder="$t('page.organization.structure.searchGroup')"
          @update:model-value="handleSearchChange"
        >
          <template #prefix>
            <LucideSearch class="size-4" />
          </template>
        </ElInput>
      </div>

      <!-- 分组列表 -->
      <div class="flex-1 overflow-y-auto">
        <ElEmpty
          v-if="filteredGroups.length === 0 && !loading"
          :description="$t('page.organization.structure.noGroup')"
        />
        <div v-else class="flex flex-col gap-2">
          <ElCard
            v-for="group in filteredGroups"
            :key="group.id"
            class="cursor-pointer p-4 transition-all hover:shadow-md"
            :class="[
              selectedId === group.id
                ? 'border-primary ring-1 ring-primary'
                : '',
            ]"
            shadow="hover"
            @click="handleSelect(group)"
          >
            <div class="flex items-start gap-4">
              <div
                class="flex size-10 flex-shrink-0 items-center justify-center rounded bg-primary/10"
              >
                <LucideFolder class="size-5 text-primary" />
              </div>
              <div class="min-w-0 flex-1">
                <div class="truncate font-medium">{{ group.name }}</div>
                <div
                  class="mt-2 truncate text-sm text-muted-foreground"
                  :title="group.description"
                >
                  {{
                    group.description ||
                    $t('page.organization.structure.noDescription')
                  }}
                </div>
              </div>
            </div>
          </ElCard>
        </div>
      </div>
    </div>
  </ElCard>
</template>
