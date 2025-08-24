import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 导入缓存清理工具（开发环境）
if (import.meta.env.DEV) {
  // @ts-ignore
  import('./utils/clearCache.js').catch(console.error)
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

app.mount('#app')

// 应用挂载后立即检查认证状态
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
authStore.checkAuthStatus()
