<template>
  <div class="space-y-4">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">部门管理</h1>
        <p class="text-muted-foreground text-sm">管理部门信息和组织架构</p>
      </div>
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon class="mr-1"><Plus /></el-icon>
        新建部门
      </el-button>
    </div>

    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-4 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索部门名称/编码"
          clearable
          class="w-64"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable class="w-32" @change="handleSearch">
          <el-option label="全部" value="" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>

      <!-- 视图切换 -->
      <div class="flex gap-2">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="table">
            <el-icon><List /></el-icon>
          </el-radio-button>
          <el-radio-button value="card">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <transition name="slide-down">
      <div v-if="selectedRows.length > 0" class="bg-muted/50 rounded-lg p-3 flex items-center justify-between">
        <span class="text-sm text-muted-foreground">
          已选择 <span class="font-medium text-foreground">{{ selectedRows.length }}</span> 项
        </span>
        <div class="flex gap-2">
          <el-button size="small" @click="selectedRows = []">取消选择</el-button>
          <el-button size="small" type="danger" @click="confirmBatchDelete">
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
        :data="filteredDepartments"
        @selection-change="handleSelectionChange"
        stripe
        highlight-current-row
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="部门名称" min-width="150">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ row.name }}</span>
              <el-tag size="small" type="info">{{ row.personCount ?? 0 }}人</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="部门编码" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editDepartment(row)">
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
        v-for="dept in filteredDepartments"
        :key="dept.id"
        class="shadow-sm hover:shadow-md transition-shadow cursor-pointer"
        :body-style="{ padding: '1rem' }"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <h3 class="font-semibold">{{ dept.name }}</h3>
              <el-tag :type="dept.status === 1 ? 'success' : 'info'" size="small">
                {{ dept.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
            <p class="text-sm text-muted-foreground mb-1">编码：{{ dept.code }}</p>
            <p class="text-sm text-muted-foreground line-clamp-2">{{ dept.description || '暂无描述' }}</p>
            <div class="flex items-center gap-4 mt-3 text-sm text-muted-foreground">
              <span>{{ dept.personCount ?? 0 }} 人</span>
              <span>{{ formatDate(dept.createTime, 'date') }}</span>
            </div>
          </div>
          <el-dropdown trigger="click">
            <el-button type="primary" link>
              <el-icon><MoreVertical /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editDepartment(dept)">
                  <el-icon class="mr-1"><Edit /></el-icon>编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(dept)">
                  <el-icon class="mr-1 text-red-500"><Delete /></el-icon>
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-card>

      <!-- 空状态 -->
      <div v-if="filteredDepartments.length === 0 && !store.loading" class="col-span-full">
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
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400">
      <p>确定要删除部门 "{{ deletingDepartment?.name }}" 吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400">
      <p>确定要删除选中的 {{ selectedRows.length }} 个部门吗？此操作无法撤销。</p>
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

  // 关键字搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (d) =>
        d.name.toLowerCase().includes(keyword) ||
        d.code.toLowerCase().includes(keyword)
    )
  }

  // 状态筛选
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
/* 滑入动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>