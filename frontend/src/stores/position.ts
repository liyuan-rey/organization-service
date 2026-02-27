import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as positionApi from '@/api/position'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'

export const usePositionStore = defineStore('position', () => {
  const positions = ref<Position[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const positionMap = computed(() => {
    const map = new Map<string, Position>()
    positions.value.forEach((pos) => map.set(pos.id, pos))
    return map
  })

  async function fetchPositions() {
    loading.value = true
    error.value = null
    try {
      const response = await positionApi.getAllPositions()
      positions.value = response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取岗位列表失败'
    } finally {
      loading.value = false
    }
  }

  async function createPosition(data: PositionCreateReq) {
    const response = await positionApi.createPosition(data)
    await fetchPositions()
    return response.data.data
  }

  async function updatePosition(id: string, data: PositionUpdateReq) {
    const response = await positionApi.updatePosition(id, data)
    await fetchPositions()
    return response.data.data
  }

  async function deletePosition(id: string) {
    await positionApi.deletePosition(id)
    await fetchPositions()
  }

  function getPositionById(id: string) {
    return positionMap.value.get(id) || null
  }

  return {
    positions,
    loading,
    error,
    positionMap,
    fetchPositions,
    createPosition,
    updatePosition,
    deletePosition,
    getPositionById,
  }
})
