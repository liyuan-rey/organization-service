<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航栏 -->
    <header class="bg-white shadow-sm border-b border-gray-200">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center space-x-4">
          <button
            @click="sidebarOpen = !sidebarOpen"
            class="lg:hidden p-2 rounded-md hover:bg-gray-100 transition-colors"
          >
            <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <h1 class="text-xl font-semibold text-gray-800">组织管理系统</h1>
        </div>
        <div class="flex items-center space-x-4">
          <span class="text-sm text-gray-500">管理员</span>
          <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white font-medium">
            A
          </div>
        </div>
      </div>
    </header>

    <div class="flex">
      <!-- 侧边栏 -->
      <aside
        :class="[
          'fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0',
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
                ? 'bg-blue-50 text-blue-600'
                : 'text-gray-600 hover:bg-gray-50'
            "
          >
            <svg
              v-if="route.meta?.icon === 'building'"
              class="w-5 h-5 mr-3"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
              />
            </svg>
            <svg
              v-else-if="route.meta?.icon === 'users'"
              class="w-5 h-5 mr-3"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
              />
            </svg>
            <span class="font-medium">{{ route.meta?.title }}</span>
          </router-link>
        </nav>
      </aside>

      <!-- 侧边栏遮罩 -->
      <div
        v-if="sidebarOpen"
        @click="sidebarOpen = false"
        class="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
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
import type { RouteRecordRaw } from 'vue-router'

const sidebarOpen = ref(false)

const routes = [
  { path: '/departments', meta: { title: '部门管理', icon: 'building' } },
  { path: '/personnel', meta: { title: '人员管理', icon: 'users' } },
  { path: '/positions', meta: { title: '岗位管理', icon: 'briefcase' } },
  { path: '/department-positions', meta: { title: '部门岗位管理', icon: 'building-office' } },
  { path: '/personnel-positions', meta: { title: '人员岗位管理', icon: 'users-briefcase' } },
]
</script>
