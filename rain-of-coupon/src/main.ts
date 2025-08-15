import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 导入缓存清理工具（开发环境）
if (import.meta.env.DEV) {
  import('./utils/clearCache.js')
}

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
