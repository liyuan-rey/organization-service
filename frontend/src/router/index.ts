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
      {
        path: 'positions',
        name: 'Positions',
        component: () => import('@/views/PositionList.vue'),
        meta: { title: '岗位管理', icon: 'briefcase' },
      },
      {
        path: 'department-positions',
        name: 'DepartmentPositions',
        component: () => import('@/views/DepartmentPositionList.vue'),
        meta: { title: '部门岗位管理', icon: 'building-office' },
      },
      {
        path: 'personnel-positions',
        name: 'PersonnelPositions',
        component: () => import('@/views/PersonnelPositionList.vue'),
        meta: { title: '人员岗位管理', icon: 'users-briefcase' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
