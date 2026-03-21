<template>
  <div class="min-h-screen bg-background">
    <!-- Desktop Sidebar -->
    <Sidebar class="hidden lg:block" />

    <!-- Mobile Sidebar (Drawer) -->
    <MobileSidebar />

    <!-- Main Content Area -->
    <div
      class="flex flex-col min-h-screen transition-all duration-300 ease-in-out"
      :style="{
        marginLeft: isMobile ? '0' : (collapsed ? 'var(--sidebar-collapsed-width)' : 'var(--sidebar-width)')
      }"
    >
      <!-- Top Toolbar -->
      <TopToolbar />

      <!-- Page Content -->
      <main class="flex-1 p-4 lg:p-6 overflow-auto bg-muted/30">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useLayoutStore } from '@/stores/layout'
import { useThemeStore } from '@/stores/theme'
import Sidebar from './components/Sidebar.vue'
import MobileSidebar from './components/MobileSidebar.vue'
import TopToolbar from './components/TopToolbar.vue'

const layoutStore = useLayoutStore()
const themeStore = useThemeStore()
const { collapsed } = storeToRefs(layoutStore)

const windowWidth = ref(window.innerWidth)
const isMobile = computed(() => windowWidth.value < 1024)

const handleResize = () => {
  windowWidth.value = window.innerWidth
}

onMounted(() => {
  layoutStore.init()
  themeStore.initTheme()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* Page transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>