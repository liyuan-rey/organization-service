<template>
  <div class="space-y-4">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">人员管理</h1>
        <p class="text-muted-foreground text-sm">管理员工信息和人事档案</p>
      </div>
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon class="mr-1"><Plus /></el-icon>
        新建人员
      </el-button>
    </div>

    <!-- 工具栏 -->
    <div class="flex flex-col sm:flex-row gap-4 justify-between">
      <!-- 搜索和筛选 -->
      <div class="flex gap-2 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名/工号"
          clearable
          class="w-48"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="departmentFilter" placeholder="所属部门" clearable class="w-40">
          <el-option
            v-for="dept in departmentStore.departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable class="w-28">
          <el-option label="全部" value="" />
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="0" />
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
        v-loading="personnelStore.loading"
        :data="filteredPersonnel"
        @selection-change="handleSelectionChange"
        stripe
        highlight-current-row
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="姓名" min-width="140">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar :size="36" class="bg-primary">
                {{ row.name.charAt(0) }}
              </el-avatar>
              <div>
                <p class="font-medium">{{ row.name }}</p>
                <p class="text-xs text-muted-foreground">{{ row.code }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="departmentId" label="所属部门" min-width="120">
          <template #default="{ row }">
            {{ getDepartmentName(row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editPersonnel(row)">
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
    <div v-show="viewMode === 'card'" class="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      <el-card
        v-for="person in filteredPersonnel"
        :key="person.id"
        class="shadow-sm hover:shadow-md transition-shadow"
        :body-style="{ padding: '1rem' }"
      >
        <div class="flex items-start gap-4">
          <el-avatar :size="48" class="bg-primary flex-shrink-0">
            {{ person.name.charAt(0) }}
          </el-avatar>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1">
              <h3 class="font-semibold truncate">{{ person.name }}</h3>
              <el-tag :type="person.status === 1 ? 'success' : 'info'" size="small">
                {{ person.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </div>
            <p class="text-sm text-muted-foreground mb-2">{{ person.position || '暂无职位' }}</p>
            <div class="text-xs text-muted-foreground space-y-1">
              <p class="truncate">{{ getDepartmentName(person.departmentId) }}</p>
              <p class="truncate">{{ person.email || '-' }}</p>
            </div>
          </div>
          <el-dropdown trigger="click">
            <el-button type="primary" link>
              <el-icon><MoreVertical /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editPersonnel(person)">
                  <el-icon class="mr-1"><Edit /></el-icon>编辑
                </el-dropdown-item>
                <el-dropdown-item divided @click="confirmDelete(person)">
                  <el-icon class="mr-1 text-red-500"><Delete /></el-icon>
                  <span class="text-red-500">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-card>

      <!-- 空状态 -->
      <div v-if="filteredPersonnel.length === 0 && !personnelStore.loading" class="col-span-full">
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
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400">
      <p>确定要删除人员 "{{ deletingPersonnel?.name }}" 吗？此操作无法撤销。</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog v-model="showBatchDeleteDialog" title="确认批量删除" width="400">
      <p>确定要删除选中的 {{ selectedRows.length }} 名人员吗？此操作无法撤销。</p>
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

// 视图模式
const viewMode = ref<'table' | 'card'>('table')

// 搜索和筛选
const searchKeyword = ref('')
const departmentFilter = ref('')
const statusFilter = ref<number | ''>('')

// 选择
const selectedRows = ref<Personnel[]>([])

// 弹窗状态
const showCreateModal = ref(false)
const editingPersonnel = ref<Personnel | null>(null)
const showDeleteDialog = ref(false)
const deletingPersonnel = ref<Personnel | null>(null)
const showBatchDeleteDialog = ref(false)

// 过滤后的人员列表
const filteredPersonnel = computed(() => {
  let result = personnelStore.personnelList

  // 关键字搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(keyword) ||
        p.code.toLowerCase().includes(keyword)
    )
  }

  // 部门筛选
  if (departmentFilter.value) {
    result = result.filter((p) => p.departmentId === departmentFilter.value)
  }

  // 状态筛选
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
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>