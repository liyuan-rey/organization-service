<template>
  <div class="space-y-5">
    <!-- 统计卡片 -->
    <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <div
        v-for="stat in statistics"
        :key="stat.title"
        class="stat-card group"
        @click="$router.push(stat.link)"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] text-muted-foreground mb-0.5">{{ stat.title }}</p>
            <p class="text-2xl font-semibold">
              <template v-if="stat.loading">
                <span class="text-muted-foreground">--</span>
              </template>
              <template v-else>{{ stat.value }}</template>
            </p>
          </div>
          <div
            class="h-9 w-9 rounded-md flex items-center justify-center transition-transform group-hover:scale-105"
            :class="stat.bgColor"
          >
            <component :is="stat.icon" class="h-4 w-4" :class="stat.iconColor" />
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-[13px] font-semibold text-muted-foreground">快捷入口</h2>
      </div>
      <div class="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="shortcut in shortcuts"
          :key="shortcut.title"
          class="shortcut-card group"
          @click="$router.push(shortcut.link)"
        >
          <div class="flex items-center gap-3">
            <div
              class="h-8 w-8 rounded-md flex items-center justify-center bg-primary/10 group-hover:bg-primary/15 transition-colors shrink-0"
            >
              <component :is="shortcut.icon" class="h-4 w-4 text-primary" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-[13px] font-medium">{{ shortcut.title }}</p>
              <p class="text-xs text-muted-foreground truncate">{{ shortcut.description }}</p>
            </div>
            <ChevronRight class="h-4 w-4 text-muted-foreground/30 group-hover:text-primary group-hover:translate-x-0.5 transition-all" />
          </div>
        </div>
      </div>
    </div>

    <!-- 系统信息 -->
    <div class="grid gap-3 lg:grid-cols-2">
      <div class="info-card">
        <div class="px-4 py-2.5 border-b border-border/50">
          <h3 class="text-[13px] font-semibold">系统状态</h3>
        </div>
        <div class="p-3 space-y-2">
          <div class="flex items-center justify-between text-[13px]">
            <div class="flex items-center gap-2">
              <div class="h-1.5 w-1.5 rounded-full bg-green-500"></div>
              <span class="text-muted-foreground">系统状态</span>
            </div>
            <span class="text-green-600 font-medium">运行正常</span>
          </div>
          <div class="flex items-center justify-between text-[13px]">
            <div class="flex items-center gap-2">
              <div class="h-1.5 w-1.5 rounded-full bg-blue-500"></div>
              <span class="text-muted-foreground">数据库连接</span>
            </div>
            <span class="text-blue-600 font-medium">已连接</span>
          </div>
          <div class="flex items-center justify-between text-[13px]">
            <div class="flex items-center gap-2">
              <div class="h-1.5 w-1.5 rounded-full bg-purple-500"></div>
              <span class="text-muted-foreground">后端服务</span>
            </div>
            <span class="text-muted-foreground font-mono text-xs">localhost:8080</span>
          </div>
        </div>
      </div>

      <div class="info-card">
        <div class="px-4 py-2.5 border-b border-border/50">
          <h3 class="text-[13px] font-semibold">最近操作</h3>
        </div>
        <div class="p-3 space-y-2">
          <div class="flex items-start gap-2.5 text-[13px]">
            <div class="h-7 w-7 rounded-md bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center shrink-0">
              <UserPlus class="h-3.5 w-3.5 text-blue-600" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium">新增人员</p>
              <p class="text-xs text-muted-foreground">2 分钟前</p>
            </div>
          </div>
          <div class="flex items-start gap-2.5 text-[13px]">
            <div class="h-7 w-7 rounded-md bg-green-100 dark:bg-green-900/30 flex items-center justify-center shrink-0">
              <Building2 class="h-3.5 w-3.5 text-green-600" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium">更新部门信息</p>
              <p class="text-xs text-muted-foreground">15 分钟前</p>
            </div>
          </div>
          <div class="flex items-start gap-2.5 text-[13px]">
            <div class="h-7 w-7 rounded-md bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center shrink-0">
              <Briefcase class="h-3.5 w-3.5 text-purple-600" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium">创建岗位</p>
              <p class="text-xs text-muted-foreground">1 小时前</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw } from 'vue'
