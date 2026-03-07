<template>
  <Dialog :open="show" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-2xl">
      <DialogHeader>
        <DialogTitle>{{ position ? '编辑岗位' : '新建岗位' }}</DialogTitle>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">岗位名称 <span class="text-destructive">*</span></Label>
            <Input id="name" v-model="formData.name" type="text" required placeholder="请输入岗位名称" />
          </div>
          <div class="space-y-2">
            <Label for="code">岗位编码 <span class="text-destructive">*</span></Label>
            <Input id="code" v-model="formData.code" type="text" required placeholder="请输入岗位编码" />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="description">岗位描述</Label>
          <Textarea id="description" v-model="formData.description" rows="3" placeholder="请输入岗位描述" />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="jobLevel">职级</Label>
            <Input id="jobLevel" v-model="formData.jobLevel" type="text" placeholder="如：P2, M1" />
          </div>
          <div class="space-y-2">
            <Label for="jobCategory">岗位类别</Label>
            <Select v-model="formData.jobCategory">
              <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
              <SelectContent>
                <SelectItem value="Technical">技术类</SelectItem>
                <SelectItem value="Management">管理类</SelectItem>
                <SelectItem value="Sales">销售类</SelectItem>
                <SelectItem value="HR">人力资源类</SelectItem>
                <SelectItem value="Finance">财务类</SelectItem>
                <SelectItem value="Operations">运营类</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="minSalary">最低薪资</Label>
            <Input id="minSalary" :model-value="formData.minSalary ?? ''" type="number" step="0.01" placeholder="0" @update:model-value="formData.minSalary = $event ? Number($event) : null" />
          </div>
          <div class="space-y-2">
            <Label for="maxSalary">最高薪资</Label>
            <Input id="maxSalary" :model-value="formData.maxSalary ?? ''" type="number" step="0.01" placeholder="0" @update:model-value="formData.maxSalary = $event ? Number($event) : null" />
          </div>
        </div>

        <div class="space-y-2">
          <Label>状态</Label>
          <Select v-model="statusValue">
            <SelectTrigger class="w-40"><SelectValue placeholder="请选择状态" /></SelectTrigger>
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
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const props = defineProps<{ show: boolean; position?: Position | null }>()
const emit = defineEmits<{ (e: 'save', data: PositionCreateReq | PositionUpdateReq): void; (e: 'close'): void }>()

const formData = ref<PositionCreateReq>({ name: '', code: '', description: '', jobLevel: '', jobCategory: '', minSalary: null, maxSalary: null, status: 1 })

const statusValue = computed({
  get: () => String(formData.value.status),
  set: (val: string) => { formData.value.status = Number(val) },
})

watch(() => props.position, (pos) => {
  if (pos) formData.value = { ...pos }
  else formData.value = { name: '', code: '', description: '', jobLevel: '', jobCategory: '', minSalary: null, maxSalary: null, status: 1 }
}, { immediate: true })

function handleSubmit() { emit('save', formData.value) }
</script>