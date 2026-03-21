<template>
  <!-- Backdrop -->
  <Transition name="fade">
    <div
      v-if="mobileMenuOpen"
      class="fixed inset-0 bg-black/60 z-40 lg:hidden"
      @click="closeMobileMenu"
    />
  </Transition>

  <!-- Drawer -->
  <Transition name="slide-left">
    <aside
      v-if="mobileMenuOpen"
      class="fixed left-0 top-0 h-full w-[220px] z-50 lg:hidden
             bg-[hsl(var(--sidebar-background))]
             flex flex-col"
    >
      <!-- Logo -->
      <div
        class="h-[var(--toolbar-height)] flex items-center px-4
               border-b border-[hsl(var(--sidebar-border))]
               shrink-0"
      >
        <div class="flex items-center gap-3">
          <div
            class="h-8 w-8 rounded-lg bg-primary/20 flex items-center justify-center"
          >
            <Building2 class="h-5 w-5 text-primary" />
          </div>
          <span class="font-semibold text-[hsl(var(--sidebar-foreground))]">组织管理系统</span>
        </div>
      </div>

      <!-- Menu -->
      <nav class="flex-1 overflow-y-auto p-2">
        <router-link
          v-for="route in menuRoutes"
          :key="route.path"
          :to="`/${route.path}`"
          class="menu-item"
          :class="{ 'is-active': isActive(`/${route.path}`) }"
          @click="closeMobileMenu"
        >
          <component :is="getIcon(route.meta?.icon)" class="h-[18px] w-[18px] shrink-0" />
          <span class="text-[13px]">{{ route.meta?.title }}</span>
        </router-link>
      </nav>
    </aside>
  </Transition>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useRoute } from 'vue-router'
import { useLayoutStore } from '@/stores/layout'
import { Building2 } from 'lucide-vue-next'
import {
  LayoutDashboard,
  Users,
  Briefcase,
  Building,
  UserCheck,
} from 'lucide-vue-next'

const route = useRoute()
const layoutStore = useLayoutStore()
const { mobileMenuOpen } = storeToRefs(layoutStore)
const { closeMobileMenu } = layoutStore

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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform 0.2s ease;
}

.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
}

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
</style>