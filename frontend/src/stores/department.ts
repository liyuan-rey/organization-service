import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as departmentApi from '@/api/department'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'

export const useDepartmentStore = defineStore('department', () => {
  const departments = ref<Department[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const departmentMap = computed(() => {
    const map = new Map<string, Department>()
    departments.value.forEach((dept) => map.set(dept.id, dept))
    return map
  })

  const treeDepartments = computed(() => {
    const tree: Department[] = []
    const childrenMap = new Map<string, Department[]>()

    departments.value.forEach((dept) => {
      const parentId = dept.parentId || 'root'
      if (!childrenMap.has(parentId)) {
        childrenMap.set(parentId, [])
      }
      childrenMap.get(parentId)!.push(dept)
    })

    const buildTree = (parentId: string | null): Department[] => {
      const key = parentId || 'root'
      const children = childrenMap.get(key) || []
      return children.map((child) => ({
        ...child,
        children: buildTree(child.id),
      }))
    }

    return buildTree(null)
  })

  async function fetchDepartments() {
    loading.value = true
    error.value = null
    try {
      const response = await departmentApi.getAllDepartments()
      departments.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取部门失败'
    } finally {
      loading.value = false
    }
  }

  async function createDepartment(data: DepartmentCreateReq) {
    const response = await departmentApi.createDepartment(data)
    await fetchDepartments()
    return response.data.data
  }

  async function updateDepartment(id: string, data: DepartmentUpdateReq) {
    const response = await departmentApi.updateDepartment(id, data)
    await fetchDepartments()
    return response.data.data
  }

  async function deleteDepartment(id: string) {
    await departmentApi.deleteDepartment(id)
    await fetchDepartments()
  }

  function getDepartmentById(id: string) {
    return departmentMap.value.get(id) || null
  }

  return {
    departments,
    loading,
    error,
    departmentMap,
    treeDepartments,
    fetchDepartments,
    createDepartment,
    updateDepartment,
    deleteDepartment,
    getDepartmentById,
  }
})
