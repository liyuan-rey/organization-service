<template>
  <header
    class="sticky top-0 z-30 h-[var(--toolbar-height)]
           bg-background/95 backdrop-blur-sm
           flex items-center justify-between px-4"
  >
    <div class="flex items-center gap-3">
      <!-- Mobile Menu Toggle -->
      <button
        class="lg:hidden p-1.5 hover:bg-muted rounded transition-colors"
        @click="toggleMobileMenu"
      >
        <Menu class="h-[18px] w-[18px]" />
      </button>

      <!-- Breadcrumb -->
      <Breadcrumb />
    </div>

    <div class="flex items-center gap-0.5">
      <!-- Global Search Input -->
      <div class="hidden md:flex items-center">
        <div
          class="flex items-center h-8 w-[220px] px-3 rounded-md
                 bg-muted/30
                 hover:bg-muted/50 transition-colors cursor-pointer"
        >
          <Search class="h-[18px] w-[18px] text-muted-foreground shrink-0" />
          <span class="ml-2 text-[13px] text-muted-foreground">搜索...</span>
          <kbd class="ml-auto text-[11px] px-1.5 py-0.5 rounded bg-muted border border-border/50 font-mono text-muted-foreground">
            ⌘K
          </kbd>
        </div>
      </div>

      <!-- Mobile Search Button -->
      <el-tooltip content="搜索" placement="bottom" :show-after="300">
        <button
          class="md:hidden p-1.5 hover:bg-muted rounded transition-colors"
        >
          <Search class="h-[18px] w-[18px] text-muted-foreground" />
        </button>
      </el-tooltip>

      <!-- Notifications -->
      <el-tooltip content="通知" placement="bottom" :show-after="300">
        <button
          class="p-1.5 hover:bg-muted rounded transition-colors relative"
        >
          <Bell class="h-[18px] w-[18px] text-muted-foreground" />
          <span
            class="absolute top-1 right-1 h-1.5 w-1.5 bg-primary rounded-full"
          />
        </button>
      </el-tooltip>

      <!-- Theme Toggle -->
      <el-tooltip
        :content="isDark ? '切换到亮色模式' : '切换到暗色模式'"
        placement="bottom"
        :show-after="300"
      >
        <button
          class="p-1.5 hover:bg-muted rounded transition-colors"
          @click="toggleTheme"
        >
          <Sun v-if="isDark" class="h-[18px] w-[18px] text-muted-foreground" />
          <Moon v-else class="h-[18px] w-[18px] text-muted-foreground" />
        </button>
      </el-tooltip>

      <!-- Divider -->
      <div class="w-px h-4 bg-border mx-1.5" />

      <!-- User Dropdown -->
      <el-dropdown trigger="click">
        <div
          class="flex items-center gap-1.5 cursor-pointer px-1.5 py-1
                 hover:bg-muted rounded transition-colors"
        >
          <el-avatar :size="26" class="bg-primary text-[11px] font-medium">A</el-avatar>
          <span class="hidden sm:inline text-[13px] font-medium">管理员</span>
          <ChevronDown class="h-3.5 w-3.5 text-muted-foreground" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <div class="flex items-center gap-2">
                <User class="h-4 w-4" />
                <span>个人中心</span>
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <div class="flex items-center gap-2">
                <Settings class="h-4 w-4" />
                <span>设置</span>
              </div>
            </el-dropdown-item>
            <el-dropdown-item divided>
              <div class="flex items-center gap-2">
                <LogOut class="h-4 w-4" />
                <span>退出登录</span>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useThemeStore } from '@/stores/theme'
import { useLayoutStore } from '@/stores/layout'
import {
  Menu,
  Search,
  Bell,
  Sun,
  Moon,
  ChevronDown,
  User,
  Settings,
  LogOut,
} from 'lucide-vue-next'
import Breadcrumb from './Breadcrumb.vue'

const themeStore = useThemeStore()
const layoutStore = useLayoutStore()
const { isDark } = storeToRefs(themeStore)
const { toggleTheme } = themeStore
const { toggleMobileMenu } = layoutStore
</script>