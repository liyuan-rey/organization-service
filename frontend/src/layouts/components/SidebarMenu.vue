<template>
  <nav class="flex-1 overflow-y-auto overflow-x-hidden p-2">
    <template v-for="route in menuRoutes" :key="route.path">
      <el-tooltip
        v-if="collapsed"
        :content="route.meta?.title"
        placement="right"
        :show-after="200"
      >
        <router-link
          :to="`/${route.path}`"
          class="menu-item collapsed"
          :class="{ 'is-active': isActive(`/${route.path}`) }"
        >
          <component :is="getIcon(route.meta?.icon)" class="h-4 w-4 shrink-0" />
        </router-link>
      </el-tooltip>
      <router-link
        v-else
        :to="`/${route.path}`"
        class="menu-item"
        :class="{ 'is-active': isActive(`/${route.path}`) }"
      >
        <component :is="getIcon(route.meta?.icon)" class="h-4 w-4 shrink-0" />
        <span class="text-[13px]">{{ route.meta?.title }}</span>
      </router-link>
    </template>
  </nav>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import {
  LayoutDashboard,
  Building2,
  Users,
  Briefcase,
  Building,
  UserCheck,
} from 'lucide-vue-next'

const props = defineProps<{
  collapsed?: boolean
}>()

const route = useRoute()

const iconMap: Record<string, any> = {
  LayoutDashboard,
  Building2,
  Users,
  Briefcase,
  Building,
  UserCheck,
}

const menuRoutes = [
  { path: 'dashboard', meta: { title: '仪表盘', icon: 'LayoutDashboard' } },
  { path: 'departments', meta: { title: '部门管理', icon: 'Building2' } },
  { path: 'personnel', meta: { title: '人员管理', icon: 'Users' } },
  { path: 'positions', meta: { title: '岗位管理', icon: 'Briefcase' } },
  { path: 'department-positions', meta: { title: '部门岗位', icon: 'Building' } },
  { path: 'personnel-positions', meta: { title: '人员岗位', icon: 'UserCheck' } },
]

function getIcon(iconName?: string) {
  if (!iconName) return LayoutDashboard
  return iconMap[iconName] || LayoutDashboard
}

function isActive(path: string) {
  return route.path === path
}
</script>

<style scoped>
.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  height: 38px;
  border-radius: 6px;
  color: hsl(var(--sidebar-foreground) / 0.7);
  font-weight: 500;
  transition: all 0.15s ease;
  overflow: hidden;
}

.menu-item:hover {
  color: hsl(var(--sidebar-foreground));
  background-color: hsl(var(--sidebar-accent));
}

.menu-item.is-active {
  color: hsl(var(--sidebar-foreground));
  background-color: hsl(var(--sidebar-accent));
}

.menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background-color: hsl(var(--primary));
  border-radius: 0 2px 2px 0;
}

.menu-item.collapsed {
  justify-content: center;
  padding: 0;
}
</style>