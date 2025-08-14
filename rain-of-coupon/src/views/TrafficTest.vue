<template>
  <div class="traffic-test">
    <h2>流量控制系统测试</h2>
    
    <div class="test-section">
      <h3>当前状态</h3>
      <div class="status-info">
        <p><strong>状态:</strong> {{ trafficStore.state.status }}</p>
        <p><strong>当前用户:</strong> {{ trafficStore.state.currentUsers }}</p>
        <p><strong>最大用户:</strong> {{ trafficStore.state.maxUsers }}</p>
        <p><strong>用户状态:</strong> {{ trafficStore.state.userStatus }}</p>
        <p><strong>队列位置:</strong> {{ trafficStore.state.queuePosition || '无' }}</p>
        <p><strong>预计等待:</strong> {{ trafficStore.state.estimatedWaitTime || '无' }}秒</p>
        <p><strong>会话ID:</strong> {{ trafficStore.state.sessionId || '无' }}</p>
        <p><strong>加载中:</strong> {{ trafficStore.loading ? '是' : '否' }}</p>
        <p><strong>错误:</strong> {{ trafficStore.error || '无' }}</p>
      </div>
    </div>

    <div class="test-section">
      <h3>测试操作</h3>
      <div class="test-buttons">
        <button @click="testTrafficCheck" :disabled="trafficStore.loading">
          检测流量状态
        </button>
        <button @click="testSmartCheck" :disabled="trafficStore.loading">
          智能流量检测
        </button>
        <button @click="testJoinActivity" :disabled="trafficStore.loading">
          尝试加入活动
        </button>
        <button @click="testLeaveActivity" :disabled="trafficStore.loading">
          离开活动
        </button>
        <button @click="resetState">
          重置状态
        </button>
      </div>
    </div>

    <div class="test-section">
      <h3>测试日志</h3>
      <div class="log-container">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useTrafficStore } from '@/stores/traffic'

const trafficStore = useTrafficStore()
const logs = ref<Array<{ time: string, message: string }>>([])

const addLog = (message: string) => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message
  })
  
  // 只保留最近50条日志
  if (logs.value.length > 50) {
    logs.value = logs.value.slice(0, 50)
  }
}

const testTrafficCheck = async () => {
  addLog('开始检测流量状态...')
  try {
    const result = await trafficStore.checkTraffic()
    addLog(`流量检测结果: ${result ? '正常' : '拥挤'}`)
  } catch (error) {
    addLog(`流量检测失败: ${error}`)
  }
}

const testSmartCheck = async () => {
  addLog('开始智能流量检测...')
  try {
    const result = await trafficStore.smartTrafficCheck()
    addLog(`智能检测结果: ${result ? '可以加入' : '需要等待'}`)
  } catch (error) {
    addLog(`智能检测失败: ${error}`)
  }
}

const testJoinActivity = async () => {
  addLog('尝试加入活动...')
  try {
    const result = await trafficStore.attemptJoinActivity('test_user_123')
    addLog(`加入活动结果: ${result ? '成功' : '失败'}`)
  } catch (error) {
    addLog(`加入活动失败: ${error}`)
  }
}

const testLeaveActivity = async () => {
  addLog('尝试离开活动...')
  try {
    await trafficStore.leaveActivitySession()
    addLog('已离开活动')
  } catch (error) {
    addLog(`离开活动失败: ${error}`)
  }
}

const resetState = () => {
  trafficStore.resetState()
  addLog('状态已重置')
}

// 初始化日志
addLog('流量控制系统测试页面已加载')
</script>

<style scoped>
.traffic-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.test-section h3 {
  margin-top: 0;
  color: #333;
}

.status-info p {
  margin: 8px 0;
  padding: 4px 0;
  border-bottom: 1px solid #eee;
}

.test-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.test-buttons button {
  padding: 10px 15px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.test-buttons button:hover:not(:disabled) {
  background: #0056b3;
}

.test-buttons button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.log-container {
  max-height: 300px;
  overflow-y: auto;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
}

.log-item {
  display: flex;
  margin-bottom: 5px;
  font-size: 14px;
}

.log-time {
  color: #666;
  margin-right: 10px;
  min-width: 80px;
}

.log-message {
  color: #333;
}
</style>