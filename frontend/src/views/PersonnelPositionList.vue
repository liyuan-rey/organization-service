<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">人员岗位管理</h2>
      <Button @click="showCreateModal = true"><Plus class="mr-2 h-4 w-4" />新增任职</Button>
    </div>

    <!-- 筛选条件 -->
    <Card class="mb-6">
      <CardContent class="p-4">
        <div class="grid grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>人员</Label>
            <Select v-model="filterPersonnelId">
              <SelectTrigger><SelectValue placeholder="全部人员" /></SelectTrigger>
              <SelectContent>
                <SelectItem value="">全部人员</SelectItem>
                <SelectItem v-for="p in personnelOptions" :key="p.value" :value="p.value">{{ p.label }}</SelectItem>
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
        </div>
      </CardContent>
    </Card>

    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>人员</TableHead>
              <TableHead>岗位</TableHead>
              <TableHead>部门</TableHead>
              <TableHead>是否主岗</TableHead>
              <TableHead>任职时间</TableHead>
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
            <TableRow v-else-if="filteredData.length === 0">
              <TableCell colspan="7" class="text-center py-8 text-muted-foreground">暂无人员岗位关联数据</TableCell>
            </TableRow>
            <TableRow v-for="item in filteredData" :key="item.id">
              <TableCell class="font-medium">{{ item.personnelName }}</TableCell>
              <TableCell>{{ item.positionName }}</TableCell>
              <TableCell class="text-muted-foreground">{{ item.departmentName || '-' }}</TableCell>
              <TableCell>
                <Badge :variant="item.isPrimary ? 'default' : 'secondary'">{{ item.isPrimary ? '主岗' : '兼岗' }}</Badge>
              </TableCell>
              <TableCell class="text-muted-foreground">
                {{ formatDate(item.startDate) }}<span v-if="item.endDate">至 {{ formatDate(item.endDate) }}</span><span v-else>至今</span>
              </TableCell>
              <TableCell>
                <Badge :variant="item.status === 1 ? 'success' : 'secondary'">{{ item.status === 1 ? '在职' : '离任' }}</Badge>
              </TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" @click="editItem(item)"><Pencil class="h-4 w-4" /></Button>
                <Button variant="ghost" size="sm" @click="confirmDelete(item)"><Trash2 class="h-4 w-4 text-destructive" /></Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <PersonnelPositionModal v-model:show="showCreateModal" :item="editingItem" :personnel-options="personnelOptions" :position-options="positionOptions" :department-options="departmentOptions" @save="handleSave" @close="handleClose" />

    <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = false">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除</AlertDialogTitle>
          <AlertDialogDescription>确定要删除该任职记录吗？此操作无法撤销。</AlertDialogDescription>
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
import { usePersonnelPositionStore } from '@/stores/personnelPosition'
import { usePersonnelStore } from '@/stores/personnel'
import { usePositionStore } from '@/stores/position'
import { useDepartmentStore } from '@/stores/department'
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'
import PersonnelPositionModal from '@/components/common/PersonnelPositionModal.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog'
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-vue-next'

const store = usePersonnelPositionStore()
const personnelStore = usePersonnelStore()
const positionStore = usePositionStore()
const departmentStore = useDepartmentStore()

const showCreateModal = ref(false)
const editingItem = ref<PersonnelPosition | null>(null)
const showDeleteDialog = ref(false)
const deletingItem = ref<PersonnelPosition | null>(null)
const filterPersonnelId = ref('')
const filterPositionId = ref('')
const filterDepartmentId = ref('')

const personnelOptions = computed(() => personnelStore.personnelList.map((p) => ({ label: p.name, value: p.id })))
const positionOptions = computed(() => positionStore.positions.map((p) => ({ label: p.name, value: p.id })))
const departmentOptions = computed(() => departmentStore.departments.map((d) => ({ label: d.name, value: d.id })))

const filteredData = computed(() => {
  let data = store.personnelPositions
  if (filterPersonnelId.value) data = data.filter((item) => item.personnelId === filterPersonnelId.value)
  if (filterPositionId.value) data = data.filter((item) => item.positionId === filterPositionId.value)
  if (filterDepartmentId.value) data = data.filter((item) => item.departmentId === filterDepartmentId.value)
  return data
})

onMounted(() => { store.fetchAllPersonnelPositions(); personnelStore.fetchPersonnel(); positionStore.fetchPositions(); departmentStore.fetchDepartments() })

const formatDate = (dateString: string | null) => dateString ? new Date(dateString).toLocaleDateString('zh-CN') : '-'

function editItem(item: PersonnelPosition) { editingItem.value = item; showCreateModal.value = true }
function confirmDelete(item: PersonnelPosition) { deletingItem.value = item; showDeleteDialog.value = true }
function handleDelete() { if (deletingItem.value) store.deletePersonnelPosition(deletingItem.value.id); showDeleteDialog.value = false; deletingItem.value = null }
function handleSave(data: PersonnelPositionReq) {
  if (editingItem.value) store.updatePersonnelPosition(editingItem.value.id, data)
  else store.createPersonnelPosition(data)
  handleClose()
}
function handleClose() { showCreateModal.value = false; editingItem.value = null }
</script>