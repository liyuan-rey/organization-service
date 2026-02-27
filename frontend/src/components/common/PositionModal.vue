<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4">
      <!-- 标题 -->
      <div class="flex justify-between items-center px-6 py-4 border-b">
        <h3 class="text-lg font-semibold text-gray-900">
          {{ position ? '编辑岗位' : '新建岗位' }}
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
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                岗位名称 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="请输入岗位名称"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                岗位编码 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.code"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="请输入岗位编码"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">岗位描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入岗位描述"
            ></textarea>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">职级</label>
              <input
                v-model="formData.jobLevel"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="如：P2, M1"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">岗位类别</label>
              <select
                v-model="formData.jobCategory"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">请选择</option>
                <option value="Technical">技术类</option>
                <option value="Management">管理类</option>
                <option value="Sales">销售类</option>
                <option value="HR">人力资源类</option>
                <option value="Finance">财务类</option>
                <option value="Operations">运营类</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">最低薪资</label>
              <input
                v-model.number="formData.minSalary"
                type="number"
                step="0.01"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="0"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">最高薪资</label>
              <input
                v-model.number="formData.maxSalary"
                type="number"
                step="0.01"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="0"
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
                <span class="ml-2 text-sm text-gray-700">启用</span>
              </label>
              <label class="flex items-center">
                <input
                  v-model.number="formData.status"
                  type="radio"
                  :value="0"
                  class="text-blue-600 focus:ring-blue-500"
                />
                <span class="ml-2 text-sm text-gray-700">禁用</span>
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
import { ref, watch, onMounted } from 'vue'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'

const props = defineProps<{
  show: boolean
  position?: Position | null
}>()

const emit = defineEmits<{
  (e: 'save', data: PositionCreateReq | PositionUpdateReq): void
  (e: 'close'): void
}>()

const formData = ref<PositionCreateReq>({
  name: '',
  code: '',
  description: '',
  jobLevel: '',
  jobCategory: '',
  minSalary: null,
  maxSalary: null,
  status: 1,
})

watch(
  () => props.position,
  (pos) => {
    if (pos) {
      formData.value = {
        name: pos.name,
        code: pos.code,
        description: pos.description,
        jobLevel: pos.jobLevel,
        jobCategory: pos.jobCategory,
        minSalary: pos.minSalary,
        maxSalary: pos.maxSalary,
        status: pos.status,
      }
    } else {
      formData.value = {
        name: '',
        code: '',
        description: '',
        jobLevel: '',
        jobCategory: '',
        minSalary: null,
        maxSalary: null,
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
