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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTop10Food, getSpecialityFood, type FoodItem } from '@/api/food'
import ActivitySection from '@/components/ActivitySection.vue'
import FoodDisplaySection from '@/components/FoodDisplaySection.vue'
import Top10FoodSection from '@/components/Top10FoodSection.vue'
import SpecialityFoodSection from '@/components/SpecialityFoodSection.vue'

// 路由
const router = useRouter()

// 响应式数据
const showOverlay = ref(false)
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
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  if (isLoggedIn === 'true') {
    // 已登录，跳转到活动页面或显示倒计时
    console.log('用户已登录，开始活动')
    // 这里可以跳转到活动页面或显示倒计时overlay
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?redirect=/')
  }
}

const showRules = () => {
  // 显示规则
  console.log('显示规则')
}

const myCoupons = () => {
  // 跳转到我的优惠券
  console.log('我的优惠券')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchFoodData()
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