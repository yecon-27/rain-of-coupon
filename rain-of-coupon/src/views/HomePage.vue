<template>
  <div class="home-page">
    <!-- 第一部分：home.png 背景和按钮 -->
    <div class="home-section">
      <img :src="getImageUrl('home.png')" alt="首页背景" class="home-bg" />
      
      <!-- 右侧按钮组 -->
      <div class="right-buttons">
        <img :src="getImageUrl('gz.png')" alt="规则" class="rule-btn" @click="showRules" />
        <img :src="getImageUrl('qb.png')" alt="券包" class="coupon-btn" @click="myCoupons" />
      </div>
      
      <!-- 底部居中按钮 -->
      <div class="center-button">
        <img :src="getImageUrl('button.png')" alt="立即挑战" class="challenge-btn" @click="joinActivity" />
      </div>
    </div>

    <!-- 第二部分：zscp.png 展示菜品 -->
    <div class="food-display-section">
      <img :src="getImageUrl('zscp.png')" alt="展示菜品" class="food-display-img" />
    </div>

    <!-- 第三部分：TOP10网络人气特色美食 -->
    <div class="food-list-section top10-section">
      <div class="food-list-container">
        <div class="food-list-header">
          <h2>2024年网络人气票选特色美食TOP10</h2>
        </div>
        <div class="food-list-content">
          <div 
            v-for="item in top10Foods" 
            :key="item.id" 
            class="food-item"
          >
            <div class="ranking-badge">{{ item.ranking }}</div>
            <div class="food-name">{{ item.foodName }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 第四部分："一镇一品"特色菜 -->
    <div class="food-list-section town-section">
      <div class="food-list-container">
        <div class="food-list-header">
          <h2>2024年网络人气票选"一镇一品"特色菜</h2>
        </div>
        <div class="food-list-content">
          <div 
            v-for="item in townFoods" 
            :key="item.id" 
            class="food-item"
          >
            <div class="ranking-badge">{{ item.ranking }}</div>
            <div class="food-name">{{ item.foodName }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 蒙版层（当有overlay时显示） -->
    <div v-if="showOverlay" class="overlay-mask"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTop10PopularFood, getTownSpecialtyFood } from '@/api/food'
import { API_CONFIG } from '@/config/api'

// 响应式数据
const showOverlay = ref(false)
const top10Foods = ref([])
const townFoods = ref([])
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

// 获取图片URL
const getImageUrl = (filename: string) => {
  return `${API_CONFIG.imageURL}${filename}`
}
</script>

<style scoped>
.home-page {
  position: relative;
  width: 100vw;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 第一部分：home.png 背景和按钮 */
.home-section {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.home-bg {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.right-buttons {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rule-btn, .coupon-btn {
  width: 60px;
  height: 60px;
  cursor: pointer;
  transition: transform 0.2s;
}

.rule-btn:hover, .coupon-btn:hover {
  transform: scale(1.1);
}

.center-button {
  position: absolute;
  bottom: 20%;
  left: 50%;
  transform: translateX(-50%);
}

.challenge-btn {
  width: 200px;
  height: 80px;
  cursor: pointer;
  transition: transform 0.2s;
}

.challenge-btn:hover {
  transform: scale(1.05);
}

/* 第二部分：zscp.png 展示菜品 */
.food-display-section {
  width: 100%;
  text-align: center;
  padding: 20px 0;
}

.food-display-img {
  max-width: 100%;
  height: auto;
}

/* 第三、四部分：美食列表 */
.food-list-section {
  width: 100%;
  padding: 20px;
}

.food-list-container {
  max-width: 800px;
  margin: 0 auto;
  background: linear-gradient(135deg, #8B0000, #DC143C);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.food-list-header {
  text-align: center;
  margin-bottom: 20px;
}

.food-list-header h2 {
  color: #FFD700;
  font-size: 24px;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  margin: 0;
}

.food-list-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.food-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 12px;
  transition: all 0.3s ease;
}

.food-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.ranking-badge {
  width: 40px;
  height: 40px;
  background: linear-gradient(45deg, #FFD700, #FFA500);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #8B0000;
  margin-right: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.food-name {
  color: white;
  font-size: 16px;
  font-weight: 500;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  flex: 1;
}

/* 区分两个列表的样式 */
.top10-section .food-list-container {
  background: linear-gradient(135deg, #8B0000, #DC143C);
}

.town-section .food-list-container {
  background: linear-gradient(135deg, #B22222, #CD5C5C);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .food-list-content {
    grid-template-columns: 1fr;
  }
  
  .food-list-header h2 {
    font-size: 20px;
  }
  
  .right-buttons {
    top: 10px;
    right: 10px;
  }
  
  .rule-btn, .coupon-btn {
    width: 50px;
    height: 50px;
  }
  
  .challenge-btn {
    width: 160px;
    height: 64px;
  }
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