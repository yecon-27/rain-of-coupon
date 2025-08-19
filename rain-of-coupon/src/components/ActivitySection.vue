<template>
  <div class="activity-section">
    <img :src="getImageUrl('home.png')" alt="首页背景" class="activity-bg" />

    <!-- 登录状态显示 -->
    <div v-if="authStore.isLoggedIn" class="login-status">
      <span class="user-info">{{ authStore.currentUser?.nickname || '用户' }}</span>
      <button @click="authStore.logout" class="logout-btn">登出</button>
    </div>

    <!-- 右侧按钮组 -->
    <img :src="getImageUrl('gz.png')" alt="规则" class="rule-btn" @click="$emit('showRules')" />
    <img :src="getImageUrl('qb.png')" alt="券包" class="coupon-btn" @click="$emit('myCoupons')" />

    <!-- 底部居中按钮 -->
    <div class="center-button">
      <img :src="getImageUrl('button.png')" alt="立即挑战" class="challenge-btn" @click="handleJoinActivity" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useGameStore } from '@/stores/gameStore'
import { API_CONFIG } from '@/config/api'

// 定义事件
defineEmits<{
  showRules: []
  myCoupons: []
}>()

// 路由和store
const router = useRouter()
const authStore = useAuthStore()
const gameStore = useGameStore()

// 获取图片URL
const getImageUrl = (filename: string) => {
  return `${API_CONFIG.imageURL}${filename}`
}

// 处理立即挑战按钮点击
const handleJoinActivity = () => {
  // 检查是否已登录
  if (!authStore.isLoggedIn) {
    // 未登录，跳转到登录页面
    router.push('/login?redirect=/')
    return
  }

  // 已登录，检查是否已中奖
  if (gameStore.hasPrize) {
    // 已中奖，跳转到PrizeModal页面显示中奖情况
    router.push('/prize')
  } else {
    // 未中奖，跳转到LoadingPage开始新游戏
    router.push('/loading')
  }
}

// 组件挂载时检查登录状态和加载中奖记录
onMounted(async () => {
  authStore.checkAuthStatus();
  // 从数据库加载真实的中奖记录
  await gameStore.loadPrizeRecord();
});
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

.login-status {
  position: fixed;
  top: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px 15px;
  border-radius: 20px;
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  background: #DC143C;
  color: white;
  border: none;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #B91C3C;
  transform: translateY(-1px);
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
  height: 96px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
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
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
}

.center-button {
  position: absolute;
  bottom: 5%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.challenge-btn {
  width: 375px;
  /* 300px * 1.25 */
  height: 125px;
  /* 100px * 1.25 */
  cursor: pointer;
  display: block;
  animation: breathe 1.2s ease-in-out infinite;
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
    width: 337.5px;
    /* 270px * 1.25 */
    height: 112.5px;
    /* 90px * 1.25 */
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
    width: 337.5px;
    /* 270px * 1.25 */
    height: 112.5px;
    /* 90px * 1.25 */
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
    width: 300px;
    /* 240px * 1.25 */
    height: 100px;
    /* 80px * 1.25 */
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
    width: 262.5px;
    /* 210px * 1.25 */
    height: 87.5px;
    /* 70px * 1.25 */
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
    width: 225px;
    /* 180px * 1.25 */
    height: 75px;
    /* 60px * 1.25 */
  }
}
</style>