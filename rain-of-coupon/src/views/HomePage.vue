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
import { getTop10PopularFood, getTownSpecialtyFood, type FoodItem } from '@/api/food'
import { API_CONFIG } from '@/config/api'

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
  object-position: center top;
  display: block;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;
}

/* 分开定义右侧按钮 */
.rule-btn {
  position: fixed;
  top: calc(50vh - 60px); /* 上方按钮位置 */
  right: 0;
  width: 80px; /* 较小尺寸 */
  height: 80px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 1000;
}

.coupon-btn {
  position: fixed;
  top: calc(50vh + 10px); /* 下方按钮位置，距离更近 */
  right: 0;
  width: 100px; /* 较大尺寸 */
  height: 100px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 1000;
}

/* 移除原来的 right-buttons 样式 */
/* .right-buttons 样式可以删除或注释掉 */

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
  0%, 100% {
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
  background: #DC143C; /* 红色背景 */
}

.food-list-container {
  max-width: 800px;
  margin: 0 auto;
  background: #F5DEB3; /* 米黄色内部背景 */
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.food-list-header {
  text-align: center;
  margin-bottom: 20px;
}

.food-list-header h2 {
  color: #8B0000;
  font-size: 24px;
  font-weight: bold;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  margin: 0;
}

.food-list-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.food-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.food-item {
  display: flex;
  align-items: center;
  background: rgba(139, 0, 0, 0.1);
  border-radius: 10px;
  padding: 12px;
  transition: all 0.3s ease;
}

.food-item:hover {
  background: rgba(139, 0, 0, 0.2);
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
  color: #8B0000;
  font-size: 16px;
  font-weight: 500;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  flex: 1;
}

/* 区分两个列表的样式 */
.top10-section .food-list-container {
  background: linear-gradient(135deg, #8B0000, #DC143C);
}

.town-section .food-list-container {
  background: linear-gradient(135deg, #B22222, #CD5C5C);
}

/* 第四部分特殊布局：第一个菜品独占一行，其余左右排序 */
.town-section .food-list-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.town-section .food-item:first-child {
  grid-column: 1 / -1; /* 第一个菜品跨越所有列 */
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
    right: -1px;
  }
  
  .rule-btn {
    width: 84px;
    height: 84px;
  }
  
  .coupon-btn {
    width: 105px;
    height: 105px;
  }
  
  .challenge-btn {
    width: 270px;
    height: 108px;
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
    flex-direction: column; /* 单列布局 */
    gap: 15px;
  }
  
  .food-column {
    gap: 12px;
  }
  
  .food-list-header h2 {
    font-size: 20px;
  }
  
  .right-buttons {
    right: -1px;
  }
  
  .rule-btn, .coupon-btn {
    width: 98px;
    height: 98px;
    object-fit: contain;
  }
  
  .challenge-btn {
    width: 240px;
    height: 96px;
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
    right: -1px;
    gap: 12px;
  }
  
  .rule-btn, .coupon-btn {
    width: 90px;
    height: 90px;
    object-fit: contain;
  }
  
  .challenge-btn {
    width: 210px;
    height: 84px;
  }
  
  .center-button {
    bottom: 5%;
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
    bottom: 5%;
  }
  
  .challenge-btn {
    width: 180px;
    height: 72px;
  }
}
@media (max-width: 1200px) {
  .rule-btn {
    width: 80px;
    height: 80px;
    top: calc(50vh - 50px);
  }
  
  .coupon-btn {
    width: 90px;
    height: 90px;
    top: calc(50vh + 8px);
  }
}

@media (max-width: 768px) {
  .home-bg {
    object-fit: cover;
    object-position: center top;
  }
  
  .rule-btn {
    width: 75px;
    height: 75px;
    top: calc(50vh - 45px);
  }
  
  .coupon-btn {
    width: 85px;
    height: 85px;
    top: calc(50vh + 6px);
  }
  
  .challenge-btn {
    width: 240px;
    height: 96px;
  }
}

@media (max-width: 480px) {
  .rule-btn {
    width: 70px;
    height: 70px;
    top: calc(50vh - 40px);
  }
  
  .coupon-btn {
    width: 80px;
    height: 80px;
    top: calc(50vh + 5px);
  }
}






/* 响应式设计 */
@media (max-width: 768px) {

}

@media (max-width: 480px) {
  .town-section .ranking-badge {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    font-size: 12px;
  }
  
  .town-section .food-name {
    font-size: 13px;
  }
}
</style>