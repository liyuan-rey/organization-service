<template>
  <div class="space-y-4">
    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-3 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索岗位名称/编码"
          clearable
          class="w-52"
        >
          <template #prefix>
            <Search class="h-4 w-4" />
          </template>
        </el-input>
        <el-select v-model="categoryFilter" placeholder="类别" clearable class="w-28">
          <el-option label="全部" value="" />
          <el-option label="技术类" value="Technical" />
          <el-option label="管理类" value="Management" />
          <el-option label="销售类" value="Sales" />
          <el-option label="人力资源类" value="HR" />
          <el-option label="财务类" value="Finance" />
          <el-option label="运营类" value="Operations" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="w-24">
          <el-option label="全部" value="" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
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
        v-loading="store.loading"
        :data="filteredPositions"
        @selection-change="handleSelectionChange"
        class="w-full"
      >
        <el-table-column type="selection" width="44" />
        <el-table-column prop="name" label="岗位名称" min-width="160">
          <template #default="{ row }">
            <div>
              <p class="font-medium text-sm">{{ row.name }}</p>
              <p class="text-xs text-muted-foreground">{{ row.code }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="jobLevel" label="职级" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.jobLevel" size="small" type="info" effect="plain">{{ row.jobLevel }}</el-tag>
            <span v-else class="text-muted-foreground text-xs">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="jobCategory" label="类别" width="100">
          <template #default="{ row }">
            <span class="text-sm">{{ getCategoryLabel(row.jobCategory) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="薪资范围" min-width="140">
          <template #default="{ row }">
            <span v-if="row.minSalary || row.maxSalary" class="text-sm text-muted-foreground">
              ¥{{ formatSalary(row.minSalary) }} - ¥{{ formatSalary(row.maxSalary) }}
            </span>
            <span v-else class="text-muted-foreground text-xs">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="text-sm text-muted-foreground">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" class="!p-1" @click="editPosition(row)">
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
    <div v-show="viewMode === 'card'" class="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="pos in filteredPositions"
        :key="pos.id"
        class="card-item"
      >
        <div class="flex items-start justify-between gap-2">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1.5">
              <h3 class="font-medium text-sm">{{ pos.name }}</h3>
              <el-tag :type="pos.status === 1 ? 'success' : 'info'" size="small" effect="light">
                {{ pos.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
            <div class="flex items-center gap-2 mb-2">
              <el-tag v-if="pos.jobLevel" size="small" type="info" effect="plain">{{ pos.jobLevel }}</el-tag>
              <span class="text-xs text-muted-foreground">{{ getCategoryLabel(pos.jobCategory) }}</span>
            </div>
            <p class="text-xs text-muted-foreground line-clamp-2 mb-2">
              {{ pos.description || '暂无描述' }}
            </p>
            <p v-if="pos.minSalary || pos.maxSalary" class="text-xs text-primary font-medium">
              ¥{{ formatSalary(pos.minSalary) }} - ¥{{ formatSalary(pos.maxSalary) }}
            </p>
          </div>
          <el-dropdown trigger="click">
            <button class="p-1 hover:bg-muted rounded transition-colors">
              <MoreVertical class="h-4 w-4 text-muted-foreground" />
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editPosition(pos)">
                  <Edit class="h-4 w-4 mr-2" />编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(pos)">
                  <Trash class="h-4 w-4 mr-2 text-red-500" />
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredPositions.length === 0 && !store.loading" class="col-span-full py-12">
        <el-empty description="暂无岗位数据" />
      </div>
    </div>

    <!-- 新建/编辑岗位弹窗 -->
    <PositionModal
      v-model:show="showCreateModal"
      :position="editingPosition"
      @save="handleSave"
      @close="handleClose"
    />

    <!-- 删除确认对话框 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除岗位 <span class="font-medium text-foreground">{{ deletingPosition?.name }}</span> 吗？此操作无法撤销。
      </p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除选中的 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 个岗位吗？此操作无法撤销。
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
import { usePositionStore } from '@/stores/position'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'
import PositionModal from '@/components/common/PositionModal.vue'
import { Plus, Search, List, Grid, Edit, Trash, MoreVertical } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const store = usePositionStore()

const viewMode = ref<'table' | 'card'>('table')
const searchKeyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref<number | ''>('')
const selectedRows = ref<Position[]>([])

const showCreateModal = ref(false)
const editingPosition = ref<Position | null>(null)
const showDeleteDialog = ref(false)
const deletingPosition = ref<Position | null>(null)
const showBatchDeleteDialog = ref(false)

const categoryMap: Record<string, string> = {
  Technical: '技术类',
  Management: '管理类',
  Sales: '销售类',
  HR: '人力资源类',
  Finance: '财务类',
  Operations: '运营类',
}

const getCategoryLabel = (category: string | null) => {
  return category ? categoryMap[category] || category : '-'
}

const filteredPositions = computed(() => {
  let result = store.positions

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(keyword) ||
        p.code.toLowerCase().includes(keyword)
    )
  }

  if (categoryFilter.value) {
    result = result.filter((p) => p.jobCategory === categoryFilter.value)
  }

  if (statusFilter.value !== '') {
    result = result.filter((p) => p.status === statusFilter.value)
  }

  return result
})

onMounted(() => {
  store.fetchPositions()
})

const formatSalary = (salary: number | null) => {
  return salary?.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) || '0'
}

function handleSelectionChange(rows: Position[]) {
  selectedRows.value = rows
}

function editPosition(pos: Position) {
  editingPosition.value = pos
  showCreateModal.value = true
}

function confirmDelete(pos: Position) {
  deletingPosition.value = pos
  showDeleteDialog.value = true
}

async function handleDelete() {
  if (deletingPosition.value) {
    try {
      await store.deletePosition(deletingPosition.value.id)
      ElMessage.success('删除成功')
    } catch {
      ElMessage.error('删除失败')
    }
  }
  showDeleteDialog.value = false
  deletingPosition.value = null
}

async function handleBatchDelete() {
  try {
    await Promise.all(selectedRows.value.map((row) => store.deletePosition(row.id)))
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个岗位`)
    selectedRows.value = []
  } catch {
    ElMessage.error('批量删除失败')
  }
  showBatchDeleteDialog.value = false
}

async function handleSave(data: PositionCreateReq | PositionUpdateReq) {
  try {
    if (editingPosition.value) {
      await store.updatePosition(editingPosition.value.id, data as PositionUpdateReq)
      ElMessage.success('更新成功')
    } else {
      await store.createPosition(data as PositionCreateReq)
      ElMessage.success('创建成功')
    }
    handleClose()
  } catch {
    ElMessage.error('操作失败')
  }
}

function handleClose() {
  showCreateModal.value = false
  editingPosition.value = null
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

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>