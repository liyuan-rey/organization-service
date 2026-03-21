<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h1 class="text-2xl font-bold tracking-tight">仪表盘</h1>
      <p class="text-muted-foreground">组织管理系统数据概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <el-card
        v-for="stat in statistics"
        :key="stat.title"
        class="hover:shadow-md transition-shadow cursor-pointer"
        :body-style="{ padding: '1.5rem' }"
        @click="$router.push(stat.link)"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-muted-foreground">{{ stat.title }}</p>
            <p class="text-3xl font-bold mt-1">
              <template v-if="stat.loading">
                <el-skeleton :rows="0" animated style="width: 60px; height: 36px" />
              </template>
              <template v-else>{{ stat.value }}</template>
            </p>
          </div>
          <div
            class="h-12 w-12 rounded-lg flex items-center justify-center"
            :class="stat.bgColor"
          >
            <component :is="stat.icon" class="h-6 w-6" :class="stat.iconColor" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 快捷入口 -->
    <div>
      <h2 class="text-lg font-semibold mb-4">快捷入口</h2>
      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <el-card
          v-for="shortcut in shortcuts"
          :key="shortcut.title"
          class="hover:shadow-md transition-shadow cursor-pointer group"
          :body-style="{ padding: '1.25rem' }"
          @click="$router.push(shortcut.link)"
        >
          <div class="flex items-center gap-4">
            <div
              class="h-10 w-10 rounded-lg flex items-center justify-center bg-primary/10 group-hover:bg-primary/20 transition-colors"
            >
              <component :is="shortcut.icon" class="h-5 w-5 text-primary" />
            </div>
            <div>
              <p class="font-medium">{{ shortcut.title }}</p>
              <p class="text-sm text-muted-foreground">{{ shortcut.description }}</p>
            </div>
            <ChevronRight class="h-5 w-5 text-muted-foreground ml-auto group-hover:text-primary transition-colors" />
          </div>
        </el-card>
      </div>
    </div>

    <!-- 最近活动 -->
    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-semibold">系统信息</span>
        </div>
      </template>
      <div class="space-y-4">
        <div class="flex items-center gap-4 text-sm">
          <div class="h-2 w-2 rounded-full bg-green-500"></div>
          <span class="text-muted-foreground">系统状态：</span>
          <span class="text-green-600 font-medium">运行正常</span>
        </div>
        <div class="flex items-center gap-4 text-sm">
          <div class="h-2 w-2 rounded-full bg-blue-500"></div>
          <span class="text-muted-foreground">数据库连接：</span>
          <span class="text-blue-600 font-medium">已连接</span>
        </div>
        <div class="flex items-center gap-4 text-sm">
          <div class="h-2 w-2 rounded-full bg-purple-500"></div>
          <span class="text-muted-foreground">后端服务：</span>
          <span class="text-purple-600 font-medium">http://localhost:8080</span>
        </div>
      </div>
    </el-card>
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

    // 暂时设为 0，后续可以添加关联数据
    statistics.value[3].value = 0
    statistics.value[3].loading = false
  } catch (error) {
    console.error('Failed to load statistics:', error)
    statistics.value.forEach(s => s.loading = false)
  }
})
</script>