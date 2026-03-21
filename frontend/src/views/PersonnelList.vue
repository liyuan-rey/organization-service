<template>
  <div class="space-y-4">
    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-3 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名/工号"
          clearable
          class="w-52"
        >
          <template #prefix>
            <Search class="h-4 w-4" />
          </template>
        </el-input>
        <el-select v-model="departmentFilter" placeholder="部门" clearable class="w-32">
          <el-option
            v-for="dept in departmentStore.departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="w-24">
          <el-option label="全部" value="" />
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="0" />
        </el-select>
      </div>

      <div class="flex gap-2">
        <!-- 视图切换 -->
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="table">
            <List class="h-4 w-4" />
          </el-radio-button>
          <el-radio-button value="card">
            <Grid class="h-4 w-4" />
          </el-radio-button>
        </el-radio-group>

        <!-- 新建按钮 -->
        <el-button type="primary" @click="showCreateModal = true">
          <Plus class="h-4 w-4 mr-1" />
          新建
        </el-button>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <transition name="slide-down">
      <div v-if="selectedRows.length > 0" class="batch-action-bar">
        <span class="text-sm text-muted-foreground">
          已选择 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 项
        </span>
        <div class="flex gap-2">
          <el-button size="small" @click="selectedRows = []">取消</el-button>
          <el-button size="small" type="danger" @click="showBatchDeleteDialog = true">
            <Trash class="h-4 w-4 mr-1" />
            删除
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 表格视图 -->
    <div v-show="viewMode === 'table'" class="table-container">
      <el-table
        v-loading="personnelStore.loading"
        :data="filteredPersonnel"
        @selection-change="handleSelectionChange"
        class="w-full"
      >
        <el-table-column type="selection" width="44" />
        <el-table-column prop="name" label="姓名" min-width="160">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar :size="32" class="bg-primary text-xs">
                {{ row.name.charAt(0) }}
              </el-avatar>
              <div>
                <p class="font-medium text-sm">{{ row.name }}</p>
                <p class="text-xs text-muted-foreground">{{ row.code }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="departmentId" label="部门" min-width="120">
          <template #default="{ row }">
            <span class="text-sm">{{ getDepartmentName(row.departmentId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" width="120">
          <template #default="{ row }">
            <span class="text-sm">{{ row.position || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="text-sm text-muted-foreground">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130">
          <template #default="{ row }">
            <span class="text-sm text-muted-foreground">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" class="!p-1" @click="editPersonnel(row)">
              <Edit class="h-4 w-4" />
            </el-button>
            <el-button type="danger" link size="small" class="!p-1" @click="confirmDelete(row)">
              <Trash class="h-4 w-4" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 卡片视图 -->
    <div v-show="viewMode === 'card'" class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      <div
        v-for="person in filteredPersonnel"
        :key="person.id"
        class="card-item"
      >
        <div class="flex items-start gap-3">
          <el-avatar :size="40" class="bg-primary text-sm shrink-0">
            {{ person.name.charAt(0) }}
          </el-avatar>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-0.5">
              <h3 class="font-medium text-sm">{{ person.name }}</h3>
              <el-tag :type="person.status === 1 ? 'success' : 'info'" size="small" effect="light">
                {{ person.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </div>
            <p class="text-xs text-muted-foreground mb-1">{{ person.position || '暂无职位' }}</p>
            <div class="text-xs text-muted-foreground space-y-0.5">
              <p class="truncate">{{ getDepartmentName(person.departmentId) }}</p>
              <p class="truncate">{{ person.email || '-' }}</p>
            </div>
          </div>
          <el-dropdown trigger="click">
            <button class="p-1 hover:bg-muted rounded transition-colors">
              <MoreVertical class="h-4 w-4 text-muted-foreground" />
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editPersonnel(person)">
                  <Edit class="h-4 w-4 mr-2" />编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(person)">
                  <Trash class="h-4 w-4 mr-2 text-red-500" />
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredPersonnel.length === 0 && !personnelStore.loading" class="col-span-full py-12">
        <el-empty description="暂无人员数据" />
      </div>
    </div>

    <!-- 新建/编辑人员弹窗 -->
    <PersonnelModal
      v-model:show="showCreateModal"
      :personnel="editingPersonnel"
      @save="handleSave"
      @close="handleClose"
    />

    <!-- 删除确认对话框 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除人员 <span class="font-medium text-foreground">{{ deletingPersonnel?.name }}</span> 吗？此操作无法撤销。
      </p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除选中的 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 名人员吗？此操作无法撤销。
      </p>
      <template #footer>
        <el-button @click="showBatchDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleBatchDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePersonnelStore } from '@/stores/personnel'
import { useDepartmentStore } from '@/stores/department'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'
import PersonnelModal from '@/components/common/PersonnelModal.vue'
import { Plus, Search, List, Grid, Edit, Trash, MoreVertical } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const personnelStore = usePersonnelStore()
const departmentStore = useDepartmentStore()

const viewMode = ref<'table' | 'card'>('table')
const searchKeyword = ref('')
const departmentFilter = ref('')
const statusFilter = ref<number | ''>('')
const selectedRows = ref<Personnel[]>([])

const showCreateModal = ref(false)
const editingPersonnel = ref<Personnel | null>(null)
const showDeleteDialog = ref(false)
const deletingPersonnel = ref<Personnel | null>(null)
const showBatchDeleteDialog = ref(false)

const filteredPersonnel = computed(() => {
  let result = personnelStore.personnelList

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(keyword) ||
        p.code.toLowerCase().includes(keyword)
    )
  }

  if (departmentFilter.value) {
    result = result.filter((p) => p.departmentId === departmentFilter.value)
  }

  if (statusFilter.value !== '') {
    result = result.filter((p) => p.status === statusFilter.value)
  }

  return result
})

onMounted(() => {
  personnelStore.fetchPersonnel()
  departmentStore.fetchDepartments()
})

const getDepartmentName = (id: string) => {
  return departmentStore.getDepartmentById(id)?.name || '-'
}

function handleSelectionChange(rows: Personnel[]) {
  selectedRows.value = rows
}

function editPersonnel(person: Personnel) {
  editingPersonnel.value = person
  showCreateModal.value = true
}

function confirmDelete(person: Personnel) {
  deletingPersonnel.value = person
  showDeleteDialog.value = true
}

async function handleDelete() {
  if (deletingPersonnel.value) {
    try {
      await personnelStore.deletePersonnel(deletingPersonnel.value.id)
      ElMessage.success('删除成功')
    } catch {
      ElMessage.error('删除失败')
    }
  }
  showDeleteDialog.value = false
  deletingPersonnel.value = null
}

async function handleBatchDelete() {
  try {
    await Promise.all(selectedRows.value.map((row) => personnelStore.deletePersonnel(row.id)))
    ElMessage.success(`成功删除 ${selectedRows.value.length} 名人员`)
    selectedRows.value = []
  } catch {
    ElMessage.error('批量删除失败')
  }
  showBatchDeleteDialog.value = false
}

async function handleSave(data: PersonnelCreateReq | PersonnelUpdateReq) {
  try {
    if (editingPersonnel.value) {
      await personnelStore.updatePersonnel(editingPersonnel.value.id, data as PersonnelUpdateReq)
      ElMessage.success('更新成功')
    } else {
      await personnelStore.createPersonnel(data as PersonnelCreateReq)
      ElMessage.success('创建成功')
    }
    handleClose()
  } catch {
    ElMessage.error('操作失败')
  }
}

function handleClose() {
  showCreateModal.value = false
  editingPersonnel.value = null
}
</script>

<style scoped>
.batch-action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.625rem 0.875rem;
  background-color: hsl(var(--muted) / 0.4);
  border-radius: 6px;
}

.table-container {
  background-color: hsl(var(--card));
  border-radius: 6px;
  border: 1px solid hsl(var(--border) / 0.4);
  overflow: hidden;
}

.card-item {
  background-color: hsl(var(--card));
  border-radius: 6px;
  padding: 0.75rem;
  border: 1px solid hsl(var(--border) / 0.4);
  transition: all 0.15s ease;
}

.card-item:hover {
  border-color: hsl(var(--border));
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.15s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>