<template>
  <div class="home-page">
    <!-- 第一部分：参与活动区域 -->
    <ActivitySection @show-rules="showRules" @my-coupons="myCoupons" @join-activity="joinActivity" />

    <!-- 第二部分：展示菜品 -->
    <FoodDisplaySection />

    <!-- 第三部分：Top10Food -->
    <Top10FoodSection :food-items="top10Foods" />

    <!-- 第四部分：SpecialityFood -->
    <SpecialityFoodSection :food-items="specialityFoods" />

    <!-- 蒙版层（当有overlay时显示） -->
    <div v-if="showOverlay" class="overlay-mask"></div>

    <!-- 拥挤提示组件 -->
    <CrowdingTip 
      :visible="showCrowdingTip" 
      @retry="handleRetry" 
      @close="handleClose" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getTop10Food, getSpecialityFood, type FoodItem } from '@/api/food'
import ActivitySection from '@/components/ActivitySection.vue'
import FoodDisplaySection from '@/components/FoodDisplaySection.vue'
import Top10FoodSection from '@/components/Top10FoodSection.vue'
import SpecialityFoodSection from '@/components/SpecialityFoodSection.vue'
import CrowdingTip from '@/components/CrowdingTip.vue'

// 路由和认证
const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const showOverlay = ref(false)
const showCrowdingTip = ref(false)
const top10Foods = ref<FoodItem[]>([])
const specialityFoods = ref<FoodItem[]>([])
const loading = ref(false)

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
const joinActivity = () => {
  // 检查是否已登录
  if (authStore.isLoggedIn) {
    // 已登录，跳转到加载页面
    console.log('用户已登录，跳转到加载页面')
    router.push('/loading')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?redirect=/')
  }
}

const showRules = () => {
  // 显示规则
  console.log('显示规则')
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

// CrowdingTip 处理函数
const handleRetry = () => {
  // 重新尝试参与活动
  showCrowdingTip.value = false
  // 重新跳转到加载页面
  router.push('/loading')
}

const handleClose = () => {
  // 关闭拥挤提示，留在首页
  showCrowdingTip.value = false
}

// 显示拥挤提示的方法（供外部调用）
const showCrowdingMessage = () => {
  showCrowdingTip.value = true
}

// 暴露方法供其他组件调用
defineExpose({
  showCrowdingMessage
})

// 组件挂载时获取数据
onMounted(() => {
  // 检查认证状态
  authStore.checkAuthStatus()
  fetchFoodData()
  
  // 检查URL参数，如果有showCrowding=true则显示拥挤提示
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('showCrowding') === 'true') {
    showCrowdingTip.value = true
    // 清除URL参数
    window.history.replaceState({}, '', window.location.pathname)
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
</style>