<template>
  <div class="activity-section">
    <img :src="getImageUrl('home.png')" alt="首页背景" class="activity-bg" />

    <!-- 右侧按钮组 -->
    <img :src="getImageUrl('gz.png')" alt="规则" class="rule-btn" @click="$emit('showRules')" />
    <img :src="getImageUrl('qb.png')" alt="券包" class="coupon-btn" @click="$emit('myCoupons')" />

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

.rule-btn {
  position: fixed;
  top: calc(50vh - 40px);
  right: -2px;
  width: 96px;
  /* 96px * 1.5 */
  height: 96px;
  /* 96px * 1.5 */
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 1000;
}

.coupon-btn {
  position: fixed;
  top: calc(50vh + 15px);
  right: -2px;
  width: 110px;
  height: 110px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 1000;
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

  0%,
  100% {
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
    width: 90px;
    /* 84px * 1.5 */
    height: 90px;
    top: calc(50vh - 45px);
  }

  .coupon-btn {
    width: 100px;
    height: 100px;
    top: calc(50vh + 10px);
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
    width: 85px;
    /* 78px * 1.5 */
    height: 85px;
    top: calc(50vh - 48px);
  }

  .coupon-btn {
    width: 95px;
    height: 95px;
    top: calc(50vh + 8px);
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
    width: 80px;
    /* 72px * 1.5 */
    height: 80px;
    top: calc(50vh - 50px);
  }

  .coupon-btn {
    width: 90px;
    height: 90px;
    top: calc(50vh + 5px);
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