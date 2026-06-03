# 企业数据交换平台前端

V1 前端基于 Vue3、Vite、Element Plus、Axios 实现，提供以下管理页面：

- 系统管理
- 接口管理
- Header 配置
- 请求映射配置
- 响应映射配置
- 接口测试
- 交换日志
- 重试任务

## 本地启动

```bash
cd frontend
npm install
npm run dev
```

默认通过 Vite proxy 转发到后端 `http://localhost:8080`。
如需指定 API 基础地址，可设置环境变量：

```bash
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```
