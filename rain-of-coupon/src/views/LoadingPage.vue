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
    
    // 使用模拟流量检测（开发阶段）
    // 生产环境应该使用: const canJoin = await trafficStore.checkTraffic()
    const canJoin = await trafficStore.simulateTrafficCheck()
    
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
    // 检测失败时显示拥挤提示
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
    // 检查登录状态
    if (!authStore.isLoggedIn) {
      router.push('/login?redirect=/loading')
      return
    }

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
</style>