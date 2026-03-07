<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">人员管理</h2>
      <Button @click="showCreateModal = true"><Plus class="mr-2 h-4 w-4" />新建人员</Button>
    </div>

    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>姓名</TableHead>
              <TableHead>工号</TableHead>
              <TableHead>邮箱</TableHead>
              <TableHead>电话</TableHead>
              <TableHead>所属部门</TableHead>
              <TableHead>职位</TableHead>
              <TableHead>状态</TableHead>
              <TableHead class="text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="store.loading">
              <TableCell colspan="8" class="text-center py-8">
                <div class="flex justify-center items-center space-x-2">
                  <Loader2 class="h-5 w-5 animate-spin" /><span class="text-muted-foreground">加载中...</span>
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="store.personnelList.length === 0">
              <TableCell colspan="8" class="text-center py-8 text-muted-foreground">暂无人员数据</TableCell>
            </TableRow>
            <TableRow v-for="person in store.personnelList" :key="person.id">
              <TableCell>
                <div class="flex items-center">
                  <div class="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center text-primary font-medium mr-3">
                    {{ person.name.charAt(0) }}
                  </div>
                  <span class="font-medium">{{ person.name }}</span>
                </div>
              </TableCell>
              <TableCell class="text-muted-foreground">{{ person.code }}</TableCell>
              <TableCell class="text-muted-foreground">{{ person.email }}</TableCell>
              <TableCell class="text-muted-foreground">{{ person.phone }}</TableCell>
              <TableCell>{{ getDepartmentName(person.departmentId) }}</TableCell>
              <TableCell class="text-muted-foreground">{{ person.position }}</TableCell>
              <TableCell>
                <Badge :variant="person.status === 1 ? 'success' : 'secondary'">{{ person.status === 1 ? '启用' : '禁用' }}</Badge>
              </TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" @click="editPersonnel(person)"><Pencil class="h-4 w-4" /></Button>
                <Button variant="ghost" size="sm" @click="confirmDelete(person)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <PersonnelModal v-model:show="showCreateModal" :personnel="editingPersonnel" :department-options="departmentOptions" @save="handleSave" @close="handleClose" />

    <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = false">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除</AlertDialogTitle>
          <AlertDialogDescription>确定要删除人员 "{{ deletingPersonnel?.name }}" 吗？此操作无法撤销。</AlertDialogDescription>
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
import { ref, onMounted, computed } from 'vue'
import { usePersonnelStore } from '@/stores/personnel'
import { useDepartmentStore } from '@/stores/department'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'
import PersonnelModal from '@/components/common/PersonnelModal.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog'
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-vue-next'

const personnelStore = usePersonnelStore()
const departmentStore = useDepartmentStore()
const showCreateModal = ref(false)
const editingPersonnel = ref<Personnel | null>(null)
const showDeleteDialog = ref(false)
const deletingPersonnel = ref<Personnel | null>(null)

const store = computed(() => personnelStore)
const departmentOptions = computed(() => departmentStore.departments.map((d) => ({ label: d.name, value: d.id })))

onMounted(() => { personnelStore.fetchPersonnel(); departmentStore.fetchDepartments() })

const getDepartmentName = (id: string) => departmentStore.getDepartmentById(id)?.name || '-'

function editPersonnel(person: Personnel) { editingPersonnel.value = person; showCreateModal.value = true }
function confirmDelete(person: Personnel) { deletingPersonnel.value = person; showDeleteDialog.value = true }
function handleDelete() { if (deletingPersonnel.value) personnelStore.deletePersonnel(deletingPersonnel.value.id); showDeleteDialog.value = false; deletingPersonnel.value = null }
function handleSave(data: PersonnelCreateReq | PersonnelUpdateReq) {
  if (editingPersonnel.value) personnelStore.updatePersonnel(editingPersonnel.value.id, data as PersonnelUpdateReq)
  else personnelStore.createPersonnel(data as PersonnelCreateReq)
  handleClose()
}
function handleClose() { showCreateModal.value = false; editingPersonnel.value = null }
</script>