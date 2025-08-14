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

    <!-- 拥挤提示组件 -->
    <CrowdingTip 
      v-if="showCrowdedMessage" 
      @retry="retryLoading" 
      @back="goHome" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoadingAnim from '@/components/LoadingAnim.vue'
import ProgressBar from '@/components/ProgressBar.vue'
import CrowdingTip from '@/components/CrowdingTip.vue'

const router = useRouter()
const authStore = useAuthStore()

// 状态管理
const showCrowdedMessage = ref(false)
const progressBarRef = ref<InstanceType<typeof ProgressBar> | null>(null)

// 进度条完成回调
const onProgressComplete = async () => {
  // 模拟检查活动状态的逻辑
  if (Math.random() > 0.3) {
    // 70%概率活动正常，跳转到倒计时页面
    setTimeout(() => {
      router.push('/countdown')
    }, 500)
  } else {
    // 30%概率显示拥挤提示
    showCrowdedMessage.value = true
  }
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

    // 模拟加载过程
    await new Promise(resolve => setTimeout(resolve, 5000))

  } catch (error) {
    console.error('加载失败:', error)
    showCrowdedMessage.value = true
  }
}

// 重新尝试
const retryLoading = () => {
  showCrowdedMessage.value = false
  progressBarRef.value?.resetProgress()
  startLoading()
}

// 返回首页
const goHome = () => {
  router.push('/')
}

// 页面初始化
onMounted(() => {
  startLoading()
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