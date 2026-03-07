<template>
  <Dialog :open="show" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-md">
      <DialogHeader>
        <DialogTitle>{{ department ? '编辑部门' : '新建部门' }}</DialogTitle>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">部门名称</Label>
          <Input id="name" v-model="formData.name" type="text" required placeholder="请输入部门名称" />
        </div>

        <div class="space-y-2">
          <Label for="code">部门编码</Label>
          <Input id="code" v-model="formData.code" type="text" required placeholder="请输入部门编码" />
        </div>

        <div class="space-y-2">
          <Label for="parentId">上级部门</Label>
          <Select v-model="formData.parentId">
            <SelectTrigger><SelectValue placeholder="请选择上级部门" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="__null__">无（作为顶级部门）</SelectItem>
              <SelectItem v-for="dept in departmentOptions" :key="dept.value" :value="dept.value">{{ dept.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="description">描述</Label>
          <Textarea id="description" v-model="formData.description" rows="3" placeholder="请输入部门描述" />
        </div>

        <div class="space-y-2">
          <Label for="sortOrder">排序</Label>
          <Input id="sortOrder" v-model.number="formData.sortOrder" type="number" placeholder="数字越小越靠前" />
        </div>

        <div class="space-y-2">
          <Label for="status">状态</Label>
          <Select v-model="statusValue">
            <SelectTrigger><SelectValue placeholder="请选择状态" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="1">启用</SelectItem>
              <SelectItem value="0">禁用</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </form>

      <DialogFooter>
        <Button type="button" variant="outline" @click="$emit('close')">取消</Button>
        <Button type="submit" @click="handleSubmit">保存</Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import { useDepartmentStore } from '@/stores/department'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface Props { show: boolean; department?: Department | null }
const props = withDefaults(defineProps<Props>(), { department: null })
const emit = defineEmits<{ close: []; save: [data: DepartmentCreateReq | DepartmentUpdateReq] }>()

const departmentStore = useDepartmentStore()

const formData = ref<{ name: string; code: string; description: string; parentId: string; sortOrder: number; status: number }>({
  name: '', code: '', description: '', parentId: '__null__', sortOrder: 0, status: 1,
})

const statusValue = computed({
  get: () => String(formData.value.status),
  set: (val: string) => { formData.value.status = Number(val) },
})

const departmentOptions = computed(() => {
  const excludeId = props.department?.id
  return departmentStore.departments.filter((d) => d.id !== excludeId).map((d) => ({ label: d.name, value: d.id }))
})

watch(
  () => props.department,
  (dept) => {
    if (dept) {
      formData.value = {
        name: dept.name, code: dept.code, description: dept.description || '',
        parentId: dept.parentId || '__null__', sortOrder: dept.sortOrder, status: dept.status,
      }
    } else {
      formData.value = { name: '', code: '', description: '', parentId: '__null__', sortOrder: 0, status: 1 }
    }
  },
  { immediate: true }
)

function handleSubmit() {
  emit('save', {
    name: formData.value.name, code: formData.value.code, description: formData.value.description,
    parentId: formData.value.parentId === '__null__' ? null : formData.value.parentId,
    sortOrder: formData.value.sortOrder || 0, status: formData.value.status || 1,
  })
}
</script>