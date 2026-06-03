-- 企业数据交换平台 V1.0 数据库初始化脚本
-- MySQL 8.x
-- Charset: utf8mb4

CREATE DATABASE IF NOT EXISTS integration_platform
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE integration_platform;

-- ============================================================
-- 1. 系统信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_system (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  system_code VARCHAR(64) NOT NULL COMMENT '系统编码，唯一，例如 ERP/WMS/MES/WCS',
  system_name VARCHAR(128) NOT NULL COMMENT '系统名称',
  system_type VARCHAR(32) NOT NULL COMMENT '系统类型，例如 ERP/WMS/MES/WCS/AGV/THIRD_PARTY',
  base_url VARCHAR(512) DEFAULT NULL COMMENT '系统基础地址',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED启用，DISABLED停用',
  remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_system_code (system_code),
  KEY idx_sys_system_type (system_type),
  KEY idx_sys_system_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统信息表';

-- ============================================================
-- 2. 接口交换配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_interface (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码，唯一，例如 WMS_GET_ERP_INBOUND',
  interface_name VARCHAR(128) NOT NULL COMMENT '接口名称',
  source_system VARCHAR(64) NOT NULL COMMENT '来源系统编码',
  target_system VARCHAR(64) NOT NULL COMMENT '目标系统编码',
  target_url VARCHAR(512) NOT NULL COMMENT '目标接口地址，可以是完整URL或相对目标系统base_url的路径',
  method VARCHAR(16) NOT NULL DEFAULT 'POST' COMMENT '请求方式：GET/POST',
  content_type VARCHAR(64) NOT NULL DEFAULT 'application/json' COMMENT 'Content-Type',
  timeout INT NOT NULL DEFAULT 5000 COMMENT '超时时间，单位毫秒',
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED启用，DISABLED停用',
  remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_api_interface_code (interface_code),
  KEY idx_api_interface_source_system (source_system),
  KEY idx_api_interface_target_system (target_system),
  KEY idx_api_interface_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口交换配置表';

-- ============================================================
-- 3. Header 配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_header (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码',
  header_name VARCHAR(128) NOT NULL COMMENT 'Header名称',
  header_value VARCHAR(1024) NOT NULL COMMENT 'Header值',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_api_header_interface_name (interface_code, header_name),
  KEY idx_api_header_interface_code (interface_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Header配置表';

-- ============================================================
-- 4. 请求映射配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_mapping_request (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码',
  source_field VARCHAR(256) DEFAULT NULL COMMENT '来源字段路径，例如 orderNo 或 data.orderNo',
  target_field VARCHAR(256) NOT NULL COMMENT '目标字段路径，例如 billNo 或 data.inboundNo',
  mapping_type VARCHAR(32) NOT NULL COMMENT '映射类型：FIELD/CONST/DEFAULT/EXPRESSION',
  default_value VARCHAR(1024) DEFAULT NULL COMMENT '默认值或固定值',
  expression VARCHAR(1024) DEFAULT NULL COMMENT '简单表达式',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_api_mapping_request_interface_code (interface_code),
  KEY idx_api_mapping_request_sort (interface_code, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请求映射配置表';

-- ============================================================
-- 5. 响应映射配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_mapping_response (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码',
  source_field VARCHAR(256) DEFAULT NULL COMMENT '来源字段路径，指目标系统响应字段',
  target_field VARCHAR(256) NOT NULL COMMENT '目标字段路径，指返回来源系统的响应字段',
  mapping_type VARCHAR(32) NOT NULL COMMENT '映射类型：FIELD/CONST/DEFAULT/EXPRESSION',
  default_value VARCHAR(1024) DEFAULT NULL COMMENT '默认值或固定值',
  expression VARCHAR(1024) DEFAULT NULL COMMENT '简单表达式',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_api_mapping_response_interface_code (interface_code),
  KEY idx_api_mapping_response_sort (interface_code, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='响应映射配置表';

-- ============================================================
-- 6. 交换日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_exchange_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  trace_id VARCHAR(64) NOT NULL COMMENT '链路追踪ID',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码',
  source_system VARCHAR(64) DEFAULT NULL COMMENT '来源系统编码',
  target_system VARCHAR(64) DEFAULT NULL COMMENT '目标系统编码',
  business_no VARCHAR(128) DEFAULT NULL COMMENT '业务单号',
  source_request_raw LONGTEXT DEFAULT NULL COMMENT '来源系统原始请求报文',
  target_request_data LONGTEXT DEFAULT NULL COMMENT '请求映射后的目标请求报文',
  target_response_raw LONGTEXT DEFAULT NULL COMMENT '目标系统原始响应报文',
  source_response_data LONGTEXT DEFAULT NULL COMMENT '响应映射后的来源响应报文',
  status VARCHAR(32) NOT NULL DEFAULT 'RECEIVED' COMMENT '状态：RECEIVED/MAPPED/CALLING/SUCCESS/FAILED/RETRYING',
  error_msg TEXT DEFAULT NULL COMMENT '错误信息',
  execute_time BIGINT DEFAULT NULL COMMENT '执行耗时，单位毫秒',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_api_exchange_log_trace_id (trace_id),
  KEY idx_api_exchange_log_interface_code (interface_code),
  KEY idx_api_exchange_log_business_no (business_no),
  KEY idx_api_exchange_log_status (status),
  KEY idx_api_exchange_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交换日志表';

-- ============================================================
-- 7. 重试任务表
-- ============================================================
CREATE TABLE IF NOT EXISTS api_retry_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  trace_id VARCHAR(64) NOT NULL COMMENT '原始交换traceId',
  interface_code VARCHAR(128) NOT NULL COMMENT '接口编码',
  retry_count INT NOT NULL DEFAULT 0 COMMENT '当前重试次数',
  max_retry_count INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  last_error_msg TEXT DEFAULT NULL COMMENT '最近一次错误信息',
  next_retry_time DATETIME DEFAULT NULL COMMENT '下次可重试时间',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/RETRYING/SUCCESS/FAILED',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_api_retry_task_trace_id (trace_id),
  KEY idx_api_retry_task_interface_code (interface_code),
  KEY idx_api_retry_task_status (status),
  KEY idx_api_retry_task_next_retry_time (next_retry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='重试任务表';
