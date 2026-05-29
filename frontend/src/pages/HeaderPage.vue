<template>
  <CrudPage title="Header 配置" description="维护调用目标系统时需要携带的 Header" base-url="/headers" :fields="headerFields" />
</template>
<script setup>
import CrudPage from '../components/CrudPage.vue'
import { headerFields } from './config'
</script>
