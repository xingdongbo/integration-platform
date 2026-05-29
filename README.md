# 企业数据交换平台 / Enterprise Integration Hub

企业数据交换平台是面向企业内部 ERP、WMS、MES、WCS、AGV 及第三方系统的数据交换中枢。平台通过统一入口、统一映射、统一调用、统一日志和统一重试能力，减少系统间点对点接口开发与维护成本。

## 项目目标

V1 阶段优先交付一套稳定可用的基础数据交换能力：

- 系统管理
- 接口配置管理
- Header 配置
- 请求映射配置
- 响应映射配置
- 统一交换入口
- 接口交换引擎
- 接口测试
- 交换日志
- 人工失败重试

## 技术栈

### 后端

- Java 17
- Spring Boot 3
- MyBatis-Plus
- MySQL
- OkHttp

### 前端

- Vue 3
- Vite
- Element Plus
- Axios

## 目录结构

```text
.
├── docs/                                  # 产品、技术设计和数据库文档
│   ├── sql/V1__init_schema.sql             # V1 MySQL 初始化脚本
│   ├── 企业数据交换平台V1产品与技术设计.md
│   └── 后端包结构与交换引擎说明.md
├── frontend/                              # Vue3 管理后台
│   ├── src/
│   └── package.json
├── src/main/java/com/company/integration/ # Spring Boot 后端源码
│   ├── api/                               # 接口管理
│   ├── common/                            # 通用响应、异常、基础 Controller
│   ├── config/                            # 基础配置
│   ├── exchange/                          # 统一交换入口和交换编排
│   ├── executor/                          # 目标接口调用
│   ├── header/                            # Header 配置
│   ├── log/                               # 交换日志
│   ├── mapping/                           # 请求/响应映射和映射引擎
│   ├── retry/                             # 重试任务和人工重试
│   └── system/                            # 系统管理
├── src/main/resources/application.yml      # 后端配置
└── pom.xml                                # 后端 Maven 工程配置
```

## 数据库初始化

MySQL 初始化脚本位于：

```text
docs/sql/V1__init_schema.sql
```

脚本包含以下核心表：

- `sys_system`
- `api_interface`
- `api_header`
- `api_mapping_request`
- `api_mapping_response`
- `api_exchange_log`
- `api_retry_task`

## 后端启动

请先创建并初始化 MySQL 数据库，然后根据实际环境调整：

```text
src/main/resources/application.yml
```

本地启动：

```bash
mvn spring-boot:run
```

后端默认端口：

```text
http://localhost:8080
```

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认端口：

```text
http://localhost:5173
```

Vite 已配置开发代理，将以下后端接口转发到 `http://localhost:8080`：

- `/systems`
- `/interfaces`
- `/headers`
- `/request-mappings`
- `/response-mappings`
- `/exchange-logs`
- `/retry-tasks`
- `/retry-operations`
- `/exchange`

## 核心接口

### 统一交换入口

```http
POST /exchange/{interfaceCode}
```

示例：

```http
POST /exchange/WMS_GET_ERP_INBOUND
POST /exchange/ERP_PUSH_WMS_OUTBOUND
```

### 接口测试入口

```http
POST /exchange/{interfaceCode}/test
```

### 人工重试入口

```http
POST /retry-operations/{traceId}/retry
```

## V1 交换流程

```text
来源系统请求
  ↓
生成 traceId 并记录来源原始请求
  ↓
查询接口配置、Header 配置、映射规则
  ↓
执行请求映射
  ↓
调用目标系统接口
  ↓
记录目标系统原始响应
  ↓
执行响应映射
  ↓
返回来源系统响应
  ↓
保存完整交换日志
```

失败时平台会记录失败日志，并创建或更新重试任务，支持后台人工重试。

## 管理后台页面

V1 前端已提供：

- 系统管理
- 接口管理
- Header 配置
- 请求映射配置
- 响应映射配置
- 接口测试
- 交换日志
- 重试任务

## 当前阶段说明

V1 聚焦基础数据交换闭环，暂不包含：

- 多租户
- 复杂流程编排
- 可视化拖拽
- API 网关
- OAuth2
- Kafka
- ELK
- SDK
- FTP / SFTP / SOAP
- 复杂权限体系
- SaaS 化能力

这些能力将在后续 P1/P2/P3 阶段逐步扩展。
