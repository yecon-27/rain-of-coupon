<template>
  <div v-if="visible" class="crowding-overlay">
    <!-- BackButton组件 -->
    <BackButton />

    <div class="crowding-content">
      <!-- 拥挤图片 -->
      <img :src="getCrowdingImageUrl()" alt="活动拥挤" class="crowding-image" @error="handleImageError"
        @load="handleImageLoad" />
      
      <!-- 流量信息卡片 -->
      <div class="traffic-info-card" v-if="trafficStore.state.status === 'crowded'">
        <div class="info-header">
          <h3>活动火爆进行中</h3>
          <div class="status-indicator crowded"></div>
        </div>
        
        <div class="info-content">
          <div class="stat-item">
            <span class="label">当前参与人数</span>
            <span class="value">{{ trafficStore.state.currentUsers }}</span>
          </div>
          
          <div class="stat-item">
            <span class="label">最大容量</span>
            <span class="value">{{ trafficStore.state.maxUsers }}</span>
          </div>
          
          <div class="stat-item" v-if="trafficStore.state.queuePosition">
            <span class="label">您的排队位置</span>
            <span class="value highlight">第 {{ trafficStore.state.queuePosition }} 位</span>
          </div>
          
          <div class="stat-item" v-if="trafficStore.state.estimatedWaitTime">
            <span class="label">预计等待时间</span>
            <span class="value">{{ formatWaitTime(trafficStore.state.estimatedWaitTime) }}</span>
          </div>
        </div>
        
        <div class="action-buttons">
          <button class="retry-btn" @click="handleRetry" :disabled="retryDisabled">
            {{ retryDisabled ? `${retryCountdown}秒后重试` : '重新尝试' }}
          </button>
          <button class="back-btn" @click="handleBack">返回首页</button>
        </div>
      </div>
      
      <!-- 简单版本（兼容原有逻辑） -->
      <div class="simple-actions" v-else>
        <button class="retry-btn" @click="handleImageClick">重新尝试</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { API_CONFIG } from '@/config/api'
import { useTrafficStore } from '@/stores/traffic'
import { useUIStore } from '@/stores/ui'
import BackButton from '@/components/BackButton.vue'

// Props
interface Props {
  visible?: boolean
}

defineProps<Props>()

const router = useRouter()
const trafficStore = useTrafficStore()
const uiStore = useUIStore()

// 重试相关状态
const retryCountdown = ref(0)
const retryDisabled = computed(() => retryCountdown.value > 0)

let countdownTimer: NodeJS.Timeout | null = null

// 获取活动拥挤图片URL
const getCrowdingImageUrl = () => {
  const filename = '活动拥挤.png'
  let imageUrl = ''

  // 如果数据库存储的是完整路径（以/开头）
  if (filename.startsWith('/')) {
    // 转换为完整URL
    const isDev = import.meta.env.DEV
    const baseUrl = isDev ? `http://${window.location.hostname}:8080` : 'https://your-production-domain.com'
    imageUrl = `${baseUrl}${filename}`
  } else {
    // 如果只是文件名，使用配置的路径
    imageUrl = `${API_CONFIG.couponImageURL}${filename}`
  }

  console.log('活动拥挤图片URL:', filename, '->', imageUrl)
  return imageUrl
}

// 格式化等待时间
const formatWaitTime = (seconds: number): string => {
  if (seconds < 60) {
    return `${seconds}秒`
  } else if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60)
    return `${minutes}分钟`
  } else {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}小时${minutes}分钟`
  }
}

// 开始重试倒计时
const startRetryCountdown = () => {
  const retryAfter = trafficStore.state.retryAfter || 60
  retryCountdown.value = retryAfter
  
  countdownTimer = setInterval(() => {
    retryCountdown.value--
    if (retryCountdown.value <= 0) {
      clearInterval(countdownTimer!)
      countdownTimer = null
    }
  }, 1000)
}

// 处理重试
const handleRetry = () => {
  if (retryDisabled.value) return
  
  // 重新尝试加载
  router.push('/loading')
}

// 点击图片处理（兼容原有逻辑）
const handleImageClick = () => {
  handleRetry()
}

// 返回首页
const handleBack = () => {
  uiStore.resetAllOverlays()
  router.push('/')
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('活动拥挤图片加载失败:', img.src)
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('活动拥挤图片加载成功:', img.src)
}

// 组件挂载时开始倒计时
onMounted(() => {
  if (trafficStore.state.retryAfter) {
    startRetryCountdown()
  }
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})
</script>

<style scoped>
.crowding-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  box-sizing: border-box;
}

.crowding-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  max-width: 90vw;
  max-height: 90vh;
}

.crowding-image {
  max-width: 400px;
  max-height: 300px;
  width: auto;
  height: auto;
  object-fit: contain;
  animation: fadeIn 0.3s ease-out;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.traffic-info-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  width: 100%;
  animation: slideUp 0.4s ease-out;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.info-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-indicator.crowded {
  background: #ff6b6b;
}

.info-content {
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-item .label {
  color: #666;
  font-size: 14px;
}

.stat-item .value {
  color: #333;
  font-weight: 600;
  font-size: 14px;
}

.stat-item .value.highlight {
  color: #ff6b6b;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.retry-btn, .back-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.retry-btn {
  background: linear-gradient(135deg, #f35917, #f7761f);
  color: white;
}

.retry-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #e04d0f, #e66b17);
  transform: translateY(-1px);
}

.retry-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.back-btn {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #e9ecef;
}

.back-btn:hover {
  background: #e9ecef;
  color: #333;
}

.simple-actions {
  display: flex;
  justify-content: center;
}

.simple-actions .retry-btn {
  padding: 16px 32px;
  font-size: 16px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .crowding-overlay {
    padding: 16px;
  }
  
  .crowding-content {
    gap: 16px;
  }
  
  .crowding-image {
    max-width: 300px;
    max-height: 200px;
  }
  
  .traffic-info-card {
    padding: 20px;
    max-width: 100%;
  }
  
  .info-header h3 {
    font-size: 16px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .retry-btn, .back-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .crowding-overlay {
    padding: 12px;
  }
  
  .crowding-image {
    max-width: 250px;
    max-height: 150px;
  }
  
  .traffic-info-card {
    padding: 16px;
  }
  
  .stat-item {
    padding: 8px 0;
  }
  
  .stat-item .label,
  .stat-item .value {
    font-size: 13px;
  }
  
  .stat-item .value.highlight {
    font-size: 14px;
  }
}
</style>
