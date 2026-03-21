<template>
  <el-dialog
    :model-value="show"
    :title="position ? '编辑岗位' : '新建岗位'"
    width="600"
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
          <el-form-item label="岗位名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入岗位名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="岗位编码" prop="code">
            <el-input v-model="formData.code" placeholder="请输入岗位编码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="岗位描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入岗位描述"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职级" prop="jobLevel">
            <el-input v-model="formData.jobLevel" placeholder="如：P2, M1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="岗位类别" prop="jobCategory">
            <el-select v-model="formData.jobCategory" placeholder="请选择岗位类别" clearable>
              <el-option label="技术类" value="Technical" />
              <el-option label="管理类" value="Management" />
              <el-option label="销售类" value="Sales" />
              <el-option label="人力资源类" value="HR" />
              <el-option label="财务类" value="Finance" />
              <el-option label="运营类" value="Operations" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="薪资范围">
        <div class="flex items-center gap-2">
          <el-input-number
            v-model="formData.minSalary"
            :min="0"
            :precision="0"
            placeholder="最低薪资"
            class="flex-1"
          />
          <span class="text-muted-foreground">-</span>
          <el-input-number
            v-model="formData.maxSalary"
            :min="0"
            :precision="0"
            placeholder="最高薪资"
            class="flex-1"
          />
        </div>
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
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'

interface Props {
  show: boolean
  position?: Position | null
}

const props = withDefaults(defineProps<Props>(), { position: null })
const emit = defineEmits<{
  close: []
  save: [data: PositionCreateReq | PositionUpdateReq]
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)

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

const rules: FormRules = {
  name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
}

watch(
  () => props.position,
  (pos) => {
    if (pos) {
      formData.value = {
        name: pos.name,
        code: pos.code,
        description: pos.description || '',
        jobLevel: pos.jobLevel || '',
        jobCategory: pos.jobCategory || '',
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

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    emit('save', {
      name: formData.value.name,
      code: formData.value.code,
      description: formData.value.description || undefined,
      jobLevel: formData.value.jobLevel || undefined,
      jobCategory: formData.value.jobCategory || undefined,
      minSalary: formData.value.minSalary,
      maxSalary: formData.value.maxSalary,
      status: formData.value.status || 1,
    })
  } finally {
    submitting.value = false
  }
}
</script>