<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- 背景遮罩 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="$emit('close')"></div>

    <!-- 模态框 -->
    <div class="flex min-h-full items-center justify-center p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full">
        <!-- 标题 -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800">
            {{ department ? '编辑部门' : '新建部门' }}
          </h3>
          <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单 -->
        <form @submit.prevent="handleSubmit" class="px-6 py-4 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">部门名称</label>
            <input
              v-model="formData.name"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入部门名称"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">部门编码</label>
            <input
              v-model="formData.code"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入部门编码"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">上级部门</label>
            <select
              v-model="formData.parentId"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option :value="null">无（作为顶级部门）</option>
              <option v-for="dept in departmentOptions" :key="dept.value" :value="dept.value">
                {{ dept.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea
              v-model="formData.description"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入部门描述"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">排序</label>
            <input
              v-model.number="formData.sortOrder"
              type="number"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="数字越小越靠前"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
            <select
              v-model.number="formData.status"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </form>

        <!-- 按钮 -->
        <div class="flex justify-end space-x-3 px-6 py-4 border-t border-gray-200">
          <button
            type="button"
            @click="$emit('close')"
            class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
          >
            取消
          </button>
          <button
            type="submit"
            @click="handleSubmit"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import { useDepartmentStore } from '@/stores/department'

interface Props {
  show: boolean
  department?: Department | null
}

const props = withDefaults(defineProps<Props>(), {
  department: null,
})

const emit = defineEmits<{
  close: []
  save: [data: DepartmentCreateReq | DepartmentUpdateReq]
}>()

const departmentStore = useDepartmentStore()

const formData = ref<DepartmentCreateReq & { parentId?: string | null }>({
  name: '',
  code: '',
  description: '',
  parentId: null,
  sortOrder: 0,
  status: 1,
})

const departmentOptions = computed(() => {
  // 排除当前编辑的部门，避免选择自己作为上级
  const excludeId = props.department?.id
  return departmentStore.departments
    .filter((dept) => dept.id !== excludeId)
    .map((dept) => ({
      label: dept.name,
      value: dept.id,
    }))
})

watch(
  () => props.department,
  (dept) => {
    if (dept) {
      formData.value = {
        name: dept.name,
        code: dept.code,
        description: dept.description || '',
        parentId: dept.parentId,
        sortOrder: dept.sortOrder,
        status: dept.status,
      }
    } else {
      formData.value = {
        name: '',
        code: '',
        description: '',
        parentId: null,
        sortOrder: 0,
        status: 1,
      }
    }
  },
  { immediate: true }
)

function handleSubmit() {
  emit('save', {
    name: formData.value.name,
    code: formData.value.code,
    description: formData.value.description,
    parentId: formData.value.parentId || null,
    sortOrder: formData.value.sortOrder || 0,
    status: formData.value.status || 1,
  })
}
</script>
