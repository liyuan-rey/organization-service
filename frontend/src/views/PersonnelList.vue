<template>
  <div>
    <!-- 页面标题和操作按钮 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">人员管理</h2>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>新建人员</span>
      </button>
    </div>

    <!-- 人员列表卡片 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                姓名
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                工号
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                邮箱
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                电话
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                所属部门
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                职位
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
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                <div class="flex justify-center items-center space-x-2">
                  <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" />
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  <span>加载中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="store.personnelList.length === 0">
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                暂无人员数据
              </td>
            </tr>
            <tr v-for="person in store.personnelList" :key="person.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-medium mr-3">
                    {{ person.name.charAt(0) }}
                  </div>
                  <div class="text-sm font-medium text-gray-900">{{ person.name }}</div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ person.code }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ person.email }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ person.phone }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ getDepartmentName(person.departmentId) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ person.position }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    person.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800',
                  ]"
                >
                  {{ person.status === 1 ? '启用' : '禁用' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="editPersonnel(person)" class="text-blue-600 hover:text-blue-900 mr-3">
                  编辑
                </button>
                <button @click="deletePersonnel(person)" class="text-red-600 hover:text-red-900">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新建/编辑人员模态框 -->
    <PersonnelModal
      v-model:show="showCreateModal"
      :personnel="editingPersonnel"
      :department-options="departmentOptions"
      @save="handleSave"
      @close="handleClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { usePersonnelStore } from '@/stores/personnel'
import { useDepartmentStore } from '@/stores/department'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'
import PersonnelModal from '@/components/common/PersonnelModal.vue'

const personnelStore = usePersonnelStore()
const departmentStore = useDepartmentStore()
const showCreateModal = ref(false)
const editingPersonnel = ref<Personnel | null>(null)

const store = computed(() => personnelStore)
const departmentOptions = computed(() => {
  return departmentStore.departments.map((dept) => ({
    label: dept.name,
    value: dept.id,
  }))
})

onMounted(() => {
  personnelStore.fetchPersonnel()
  departmentStore.fetchDepartments()
})

function getDepartmentName(departmentId: string) {
  const dept = departmentStore.getDepartmentById(departmentId)
  return dept?.name || '-'
}

function editPersonnel(person: Personnel) {
  editingPersonnel.value = person
  showCreateModal.value = true
}

function deletePersonnel(person: Personnel) {
  if (confirm(`确定要删除人员 "${person.name}" 吗？`)) {
    personnelStore.deletePersonnel(person.id)
  }
}

function handleSave(data: PersonnelCreateReq | PersonnelUpdateReq) {
  if (editingPersonnel.value) {
    personnelStore.updatePersonnel(editingPersonnel.value.id, data as PersonnelUpdateReq)
  } else {
    personnelStore.createPersonnel(data as PersonnelCreateReq)
  }
  handleClose()
}

function handleClose() {
  showCreateModal.value = false
  editingPersonnel.value = null
}
</script>
