import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  const collapsed = ref(false)
  const mobileMenuOpen = ref(false)

  // 初始化布局状态
  function init() {
    const stored = localStorage.getItem('sidebar-collapsed')
    if (stored !== null) {
      collapsed.value = stored === 'true'
    }
  }

  // 切换折叠状态
  function toggleCollapsed() {
    collapsed.value = !collapsed.value
    localStorage.setItem('sidebar-collapsed', String(collapsed.value))
  }

  // 设置折叠状态
  function setCollapsed(value: boolean) {
    collapsed.value = value
    localStorage.setItem('sidebar-collapsed', String(value))
  }

  // 切换移动端菜单
  function toggleMobileMenu() {
    mobileMenuOpen.value = !mobileMenuOpen.value
  }

  // 关闭移动端菜单
  function closeMobileMenu() {
    mobileMenuOpen.value = false
  }

  return {
    collapsed,
    mobileMenuOpen,
    init,
    toggleCollapsed,
    setCollapsed,
    toggleMobileMenu,
    closeMobileMenu,
  }
})