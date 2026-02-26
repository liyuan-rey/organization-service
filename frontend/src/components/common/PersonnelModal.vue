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
            {{ personnel ? '编辑人员' : '新建人员' }}
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
            <label class="block text-sm font-medium text-gray-700 mb-1">姓名</label>
            <input
              v-model="formData.name"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入姓名"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">工号</label>
            <input
              v-model="formData.code"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入工号"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">所属部门</label>
            <select
              v-model="formData.departmentId"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="" disabled>请选择部门</option>
              <option v-for="dept in departmentOptions" :key="dept.value" :value="dept.value">
                {{ dept.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">职位</label>
            <input
              v-model="formData.position"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入职位"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
            <input
              v-model="formData.email"
              type="email"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入邮箱"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">电话</label>
            <input
              v-model="formData.phone"
              type="tel"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="请输入电话"
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
import { ref, watch } from 'vue'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'

interface DepartmentOption {
  label: string
  value: string
}

interface Props {
  show: boolean
  personnel?: Personnel | null
  departmentOptions: DepartmentOption[]
}

const props = withDefaults(defineProps<Props>(), {
  personnel: null,
  departmentOptions: () => [],
})

const emit = defineEmits<{
  close: []
  save: [data: PersonnelCreateReq | PersonnelUpdateReq]
}>()

const formData = ref<PersonnelCreateReq>({
  name: '',
  code: '',
  email: '',
  phone: '',
  departmentId: '',
  position: '',
  status: 1,
})

watch(
  () => props.personnel,
  (person) => {
    if (person) {
      formData.value = {
        name: person.name,
        code: person.code,
        email: person.email || '',
        phone: person.phone || '',
        departmentId: person.departmentId,
        position: person.position || '',
        status: person.status,
      }
    } else {
      formData.value = {
        name: '',
        code: '',
        email: '',
        phone: '',
        departmentId: '',
        position: '',
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
    email: formData.value.email,
    phone: formData.value.phone,
    departmentId: formData.value.departmentId,
    position: formData.value.position,
    status: formData.value.status || 1,
  })
}
</script>
