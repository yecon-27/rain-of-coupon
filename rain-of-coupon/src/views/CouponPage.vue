<template>
  <div class="coupon-page">
    <!-- 头部导航 -->
    <div class="coupon-header">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h2>我的券包</h2>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 券包内容 -->
    <div v-else class="coupon-container">
      <CouponCard :rewards="couponRewards" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useGameStore } from '@/stores/game'
import CouponCard from '@/components/CouponCard.vue'

const router = useRouter()
const authStore = useAuthStore()
const gameStore = useGameStore()

// 状态管理
const loading = ref(true)

// 计算属性：获取优惠券奖励
const couponRewards = computed(() => {
  return gameStore.userRewards.filter(reward => reward.type === 'coupon')
})

// 检查用户登录状态和获取奖励数据
const checkUserStatus = async () => {
  try {
    // 检查登录状态
    if (!authStore.isLoggedIn) {
      router.push('/login?redirect=/coupon')
      return
    }

    // 初始化游戏数据
    gameStore.initializeData()

    // 从游戏store获取用户奖励数据
    const userId = authStore.currentUser?.id || ''
    await gameStore.getUserRewards(userId)

    // 临时mock中奖数据用于测试视图 - 已注释
    // const mockRewards = [
    //   {
    //     id: 'mock_coupon_001',
    //     type: 'coupon' as const,
    //     name: '满500元优惠券',
    //     description: '满500元且消费一道特色菜可使用',
    //     image: '/image/coupon/满500元且消费一道特色菜可使用.png',
    //     value: 500,
    //     expireDate: '2024-12-31',
    //     isUsed: false
    //   },
    //   {
    //     id: 'mock_coupon_002',
    //     type: 'coupon' as const,
    //     name: '满1500元优惠券',
    //     description: '满1500元且消费一道特色菜可使用',
    //     image: '/image/coupon/满1500元且消费一道特色菜可使用.png',
    //     value: 1500,
    //     expireDate: '2024-11-30',
    //     isUsed: true
    //   }
    // ]

    // 添加mock数据到gameStore - 已注释
    // gameStore.userRewards.push(...mockRewards)

  } catch (error: unknown) {
    console.error('获取用户奖励失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}

// 页面初始化
onMounted(() => {
  checkUserStatus()
})
</script>

<style scoped>
.coupon-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #DC143C, #FF6B6B);
}

.coupon-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 20px;
  background: rgba(0, 0, 0, 0.1);
  color: white;
  position: sticky;
  top: 0;
  z-index: 100;
  position: relative;
}

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background-color 0.3s;
  position: absolute;
  left: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.coupon-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 60px);
  color: white;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.coupon-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-height: calc(100vh - 60px);
  padding-top: 20px;
}
</style>