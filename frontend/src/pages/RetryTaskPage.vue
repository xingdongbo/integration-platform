<template>
  <el-card class="page-card" shadow="never">
    <template #header>
      <div class="toolbar">
        <div>
          <strong>重试任务</strong>
          <div style="color: #6b7280; font-size: 13px; margin-top: 4px">查看失败交换并执行人工重试</div>
        </div>
        <el-button @click="load">刷新</el-button>
      </div>
    </template>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="traceId" label="TraceId" width="260" show-overflow-tooltip />
      <el-table-column prop="interfaceCode" label="接口编码" width="220" show-overflow-tooltip />
      <el-table-column prop="retryCount" label="重试次数" width="100" />
      <el-table-column prop="maxRetryCount" label="最大次数" width="100" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="lastErrorMsg" label="最近错误" min-width="240" show-overflow-tooltip />
      <el-table-column prop="nextRetryTime" label="下次重试时间" width="180" />
      <el-table-column label="操作" fixed="right" width="120">
        <template #default="scope">
          <el-button link type="primary" :loading="retryingTraceId === scope.row.traceId" @click="retry(scope.row)">人工重试</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px">
      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="load" @current-change="load" />
    </div>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listPage } from '../api/crud'
import { retryExchange } from '../api/exchange'

const loading = ref(false)
const retryingTraceId = ref('')
const records = ref([])
const pagination = reactive({ current: 1, size: 20, total: 0 })

async function load() {
  loading.value = true
  try {
    const page = await listPage('/retry-tasks', { current: pagination.current, size: pagination.size })
    records.value = page?.records || []
    pagination.total = Number(page?.total || records.value.length)
  } finally {
    loading.value = false
  }
}

async function retry(row) {
  retryingTraceId.value = row.traceId
  try {
    await retryExchange(row.traceId)
    ElMessage.success('重试执行完成')
    await load()
  } finally {
    retryingTraceId.value = ''
  }
}

onMounted(load)
</script>
