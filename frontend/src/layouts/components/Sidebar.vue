<template>
  <aside
    class="fixed left-0 top-0 h-full z-40 flex flex-col
           bg-[hsl(var(--sidebar-background))]
           transition-all duration-300 ease-in-out"
    :style="{ width: collapsed ? 'var(--sidebar-collapsed-width)' : 'var(--sidebar-width)' }"
  >
    <!-- Logo Area -->
    <div
      class="h-[var(--toolbar-height)] flex items-center px-4
             border-b border-[hsl(var(--sidebar-border))]
             shrink-0"
    >
      <div class="flex items-center gap-3 overflow-hidden">
        <div
          class="h-8 w-8 rounded-lg bg-primary/20 flex items-center justify-center shrink-0"
        >
          <Building2 class="h-5 w-5 text-primary" />
        </div>
        <Transition name="fade-slide">
          <span
            v-if="!collapsed"
            class="font-semibold text-[hsl(var(--sidebar-foreground))] whitespace-nowrap text-base"
          >
            组织管理系统
          </span>
        </Transition>
      </div>
    </div>

    <!-- Navigation Menu -->
    <SidebarMenu :collapsed="collapsed" />

    <!-- Collapse Toggle Button -->
    <div class="shrink-0 px-3 py-3">
      <button
        class="w-full h-9 flex items-center justify-center gap-2 rounded-md
               text-[hsl(var(--sidebar-foreground)/0.7)]
               hover:text-[hsl(var(--sidebar-foreground))]
               hover:bg-[hsl(var(--sidebar-accent))]
               transition-all duration-200"
        @click="toggleCollapsed"
      >
        <component
          :is="collapsed ? ChevronRight : ChevronLeft"
          class="h-4 w-4 shrink-0"
        />
        <Transition name="fade">
          <span v-if="!collapsed" class="text-xs">收起菜单</span>
        </Transition>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useLayoutStore } from '@/stores/layout'
import { Building2, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import SidebarMenu from './SidebarMenu.vue'

const layoutStore = useLayoutStore()
const { collapsed } = storeToRefs(layoutStore)
const { toggleCollapsed } = layoutStore
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

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>