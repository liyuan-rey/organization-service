<template>
  <div class="min-h-screen bg-muted/30">
    <!-- 顶部导航栏 -->
    <header class="bg-background shadow-sm border-b">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center space-x-4">
          <Button variant="ghost" size="icon" class="lg:hidden" @click="sidebarOpen = !sidebarOpen">
            <Menu class="h-6 w-6" />
          </Button>
          <h1 class="text-xl font-semibold">组织管理系统</h1>
        </div>
        <div class="flex items-center space-x-4">
          <span class="text-sm text-muted-foreground">管理员</span>
          <div class="w-8 h-8 bg-primary rounded-full flex items-center justify-center text-primary-foreground font-medium">
            A
          </div>
        </div>
      </div>
    </header>

    <div class="flex">
      <!-- 侧边栏 -->
      <aside
        :class="[
          'fixed inset-y-0 left-0 z-50 w-64 bg-background shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full',
        ]"
      >
        <nav class="mt-6 px-4">
          <router-link
            v-for="route in routes"
            :key="route.path"
            :to="route.path"
            class="flex items-center px-4 py-3 mb-2 rounded-lg transition-colors"
            :class="
              $route.path === route.path
                ? 'bg-primary/10 text-primary'
                : 'text-muted-foreground hover:bg-muted'
            "
          >
            <component :is="route.meta?.icon" class="w-5 h-5 mr-3" />
            <span class="font-medium">{{ route.meta?.title }}</span>
          </router-link>
        </nav>
      </aside>

      <!-- 侧边栏遮罩 -->
      <div
        v-if="sidebarOpen"
        @click="sidebarOpen = false"
        class="fixed inset-0 bg-black/50 z-40 lg:hidden"
      ></div>

      <!-- 主内容区 -->
      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Menu, Building2, Users, Briefcase, Building, UserCheck } from 'lucide-vue-next'

const sidebarOpen = ref(false)

const routes = [
  { path: '/departments', meta: { title: '部门管理', icon: Building2 } },
  { path: '/personnel', meta: { title: '人员管理', icon: Users } },
  { path: '/positions', meta: { title: '岗位管理', icon: Briefcase } },
  { path: '/department-positions', meta: { title: '部门岗位管理', icon: Building } },
  { path: '/personnel-positions', meta: { title: '人员岗位管理', icon: UserCheck } },
]
</script>