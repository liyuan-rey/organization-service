<template>
  <div class="space-y-4">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">人员岗位管理</h1>
        <p class="text-muted-foreground text-sm">管理人员与岗位的任职关系</p>
      </div>
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon class="mr-1"><Plus /></el-icon>
        新增任职
      </el-button>
    </div>

    <!-- 筛选条件 -->
    <el-card class="shadow-sm">
      <div class="flex flex-wrap gap-4">
        <el-select v-model="filterPersonnelId" placeholder="选择人员" clearable filterable class="w-48">
          <el-option
            v-for="p in personnelOptions"
            :key="p.value"
            :label="p.label"
            :value="p.value"
          />
        </el-select>
        <el-select v-model="filterPositionId" placeholder="选择岗位" clearable class="w-48">
          <el-option
            v-for="p in positionOptions"
            :key="p.value"
            :label="p.label"
            :value="p.value"
          />
        </el-select>
        <el-select v-model="filterDepartmentId" placeholder="选择部门" clearable class="w-48">
          <el-option
            v-for="d in departmentOptions"
            :key="d.value"
            :label="d.label"
            :value="d.value"
          />
        </el-select>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card class="shadow-sm">
      <el-table v-loading="store.loading" :data="filteredData" stripe highlight-current-row>
        <el-table-column prop="personnelName" label="人员" min-width="120">
          <template #default="{ row }">
            <span class="font-medium">{{ row.personnelName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="positionName" label="岗位" min-width="120" />
        <el-table-column prop="departmentName" label="部门" min-width="120">
          <template #default="{ row }">
            {{ row.departmentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isPrimary" label="岗位类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPrimary ? 'primary' : 'info'" size="small">
              {{ row.isPrimary ? '主岗' : '兼岗' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="任职时间" min-width="180">
          <template #default="{ row }">
            <span v-if="row.startDate">
              {{ formatDate(row.startDate) }}
              <span v-if="row.endDate">至 {{ formatDate(row.endDate) }}</span>
              <span v-else>至今</span>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在职' : '离任' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editItem(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" link size="small" @click="confirmDelete(row)">
              <el-icon><Trash /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="filteredData.length === 0 && !store.loading" description="暂无人员岗位关联数据" />
    </el-card>

    <!-- 新建/编辑弹窗 -->
    <PersonnelPositionModal
      v-model:show="showCreateModal"
      :item="editingItem"
      @save="handleSave"
      @close="handleClose"
    />

    <!-- 删除确认对话框 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400">
      <p>确定要删除该任职记录吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>
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
import { Plus, Edit, Trash } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

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

const personnelOptions = computed(() =>
  personnelStore.personnelList.map((p) => ({ label: p.name, value: p.id }))
)

const positionOptions = computed(() =>
  positionStore.positions.map((p) => ({ label: p.name, value: p.id }))
)

const departmentOptions = computed(() =>
  departmentStore.departments.map((d) => ({ label: d.name, value: d.id }))
)

const filteredData = computed(() => {
  let data = store.personnelPositions
  if (filterPersonnelId.value) {
    data = data.filter((item) => item.personnelId === filterPersonnelId.value)
  }
  if (filterPositionId.value) {
    data = data.filter((item) => item.positionId === filterPositionId.value)
  }
  if (filterDepartmentId.value) {
    data = data.filter((item) => item.departmentId === filterDepartmentId.value)
  }
  return data
})

onMounted(() => {
  store.fetchAllPersonnelPositions()
  personnelStore.fetchPersonnel()
  positionStore.fetchPositions()
  departmentStore.fetchDepartments()
})

function formatDate(dateString: string | null) {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

function editItem(item: PersonnelPosition) {
  editingItem.value = item
  showCreateModal.value = true
}

function confirmDelete(item: PersonnelPosition) {
  deletingItem.value = item
  showDeleteDialog.value = true
}

async function handleDelete() {
  if (deletingItem.value) {
    try {
      await store.deletePersonnelPosition(deletingItem.value.id)
      ElMessage.success('删除成功')
    } catch {
      ElMessage.error('删除失败')
    }
  }
  showDeleteDialog.value = false
  deletingItem.value = null
}

async function handleSave(data: PersonnelPositionReq) {
  try {
    if (editingItem.value) {
      await store.updatePersonnelPosition(editingItem.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await store.createPersonnelPosition(data)
      ElMessage.success('创建成功')
    }
    handleClose()
  } catch {
    ElMessage.error('操作失败')
  }
}

function handleClose() {
  showCreateModal.value = false
  editingItem.value = null
}
</script>