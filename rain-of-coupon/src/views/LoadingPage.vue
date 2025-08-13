<template>
  <div class="loading-page">
    <!-- 主要内容 -->
    <div class="loading-container">
      <!-- 加载动画 -->
      <div class="loading-animation">
        <LoadingAnim />
      </div>

      <!-- 进度条 -->
      <div class="progress-section">
        <!-- 努力加载中文字 -->
        <div class="loading-text">努力加载中...</div>

        <div class="progress-container">
          <div class="progress-bar">
            <div class="progress" :style="{ width: progress + '%' }"></div>
          </div>
          <div class="percent">{{ progress }}%</div>
        </div>
      </div>

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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoadingAnim from '@/components/LoadingAnim.vue'

const router = useRouter()
const authStore = useAuthStore()

// 状态管理
const showCrowdedMessage = ref(false)
const progress = ref(0)
let progressInterval: number | null = null

// 开始进度条动画
const startProgress = () => {
  progress.value = 0
  progressInterval = setInterval(() => {
    if (progress.value >= 100) {
      if (progressInterval) {
        clearInterval(progressInterval)
        progressInterval = null
      }
    } else {
      progress.value++
    }
  }, 50) // 每50ms更新1%，总共5秒完成
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
    startProgress()

    // 模拟加载过程
    await new Promise(resolve => setTimeout(resolve, 5000))

    // 等待进度条完成
    while (progress.value < 100) {
      await new Promise(resolve => setTimeout(resolve, 100))
    }

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

  } catch (error) {
    console.error('加载失败:', error)
    showCrowdedMessage.value = true
  }
}

// 重新尝试
const retryLoading = () => {
  showCrowdedMessage.value = false
  progress.value = 0
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

// 清理定时器
onUnmounted(() => {
  if (progressInterval) {
    clearInterval(progressInterval)
  }
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
  align-items: center; /* 新增：确保所有内容居中 */
}

.loading-animation {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  align-items: center; /* 新增：垂直居中 */
  width: 100%; /* 新增：确保占满容器宽度 */
}

.progress-section {
  margin: 20px 0;
  width: 100%; /* 新增：确保占满容器宽度 */
  display: flex;
  flex-direction: column;
  align-items: center; /* 新增：内容居中 */
}

/* 努力加载中文字样式 */
.loading-text {
  font-size: 24px;
  color: orange;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: center;
  width: 100%; /* 新增：确保文字完全居中 */
}

/* 进度条容器布局 */
.progress-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  width: 100%; /* 新增：确保进度条容器居中 */
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .loading-page {
    padding-top: 8vh;
  }
  
  .loading-container {
    padding: 15px;
  }
  
  .loading-animation {
    margin-bottom: 20px; /* 移动端进一步缩小距离 */
  }
  
  .progress-section {
    margin-left: 20px; /* 移动端减少右移距离 */
  }
  
  .loading-text {
    margin-right: 20px; /* 移动端相应调整 */
  }
  
  .progress-bar {
    width: 275px;
  }
  
  .message-content {
    padding: 30px 20px;
  }
  
  .retry-btn, .home-btn {
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
    margin-bottom: 15px; /* 小屏幕进一步缩小距离 */
  }
  
  .progress-section {
    margin-left: 10px; /* 小屏幕减少右移距离 */
  }
  
  .loading-text {
    margin-right: 10px; /* 小屏幕相应调整 */
    font-size: 16px;
  }
  
  .progress-bar {
    width: 220px;
  }
  
  .progress-container {
    gap: 10px;
  }
}
.progress-bar {
  flex: 1;
  height: 40px;
  border: 3px solid orange;
  border-radius: 25px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
}

.progress {
  height: 100%;
  width: 0;
  background: repeating-linear-gradient(
    -45deg,
    orange,
    orange 6px,
    #ffcc66 6px,
    #ffcc66 12px
  );
  transition: width 0.3s ease;
  border-radius: 22px;
}

.progress {
  height: 100%;
  width: 0;
  background: repeating-linear-gradient(-45deg,
      orange,
      orange 6px,
      #ffcc66 6px,
      #ffcc66 12px);
  transition: width 0.3s ease;
  border-radius: 17px;
}

/* 进度数字 */
.percent {
  font-size: 24px;
  color: orange;
  font-weight: normal;
  min-width: 60px;
  text-align: left;
  margin-right: 0;
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
    margin-bottom: 25px;
  }

  .loading-text {
    font-size: 20px;
  }

  .progress-container {
    margin-left: 30px;
    margin-right: 30px;
  }

  .progress-bar {
    flex: 1;
    height: 35px;
  }

  .percent {
    font-size: 20px;
    margin-right: 0;
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
    margin-bottom: 20px;
  }

  .loading-text {
    font-size: 18px;
  }

  .progress-container {
    margin-left: 15px;
    margin-right: 5px;
  }

  .progress-bar {
    width: 250px;
    height: 24px;
  }

  .percent {
    font-size: 18px;
    margin-right: 0;
  }
}
</style>