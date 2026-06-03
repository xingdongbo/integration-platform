export const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '停用', value: 'DISABLED' }
]

export const methodOptions = [
  { label: 'POST', value: 'POST' },
  { label: 'GET', value: 'GET' }
]

export const mappingTypeOptions = [
  { label: '字段映射 FIELD', value: 'FIELD' },
  { label: '固定值 CONST', value: 'CONST' },
  { label: '默认值 DEFAULT', value: 'DEFAULT' },
  { label: '简单表达式 EXPRESSION', value: 'EXPRESSION' }
]

export const systemFields = [
  { prop: 'systemCode', label: '系统编码', required: true, placeholder: 'ERP/WMS/MES/WCS' },
  { prop: 'systemName', label: '系统名称', required: true },
  { prop: 'systemType', label: '系统类型', required: true },
  { prop: 'baseUrl', label: '基础地址' },
  { prop: 'status', label: '状态', options: statusOptions },
  { prop: 'remark', label: '备注', type: 'textarea' }
]

export const interfaceFields = [
  { prop: 'interfaceCode', label: '接口编码', required: true, placeholder: 'WMS_GET_ERP_INBOUND' },
  { prop: 'interfaceName', label: '接口名称', required: true },
  { prop: 'sourceSystem', label: '来源系统', required: true },
  { prop: 'targetSystem', label: '目标系统', required: true },
  { prop: 'targetUrl', label: '目标地址', required: true },
  { prop: 'method', label: '请求方式', options: methodOptions },
  { prop: 'contentType', label: 'Content-Type' },
  { prop: 'timeout', label: '超时(ms)', type: 'number', min: 100 },
  { prop: 'status', label: '状态', options: statusOptions },
  { prop: 'remark', label: '备注', type: 'textarea' }
]

export const headerFields = [
  { prop: 'interfaceCode', label: '接口编码', required: true },
  { prop: 'headerName', label: 'Header 名称', required: true },
  { prop: 'headerValue', label: 'Header 值', required: true, type: 'textarea' }
]

export const mappingFields = [
  { prop: 'interfaceCode', label: '接口编码', required: true },
  { prop: 'sourceField', label: '来源字段' },
  { prop: 'targetField', label: '目标字段', required: true },
  { prop: 'mappingType', label: '映射类型', required: true, options: mappingTypeOptions },
  { prop: 'defaultValue', label: '默认/固定值' },
  { prop: 'expression', label: '表达式' },
  { prop: 'sort', label: '排序', type: 'number' }
]
