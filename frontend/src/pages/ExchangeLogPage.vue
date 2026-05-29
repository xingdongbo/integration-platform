<template>
  <el-card class="page-card" shadow="never">
    <template #header>
      <div class="toolbar">
        <div>
          <strong>交换日志</strong>
          <div style="color: #6b7280; font-size: 13px; margin-top: 4px">查看每次接口交换的完整链路和原始报文</div>
        </div>
        <el-button @click="load">刷新</el-button>
      </div>
    </template>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="traceId" label="TraceId" width="260" show-overflow-tooltip />
      <el-table-column prop="interfaceCode" label="接口编码" width="220" show-overflow-tooltip />
      <el-table-column prop="sourceSystem" label="来源" width="100" />
      <el-table-column prop="targetSystem" label="目标" width="100" />
      <el-table-column prop="businessNo" label="业务单号" width="150" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="executeTime" label="耗时(ms)" width="110" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" fixed="right" width="100">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px">
      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="load" @current-change="load" />
    </div>

    <el-drawer v-model="detailVisible" title="交换日志详情" size="60%">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="TraceId">{{ detail.traceId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="接口编码">{{ detail.interfaceCode }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detail.executeTime }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ detail.errorMsg || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4>来源原始请求</h4><pre class="code-block">{{ detail?.sourceRequestRaw }}</pre>
      <h4>目标请求</h4><pre class="code-block">{{ detail?.targetRequestData }}</pre>
      <h4>目标原始响应</h4><pre class="code-block">{{ detail?.targetResponseRaw }}</pre>
      <h4>来源响应</h4><pre class="code-block">{{ detail?.sourceResponseData }}</pre>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { listPage } from '../api/crud'

const loading = ref(false)
const records = ref([])
const detail = ref(null)
const detailVisible = ref(false)
const pagination = reactive({ current: 1, size: 20, total: 0 })

async function load() {
  loading.value = true
  try {
    const page = await listPage('/exchange-logs', { current: pagination.current, size: pagination.size })
    records.value = page?.records || []
    pagination.total = Number(page?.total || records.value.length)
  } finally {
    loading.value = false
  }
}

function openDetail(row) {
  detail.value = row
  detailVisible.value = true
}

onMounted(load)
</script>
