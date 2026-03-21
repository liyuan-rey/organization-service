import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import TopBarLayout from '@/layouts/TopBarLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: TopBarLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('@/views/DepartmentList.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'personnel',
        name: 'Personnel',
        component: () => import('@/views/PersonnelList.vue'),
        meta: { title: '人员管理' },
      },
      {
        path: 'positions',
        name: 'Positions',
        component: () => import('@/views/PositionList.vue'),
        meta: { title: '岗位管理' },
      },
      {
        path: 'department-positions',
        name: 'DepartmentPositions',
        component: () => import('@/views/DepartmentPositionList.vue'),
        meta: { title: '部门岗位管理' },
      },
      {
        path: 'personnel-positions',
        name: 'PersonnelPositions',
        component: () => import('@/views/PersonnelPositionList.vue'),
        meta: { title: '人员岗位管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
