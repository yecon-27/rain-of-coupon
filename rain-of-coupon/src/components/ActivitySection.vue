<template>
  <div class="activity-section">
    <img :src="getImageUrl('home.png')" alt="首页背景" class="activity-bg" />
    
    <!-- 右侧按钮组 -->
    <div class="side-buttons">
      <img :src="getImageUrl('gz.png')" alt="规则" class="rule-btn" @click="$emit('showRules')" />
      <img :src="getImageUrl('qb.png')" alt="券包" class="coupon-btn" @click="$emit('myCoupons')" />
    </div>
    
    <!-- 底部居中按钮 -->
    <div class="center-button">
      <img :src="getImageUrl('button.png')" alt="立即挑战" class="challenge-btn" @click="$emit('joinActivity')" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { API_CONFIG } from '@/config/api'

// 定义事件
defineEmits<{
  showRules: []
  myCoupons: []
  joinActivity: []
}>()

// 获取图片URL
const getImageUrl = (filename: string) => {
  return `${API_CONFIG.imageURL}${filename}`
}
</script>

<style scoped>
.activity-section {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.activity-bg {
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  object-position: center top;
  display: block;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;
}

.side-buttons {
  position: fixed;
  top: 50vh;
  right: -1px;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 15px;
  z-index: 1000;
}

.rule-btn {
  width: 96px;
  height: 96px;
  cursor: pointer;
  display: block;
  object-fit: contain;
}

.coupon-btn {
  width: 120px;
  height: 120px;
  cursor: pointer;
  display: block;
  object-fit: contain;
}

.center-button {
  position: absolute;
  bottom: 5%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.challenge-btn {
  width: 300px;
  height: 120px;
  cursor: pointer;
  display: block;
  animation: breathe 2s ease-in-out infinite;
}

@keyframes breathe {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

.challenge-btn:hover {
  animation-play-state: paused;
  transform: scale(1.08);
}

/* 响应式设计 */
@media (max-width: 1199px) and (min-width: 992px) {
  .challenge-btn {
    width: 270px;
    height: 108px;
  }
}

@media (max-width: 991px) and (min-width: 768px) {
  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .rule-btn {
    width: 84px;
    height: 84px;
  }
  
  .coupon-btn {
    width: 105px;
    height: 105px;
  }
  
  .challenge-btn {
    width: 270px;
    height: 108px;
  }
}

@media (max-width: 768px) {
  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .rule-btn {
    width: 78px;
    height: 78px;
  }
  
  .coupon-btn {
    width: 98px;
    height: 98px;
  }
  
  .challenge-btn {
    width: 240px;
    height: 96px;
  }
}

@media (max-width: 480px) {
  .activity-section {
    min-height: 600px;
  }
  
  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .rule-btn {
    width: 72px;
    height: 72px;
  }
  
  .coupon-btn {
    width: 90px;
    height: 90px;
  }
  
  .challenge-btn {
    width: 210px;
    height: 84px;
  }
  
  .center-button {
    bottom: 5%;
  }
}

@media (max-height: 500px) and (orientation: landscape) {
  .activity-section {
    height: 100vh;
  }
  
  .center-button {
    bottom: 5%;
  }
  
  .challenge-btn {
    width: 180px;
    height: 72px;
  }
}
</style>