<template>
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
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'

// 定义事件
const emit = defineEmits<{
  progressComplete: []
}>()

// 状态管理
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
      emit('progressComplete')
    } else {
      progress.value++
    }
  }, 50) // 每50ms更新1%，总共5秒完成
}

// 重置进度条
const resetProgress = () => {
  progress.value = 0
  if (progressInterval) {
    clearInterval(progressInterval)
    progressInterval = null
  }
}

// 暴露方法给父组件
defineExpose({
  startProgress,
  resetProgress,
  progress
})

// 清理定时器
onUnmounted(() => {
  if (progressInterval) {
    clearInterval(progressInterval)
  }
})
</script>

<style scoped>
.progress-section {
  margin: 20px 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 努力加载中文字样式 */
.loading-text {
  font-size: 30px;
  color: orange;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: center;
  width: 100%;
}

/* 进度条容器布局 */
.progress-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  width: 100%;
}

.progress-bar {
  flex: 1;
  height: 28px; /* 从40px改回30px，与原版一致 */
  border: 3px solid orange;
  border-radius: 20px; /* 从25px改为20px */
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
  border-radius: 17px; /* 保持原版的17px */
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

/* 响应式设计调整 */
@media (max-width: 768px) {
  .progress-section {
    margin-left: 20px;
  }
  
  .loading-text {
    margin-right: 20px;
  }
  
  .progress-bar {
    width: 275px;
  }
}

@media (max-width: 480px) {
  .progress-section {
    margin-left: 10px;
  }
  
  .loading-text {
    margin-right: 10px;
    font-size: 20px;
  }
  
  .progress-bar {
    width: 220px;
  }
  
  .progress-container {
    gap: 10px;
  }
}
</style>