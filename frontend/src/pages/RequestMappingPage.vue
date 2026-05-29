<template>
  <CrudPage title="请求映射配置" description="将来源系统请求转换为目标系统请求" base-url="/request-mappings" :fields="mappingFields" :defaults="defaults" />
</template>
<script setup>
import CrudPage from '../components/CrudPage.vue'
import { mappingFields } from './config'
const defaults = { mappingType: 'FIELD', sort: 0 }
</script>
