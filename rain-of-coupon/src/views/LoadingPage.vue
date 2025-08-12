<template>
  <div class="loading-page">
    <!-- 加载动画区域 -->
    <div class="loading-container">
      <div class="loading-animation">
        <!-- 可以使用旋转的红包图标或其他加载动画 -->
        <div class="spinner"></div>
      </div>
      
      <h2 class="loading-title">正在为您准备红包雨...</h2>
      <p class="loading-text">{{ loadingText }}</p>
    </div>

    <!-- 拥挤提示组件 -->
    <CrowdedTip 
      v-if="showCrowdedTip" 
      @retry="checkActivityStatus"
      @back="goBack"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CrowdedTip from '@/components/CrowdedTip.vue'

const router = useRouter()
const showCrowdedTip = ref(false)
const loadingText = ref('检查活动状态中...')
const checkInterval: NodeJS.Timeout | null = null

// 检查活动状态
const checkActivityStatus = async () => {
  try {
    loadingText.value = '检查活动人数...'
    
    // 调用API检查活动状态
    const response = await fetch('/api/activity/status')
    const data = await response.json()
    
    if (data.isCrowded) {
      // 人数拥挤，显示拥挤提示
      showCrowdedTip.value = true
      loadingText.value = '活动火爆，请稍后再试'
    } else {
      // 不拥挤，跳转到倒计时页面
      loadingText.value = '准备就绪，即将开始...'
      setTimeout(() => {
        router.push('/countdown')
      }, 1000)
    }
  } catch (error) {
    console.error('检查活动状态失败:', error)
    loadingText.value = '网络异常，请重试'
    // 可以显示重试按钮或自动重试
    setTimeout(checkActivityStatus, 3000)
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 页面初始化
onMounted(() => {
  // 延迟一下再检查，给用户看到加载动画的时间
  setTimeout(checkActivityStatus, 1500)
  
  // 可以设置定期检查（如果需要实时更新状态）
  // checkInterval = setInterval(checkActivityStatus, 10000)
})

// 清理定时器
onUnmounted(() => {
  if (checkInterval) {
    clearInterval(checkInterval)
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
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.loading-container {
  text-align: center;
  padding: 40px;
}

.loading-animation {
  margin-bottom: 30px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  color: white;
}

.loading-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .loading-container {
    padding: 20px;
  }
  
  .loading-title {
    font-size: 20px;
  }
  
  .spinner {
    width: 50px;
    height: 50px;
  }
}
</style>