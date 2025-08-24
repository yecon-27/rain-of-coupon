<template>
  <div class="traffic-test-page">
    <div class="test-container">
      <h1>流量控制系统测试</h1>
      
      <!-- 当前状态 -->
      <div class="status-section">
        <h2>当前状态</h2>
        <div class="status-grid">
          <div class="status-item">
            <span class="label">流量状态</span>
            <span class="value" :class="trafficStore.state.status">
              {{ getStatusText(trafficStore.state.status) }}
            </span>
          </div>
          
          <div class="status-item">
            <span class="label">当前用户数</span>
            <span class="value">{{ trafficStore.state.currentUsers }}</span>
          </div>
          
          <div class="status-item">
            <span class="label">最大用户数</span>
            <span class="value">{{ trafficStore.state.maxUsers }}</span>
          </div>
          
          <div class="status-item" v-if="trafficStore.state.queuePosition">
            <span class="label">队列位置</span>
            <span class="value">第 {{ trafficStore.state.queuePosition }} 位</span>
          </div>
          
          <div class="status-item">
            <span class="label">用户状态</span>
            <span class="value" :class="trafficStore.state.userStatus">
              {{ getUserStatusText(trafficStore.state.userStatus) }}
            </span>
          </div>
          
          <div class="status-item">
            <span class="label">会话ID</span>
            <span class="value session-id">{{ trafficStore.state.sessionId || '无' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="actions-section">
        <h2>操作测试</h2>
        <div class="button-grid">
          <button @click="checkTraffic" :disabled="trafficStore.loading" class="test-btn">
            {{ trafficStore.loading ? '检测中...' : '检测流量状态' }}
          </button>
          
          <button @click="joinActivity" :disabled="trafficStore.loading" class="test-btn">
            {{ trafficStore.loading ? '加入中...' : '尝试加入活动' }}
          </button>
          
          <button @click="leaveActivity" :disabled="!trafficStore.state.isInActivity" class="test-btn">
            离开活动
          </button>
          
          <button @click="resetState" class="test-btn danger">
            重置状态
          </button>
        </div>
      </div>
      
      <!-- 统计信息 -->
      <div class="stats-section">
        <h2>统计信息</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <h3>模拟统计</h3>
            <p>当前时间: {{ currentTime }}</p>
            <p>高峰期: {{ isRushHour ? '是' : '否' }}</p>
            <p>测试次数: {{ testCount }}</p>
          </div>
        </div>
      </div>
      
      <!-- 错误信息 -->
      <div class="error-section" v-if="trafficStore.error">
        <h2>错误信息</h2>
        <div class="error-message">
          {{ trafficStore.error }}
        </div>
      </div>
      
      <!-- 返回按钮 -->
      <div class="back-section">
        <button @click="goBack" class="back-btn">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTrafficStore } from '@/stores/traffic'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const trafficStore = useTrafficStore()
const authStore = useAuthStore()

const currentTime = ref(new Date().toLocaleString())
const testCount = ref(0)

let timeTimer: number | null = null

const isRushHour = computed(() => {
  const hour = new Date().getHours()
  return (hour >= 11 && hour <= 14) || (hour >= 17 && hour <= 20)
})

const getStatusText = (status: string) => {
  const statusMap = {
    'ok': '正常',
    'crowded': '拥挤',
    'maintenance': '维护中',
    'checking': '检测中'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getUserStatusText = (status: string) => {
  const statusMap = {
    'active': '活跃',
    'queued': '排队中',
    'blocked': '被阻止',
    'idle': '空闲'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const checkTraffic = async () => {
  testCount.value++
  await trafficStore.smartTrafficCheck()
}

const joinActivity = async () => {
  testCount.value++
  await trafficStore.attemptJoinActivity(authStore.currentUser?.id || 'test_user')
}

const leaveActivity = async () => {
  await trafficStore.leaveActivitySession()
}

const resetState = () => {
  trafficStore.resetState()
  testCount.value = 0
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  // 更新时间显示
  timeTimer = setInterval(() => {
    currentTime.value = new Date().toLocaleString()
  }, 1000)
  
  // 初始检测
  checkTraffic()
})

onUnmounted(() => {
  if (timeTimer) {
    clearInterval(timeTimer)
  }
})
</script>

<style scoped>
.traffic-test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.test-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 32px;
  font-size: 28px;
}

h2 {
  color: #555;
  margin-bottom: 16px;
  font-size: 20px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 8px;
}

.status-section, .actions-section, .stats-section, .error-section {
  margin-bottom: 32px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #ddd;
}

.status-item .label {
  font-weight: 500;
  color: #666;
}

.status-item .value {
  font-weight: 600;
  color: #333;
}

.status-item .value.ok {
  color: #28a745;
}

.status-item .value.crowded {
  color: #dc3545;
}

.status-item .value.maintenance {
  color: #ffc107;
}

.status-item .value.active {
  color: #28a745;
}

.status-item .value.queued {
  color: #ffc107;
}

.status-item .value.blocked {
  color: #dc3545;
}

.session-id {
  font-family: monospace;
  font-size: 12px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.test-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.test-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.test-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.test-btn.danger {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
}

.test-btn.danger:hover:not(:disabled) {
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.3);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.stat-card {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.stat-card h3 {
  margin: 0 0 12px 0;
  color: #495057;
  font-size: 16px;
}

.stat-card p {
  margin: 8px 0;
  color: #6c757d;
  font-size: 14px;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #f5c6cb;
}

.back-section {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e9ecef;
}

.back-btn {
  padding: 12px 32px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .test-container {
    padding: 20px;
  }
  
  .status-grid, .button-grid {
    grid-template-columns: 1fr;
  }
  
  h1 {
    font-size: 24px;
  }
}
</style>