import { useDepartmentStore } from '@/stores/department'
import { usePersonnelStore } from '@/stores/personnel'
import { usePositionStore } from '@/stores/position'
import {
  Building2,
  Users,
  Briefcase,
  UserCheck,
  ChevronRight,
  UserPlus,
  Building,
  Settings,
} from 'lucide-vue-next'

const departmentStore = useDepartmentStore()
const personnelStore = usePersonnelStore()
const positionStore = usePositionStore()

const statistics = ref([
  {
    title: '部门总数',
    value: 0,
    loading: true,
    icon: markRaw(Building2),
    bgColor: 'bg-blue-100 dark:bg-blue-900/30',
    iconColor: 'text-blue-600 dark:text-blue-400',
    link: '/departments',
  },
  {
    title: '人员总数',
    value: 0,
    loading: true,
    icon: markRaw(Users),
    bgColor: 'bg-green-100 dark:bg-green-900/30',
    iconColor: 'text-green-600 dark:text-green-400',
    link: '/personnel',
  },
  {
    title: '岗位总数',
    value: 0,
    loading: true,
    icon: markRaw(Briefcase),
    bgColor: 'bg-purple-100 dark:bg-purple-900/30',
    iconColor: 'text-purple-600 dark:text-purple-400',
    link: '/positions',
  },
  {
    title: '人员岗位关联',
    value: 0,
    loading: true,
    icon: markRaw(UserCheck),
    bgColor: 'bg-orange-100 dark:bg-orange-900/30',
    iconColor: 'text-orange-600 dark:text-orange-400',
    link: '/personnel-positions',
  },
])

const shortcuts = [
  {
    title: '添加部门',
    description: '创建新的组织部门',
    icon: markRaw(Building2),
    link: '/departments',
  },
  {
    title: '添加人员',
    description: '录入新员工信息',
    icon: markRaw(UserPlus),
    link: '/personnel',
  },
  {
    title: '添加岗位',
    description: '定义新的岗位角色',
    icon: markRaw(Briefcase),
    link: '/positions',
  },
  {
    title: '部门岗位配置',
    description: '管理部门与岗位关系',
    icon: markRaw(Building),
    link: '/department-positions',
  },
  {
    title: '人员岗位分配',
    description: '分配人员到具体岗位',
    icon: markRaw(UserCheck),
    link: '/personnel-positions',
  },
  {
    title: '系统设置',
    description: '配置系统参数',
    icon: markRaw(Settings),
    link: '/dashboard',
  },
]

onMounted(async () => {
  try {
    await Promise.all([
      departmentStore.fetchDepartments(),
      personnelStore.fetchPersonnel(),
      positionStore.fetchPositions(),
    ])

    statistics.value[0].value = departmentStore.departments.length
    statistics.value[0].loading = false

    statistics.value[1].value = personnelStore.personnel.length
    statistics.value[1].loading = false

    statistics.value[2].value = positionStore.positions.length
    statistics.value[2].loading = false

    statistics.value[3].value = 0
    statistics.value[3].loading = false
  } catch (error) {
    console.error('Failed to load statistics:', error)
    statistics.value.forEach(s => s.loading = false)
  }
})
</script>

<style scoped>
.stat-card {
  background-color: hsl(var(--card));
  border-radius: 6px;
  padding: 0.875rem;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 1px solid hsl(var(--border) / 0.4);
}

.stat-card:hover {
  border-color: hsl(var(--border));
}

.shortcut-card {
  background-color: hsl(var(--card));
  border-radius: 6px;
  padding: 0.75rem;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 1px solid hsl(var(--border) / 0.4);
}

.shortcut-card:hover {
  border-color: hsl(var(--border));
}

.info-card {
  background-color: hsl(var(--card));
  border-radius: 6px;
  border: 1px solid hsl(var(--border) / 0.4);
  overflow: hidden;
}
</style>