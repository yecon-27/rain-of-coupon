<template>
  <div class="clear-cache-page">
    <div class="container">
      <h1>🧹 缓存清理工具</h1>
      
      <div class="info-section">
        <h3>当前缓存状态</h3>
        <div class="cache-info">
          <p><strong>localStorage项目数:</strong> {{ localStorageCount }}</p>
          <p><strong>sessionStorage项目数:</strong> {{ sessionStorageCount }}</p>
          <p><strong>cookies数量:</strong> {{ cookiesCount }}</p>
        </div>
      </div>

      <div class="actions-section">
        <button @click="clearAllCache" class="clear-btn" :disabled="clearing">
          {{ clearing ? '清理中...' : '🗑️ 清除所有缓存' }}
        </button>
        
        <button @click="clearAndReload" class="reload-btn" :disabled="clearing">
          {{ clearing ? '清理中...' : '🔄 清除缓存并刷新' }}
        </button>
      </div>

      <div class="logs-section">
        <h3>操作日志</h3>
        <div class="logs">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message" :class="log.type">{{ log.message }}</span>
          </div>
        </div>
      </div>

      <div class="help-section">
        <h3>💡 使用说明</h3>
        <ul>
          <li>如果你在数据库注册admin用户时提示"已被占用"</li>
          <li>说明浏览器中还有旧的缓存数据</li>
          <li>点击"清除所有缓存"按钮可以完全清除</li>
          <li>清除后可以重新注册admin用户</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const localStorageCount = ref(0)
const sessionStorageCount = ref(0)
const cookiesCount = ref(0)
const clearing = ref(false)
const logs = ref<Array<{time: string, message: string, type: string}>>([])

const addLog = (message: string, type: 'info' | 'success' | 'error' = 'info') => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message,
    type
  })
  
  // 只保留最近20条日志
  if (logs.value.length > 20) {
    logs.value = logs.value.slice(0, 20)
  }
}

const updateCacheInfo = () => {
  localStorageCount.value = localStorage.length
  sessionStorageCount.value = sessionStorage.length
  cookiesCount.value = document.cookie.split(';').filter(c => c.trim()).length
}

const clearAllCookies = () => {
  const cookies = document.cookie.split(";")
  let clearedCount = 0
  
  for (let cookie of cookies) {
    const eqPos = cookie.indexOf("=")
    const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim()
    
    if (name) {
      // 删除cookie - 多种路径和域名组合确保完全清除
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=${window.location.hostname}`
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.${window.location.hostname}`
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=localhost`
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.localhost`
      clearedCount++
    }
  }
  
  return clearedCount
}

const clearAllCache = async () => {
  clearing.value = true
  
  try {
    addLog('开始清除所有缓存...', 'info')
    
    // 清除localStorage
    const localCount = localStorage.length
    localStorage.clear()
    addLog(`清除了 ${localCount} 个localStorage项目`, 'success')
    
    // 清除sessionStorage
    const sessionCount = sessionStorage.length
    sessionStorage.clear()
    addLog(`清除了 ${sessionCount} 个sessionStorage项目`, 'success')
    
    // 清除cookies
    const cookieCount = clearAllCookies()
    addLog(`清除了 ${cookieCount} 个cookies`, 'success')
    
    addLog('✅ 所有缓存清除完成！', 'success')
    
    // 更新显示
    updateCacheInfo()
    
  } catch (error) {
    addLog(`❌ 清除缓存失败: ${error}`, 'error')
  } finally {
    clearing.value = false
  }
}

const clearAndReload = async () => {
  await clearAllCache()
  
  addLog('即将刷新页面...', 'info')
  setTimeout(() => {
    window.location.reload()
  }, 2000)
}

onMounted(() => {
  updateCacheInfo()
  addLog('缓存清理工具已加载', 'info')
})
</script>

<style scoped>
.clear-cache-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.info-section, .actions-section, .logs-section, .help-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.cache-info p {
  margin: 8px 0;
  font-family: monospace;
}

.actions-section {
  text-align: center;
}

.clear-btn, .reload-btn {
  padding: 12px 24px;
  margin: 0 10px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-btn {
  background: #dc3545;
  color: white;
}

.clear-btn:hover:not(:disabled) {
  background: #c82333;
}

.reload-btn {
  background: #007bff;
  color: white;
}

.reload-btn:hover:not(:disabled) {
  background: #0056b3;
}

.clear-btn:disabled, .reload-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.logs {
  max-height: 300px;
  overflow-y: auto;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  padding: 10px;
}

.log-item {
  display: flex;
  margin-bottom: 5px;
  font-size: 14px;
}

.log-time {
  color: #6c757d;
  margin-right: 10px;
  min-width: 80px;
}

.log-message.info {
  color: #17a2b8;
}

.log-message.success {
  color: #28a745;
}

.log-message.error {
  color: #dc3545;
}

.help-section ul {
  margin: 0;
  padding-left: 20px;
}

.help-section li {
  margin: 8px 0;
  color: #495057;
}
</style>