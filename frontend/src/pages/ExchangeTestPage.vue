<template>
  <el-card class="page-card" shadow="never">
    <template #header>
      <strong>接口测试</strong>
      <div style="color: #6b7280; font-size: 13px; margin-top: 4px">输入接口编码和 JSON 请求参数，查看目标请求、目标响应和最终响应</div>
    </template>

    <el-form label-width="100px">
      <el-form-item label="接口编码" required>
        <el-input v-model="interfaceCode" placeholder="WMS_GET_ERP_INBOUND" />
      </el-form-item>
      <el-form-item label="请求 JSON" required>
        <el-input v-model="requestJson" type="textarea" :rows="12" placeholder='{"orderNo":"IN001"}' />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">执行测试</el-button>
        <el-button @click="formatJson">格式化 JSON</el-button>
      </el-form-item>
    </el-form>

    <el-divider />
    <el-descriptions v-if="result" :column="1" border>
      <el-descriptions-item label="TraceId">{{ result.traceId }}</el-descriptions-item>
      <el-descriptions-item label="目标请求">
        <pre class="code-block">{{ pretty(result.targetRequest) }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="目标原始响应">
        <pre class="code-block">{{ result.targetResponseRaw }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="最终返回来源响应">
        <pre class="code-block">{{ pretty(result.sourceResponse) }}</pre>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { testExchange } from '../api/exchange'

const interfaceCode = ref('')
const requestJson = ref('{\n  "orderNo": "IN001"\n}')
const result = ref(null)
const loading = ref(false)

function parseRequest() {
  try {
    return JSON.parse(requestJson.value || '{}')
  } catch (error) {
    ElMessage.error('请求 JSON 格式不正确')
    throw error
  }
}

function pretty(value) {
  return JSON.stringify(value ?? {}, null, 2)
}

function formatJson() {
  requestJson.value = pretty(parseRequest())
}

async function submit() {
  if (!interfaceCode.value) {
    ElMessage.warning('请输入接口编码')
    return
  }
  loading.value = true
  try {
    result.value = await testExchange(interfaceCode.value, parseRequest())
    ElMessage.success('测试完成')
  } finally {
    loading.value = false
  }
}
</script>
