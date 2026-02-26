<template>
  <div>
    <!-- 页面标题和操作按钮 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">部门管理</h2>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>新建部门</span>
      </button>
    </div>

    <!-- 部门列表卡片 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                部门名称
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                部门编码
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                描述
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                状态
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                创建时间
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="store.loading">
              <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                <div class="flex justify-center items-center space-x-2">
                  <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" />
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  <span>加载中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="store.departments.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                暂无部门数据
              </td>
            </tr>
            <tr v-for="dept in store.departments" :key="dept.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ dept.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ dept.code }}</div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-500 max-w-xs truncate">{{ dept.description }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    dept.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800',
                  ]"
                >
                  {{ dept.status === 1 ? '启用' : '禁用' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ formatDate(dept.createTime) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="editDepartment(dept)" class="text-blue-600 hover:text-blue-900 mr-3">
                  编辑
                </button>
                <button @click="deleteDepartment(dept)" class="text-red-600 hover:text-red-900">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新建/编辑部门模态框 -->
    <DepartmentModal
      v-model:show="showCreateModal"
      :department="editingDepartment"
      @save="handleSave"
      @close="handleClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useDepartmentStore } from '@/stores/department'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import DepartmentModal from '@/components/common/DepartmentModal.vue'

const store = useDepartmentStore()
const showCreateModal = ref(false)
const editingDepartment = ref<Department | null>(null)

const departmentOptions = computed(() => {
  return store.departments.map((dept) => ({
    label: dept.name,
    value: dept.id,
  }))
})

onMounted(() => {
  store.fetchDepartments()
})

function formatDate(dateString: string) {
  return new Date(dateString).toLocaleString('zh-CN')
}

function editDepartment(dept: Department) {
  editingDepartment.value = dept
  showCreateModal.value = true
}

function deleteDepartment(dept: Department) {
  if (confirm(`确定要删除部门 "${dept.name}" 吗？`)) {
    store.deleteDepartment(dept.id)
  }
}

function handleSave(data: DepartmentCreateReq | DepartmentUpdateReq) {
  if (editingDepartment.value) {
    store.updateDepartment(editingDepartment.value.id, data as DepartmentUpdateReq)
  } else {
    store.createDepartment(data as DepartmentCreateReq)
  }
  handleClose()
}

function handleClose() {
  showCreateModal.value = false
  editingDepartment.value = null
}
</script>
