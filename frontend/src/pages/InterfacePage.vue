<template>
  <CrudPage title="接口管理" description="配置来源系统和目标系统之间的交换关系" base-url="/interfaces" :fields="interfaceFields" :defaults="defaults" />
</template>
<script setup>
import CrudPage from '../components/CrudPage.vue'
import { interfaceFields } from './config'
const defaults = { method: 'POST', contentType: 'application/json', timeout: 5000, status: 'ENABLED' }
</script>
