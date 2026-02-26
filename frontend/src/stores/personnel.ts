import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as personnelApi from '@/api/personnel'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'

export const usePersonnelStore = defineStore('personnel', () => {
  const personnelList = ref<Personnel[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchPersonnel() {
    loading.value = true
    error.value = null
    try {
      const response = await personnelApi.getAllPersonnel()
      personnelList.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取人员列表失败'
    } finally {
      loading.value = false
    }
  }

  async function createPersonnel(data: PersonnelCreateReq) {
    const response = await personnelApi.createPersonnel(data)
    await fetchPersonnel()
    return response.data.data
  }

  async function updatePersonnel(id: string, data: PersonnelUpdateReq) {
    const response = await personnelApi.updatePersonnel(id, data)
    await fetchPersonnel()
    return response.data.data
  }

  async function deletePersonnel(id: string) {
    await personnelApi.deletePersonnel(id)
    await fetchPersonnel()
  }

  function getPersonnelById(id: string) {
    return personnelList.value.find((p) => p.id === id) || null
  }

  function getPersonnelByDepartment(departmentId: string) {
    return personnelList.value.filter((p) => p.departmentId === departmentId)
  }

  return {
    personnelList,
    loading,
    error,
    fetchPersonnel,
    createPersonnel,
    updatePersonnel,
    deletePersonnel,
    getPersonnelById,
    getPersonnelByDepartment,
  }
})
