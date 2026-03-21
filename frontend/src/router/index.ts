import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'LayoutDashboard' },
      },
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('@/views/DepartmentList.vue'),
        meta: { title: '部门管理', icon: 'Building2' },
      },
      {
        path: 'personnel',
        name: 'Personnel',
        component: () => import('@/views/PersonnelList.vue'),
        meta: { title: '人员管理', icon: 'Users' },
      },
      {
        path: 'positions',
        name: 'Positions',
        component: () => import('@/views/PositionList.vue'),
        meta: { title: '岗位管理', icon: 'Briefcase' },
      },
      {
        path: 'department-positions',
        name: 'DepartmentPositions',
        component: () => import('@/views/DepartmentPositionList.vue'),
        meta: { title: '部门岗位', icon: 'Building' },
      },
      {
        path: 'personnel-positions',
        name: 'PersonnelPositions',
        component: () => import('@/views/PersonnelPositionList.vue'),
        meta: { title: '人员岗位', icon: 'UserCheck' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
