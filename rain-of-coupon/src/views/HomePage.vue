<template>
  <div class="home-page">


    <!-- 第一部分：参与活动区域 -->
    <ActivitySection @show-rules="showRules" @my-coupons="myCoupons" />

    <!-- 第二部分：展示菜品 -->
    <FoodDisplaySection />

    <!-- 第三部分：Top10Food -->
    <Top10FoodSection :food-items="top10Foods" />

    <!-- 第四部分：SpecialityFood -->
    <SpecialityFoodSection :food-items="specialityFoods" />

    <!-- 蒙版层（当有overlay时显示） -->
    <div v-if="uiStore.showOverlay" class="overlay-mask"></div>

    <!-- 临时API测试按钮
    <div class="api-test-panel" v-if="isDev">
      <button @click="runAPITest" class="test-btn">测试图片API</button>
      <button @click="runNetworkTest" class="test-btn">测试网络连接</button>
    </div> -->

    <!-- 警告提示组件 -->
    <WarningTip :visible="showWarningTip" @close="showWarningTip = false" />

    <!-- 规则弹窗组件 -->
    <RulePopup 
      :visible="showRulePopup" 
      @close="showRulePopup = false"
      @confirm="showRulePopup = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted,onBeforeUnmount, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUIStore } from '@/stores/ui'
import { getTop10Food, getSpecialityFood, type FoodItem } from '@/api/food'
import { testImageAPI, testNetworkConnection } from '@/utils/apiTest'

import ActivitySection from '@/components/ActivitySection.vue'
import FoodDisplaySection from '@/components/FoodDisplaySection.vue'
import Top10FoodSection from '@/components/Top10FoodSection.vue'
import SpecialityFoodSection from '@/components/SpecialityFoodSection.vue'
import WarningTip from '@/components/WarningTip.vue'
import RulePopup from '@/components/RulePopup.vue'

// 路由和认证
const router = useRouter()
const authStore = useAuthStore()
const uiStore = useUIStore()
const top10Foods = ref<FoodItem[]>([])
const specialityFoods = ref<FoodItem[]>([])
const loading = ref(false)
const showRulePopup = ref(false)
const showWarningTip = ref(false)
const isDev = import.meta.env.DEV



let sessionTimer: number | null = null;
// 定义更新 sessionId 的函数
const updateSessionId = () => {
    console.log('🔄 30分钟定时器触发，正在更新 sessionId');
    // 生成一个新的 sessionId
    const newSessionId = Math.random().toString(36).substring(2, 15);
    // 覆盖 localStorage 中的旧值
    localStorage.setItem('sessionId', newSessionId);
    console.log('✅ sessionId 已更新为:', newSessionId);
};
// 获取美食列表数据
const fetchFoodData = async () => {
  loading.value = true
  try {
    // 并行获取两个列表的数据
    const [top10Response, specialityResponse] = await Promise.all([
      getTop10Food(),
      getSpecialityFood()
    ])

    if (top10Response.code === 200) {
      top10Foods.value = top10Response.rows
    }

    if (specialityResponse.code === 200) {
      specialityFoods.value = specialityResponse.rows
    }
  } catch (error) {
    console.error('获取美食数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 按钮点击事件
// 移除 joinActivity 函数，因为现在由 ActivitySection 直接处理
// const joinActivity = () => {
//   // 检查是否已登录
//   if (authStore.isLoggedIn) {
//     // 已登录，跳转到加载页面
//     console.log('用户已登录，跳转到加载页面')
//     router.push('/loading')
//   } else {
//     // 未登录，跳转到登录页面
//     router.push('/login?redirect=/')
//   }
// }

const showRules = () => {
  // 显示规则弹窗
  showRulePopup.value = true
}

// 登出功能（暂时未使用，保留备用）
// const logout = () => {
//   localStorage.removeItem('isLoggedIn')
//   localStorage.removeItem('currentUser')
//   console.log('用户已登出')
//   // 可以选择刷新页面或显示提示
//   window.location.reload()
// }

const myCoupons = () => {
  // 检查是否已登录
  if (authStore.isLoggedIn) {
    // 已登录，跳转到券包页面
    console.log('用户已登录，跳转到券包页面')
    router.push('/coupon')
  } else {
    // 未登录，跳转到登录页面，登录成功后回到券包页面
    console.log('用户未登录，跳转到登录页面')
    router.push('/login?redirect=/coupon')
  }
}

// API测试函数
const runAPITest = () => {
  testImageAPI()
}

const runNetworkTest = () => {
  testNetworkConnection()
}



// CrowdingTip现在通过点击图片直接处理跳转，不需要额外的处理函数

// 组件挂载时获取数据
onMounted(() => {
  // 检查认证状态
  authStore.checkAuthStatus()
  fetchFoodData()
  

  
  // 检查URL参数，如果有showWarning=true则显示警告提示
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('showWarning') === 'true') {
    console.log('🚨 [HomePage] 检测到showWarning参数，显示WarningTip')
    showWarningTip.value = true
    // 清除URL参数
    window.history.replaceState({}, '', window.location.pathname)
  }
  
  // 设置30分钟定时器更新sessionId（正常情况下）
  sessionTimer = setInterval(updateSessionId, 30 * 60 * 1000);
})
// 组件卸载时清除定时器，防止内存泄漏
onBeforeUnmount(() => {
    if (sessionTimer) {
        clearInterval(sessionTimer);
        sessionTimer = null;
        console.log('🧹 页面卸载，定时器已清除');
    }
})
</script>

<style scoped>
.home-page {
  position: relative;
  width: 100vw;
  min-height: 100vh;
  background-color: #f5f5f5;
  margin: 0;
  padding: 0;
}

.overlay-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.api-test-panel {
  position: fixed;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 10px;
  z-index: 9999;
}

.test-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.test-btn:hover {
  background: #0056b3;
}
</style>