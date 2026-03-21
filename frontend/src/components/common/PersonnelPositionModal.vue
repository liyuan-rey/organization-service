<template>
  <el-dialog
    :model-value="show"
    :title="item ? '编辑任职' : '新增任职'"
    width="550"
    @update:model-value="$emit('close')"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="left"
    >
      <el-form-item label="人员" prop="personnelId">
        <el-select
          v-model="formData.personnelId"
          placeholder="请选择人员"
          filterable
          :disabled="!!item"
        >
          <el-option
            v-for="p in personnelStore.personnel"
            :key="p.id"
            :label="p.name"
            :value="p.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="岗位" prop="positionId">
        <el-select
          v-model="formData.positionId"
          placeholder="请选择岗位"
          :disabled="!!item"
        >
          <el-option
            v-for="p in positionStore.positions"
            :key="p.id"
            :label="p.name"
            :value="p.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="部门" prop="departmentId">
        <el-tree-select
          v-model="formData.departmentId"
          :data="departmentTreeData"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择部门"
          clearable
          check-strictly
          :render-after-expand="false"
        />
      </el-form-item>

      <el-form-item label="岗位类型" prop="isPrimary">
        <el-radio-group v-model="formData.isPrimary">
          <el-radio :value="true">主岗</el-radio>
          <el-radio :value="false">兼岗</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker
              v-model="formData.startDate"
              type="date"
              placeholder="选择开始日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker
              v-model="formData.endDate"
              type="date"
              placeholder="选择结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">在职</el-radio>
          <el-radio :value="0">离任</el-radio>
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
import { ref, computed, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'
import { usePersonnelStore } from '@/stores/personnel'
import { usePositionStore } from '@/stores/position'
import { useDepartmentStore } from '@/stores/department'

interface Props {
  show: boolean
  item?: PersonnelPosition | null
}

const props = withDefaults(defineProps<Props>(), { item: null })
const emit = defineEmits<{
  close: []
  save: [data: PersonnelPositionReq]
}>()

const personnelStore = usePersonnelStore()
const positionStore = usePositionStore()
const departmentStore = useDepartmentStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = ref<PersonnelPositionReq>({
  personnelId: '',
  positionId: '',
  departmentId: null,
  isPrimary: true,
  startDate: null,
  endDate: null,
  status: 1,
})

const rules: FormRules = {
  personnelId: [{ required: true, message: '请选择人员', trigger: 'change' }],
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

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    emit('save', {
      personnelId: formData.value.personnelId,
      positionId: formData.value.positionId,
      departmentId: formData.value.departmentId || null,
      isPrimary: formData.value.isPrimary ?? true,
      startDate: formData.value.startDate || null,
      endDate: formData.value.endDate || null,
      status: formData.value.status ?? 1,
    })
  } finally {
    submitting.value = false
  }
}
</script>