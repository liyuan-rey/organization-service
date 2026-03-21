<template>
  <el-dialog
    :model-value="show"
    title="配置部门岗位"
    width="500"
    @update:model-value="$emit('close')"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="left"
    >
      <el-form-item label="部门" prop="departmentId">
        <el-tree-select
          v-model="formData.departmentId"
          :data="departmentTreeData"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择部门"
          check-strictly
          :render-after-expand="false"
        />
      </el-form-item>

      <el-form-item label="岗位" prop="positionId">
        <el-select v-model="formData.positionId" placeholder="请选择岗位" filterable>
          <el-option
            v-for="p in positionStore.positions"
            :key="p.id"
            :label="p.name"
            :value="p.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="岗位类型" prop="isPrimary">
        <el-radio-group v-model="formData.isPrimary">
          <el-radio :value="true">主岗</el-radio>
          <el-radio :value="false">普通岗</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
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
import { ref, computed, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { DepartmentPositionReq } from '@/types'
import { useDepartmentStore } from '@/stores/department'
import { usePositionStore } from '@/stores/position'

interface Props {
  show: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  close: []
  save: [data: DepartmentPositionReq]
}>()

const departmentStore = useDepartmentStore()
const positionStore = usePositionStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = ref<DepartmentPositionReq>({
  departmentId: '',
  positionId: '',
  isPrimary: false,
  sortOrder: 0,
})

const rules: FormRules = {
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  positionId: [{ required: true, message: '请选择岗位', trigger: 'change' }],
}

// 部门树形数据
const departmentTreeData = computed(() => {
  const departments = departmentStore.departments

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
  () => props.show,
  (show) => {
    if (show) {
      formData.value = {
        departmentId: '',
        positionId: '',
        isPrimary: false,
        sortOrder: 0,
      }
    }
  }
)

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    emit('save', {
      departmentId: formData.value.departmentId,
      positionId: formData.value.positionId,
      isPrimary: formData.value.isPrimary || false,
      sortOrder: formData.value.sortOrder || 0,
    })
  } finally {
    submitting.value = false
  }
}
</script>