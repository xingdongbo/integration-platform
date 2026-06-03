<template>
  <el-container style="min-height: 100vh">
    <el-aside width="250px" style="background: #111827">
      <div style="height: 60px; color: white; display: flex; align-items: center; padding: 0 20px; font-weight: 700">
        企业数据交换平台
      </div>
      <el-menu :default-active="active" background-color="#111827" text-color="#cbd5e1" active-text-color="#60a5fa" @select="active = $event">
        <el-menu-item v-for="item in menu" :key="item.key" :index="item.key">
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: white; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e5e7eb">
        <div>{{ current?.label }}</div>
        <el-tag type="success">V1 基础版</el-tag>
      </el-header>
      <el-main>
        <component :is="current.component" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import SystemPage from './pages/SystemPage.vue'
import InterfacePage from './pages/InterfacePage.vue'
import HeaderPage from './pages/HeaderPage.vue'
import RequestMappingPage from './pages/RequestMappingPage.vue'
import ResponseMappingPage from './pages/ResponseMappingPage.vue'
import ExchangeTestPage from './pages/ExchangeTestPage.vue'
import ExchangeLogPage from './pages/ExchangeLogPage.vue'
import RetryTaskPage from './pages/RetryTaskPage.vue'

const active = ref('systems')
const menu = [
  { key: 'systems', label: '系统管理', component: SystemPage },
  { key: 'interfaces', label: '接口管理', component: InterfacePage },
  { key: 'headers', label: 'Header 配置', component: HeaderPage },
  { key: 'requestMappings', label: '请求映射', component: RequestMappingPage },
  { key: 'responseMappings', label: '响应映射', component: ResponseMappingPage },
  { key: 'exchangeTest', label: '接口测试', component: ExchangeTestPage },
  { key: 'exchangeLogs', label: '交换日志', component: ExchangeLogPage },
  { key: 'retryTasks', label: '重试任务', component: RetryTaskPage }
]
const current = computed(() => menu.find((item) => item.key === active.value) || menu[0])
</script>
