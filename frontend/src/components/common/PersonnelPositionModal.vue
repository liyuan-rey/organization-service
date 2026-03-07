<template>
  <Dialog :open="show" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-2xl">
      <DialogHeader>
        <DialogTitle>{{ item ? '编辑任职' : '新增任职' }}</DialogTitle>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="personnelId">人员 <span class="text-destructive">*</span></Label>
          <Select v-model="formData.personnelId" required :disabled="!!item">
            <SelectTrigger><SelectValue placeholder="请选择人员" /></SelectTrigger>
            <SelectContent>
              <SelectItem v-for="opt in personnelOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="positionId">岗位 <span class="text-destructive">*</span></Label>
          <Select v-model="formData.positionId" required :disabled="!!item">
            <SelectTrigger><SelectValue placeholder="请选择岗位" /></SelectTrigger>
            <SelectContent>
              <SelectItem v-for="opt in positionOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="departmentId">部门</Label>
          <Select v-model="departmentIdValue">
            <SelectTrigger><SelectValue placeholder="请选择部门" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="__null__">无</SelectItem>
              <SelectItem v-for="opt in departmentOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label>是否主岗</Label>
          <Select v-model="isPrimaryValue">
            <SelectTrigger class="w-40"><SelectValue placeholder="请选择" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="true">主岗</SelectItem>
              <SelectItem value="false">兼岗</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="startDate">开始日期</Label>
            <Input id="startDate" :model-value="formData.startDate ?? ''" type="date" @update:model-value="(v: string | number) => formData.startDate = String(v) || null" />
          </div>
          <div class="space-y-2">
            <Label for="endDate">结束日期</Label>
            <Input id="endDate" :model-value="formData.endDate ?? ''" type="date" @update:model-value="(v: string | number) => formData.endDate = String(v) || null" />
          </div>
        </div>

        <div class="space-y-2">
          <Label>状态</Label>
          <Select v-model="statusValue">
            <SelectTrigger class="w-40"><SelectValue placeholder="请选择状态" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="1">在职</SelectItem>
              <SelectItem value="0">离任</SelectItem>
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
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface Option { label: string; value: string }
const props = defineProps<{ show: boolean; item?: PersonnelPosition | null; personnelOptions: Option[]; positionOptions: Option[]; departmentOptions: Option[] }>()
const emit = defineEmits<{ (e: 'save', data: PersonnelPositionReq): void; (e: 'close'): void }>()

const formData = ref<PersonnelPositionReq>({ personnelId: '', positionId: '', departmentId: null, isPrimary: true, startDate: null, endDate: null, status: 1 })

const departmentIdValue = computed({
  get: () => formData.value.departmentId || '__null__',
  set: (val: string) => { formData.value.departmentId = val === '__null__' ? null : val },
})

const isPrimaryValue = computed({
  get: () => String(formData.value.isPrimary),
  set: (val: string) => { formData.value.isPrimary = val === 'true' },
})

const statusValue = computed({
  get: () => String(formData.value.status),
  set: (val: string) => { formData.value.status = Number(val) },
})

watch(() => props.item, (item) => {
  if (item) {
    formData.value = { personnelId: item.personnelId, positionId: item.positionId, departmentId: item.departmentId, isPrimary: item.isPrimary, startDate: item.startDate, endDate: item.endDate, status: item.status }
  } else {
    formData.value = { personnelId: '', positionId: '', departmentId: null, isPrimary: true, startDate: new Date().toISOString().split('T')[0], endDate: null, status: 1 }
  }
}, { immediate: true })

function handleSubmit() { emit('save', formData.value) }
</script>