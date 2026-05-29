import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/systems': 'http://localhost:8080',
      '/interfaces': 'http://localhost:8080',
      '/headers': 'http://localhost:8080',
      '/request-mappings': 'http://localhost:8080',
      '/response-mappings': 'http://localhost:8080',
      '/exchange-logs': 'http://localhost:8080',
      '/retry-tasks': 'http://localhost:8080',
      '/retry-operations': 'http://localhost:8080',
      '/exchange': 'http://localhost:8080'
    }
  }
})
