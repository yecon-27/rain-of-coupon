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

      <!-- 拥挤提示 -->
      <div v-if="showCrowdedMessage" class="crowded-message">
        <div class="message-content">
          <h3>🎉 活动火爆进行中！</h3>
          <p>当前参与人数较多，请稍后再试</p>
          <button @click="retryLoading" class="retry-btn">重新尝试</button>
          <button @click="goHome" class="home-btn">返回首页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoadingAnim from '@/components/LoadingAnim.vue'
import ProgressBar from '@/components/ProgressBar.vue'

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
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.crowded-message {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.message-content {
  background: white;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  color: #333;
}

.message-content h3 {
  font-size: 24px;
  margin: 0 0 20px 0;
  color: orange;
}

.message-content p {
  font-size: 16px;
  margin: 0 0 30px 0;
  color: #666;
}

.retry-btn,
.home-btn {
  background: orange;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  margin: 0 10px;
  transition: all 0.3s ease;
}

.retry-btn:hover,
.home-btn:hover {
  background: #ff8c00;
  transform: translateY(-2px);
}

.home-btn {
  background: #666;
}

.home-btn:hover {
  background: #555;
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

  .message-content {
    padding: 30px 20px;
  }

  .retry-btn,
  .home-btn {
    display: block;
    width: 100%;
    margin: 10px 0;
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