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
      <!-- 不再传递rewards，让CouponCard自己读取gameStore -->
      <CouponCard />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

import CouponCard from '@/components/CouponCard.vue'

const router = useRouter()
const authStore = useAuthStore()


// 状态管理
const loading = ref(true)

// 检查用户状态方法
const checkUserStatus = async () => {
  try {
    console.log('🎫 [CouponPage] 开始检查用户状态')
    
    if (!authStore.isLoggedIn) {
      console.log('🎫 [CouponPage] 用户未登录，跳转到登录页')
      router.push('/login?redirect=/coupon')
      return
    }

    console.log('🎫 [CouponPage] 用户已登录')
    // CouponCard组件会自己加载中奖记录，这里不需要重复加载
    
  } catch (error) {
    console.error('🎫 [CouponPage] 检查用户状态失败:', error)
  } finally {
    loading.value = false
    console.log('🎫 [CouponPage] 加载状态设置为false')
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
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