<template>
  <div class="space-y-4">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">部门岗位管理</h1>
        <p class="text-muted-foreground text-sm">配置部门与岗位的关联关系</p>
      </div>
      <el-button type="primary" @click="showCreateModal = true">
        <el-icon class="mr-1"><Plus /></el-icon>
        配置岗位
      </el-button>
    </div>

    <!-- 筛选条件 -->
    <el-card class="shadow-sm">
      <div class="flex flex-wrap gap-4">
        <el-select v-model="filterDepartmentId" placeholder="选择部门" clearable class="w-48">
          <el-option
            v-for="d in departmentOptions"
            :key="d.value"
            :label="d.label"
            :value="d.value"
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
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card class="shadow-sm">
      <el-table v-loading="store.loading" :data="filteredData" stripe highlight-current-row>
        <el-table-column prop="departmentName" label="部门" min-width="150">
          <template #default="{ row }">
            <span class="font-medium">{{ row.departmentName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="positionName" label="岗位" min-width="150" />
        <el-table-column prop="isPrimary" label="岗位类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPrimary ? 'primary' : 'info'" size="small">
              {{ row.isPrimary ? '主岗' : '普通岗' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="confirmDelete(row)">
              <el-icon><Trash /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="filteredData.length === 0 && !store.loading" description="暂无部门岗位关联数据" />
    </el-card>

    <!-- 新建弹窗 -->
    <DepartmentPositionModal
      v-model:show="showCreateModal"
      @save="handleSave"
      @close="showCreateModal = false"
    />

    <!-- 删除确认对话框 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400">
      <p>确定要删除部门 "{{ deletingItem?.departmentName }}" 的岗位 "{{ deletingItem?.positionName }}" 配置吗？</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDepartmentPositionStore } from '@/stores/departmentPosition'
import { useDepartmentStore } from '@/stores/department'
import { usePositionStore } from '@/stores/position'
import type { DepartmentPosition, DepartmentPositionReq } from '@/types'
import DepartmentPositionModal from '@/components/common/DepartmentPositionModal.vue'
import { Plus, Trash } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const store = useDepartmentPositionStore()
const departmentStore = useDepartmentStore()
const positionStore = usePositionStore()

const showCreateModal = ref(false)
const showDeleteDialog = ref(false)
const deletingItem = ref<DepartmentPosition | null>(null)
const filterDepartmentId = ref('')
const filterPositionId = ref('')

const departmentOptions = computed(() =>
  departmentStore.departments.map((d) => ({ label: d.name, value: d.id }))
)

const positionOptions = computed(() =>
  positionStore.positions.map((p) => ({ label: p.name, value: p.id }))
)

const filteredData = computed(() => {
  let data = store.departmentPositions
  if (filterDepartmentId.value) {
    data = data.filter((item) => item.departmentId === filterDepartmentId.value)
  }
  if (filterPositionId.value) {
    data = data.filter((item) => item.positionId === filterPositionId.value)
  }
  return data
})

onMounted(() => {
  store.fetchAllDepartmentPositions()
  departmentStore.fetchDepartments()
  positionStore.fetchPositions()
})

function formatDate(dateString: string) {
  return new Date(dateString).toLocaleString('zh-CN')
}

function confirmDelete(item: DepartmentPosition) {
  deletingItem.value = item
  showDeleteDialog.value = true
}

async function handleDelete() {
  if (deletingItem.value) {
    try {
      await store.deleteDepartmentPosition(
        deletingItem.value.departmentId,
        deletingItem.value.positionId
      )
      ElMessage.success('删除成功')
    } catch {
      ElMessage.error('删除失败')
    }
  }
  showDeleteDialog.value = false
  deletingItem.value = null
}

async function handleSave(data: DepartmentPositionReq) {
  try {
    await store.createDepartmentPosition(data)
    ElMessage.success('配置成功')
    showCreateModal.value = false
  } catch {
    ElMessage.error('配置失败')
  }
}
</script>