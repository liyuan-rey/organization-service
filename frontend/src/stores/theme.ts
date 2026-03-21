import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(false)

  // 初始化主题
  function initTheme() {
    // 检查本地存储
    const stored = localStorage.getItem('theme')
    if (stored) {
      isDark.value = stored === 'dark'
    } else {
      // 跟随系统
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    applyTheme()
  }

  // 应用主题
  function applyTheme() {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 切换主题
  function toggleTheme() {
    isDark.value = !isDark.value
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
    applyTheme()
  }

  // 设置主题
  function setTheme(dark: boolean) {
    isDark.value = dark
    localStorage.setItem('theme', dark ? 'dark' : 'light')
    applyTheme()
  }

  // 监听变化
  watch(isDark, () => {
    applyTheme()
  })

  return {
    isDark,
    initTheme,
    toggleTheme,
    setTheme,
  }
})