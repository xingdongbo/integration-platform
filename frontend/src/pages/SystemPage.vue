<template>
  <CrudPage title="系统管理" description="维护 ERP、WMS、MES、WCS 等系统基础信息" base-url="/systems" :fields="systemFields" :defaults="{ status: 'ENABLED' }" />
</template>
<script setup>
import CrudPage from '../components/CrudPage.vue'
import { systemFields } from './config'
</script>
