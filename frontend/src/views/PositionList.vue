<template>
  <div>
    <!-- 页面标题和操作按钮 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">岗位管理</h2>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>新建岗位</span>
      </button>
    </div>

    <!-- 岗位列表卡片 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                岗位名称
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                岗位编码
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                职级
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                岗位类别
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                薪资范围
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                状态
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="store.loading">
              <td colspan="7" class="px-6 py-8 text-center text-gray-500">
                <div class="flex justify-center items-center space-x-2">
                  <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" />
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  <span>加载中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="store.positions.length === 0">
              <td colspan="7" class="px-6 py-8 text-center text-gray-500">
                暂无岗位数据
              </td>
            </tr>
            <tr v-for="pos in store.positions" :key="pos.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ pos.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ pos.code }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ pos.jobLevel || '-' }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ pos.jobCategory || '-' }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">
                  <span v-if="pos.minSalary || pos.maxSalary">
                    ¥{{ formatSalary(pos.minSalary) }} - ¥{{ formatSalary(pos.maxSalary) }}
                  </span>
                  <span v-else>-</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    pos.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800',
                  ]"
                >
                  {{ pos.status === 1 ? '启用' : '禁用' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="editPosition(pos)" class="text-blue-600 hover:text-blue-900 mr-3">
                  编辑
                </button>
                <button @click="deletePosition(pos)" class="text-red-600 hover:text-red-900">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新建/编辑岗位模态框 -->
    <PositionModal
      v-model:show="showCreateModal"
      :position="editingPosition"
      @save="handleSave"
      @close="handleClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usePositionStore } from '@/stores/position'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'
import PositionModal from '@/components/common/PositionModal.vue'

const store = usePositionStore()
const showCreateModal = ref(false)
const editingPosition = ref<Position | null>(null)

onMounted(() => {
  store.fetchPositions()
})

function formatSalary(salary: number | null) {
  if (!salary) return '0'
  return salary.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
}

function editPosition(pos: Position) {
  editingPosition.value = pos
  showCreateModal.value = true
}

function deletePosition(pos: Position) {
  if (confirm(`确定要删除岗位 "${pos.name}" 吗？`)) {
    store.deletePosition(pos.id)
  }
}

function handleSave(data: PositionCreateReq | PositionUpdateReq) {
  if (editingPosition.value) {
    store.updatePosition(editingPosition.value.id, data as PositionUpdateReq)
  } else {
    store.createPosition(data as PositionCreateReq)
  }
  handleClose()
}

function handleClose() {
  showCreateModal.value = false
  editingPosition.value = null
}
</script>
