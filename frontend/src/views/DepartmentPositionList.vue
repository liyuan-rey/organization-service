<template>
  <div>
    <!-- 页面标题和操作按钮 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">部门岗位管理</h2>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>配置岗位</span>
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="bg-white rounded-lg shadow mb-6 p-4">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">部门</label>
          <select
            v-model="filterDepartmentId"
            @change="applyFilter"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">全部部门</option>
            <option v-for="d in departmentOptions" :key="d.value" :value="d.value">
              {{ d.label }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">岗位</label>
          <select
            v-model="filterPositionId"
            @change="applyFilter"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">全部岗位</option>
            <option v-for="p in positionOptions" :key="p.value" :value="p.value">
              {{ p.label }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- 部门岗位关联列表卡片 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                部门
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                岗位
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                是否主岗
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                排序
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="store.loading">
              <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                <div class="flex justify-center items-center space-x-2">
                  <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" />
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  <span>加载中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredData.length === 0">
              <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                暂无部门岗位关联数据
              </td>
            </tr>
            <tr v-for="item in filteredData" :key="item.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ item.departmentName }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ item.positionName }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    item.isPrimary ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800',
                  ]"
                >
                  {{ item.isPrimary ? '主岗' : '普通岗' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ item.sortOrder }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="deleteItem(item)" class="text-red-600 hover:text-red-900">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新建模态框 -->
    <DepartmentPositionModal
      v-model:show="showCreateModal"
      :department-options="departmentOptions"
      :position-options="positionOptions"
      @save="handleSave"
      @close="handleClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDepartmentPositionStore } from '@/stores/departmentPosition'
import { useDepartmentStore } from '@/stores/department'
import { usePositionStore } from '@/stores/position'
import type { DepartmentPosition, DepartmentPositionReq } from '@/types'
import DepartmentPositionModal from '@/components/common/DepartmentPositionModal.vue'

const store = useDepartmentPositionStore()
const departmentStore = useDepartmentStore()
const positionStore = usePositionStore()

const showCreateModal = ref(false)

const filterDepartmentId = ref('')
const filterPositionId = ref('')

const departmentOptions = computed(() => {
  return departmentStore.departments.map((d) => ({
    label: d.name,
    value: d.id,
  }))
})

const positionOptions = computed(() => {
  return positionStore.positions.map((p) => ({
    label: p.name,
    value: p.id,
  }))
})

const filteredData = computed(() => {
  let data = store.departmentPositions
  if (filterDepartmentId.value) {
    data = data.filter((item) => item.departmentId === filterDepartmentId.value)
  }
  if (filterPositionId.value) {
    data = data.filter((item) => item.positionId === filterPositionId.value)
  }
  return data
})

function applyFilter() {
  // 筛选逻辑在 filteredData 中处理
}

onMounted(() => {
  store.fetchAllDepartmentPositions()
  departmentStore.fetchDepartments()
  positionStore.fetchPositions()
})

function deleteItem(item: DepartmentPosition) {
  if (confirm(`确定要删除部门 "${item.departmentName}" 的岗位 "${item.positionName}" 配置吗？`)) {
    store.deleteDepartmentPosition(item.departmentId, item.positionId)
  }
}

function handleSave(data: DepartmentPositionReq) {
  store.createDepartmentPosition(data)
  handleClose()
}

function handleClose() {
  showCreateModal.value = false
}
</script>
