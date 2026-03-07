<template>
  <div>
    <!-- 页面标题和操作按钮 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">部门管理</h2>
      <Button @click="showCreateModal = true">
        <Plus class="mr-2 h-4 w-4" />
        新建部门
      </Button>
    </div>

    <!-- 部门列表卡片 -->
    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>部门名称</TableHead>
              <TableHead>部门编码</TableHead>
              <TableHead>描述</TableHead>
              <TableHead>状态</TableHead>
              <TableHead>创建时间</TableHead>
              <TableHead class="text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="store.loading">
              <TableCell colspan="6" class="text-center py-8">
                <div class="flex justify-center items-center space-x-2">
                  <Loader2 class="h-5 w-5 animate-spin" />
                  <span class="text-muted-foreground">加载中...</span>
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="store.departments.length === 0">
              <TableCell colspan="6" class="text-center py-8 text-muted-foreground">
                暂无部门数据
              </TableCell>
            </TableRow>
            <TableRow v-for="dept in store.departments" :key="dept.id">
              <TableCell>
                <div class="flex items-center space-x-2">
                  <span class="font-medium">{{ dept.name }}</span>
                  <Badge variant="secondary">{{ dept.personCount ?? 0 }}人</Badge>
                </div>
              </TableCell>
              <TableCell class="text-muted-foreground">{{ dept.code }}</TableCell>
              <TableCell>
                <div class="max-w-xs truncate text-muted-foreground">{{ dept.description }}</div>
              </TableCell>
              <TableCell>
                <Badge :variant="dept.status === 1 ? 'success' : 'secondary'">
                  {{ dept.status === 1 ? '启用' : '禁用' }}
                </Badge>
              </TableCell>
              <TableCell class="text-muted-foreground">{{ formatDate(dept.createTime) }}</TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" @click="editDepartment(dept)">
                  <Pencil class="h-4 w-4" />
                </Button>
                <Button variant="ghost" size="sm" @click="confirmDelete(dept)">
                  <Trash2 class="h-4 w-4 text-destructive" />
                </Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <!-- 新建/编辑部门模态框 -->
    <DepartmentModal
      v-model:show="showCreateModal"
      :department="editingDepartment"
      @save="handleSave"
      @close="handleClose"
    />

    <!-- 删除确认对话框 -->
    <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = false">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除</AlertDialogTitle>
          <AlertDialogDescription>
            确定要删除部门 "{{ deletingDepartment?.name }}" 吗？此操作无法撤销。
          </AlertDialogDescription>
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
import { useDepartmentStore } from '@/stores/department'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import DepartmentModal from '@/components/common/DepartmentModal.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-vue-next'

const store = useDepartmentStore()
const showCreateModal = ref(false)
const editingDepartment = ref<Department | null>(null)
const showDeleteDialog = ref(false)
const deletingDepartment = ref<Department | null>(null)

onMounted(() => {
  store.fetchDepartments()
})

function formatDate(dateString: string) {
  return new Date(dateString).toLocaleString('zh-CN')
}

function editDepartment(dept: Department) {
  editingDepartment.value = dept
  showCreateModal.value = true
}

function confirmDelete(dept: Department) {
  deletingDepartment.value = dept
  showDeleteDialog.value = true
}

function handleDelete() {
  if (deletingDepartment.value) {
    store.deleteDepartment(deletingDepartment.value.id)
  }
  showDeleteDialog.value = false
  deletingDepartment.value = null
}

function handleSave(data: DepartmentCreateReq | DepartmentUpdateReq) {
  if (editingDepartment.value) {
    store.updateDepartment(editingDepartment.value.id, data as DepartmentUpdateReq)
  } else {
    store.createDepartment(data as DepartmentCreateReq)
  }
  handleClose()
}

function handleClose() {
  showCreateModal.value = false
  editingDepartment.value = null
}
</script>