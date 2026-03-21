<template>
  <div class="space-y-4">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">岗位管理</h1>
        <p class="text-muted-foreground text-sm">管理组织岗位和职级体系</p>
      </div>
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon class="mr-1"><Plus /></el-icon>
        新建岗位
      </el-button>
    </div>

    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-4 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索岗位名称/编码"
          clearable
          class="w-56"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="categoryFilter" placeholder="岗位类别" clearable class="w-36">
          <el-option label="全部" value="" />
          <el-option label="技术类" value="Technical" />
          <el-option label="管理类" value="Management" />
          <el-option label="销售类" value="Sales" />
          <el-option label="人力资源类" value="HR" />
          <el-option label="财务类" value="Finance" />
          <el-option label="运营类" value="Operations" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="w-28">
          <el-option label="全部" value="" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>

      <!-- 视图切换 -->
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button value="table">
          <el-icon><List /></el-icon>
        </el-radio-button>
        <el-radio-button value="card">
          <el-icon><Grid /></el-icon>
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 批量操作栏 -->
    <transition name="slide-down">
      <div v-if="selectedRows.length > 0" class="bg-muted/50 rounded-lg p-3 flex items-center justify-between">
        <span class="text-sm text-muted-foreground">
          已选择 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 项
        </span>
        <div class="flex gap-2">
          <el-button size="small" @click="selectedRows = []">取消选择</el-button>
          <el-button size="small" type="danger" @click="showBatchDeleteDialog = true">
            <el-icon class="mr-1"><Trash /></el-icon>
            批量删除
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 表格视图 -->
    <el-card v-show="viewMode === 'table'" class="shadow-sm">
      <el-table
        v-loading="store.loading"
        :data="filteredPositions"
        @selection-change="handleSelectionChange"
        stripe
        highlight-current-row
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="岗位名称" min-width="150">
          <template #default="{ row }">
            <div>
              <p class="font-medium">{{ row.name }}</p>
              <p class="text-xs text-muted-foreground">{{ row.code }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="jobLevel" label="职级" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.jobLevel" size="small" type="info">{{ row.jobLevel }}</el-tag>
            <span v-else class="text-muted-foreground">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="jobCategory" label="岗位类别" width="120">
          <template #default="{ row }">
            {{ getCategoryLabel(row.jobCategory) }}
          </template>
        </el-table-column>
        <el-table-column label="薪资范围" min-width="150">
          <template #default="{ row }">
            <span v-if="row.minSalary || row.maxSalary" class="text-sm">
              ¥{{ formatSalary(row.minSalary) }} - ¥{{ formatSalary(row.maxSalary) }}
            </span>
            <span v-else class="text-muted-foreground">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editPosition(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" link size="small" @click="confirmDelete(row)">
              <el-icon><Trash /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 卡片视图 -->
    <div v-show="viewMode === 'card'" class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <el-card
        v-for="pos in filteredPositions"
        :key="pos.id"
        class="shadow-sm hover:shadow-md transition-shadow"
        :body-style="{ padding: '1rem' }"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <h3 class="font-semibold">{{ pos.name }}</h3>
              <el-tag :type="pos.status === 1 ? 'success' : 'info'" size="small">
                {{ pos.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
            <div class="flex items-center gap-2 mb-2 text-sm">
              <el-tag v-if="pos.jobLevel" size="small" type="info">{{ pos.jobLevel }}</el-tag>
              <span class="text-muted-foreground">{{ getCategoryLabel(pos.jobCategory) }}</span>
            </div>
            <p class="text-sm text-muted-foreground line-clamp-2 mb-2">
              {{ pos.description || '暂无描述' }}
            </p>
            <p v-if="pos.minSalary || pos.maxSalary" class="text-sm text-primary font-medium">
              ¥{{ formatSalary(pos.minSalary) }} - ¥{{ formatSalary(pos.maxSalary) }}
            </p>
          </div>
          <el-dropdown trigger="click">
            <el-button type="primary" link>
              <el-icon><MoreVertical /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editPosition(pos)">
                  <el-icon class="mr-1"><Edit /></el-icon>编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(pos)">
                  <el-icon class="mr-1 text-red-500"><Delete /></el-icon>
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-card>

      <!-- 空状态 -->
      <div v-if="filteredPositions.length === 0 && !store.loading" class="col-span-full">
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
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400">
      <p>确定要删除岗位 "{{ deletingPosition?.name }}" 吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400">
      <p>确定要删除选中的 {{ selectedRows.length }} 个岗位吗？此操作无法撤销。</p>
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

// 视图模式
const viewMode = ref<'table' | 'card'>('table')

// 搜索和筛选
const searchKeyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref<number | ''>('')

// 选择
const selectedRows = ref<Position[]>([])

// 弹窗状态
const showCreateModal = ref(false)
const editingPosition = ref<Position | null>(null)
const showDeleteDialog = ref(false)
const deletingPosition = ref<Position | null>(null)
const showBatchDeleteDialog = ref(false)

// 岗位类别映射
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

// 过滤后的岗位列表
const filteredPositions = computed(() => {
  let result = store.positions

  // 关键字搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(keyword) ||
        p.code.toLowerCase().includes(keyword)
    )
  }

  // 类别筛选
  if (categoryFilter.value) {
    result = result.filter((p) => p.jobCategory === categoryFilter.value)
  }

  // 状态筛选
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
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>