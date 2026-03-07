<template>
  <Dialog :open="show" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-md">
      <DialogHeader>
        <DialogTitle>{{ personnel ? '编辑人员' : '新建人员' }}</DialogTitle>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">姓名</Label>
          <Input id="name" v-model="formData.name" type="text" required placeholder="请输入姓名" />
        </div>

        <div class="space-y-2">
          <Label for="code">工号</Label>
          <Input id="code" v-model="formData.code" type="text" required placeholder="请输入工号" />
        </div>

        <div class="space-y-2">
          <Label for="departmentId">所属部门</Label>
          <Select v-model="formData.departmentId" required>
            <SelectTrigger><SelectValue placeholder="请选择部门" /></SelectTrigger>
            <SelectContent>
              <SelectItem v-for="dept in departmentOptions" :key="dept.value" :value="dept.value">{{ dept.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="position">职位</Label>
          <Input id="position" v-model="formData.position" type="text" placeholder="请输入职位" />
        </div>

        <div class="space-y-2">
          <Label for="email">邮箱</Label>
          <Input id="email" v-model="formData.email" type="email" placeholder="请输入邮箱" />
        </div>

        <div class="space-y-2">
          <Label for="phone">电话</Label>
          <Input id="phone" v-model="formData.phone" type="tel" placeholder="请输入电话" />
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
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface Option { label: string; value: string }
interface Props { show: boolean; personnel?: Personnel | null; departmentOptions: Option[] }
const props = withDefaults(defineProps<Props>(), { personnel: null, departmentOptions: () => [] })
const emit = defineEmits<{ close: []; save: [data: PersonnelCreateReq | PersonnelUpdateReq] }>()

const formData = ref<{ name: string; code: string; email: string; phone: string; departmentId: string; position: string; status: number }>({
  name: '', code: '', email: '', phone: '', departmentId: '', position: '', status: 1,
})

const statusValue = computed({
  get: () => String(formData.value.status),
  set: (val: string) => { formData.value.status = Number(val) },
})

watch(
  () => props.personnel,
  (person) => {
    if (person) {
      formData.value = {
        name: person.name, code: person.code, email: person.email || '', phone: person.phone || '',
        departmentId: person.departmentId, position: person.position || '', status: person.status,
      }
    } else {
      formData.value = { name: '', code: '', email: '', phone: '', departmentId: '', position: '', status: 1 }
    }
  },
  { immediate: true }
)

function handleSubmit() {
  emit('save', { ...formData.value, status: formData.value.status || 1 })
}
</script>