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
  margin: 0;
  padding: 0;
}

/* 第一部分：home.png 背景和按钮 */
.home-section {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.home-bg {
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  object-position: center top; /* 确保图片顶部内容优先显示 */
  display: block;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;
}

.right-buttons {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 10;
}

.rule-btn, .coupon-btn {
  width: 60px;
  height: 60px;
  cursor: pointer;
  transition: transform 0.2s;
  display: block;
}

.rule-btn:hover, .coupon-btn:hover {
  transform: scale(1.1);
}

.center-button {
  position: absolute;
  bottom: 20%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.challenge-btn {
  width: 200px;
  height: 80px;
  cursor: pointer;
  transition: transform 0.2s;
  display: block;
}

.challenge-btn:hover {
  transform: scale(1.05);
}

/* 第二部分：zscp.png 展示菜品 */
.food-display-section {
  width: 100vw;
  margin: 0;
  padding: 0;
  display: block;
}

.food-display-img {
  width: 100vw;
  height: auto;
  display: block;
  margin: 0;
  padding: 0;
  object-fit: cover;
}

/* 第三、四部分：美食列表 */
.food-list-section {
  width: 100%;
  padding: 20px;
  margin: 0;
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

.overlay-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

/* 响应式设计 - 多断点适配 */

/* 大屏幕 (1200px+) */
@media (min-width: 1200px) {
  .home-bg {
    object-fit: fill; /* 大屏幕可以使用fill避免裁剪 */
  }
  
  .food-list-container {
    max-width: 1000px;
    padding: 30px;
  }
  
  .food-list-header h2 {
    font-size: 28px;
  }
}

/* 中等屏幕 (992px - 1199px) */
@media (max-width: 1199px) and (min-width: 992px) {
  .food-list-container {
    max-width: 900px;
  }
  
  .food-list-header h2 {
    font-size: 26px;
  }
}

/* 小屏幕 (768px - 991px) */
@media (max-width: 991px) and (min-width: 768px) {
  .home-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .right-buttons {
    top: 15px;
    right: 15px;
  }
  
  .rule-btn, .coupon-btn {
    width: 55px;
    height: 55px;
  }
  
  .challenge-btn {
    width: 180px;
    height: 72px;
  }
  
  .food-list-section {
    padding: 15px;
  }
  
  .food-list-header h2 {
    font-size: 22px;
  }
}

/* 移动端 (最大768px) */
@media (max-width: 768px) {
  .home-bg {
    object-fit: cover;
    object-position: center top; /* 确保重要内容在顶部显示 */
  }
  
  .food-list-content {
    grid-template-columns: 1fr; /* 单列布局 */
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
  
  .food-list-section {
    padding: 10px;
  }
  
  .food-list-container {
    padding: 15px;
    border-radius: 10px;
  }
  
  .food-item {
    padding: 10px;
  }
  
  .ranking-badge {
    width: 35px;
    height: 35px;
    margin-right: 12px;
  }
  
  .food-name {
    font-size: 14px;
  }
}

/* 超小屏幕 (最大480px) */
@media (max-width: 480px) {
  .home-section {
    height: 100vh;
    min-height: 600px; /* 确保最小高度 */
  }
  
  .home-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .right-buttons {
    top: 8px;
    right: 8px;
    gap: 8px;
  }
  
  .rule-btn, .coupon-btn {
    width: 45px;
    height: 45px;
  }
  
  .challenge-btn {
    width: 140px;
    height: 56px;
  }
  
  .center-button {
    bottom: 15%;
  }
  
  .food-list-header h2 {
    font-size: 18px;
  }
  
  .food-list-container {
    padding: 12px;
  }
  
  .food-item {
    padding: 8px;
  }
  
  .ranking-badge {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    font-size: 12px;
  }
  
  .food-name {
    font-size: 13px;
  }
}

/* 横屏适配 */
@media (max-height: 500px) and (orientation: landscape) {
  .home-section {
    height: 100vh;
  }
  
  .center-button {
    bottom: 10%;
  }
  
  .challenge-btn {
    width: 120px;
    height: 48px;
  }
}
</style>