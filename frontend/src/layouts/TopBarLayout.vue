<template>
  <div class="min-h-screen bg-background">
    <!-- 顶部导航栏 -->
    <header class="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div class="container flex h-16 items-center justify-between px-4">
        <!-- Logo 和导航菜单 -->
        <div class="flex items-center gap-8">
          <!-- Logo -->
          <router-link to="/dashboard" class="flex items-center gap-2 font-semibold text-lg">
            <Building2 class="h-6 w-6 text-primary" />
            <span>组织管理系统</span>
          </router-link>

          <!-- 桌面端导航菜单 -->
          <nav class="hidden md:flex items-center gap-1">
            <router-link
              v-for="route in routes"
              :key="route.path"
              :to="route.path"
              class="px-4 py-2 text-sm font-medium rounded-md transition-colors"
              :class="
                isActive(route.path)
                  ? 'bg-primary/10 text-primary'
                  : 'text-muted-foreground hover:text-foreground hover:bg-muted'
              "
            >
              {{ route.meta?.title }}
            </router-link>
          </nav>
        </div>

        <!-- 右侧操作区 -->
        <div class="flex items-center gap-4">
          <!-- 主题切换 -->
          <el-tooltip :content="isDark ? '切换到亮色模式' : '切换到暗色模式'" placement="bottom">
            <el-button
              :icon="isDark ? Sun : Moon"
              circle
              @click="toggleTheme"
            />
          </el-tooltip>

          <!-- 用户头像 -->
          <el-dropdown trigger="click">
            <div class="flex items-center gap-2 cursor-pointer">
              <el-avatar :size="32" class="bg-primary">
                A
              </el-avatar>
              <span class="hidden sm:inline text-sm">管理员</span>
              <ChevronDown class="h-4 w-4 text-muted-foreground" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 移动端菜单按钮 -->
          <el-button
            class="md:hidden"
            :icon="mobileMenuOpen ? X : Menu"
            circle
            @click="mobileMenuOpen = !mobileMenuOpen"
          />
        </div>
      </div>

      <!-- 移动端导航菜单 -->
      <div
        v-show="mobileMenuOpen"
        class="md:hidden border-t bg-background"
      >
        <nav class="container px-4 py-3 space-y-1">
          <router-link
            v-for="route in routes"
            :key="route.path"
            :to="route.path"
            class="block px-4 py-2 text-sm font-medium rounded-md transition-colors"
            :class="
              isActive(route.path)
                ? 'bg-primary/10 text-primary'
                : 'text-muted-foreground hover:text-foreground hover:bg-muted'
            "
            @click="mobileMenuOpen = false"
          >
            {{ route.meta?.title }}
          </router-link>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="container px-4 py-6">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from 'pinia'
import { Building2, Moon, Sun, ChevronDown, Menu, X } from 'lucide-vue-next'

const route = useRoute()
const themeStore = useThemeStore()
const { isDark } = storeToRefs(themeStore)

const mobileMenuOpen = ref(false)

const routes = [
  { path: '/dashboard', meta: { title: '仪表盘' } },
  { path: '/departments', meta: { title: '部门管理' } },
  { path: '/personnel', meta: { title: '人员管理' } },
  { path: '/positions', meta: { title: '岗位管理' } },
  { path: '/department-positions', meta: { title: '部门岗位' } },
  { path: '/personnel-positions', meta: { title: '人员岗位' } },
]

const isActive = (path: string) => {
  return route.path === path
}

const toggleTheme = () => {
  themeStore.toggleTheme()
}

// 初始化主题
onMounted(() => {
  themeStore.initTheme()
})
</script>

<style scoped>
/* 页面切换过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>