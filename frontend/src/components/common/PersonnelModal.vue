<template>
  <el-dialog
    :model-value="show"
    :title="personnel ? '编辑人员' : '新建人员'"
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工号" prop="code">
            <el-input v-model="formData.code" placeholder="请输入工号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="所属部门" prop="departmentId">
        <el-tree-select
          v-model="formData.departmentId"
          :data="departmentTreeData"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择所属部门"
          check-strictly
          :render-after-expand="false"
        />
      </el-form-item>

      <el-form-item label="职位" prop="position">
        <el-input v-model="formData.position" placeholder="请输入职位" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="电话" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">在职</el-radio>
          <el-radio :value="0">离职</el-radio>
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
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'
import { useDepartmentStore } from '@/stores/department'

interface Props {
  show: boolean
  personnel?: Personnel | null
}

const props = withDefaults(defineProps<Props>(), { personnel: null })
const emit = defineEmits<{
  close: []
  save: [data: PersonnelCreateReq | PersonnelUpdateReq]
}>()

const departmentStore = useDepartmentStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = ref({
  name: '',
  code: '',
  email: '',
  phone: '',
  departmentId: '' as string,
  position: '',
  status: 1,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  code: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
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

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    emit('save', {
      name: formData.value.name,
      code: formData.value.code,
      email: formData.value.email || undefined,
      phone: formData.value.phone || undefined,
      departmentId: formData.value.departmentId,
      position: formData.value.position || undefined,
      status: formData.value.status || 1,
    })
  } finally {
    submitting.value = false
  }
}
</script>