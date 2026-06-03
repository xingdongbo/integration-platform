<template>
  <el-card class="page-card" shadow="never">
    <template #header>
      <div class="toolbar">
        <div>
          <strong>{{ title }}</strong>
          <div v-if="description" style="color: #6b7280; font-size: 13px; margin-top: 4px">{{ description }}</div>
        </div>
        <div>
          <el-button @click="load">刷新</el-button>
          <el-button type="primary" @click="openCreate">新增</el-button>
        </div>
      </div>
    </template>

    <el-table v-loading="loading" :data="records" border stripe style="width: 100%">
      <el-table-column v-for="field in tableFields" :key="field.prop" :prop="field.prop" :label="field.label" :min-width="field.width || 120" show-overflow-tooltip />
      <el-table-column label="操作" fixed="right" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除该记录？" @confirm="remove(scope.row)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="load"
        @current-change="load"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? `编辑${title}` : `新增${title}`" width="760px" destroy-on-close>
      <el-form :model="form" label-width="130px">
        <div class="form-grid">
          <el-form-item v-for="field in fields" :key="field.prop" :label="field.label" :required="field.required">
            <el-select v-if="field.options" v-model="form[field.prop]" clearable filterable style="width: 100%">
              <el-option v-for="item in field.options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-input-number v-else-if="field.type === 'number'" v-model="form[field.prop]" :min="field.min ?? 0" style="width: 100%" />
            <el-input v-else-if="field.type === 'textarea'" v-model="form[field.prop]" type="textarea" :rows="4" />
            <el-input v-else v-model="form[field.prop]" :placeholder="field.placeholder" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createRecord, deleteRecord, listPage, updateRecord } from '../api/crud'

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  baseUrl: { type: String, required: true },
  fields: { type: Array, required: true },
  columns: { type: Array, default: () => [] },
  defaults: { type: Object, default: () => ({}) }
})

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const records = ref([])
const pagination = reactive({ current: 1, size: 20, total: 0 })
const form = reactive({})

const tableFields = computed(() => props.columns.length ? props.columns : props.fields)

function resetForm(row = {}) {
  Object.keys(form).forEach((key) => delete form[key])
  props.fields.forEach((field) => {
    form[field.prop] = row[field.prop] ?? props.defaults[field.prop] ?? null
  })
}

async function load() {
  loading.value = true
  try {
    const page = await listPage(props.baseUrl, { current: pagination.current, size: pagination.size })
    records.value = page?.records || []
    pagination.total = Number(page?.total || records.value.length)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  resetForm(row)
  dialogVisible.value = true
}

async function save() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateRecord(props.baseUrl, editingId.value, { id: editingId.value, ...form })
    } else {
      await createRecord(props.baseUrl, form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await deleteRecord(props.baseUrl, row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>
