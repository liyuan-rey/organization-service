import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/departments',
    children: [
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('@/views/DepartmentList.vue'),
        meta: { title: '部门管理', icon: 'building' },
      },
      {
        path: 'personnel',
        name: 'Personnel',
        component: () => import('@/views/PersonnelList.vue'),
        meta: { title: '人员管理', icon: 'users' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
