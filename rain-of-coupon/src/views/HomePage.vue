<template>
  <div class="home-page">
    <!-- 第一部分：参与活动区域 -->
    <ActivitySection @show-rules="showRules" @my-coupons="myCoupons" @join-activity="joinActivity" />

    <!-- 第二部分：展示菜品 -->
    <FoodDisplaySection />

    <!-- 第三部分：TOP10网络人气特色美食 -->
    <FoodListSection title="2024年网络人气票选特色美食TOP10" :food-items="top10Foods" section-class="top10-section" />

    <!-- 第四部分："一镇一品"特色菜 -->
    <FoodListSection title="2024年网络人气票选'一镇一品'特色菜" :food-items="townFoods" section-class="town-section" />

    <!-- 蒙版层（当有overlay时显示） -->
    <div v-if="showOverlay" class="overlay-mask"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTop10PopularFood, getTownSpecialtyFood, type FoodItem } from '@/api/food'
import ActivitySection from '@/components/ActivitySection.vue'
import FoodDisplaySection from '@/components/FoodDisplaySection.vue'
import FoodListSection from '@/components/FoodListSection.vue'

// 响应式数据
const showOverlay = ref(false)
const top10Foods = ref<FoodItem[]>([])
const townFoods = ref<FoodItem[]>([])
const loading = ref(false)

// 获取美食列表数据
const fetchFoodData = async () => {
  loading.value = true
  try {
    // 并行获取两个列表的数据
    const [top10Response, townResponse] = await Promise.all([
      getTop10PopularFood(),
      getTownSpecialtyFood()
    ])

    if (top10Response.code === 200) {
      top10Foods.value = top10Response.rows
    }

    if (townResponse.code === 200) {
      townFoods.value = townResponse.rows
    }
  } catch (error) {
    console.error('获取美食数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 按钮点击事件
const joinActivity = () => {
  // 跳转到倒计时页面或直接显示倒计时overlay
  console.log('加入活动')
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