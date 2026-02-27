import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as deptPosApi from '@/api/departmentPosition'
import type { DepartmentPosition, DepartmentPositionReq } from '@/types'

export const useDepartmentPositionStore = defineStore('departmentPosition', () => {
  const departmentPositions = ref<DepartmentPosition[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAllDepartmentPositions() {
    loading.value = true
    error.value = null
    try {
      const response = await deptPosApi.getAllDepartmentPositions()
      departmentPositions.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取部门岗位关联失败'
    } finally {
      loading.value = false
    }
  }

  async function fetchPositionsByDepartmentId(departmentId: string) {
    loading.value = true
    error.value = null
    try {
      const response = await deptPosApi.getPositionsByDepartmentId(departmentId)
      departmentPositions.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取部门岗位列表失败'
    } finally {
      loading.value = false
    }
  }

  async function createDepartmentPosition(data: DepartmentPositionReq) {
    const response = await deptPosApi.createDepartmentPosition(data)
    await fetchAllDepartmentPositions()
    return response.data.data
  }

  async function deleteDepartmentPosition(departmentId: string, positionId: string) {
    await deptPosApi.deleteDepartmentPosition(departmentId, positionId)
    await fetchAllDepartmentPositions()
  }

  function getPositionsByDepartment(departmentId: string) {
    return departmentPositions.value.filter((dp) => dp.departmentId === departmentId)
  }

  return {
    departmentPositions,
    loading,
    error,
    fetchAllDepartmentPositions,
    fetchPositionsByDepartmentId,
    createDepartmentPosition,
    deleteDepartmentPosition,
    getPositionsByDepartment,
  }
})
