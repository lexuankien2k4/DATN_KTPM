import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  sourcemap: false,
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }

  },

  server: {
    port: 3000,
    host: true,

    allowedHosts: ['bonniest-unviolated-carlie.ngrok-free.dev'],
   
    proxy: {
      // 1. Proxy cho các đường dẫn ảnh tĩnh của Spring Boot
      '/images': {
        target: 'http://localhost:8080', // Địa chỉ Backend
        changeOrigin: true, // Quan trọng cho CORS
      },
      // 2. Proxy cho các API request
      '/api': {
        target: 'http://localhost:8080', // Địa chỉ Backend
        changeOrigin: true,
      }
    }
  }
})