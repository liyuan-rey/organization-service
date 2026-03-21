<template>
  <div class="space-y-4">
    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-3 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索部门名称/编码"
          clearable
          class="w-60"
          @input="handleSearch"
        >
          <template #prefix>
            <Search class="h-4 w-4" />
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="w-28" @change="handleSearch">
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
          <el-button size="small" type="danger" @click="confirmBatchDelete">
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
        :data="filteredDepartments"
        @selection-change="handleSelectionChange"
        class="w-full"
      >
        <el-table-column type="selection" width="44" />
        <el-table-column prop="name" label="部门名称" min-width="180">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ row.name }}</span>
              <el-tag size="small" type="info" effect="plain">{{ row.personCount ?? 0 }}人</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="编码" width="120">
          <template #default="{ row }">
            <code class="text-xs bg-muted px-1.5 py-0.5 rounded">{{ row.code }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            <span class="text-sm text-muted-foreground">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" class="!p-1" @click="editDepartment(row)">
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
        v-for="dept in filteredDepartments"
        :key="dept.id"
        class="card-item"
      >
        <div class="flex items-start justify-between gap-2">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1.5">
              <h3 class="font-medium text-sm">{{ dept.name }}</h3>
              <el-tag :type="dept.status === 1 ? 'success' : 'info'" size="small" effect="light">
                {{ dept.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
            <p class="text-xs text-muted-foreground mb-1">
              <code class="bg-muted px-1 py-0.5 rounded">{{ dept.code }}</code>
            </p>
            <p class="text-xs text-muted-foreground line-clamp-2">{{ dept.description || '暂无描述' }}</p>
            <div class="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
              <span>{{ dept.personCount ?? 0 }} 人</span>
              <span>{{ formatDate(dept.createTime, 'date') }}</span>
            </div>
          </div>
          <el-dropdown trigger="click">
            <button class="p-1 hover:bg-muted rounded transition-colors">
              <MoreVertical class="h-4 w-4 text-muted-foreground" />
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editDepartment(dept)">
                  <Edit class="h-4 w-4 mr-2" />编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(dept)">
                  <Trash class="h-4 w-4 mr-2 text-red-500" />
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredDepartments.length === 0 && !store.loading" class="col-span-full py-12">
        <el-empty description="暂无部门数据" />
      </div>
    </div>

    <!-- 新建/编辑部门弹窗 -->
    <DepartmentModal
      v-model:show="showCreateModal"
      :department="editingDepartment"
      @save="handleSave"
      @close="handleClose"
    />

    <!-- 删除确认对话框 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除部门 <span class="font-medium text-foreground">{{ deletingDepartment?.name }}</span> 吗？此操作无法撤销。
      </p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400" :show-close="false">
      <p class="text-sm text-muted-foreground">
        确定要删除选中的 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 个部门吗？此操作无法撤销。
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
import { useDepartmentStore } from '@/stores/department'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'
import DepartmentModal from '@/components/common/DepartmentModal.vue'
import { Plus, Search, List, Grid, Edit, Trash, MoreVertical } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const store = useDepartmentStore()

// 视图模式
const viewMode = ref<'table' | 'card'>('table')

// 搜索和筛选
const searchKeyword = ref('')
const statusFilter = ref<number | ''>('')

// 选择
const selectedRows = ref<Department[]>([])

// 弹窗状态
const showCreateModal = ref(false)
const editingDepartment = ref<Department | null>(null)
const showDeleteDialog = ref(false)
const deletingDepartment = ref<Department | null>(null)
const showBatchDeleteDialog = ref(false)

// 过滤后的部门列表
const filteredDepartments = computed(() => {
  let result = store.departments

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (d) =>
        d.name.toLowerCase().includes(keyword) ||
        d.code.toLowerCase().includes(keyword)
    )
  }

  if (statusFilter.value !== '') {
    result = result.filter((d) => d.status === statusFilter.value)
  }

  return result
})

onMounted(() => {
  store.fetchDepartments()
})

function formatDate(dateString: string, mode: 'full' | 'date' = 'full') {
  const date = new Date(dateString)
  if (mode === 'date') {
    return date.toLocaleDateString('zh-CN')
  }
  return date.toLocaleString('zh-CN')
}

function handleSearch() {
  // 搜索逻辑已在 computed 中处理
}

function handleSelectionChange(rows: Department[]) {
  selectedRows.value = rows
}

function editDepartment(dept: Department) {
  editingDepartment.value = dept
  showCreateModal.value = true
}

function confirmDelete(dept: Department) {
  deletingDepartment.value = dept
  showDeleteDialog.value = true
}

function confirmBatchDelete() {
  showBatchDeleteDialog.value = true
}

async function handleDelete() {
  if (deletingDepartment.value) {
    try {
      await store.deleteDepartment(deletingDepartment.value.id)
      ElMessage.success('删除成功')
    } catch {
      ElMessage.error('删除失败')
    }
  }
  showDeleteDialog.value = false
  deletingDepartment.value = null
}

async function handleBatchDelete() {
  try {
    await Promise.all(selectedRows.value.map((row) => store.deleteDepartment(row.id)))
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个部门`)
    selectedRows.value = []
  } catch {
    ElMessage.error('批量删除失败')
  }
  showBatchDeleteDialog.value = false
}

async function handleSave(data: DepartmentCreateReq | DepartmentUpdateReq) {
  try {
    if (editingDepartment.value) {
      await store.updateDepartment(editingDepartment.value.id, data as DepartmentUpdateReq)
      ElMessage.success('更新成功')
    } else {
      await store.createDepartment(data as DepartmentCreateReq)
      ElMessage.success('创建成功')
    }
    handleClose()
  } catch {
    ElMessage.error('操作失败')
  }
}

function handleClose() {
  showCreateModal.value = false
  editingDepartment.value = null
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

/* 滑入动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.15s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>