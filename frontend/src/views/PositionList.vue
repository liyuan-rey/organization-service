<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">岗位管理</h2>
      <Button @click="showCreateModal = true"><Plus class="mr-2 h-4 w-4" />新建岗位</Button>
    </div>

    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>岗位名称</TableHead>
              <TableHead>岗位编码</TableHead>
              <TableHead>职级</TableHead>
              <TableHead>岗位类别</TableHead>
              <TableHead>薪资范围</TableHead>
              <TableHead>状态</TableHead>
              <TableHead class="text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="store.loading">
              <TableCell colspan="7" class="text-center py-8">
                <div class="flex justify-center items-center space-x-2">
                  <Loader2 class="h-5 w-5 animate-spin" /><span class="text-muted-foreground">加载中...</span>
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="store.positions.length === 0">
              <TableCell colspan="7" class="text-center py-8 text-muted-foreground">暂无岗位数据</TableCell>
            </TableRow>
            <TableRow v-for="pos in store.positions" :key="pos.id">
              <TableCell class="font-medium">{{ pos.name }}</TableCell>
              <TableCell class="text-muted-foreground">{{ pos.code }}</TableCell>
              <TableCell class="text-muted-foreground">{{ pos.jobLevel || '-' }}</TableCell>
              <TableCell class="text-muted-foreground">{{ pos.jobCategory || '-' }}</TableCell>
              <TableCell class="text-muted-foreground">
                <span v-if="pos.minSalary || pos.maxSalary">¥{{ formatSalary(pos.minSalary) }} - ¥{{ formatSalary(pos.maxSalary) }}</span>
                <span v-else>-</span>
              </TableCell>
              <TableCell>
                <Badge :variant="pos.status === 1 ? 'success' : 'secondary'">{{ pos.status === 1 ? '启用' : '禁用' }}</Badge>
              </TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" @click="editPosition(pos)"><Pencil class="h-4 w-4" /></Button>
                <Button variant="ghost" size="sm" @click="confirmDelete(pos)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <PositionModal v-model:show="showCreateModal" :position="editingPosition" @save="handleSave" @close="handleClose" />

    <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = false">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除</AlertDialogTitle>
          <AlertDialogDescription>确定要删除岗位 "{{ deletingPosition?.name }}" 吗？此操作无法撤销。</AlertDialogDescription>
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
import { ref, onMounted } from 'vue'
import { usePositionStore } from '@/stores/position'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'
import PositionModal from '@/components/common/PositionModal.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog'
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-vue-next'

const store = usePositionStore()
const showCreateModal = ref(false)
const editingPosition = ref<Position | null>(null)
const showDeleteDialog = ref(false)
const deletingPosition = ref<Position | null>(null)

onMounted(() => store.fetchPositions())

const formatSalary = (salary: number | null) => salary?.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) || '0'

function editPosition(pos: Position) { editingPosition.value = pos; showCreateModal.value = true }
function confirmDelete(pos: Position) { deletingPosition.value = pos; showDeleteDialog.value = true }
function handleDelete() { if (deletingPosition.value) store.deletePosition(deletingPosition.value.id); showDeleteDialog.value = false; deletingPosition.value = null }
function handleSave(data: PositionCreateReq | PositionUpdateReq) {
  if (editingPosition.value) store.updatePosition(editingPosition.value.id, data as PositionUpdateReq)
  else store.createPosition(data as PositionCreateReq)
  handleClose()
}
function handleClose() { showCreateModal.value = false; editingPosition.value = null }
</script>