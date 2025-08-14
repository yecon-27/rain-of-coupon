<template>
  <div class="loading-page">
    <!-- 主要内容 -->
    <div class="loading-container">
      <!-- 加载动画 -->
      <div class="loading-animation">
        <LoadingAnim />
      </div>

      <!-- 进度条组件 -->
      <ProgressBar ref="progressBarRef" @progress-complete="onProgressComplete" />
      
      <!-- 流量检测状态显示 -->
      <div v-if="checkingTraffic" class="traffic-status">
        <div class="status-text">正在检测活动流量...</div>
        <div class="status-detail">
          <span v-if="trafficStore.state.currentUsers > 0">
            当前在线: {{ trafficStore.state.currentUsers }} / {{ trafficStore.state.maxUsers }}
          </span>
        </div>
      </div>
      
      <!-- 错误重试 -->
      <div v-if="trafficStore.error && !checkingTraffic" class="error-retry">
        <div class="error-message">{{ trafficStore.error }}</div>
        <button @click="retryTrafficCheck" class="retry-btn">重试</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTrafficStore } from '@/stores/traffic'
import { useUIStore } from '@/stores/ui'
import LoadingAnim from '@/components/LoadingAnim.vue'
import ProgressBar from '@/components/ProgressBar.vue'

const router = useRouter()
const authStore = useAuthStore()
const trafficStore = useTrafficStore()
const uiStore = useUIStore()

// 状态管理
const progressBarRef = ref<InstanceType<typeof ProgressBar> | null>(null)
const checkingTraffic = ref(false)

// 进度条完成回调
const onProgressComplete = async () => {
  await performTrafficCheck()
}

// 执行流量检测
const performTrafficCheck = async () => {
  checkingTraffic.value = true
  
  try {
    console.log('开始流量检测...')
    
    // 使用智能流量检测（优先后端API，降级到模拟服务）
    const canJoin = await trafficStore.smartTrafficCheck()
    
    if (canJoin) {
      // 流量正常，尝试加入活动
      console.log('流量正常，尝试加入活动...')
      const joinSuccess = await trafficStore.attemptJoinActivity(authStore.currentUser?.id)
      
      if (joinSuccess) {
        console.log('成功加入活动，跳转到倒计时页面')
        setTimeout(() => {
          router.push('/countdown')
        }, 500)
      } else {
        console.log('加入活动失败，显示拥挤提示')
        showCrowdingMessage()
      }
    } else {
      console.log('流量拥挤，显示拥挤提示')
      showCrowdingMessage()
    }
  } catch (error) {
    console.error('流量检测失败:', error)
    // 检测失败时显示拥挤提示，但提供重试机制
    showCrowdingMessage()
  } finally {
    checkingTraffic.value = false
  }
}

// 显示拥挤提示
const showCrowdingMessage = () => {
  // 设置UI状态显示拥挤提示
  uiStore.setCrowdingTip(true)
  // 跳转回首页
  router.push('/')
}

// 开始加载检查
const startLoading = async () => {
  try {
    // 首先检查并恢复登录状态
    if (!authStore.isLoggedIn) {
      const hasValidAuth = authStore.checkAuthStatus()
      if (!hasValidAuth) {
        console.log('用户未登录，跳转到登录页面')
        router.push('/login?redirect=/loading')
        return
      }
    }

    console.log('用户已登录，开始加载流程')
    console.log('当前用户:', authStore.currentUser)
    console.log('Token存在:', !!authStore.token)

    // 开始进度条
    progressBarRef.value?.startProgress()

  } catch (error) {
    console.error('加载失败:', error)
    showCrowdingMessage()
  }
}

// 页面初始化
onMounted(() => {
  startLoading()
})

// 重试流量检测
const retryTrafficCheck = async () => {
  await performTrafficCheck()
}

// 页面卸载时清理
onUnmounted(() => {
  // 如果用户离开页面，清理流量状态
  trafficStore.leaveActivitySession()
})
</script>

<style scoped>
.loading-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: radial-gradient(ellipse 80% 60% at center, #ffcc99, #ffe4cc, #fff4e8);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow: hidden;
  padding-top: 10vh;
}

.loading-container {
  text-align: center;
  color: #333;
  z-index: 10;
  max-width: 500px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-animation {
  margin-bottom: 30px;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-page {
    padding-top: 8vh;
  }

  .loading-container {
    padding: 15px;
  }

  .loading-animation {
    margin-bottom: 20px;
  }
}

@media (max-width: 480px) {
  .loading-page {
    padding-top: 6vh;
  }

  .loading-animation {
    margin-bottom: 15px;
  }
}

/* 流量检测状态样式 */
.traffic-status {
  margin-top: 20px;
  text-align: center;
  color: #666;
}

.status-text {
  font-size: 16px;
  margin-bottom: 8px;
  color: #409EFF;
}

.status-detail {
  font-size: 14px;
  color: #909399;
}

/* 错误重试样式 */
.error-retry {
  margin-top: 20px;
  text-align: center;
}

.error-message {
  color: #F56C6C;
  font-size: 14px;
  margin-bottom: 12px;
}

.retry-btn {
  background: linear-gradient(135deg, #f35917, #f7761f);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background: linear-gradient(135deg, #e04d0f, #e66b17);
  transform: translateY(-1px);
}
</style>