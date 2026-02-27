import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as personPosApi from '@/api/personnelPosition'
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'

export const usePersonnelPositionStore = defineStore('personnelPosition', () => {
  const personnelPositions = ref<PersonnelPosition[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAllPersonnelPositions() {
    loading.value = true
    error.value = null
    try {
      const response = await personPosApi.getAllPersonnelPositions()
      personnelPositions.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取人员岗位关联失败'
    } finally {
      loading.value = false
    }
  }

  async function fetchPositionsByPersonnelId(personnelId: string) {
    loading.value = true
    error.value = null
    try {
      const response = await personPosApi.getPositionsByPersonnelId(personnelId)
      personnelPositions.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取人员岗位列表失败'
    } finally {
      loading.value = false
    }
  }

  async function createPersonnelPosition(data: PersonnelPositionReq) {
    const response = await personPosApi.createPersonnelPosition(data)
    await fetchAllPersonnelPositions()
    return response.data.data
  }

  async function updatePersonnelPosition(id: string, data: PersonnelPositionReq) {
    const response = await personPosApi.updatePersonnelPosition(id, data)
    await fetchAllPersonnelPositions()
    return response.data.data
  }

  async function deletePersonnelPosition(id: string) {
    await personPosApi.deletePersonnelPosition(id)
    await fetchAllPersonnelPositions()
  }

  function getPositionsByPersonnel(personnelId: string) {
    return personnelPositions.value.filter((pp) => pp.personnelId === personnelId)
  }

  function getPersonnelByPosition(positionId: string) {
    return personnelPositions.value.filter((pp) => pp.positionId === positionId)
  }

  return {
    personnelPositions,
    loading,
    error,
    fetchAllPersonnelPositions,
    fetchPositionsByPersonnelId,
    createPersonnelPosition,
    updatePersonnelPosition,
    deletePersonnelPosition,
    getPositionsByPersonnel,
    getPersonnelByPosition,
  }
})
