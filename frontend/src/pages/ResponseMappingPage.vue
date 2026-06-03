<template>
  <CrudPage title="响应映射配置" description="将目标系统响应转换为来源系统响应" base-url="/response-mappings" :fields="mappingFields" :defaults="defaults" />
</template>
<script setup>
import CrudPage from '../components/CrudPage.vue'
import { mappingFields } from './config'
const defaults = { mappingType: 'FIELD', sort: 0 }
</script>
