<template>
  <Dialog :open="show" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-lg">
      <DialogHeader>
        <DialogTitle>配置部门岗位</DialogTitle>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="departmentId">部门 <span class="text-destructive">*</span></Label>
          <Select v-model="formData.departmentId" required>
            <SelectTrigger><SelectValue placeholder="请选择部门" /></SelectTrigger>
            <SelectContent>
              <SelectItem v-for="opt in departmentOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="positionId">岗位 <span class="text-destructive">*</span></Label>
          <Select v-model="formData.positionId" required>
            <SelectTrigger><SelectValue placeholder="请选择岗位" /></SelectTrigger>
            <SelectContent>
              <SelectItem v-for="opt in positionOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label>是否主岗</Label>
          <Select v-model="isPrimaryValue">
            <SelectTrigger class="w-40"><SelectValue placeholder="请选择" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="true">主岗</SelectItem>
              <SelectItem value="false">普通岗</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="space-y-2">
          <Label for="sortOrder">排序</Label>
          <Input id="sortOrder" v-model.number="formData.sortOrder" type="number" min="0" placeholder="数字越小越靠前" />
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
import type { DepartmentPositionReq } from '@/types'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface Option { label: string; value: string }
const props = defineProps<{ show: boolean; departmentOptions: Option[]; positionOptions: Option[] }>()
const emit = defineEmits<{ (e: 'save', data: DepartmentPositionReq): void; (e: 'close'): void }>()

const formData = ref<DepartmentPositionReq>({ departmentId: '', positionId: '', isPrimary: false, sortOrder: 0 })

const isPrimaryValue = computed({
  get: () => String(formData.value.isPrimary),
  set: (val: string) => { formData.value.isPrimary = val === 'true' },
})

watch(() => props.show, (show) => {
  if (show) formData.value = { departmentId: '', positionId: '', isPrimary: false, sortOrder: 0 }
})

function handleSubmit() { emit('save', formData.value) }
</script>