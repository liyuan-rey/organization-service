<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4">
      <!-- 标题 -->
      <div class="flex justify-between items-center px-6 py-4 border-b">
        <h3 class="text-lg font-semibold text-gray-900">
          {{ item ? '编辑任职' : '新增任职' }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- 表单 -->
      <div class="px-6 py-4 max-h-[70vh] overflow-y-auto">
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              人员 <span class="text-red-500">*</span>
            </label>
            <select
              v-model="formData.personnelId"
              required
              :disabled="!!item"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
            >
              <option value="">请选择人员</option>
              <option v-for="opt in personnelOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              岗位 <span class="text-red-500">*</span>
            </label>
            <select
              v-model="formData.positionId"
              required
              :disabled="!!item"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
            >
              <option value="">请选择岗位</option>
              <option v-for="opt in positionOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">部门</label>
            <select
              v-model="formData.departmentId"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">请选择部门</option>
              <option v-for="opt in departmentOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">是否主岗</label>
            <div class="flex items-center space-x-4">
              <label class="flex items-center">
                <input
                  v-model="formData.isPrimary"
                  type="radio"
                  :value="true"
                  class="text-blue-600 focus:ring-blue-500"
                />
                <span class="ml-2 text-sm text-gray-700">主岗</span>
              </label>
              <label class="flex items-center">
                <input
                  v-model="formData.isPrimary"
                  type="radio"
                  :value="false"
                  class="text-blue-600 focus:ring-blue-500"
                />
                <span class="ml-2 text-sm text-gray-700">兼岗</span>
              </label>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">开始日期</label>
              <input
                v-model="formData.startDate"
                type="date"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
              <input
                v-model="formData.endDate"
                type="date"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
            <div class="flex items-center space-x-4">
              <label class="flex items-center">
                <input
                  v-model.number="formData.status"
                  type="radio"
                  :value="1"
                  class="text-blue-600 focus:ring-blue-500"
                />
                <span class="ml-2 text-sm text-gray-700">在职</span>
              </label>
              <label class="flex items-center">
                <input
                  v-model.number="formData.status"
                  type="radio"
                  :value="0"
                  class="text-blue-600 focus:ring-blue-500"
                />
                <span class="ml-2 text-sm text-gray-700">离任</span>
              </label>
            </div>
          </div>
        </form>
      </div>

      <!-- 底部按钮 -->
      <div class="flex justify-end space-x-3 px-6 py-4 border-t bg-gray-50 rounded-b-lg">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          取消
        </button>
        <button
          @click="handleSubmit"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          保存
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'

interface Option {
  label: string
  value: string
}

const props = defineProps<{
  show: boolean
  item?: PersonnelPosition | null
  personnelOptions: Option[]
  positionOptions: Option[]
  departmentOptions: Option[]
}>()

const emit = defineEmits<{
  (e: 'save', data: PersonnelPositionReq): void
  (e: 'close'): void
}>()

const formData = ref<PersonnelPositionReq>({
  personnelId: '',
  positionId: '',
  departmentId: null,
  isPrimary: true,
  startDate: null,
  endDate: null,
  status: 1,
})

watch(
  () => props.item,
  (item) => {
    if (item) {
      formData.value = {
        personnelId: item.personnelId,
        positionId: item.positionId,
        departmentId: item.departmentId,
        isPrimary: item.isPrimary,
        startDate: item.startDate,
        endDate: item.endDate,
        status: item.status,
      }
    } else {
      formData.value = {
        personnelId: '',
        positionId: '',
        departmentId: null,
        isPrimary: true,
        startDate: new Date().toISOString().split('T')[0],
        endDate: null,
        status: 1,
      }
    }
  },
  { immediate: true }
)

function handleSubmit() {
  emit('save', formData.value)
}
</script>
