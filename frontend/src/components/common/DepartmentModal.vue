<template>
  <el-dialog
    :model-value="show"
    :title="department ? '编辑部门' : '新建部门'"
    width="500"
    @update:model-value="$emit('close')"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      label-position="left"
    >
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入部门名称" />
      </el-form-item>

      <el-form-item label="部门编码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入部门编码" />
      </el-form-item>

      <el-form-item label="上级部门" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          :data="departmentTreeData"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择上级部门"
          clearable
          check-strictly
          :render-after-expand="false"
        />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入部门描述"
        />
      </el-form-item>

      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        保存
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import { useDepartmentStore } from '@/stores/department'

interface Props {
  show: boolean
  department?: Department | null
}

const props = withDefaults(defineProps<Props>(), { department: null })
const emit = defineEmits<{
  close: []
  save: [data: DepartmentCreateReq | DepartmentUpdateReq]
}>()

const departmentStore = useDepartmentStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = ref({
  name: '',
  code: '',
  description: '',
  parentId: null as string | null,
  sortOrder: 0,
  status: 1,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
}

// 部门树形数据
const departmentTreeData = computed(() => {
  const excludeId = props.department?.id
  const departments = departmentStore.departments.filter((d) => d.id !== excludeId)

  // 构建树形结构
  const buildTree = (items: typeof departments, parentId: string | null = null): any[] => {
    return items
      .filter((item) => item.parentId === parentId)
      .map((item) => ({
        ...item,
        children: buildTree(items, item.id),
      }))
  }

  return buildTree(departments)
})

watch(
  () => props.department,
  (dept) => {
    if (dept) {
      formData.value = {
        name: dept.name,
        code: dept.code,
        description: dept.description || '',
        parentId: dept.parentId || null,
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

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    emit('save', {
      name: formData.value.name,
      code: formData.value.code,
      description: formData.value.description || undefined,
      parentId: formData.value.parentId || null,
      sortOrder: formData.value.sortOrder || 0,
      status: formData.value.status || 1,
    })
  } finally {
    submitting.value = false
  }
}
</script>