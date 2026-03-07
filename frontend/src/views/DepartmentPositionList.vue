<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">部门岗位管理</h2>
      <Button @click="showCreateModal = true"><Plus class="mr-2 h-4 w-4" />配置岗位</Button>
    </div>

    <!-- 筛选条件 -->
    <Card class="mb-6">
      <CardContent class="p-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label>部门</Label>
            <Select v-model="filterDepartmentId">
              <SelectTrigger><SelectValue placeholder="全部部门" /></SelectTrigger>
              <SelectContent>
                <SelectItem value="">全部部门</SelectItem>
                <SelectItem v-for="d in departmentOptions" :key="d.value" :value="d.value">{{ d.label }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>岗位</Label>
            <Select v-model="filterPositionId">
              <SelectTrigger><SelectValue placeholder="全部岗位" /></SelectTrigger>
              <SelectContent>
                <SelectItem value="">全部岗位</SelectItem>
                <SelectItem v-for="p in positionOptions" :key="p.value" :value="p.value">{{ p.label }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </CardContent>
    </Card>

    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>部门</TableHead>
              <TableHead>岗位</TableHead>
              <TableHead>是否主岗</TableHead>
              <TableHead>排序</TableHead>
              <TableHead class="text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="store.loading">
              <TableCell colspan="5" class="text-center py-8">
                <div class="flex justify-center items-center space-x-2">
                  <Loader2 class="h-5 w-5 animate-spin" /><span class="text-muted-foreground">加载中...</span>
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="filteredData.length === 0">
              <TableCell colspan="5" class="text-center py-8 text-muted-foreground">暂无部门岗位关联数据</TableCell>
            </TableRow>
            <TableRow v-for="item in filteredData" :key="item.id">
              <TableCell class="font-medium">{{ item.departmentName }}</TableCell>
              <TableCell>{{ item.positionName }}</TableCell>
              <TableCell>
                <Badge :variant="item.isPrimary ? 'default' : 'secondary'">{{ item.isPrimary ? '主岗' : '普通岗' }}</Badge>
              </TableCell>
              <TableCell class="text-muted-foreground">{{ item.sortOrder }}</TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" @click="confirmDelete(item)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <DepartmentPositionModal v-model:show="showCreateModal" :department-options="departmentOptions" :position-options="positionOptions" @save="handleSave" @close="handleClose" />

    <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = false">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除</AlertDialogTitle>
          <AlertDialogDescription>确定要删除部门 "{{ deletingItem?.departmentName }}" 的岗位 "{{ deletingItem?.positionName }}" 配置吗？</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>取消</AlertDialogCancel>
          <AlertDialogAction @click="handleDelete">删除</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDepartmentPositionStore } from '@/stores/departmentPosition'
import { useDepartmentStore } from '@/stores/department'
import { usePositionStore } from '@/stores/position'
import type { DepartmentPosition, DepartmentPositionReq } from '@/types'
import DepartmentPositionModal from '@/components/common/DepartmentPositionModal.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog'
import { Plus, Trash2, Loader2 } from 'lucide-vue-next'

const store = useDepartmentPositionStore()
const departmentStore = useDepartmentStore()
const positionStore = usePositionStore()

const showCreateModal = ref(false)
const showDeleteDialog = ref(false)
const deletingItem = ref<DepartmentPosition | null>(null)
const filterDepartmentId = ref('')
const filterPositionId = ref('')

const departmentOptions = computed(() => departmentStore.departments.map((d) => ({ label: d.name, value: d.id })))
const positionOptions = computed(() => positionStore.positions.map((p) => ({ label: p.name, value: p.id })))

const filteredData = computed(() => {
  let data = store.departmentPositions
  if (filterDepartmentId.value) data = data.filter((item) => item.departmentId === filterDepartmentId.value)
  if (filterPositionId.value) data = data.filter((item) => item.positionId === filterPositionId.value)
  return data
})

onMounted(() => { store.fetchAllDepartmentPositions(); departmentStore.fetchDepartments(); positionStore.fetchPositions() })

function confirmDelete(item: DepartmentPosition) { deletingItem.value = item; showDeleteDialog.value = true }
function handleDelete() {
  if (deletingItem.value) store.deleteDepartmentPosition(deletingItem.value.departmentId, deletingItem.value.positionId)
  showDeleteDialog.value = false; deletingItem.value = null
}
function handleSave(data: DepartmentPositionReq) { store.createDepartmentPosition(data); handleClose() }
function handleClose() { showCreateModal.value = false }
</script>