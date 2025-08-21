<template>
  <div v-if="visible" class="warning-tip-overlay" @click="handleOverlayClick">
    <div class="warning-tip-container" @click.stop>
      <!-- 警告图标 -->
      <div class="warning-icon-container">
        <div class="warning-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 9V13M12 17H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#FF6B35" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
      
      <!-- 标题 -->
      <h3 class="warning-title">今日已参与</h3>
      
      <!-- 提示文本 -->
      <p class="warning-text">你今天已经抽过奖了，<br>过会再试试吧～</p>
      
      <!-- 按钮 -->
      <button @click="handleConfirm" class="confirm-button">
        <span>我知道了</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, onMounted, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['close'])

// 添加调试信息
onMounted(() => {
  console.log('🚨 WarningTip组件已挂载，visible状态:', props.visible)
})

// 监听visible变化
watch(() => props.visible, (newVal) => {
  console.log('🚨 WarningTip visible状态变化:', newVal)
}, { immediate: true })

const handleConfirm = () => {
  console.log('🚨 用户点击了确认按钮')
  emit('close')
}

const handleOverlayClick = () => {
  console.log('🚨 用户点击了遮罩层')
  emit('close')
}
</script>

<style scoped>
.warning-tip-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.6));
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease-out;
}

.warning-tip-container {
  background: linear-gradient(145deg, #ffffff, #f8f9fa);
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  width: 85%;
  max-width: 380px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideUp 0.4s ease-out;
  position: relative;
  overflow: hidden;
}

.warning-tip-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF6B35, #F7931E, #FF6B35);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
}

.warning-icon-container {
  margin-bottom: 24px;
}

.warning-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto;
  background: linear-gradient(135deg, #FFF5F5, #FED7D7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.2);
  animation: pulse 2s infinite;
}

.warning-icon svg {
  width: 32px;
  height: 32px;
}

.warning-title {
  font-size: 22px;
  font-weight: 600;
  color: #2D3748;
  margin: 0 0 16px 0;
  letter-spacing: -0.5px;
}

.warning-text {
  font-size: 16px;
  color: #4A5568;
  margin-bottom: 32px;
  line-height: 1.6;
  font-weight: 400;
}

.confirm-button {
  background: linear-gradient(135deg, #FF6B35, #F7931E);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
  position: relative;
  overflow: hidden;
}

.confirm-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}

.confirm-button:active {
  transform: translateY(0);
}

.confirm-button span {
  position: relative;
  z-index: 1;
}

.confirm-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.confirm-button:hover::before {
  left: 100%;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>