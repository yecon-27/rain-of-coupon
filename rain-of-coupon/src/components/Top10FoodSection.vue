<template>
  <div class="top10-food-section">
    <div class="food-list-container">
      <div class="food-list-header">
        <h2>2024年网络人气票选特色美食TOP10</h2>
      </div>
      <div class="food-list-content">
        <div v-for="item in foodItems" :key="item.id" class="food-item">
          <div class="ranking-badge">{{ item.ranking }}</div>
          <div class="food-name">{{ item.foodName }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FoodItem } from '@/api/food'

// 定义props
interface Props {
  foodItems: FoodItem[]
}

defineProps<Props>()
</script>

<style scoped>
.top10-food-section {
  width: 100%;
  padding: 60px 0 0 0;
  margin: 0;
  background: rgb(205 51 51);
  /* 红色背景 */
}

.food-list-container {
  max-width: 800px;
  margin: 35px auto 0 auto;
  background: OldLace;
  /* 浅黄色内部背景 */
  border-radius: 15px;
  padding: 45px 20px 20px 20px;
  position: relative;
}

.food-list-header {
  text-align: center;
  margin-bottom: 20px;
  width: 100vw;
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.food-list-header h2 {
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin: 0 auto;
  background: linear-gradient(135deg, #FF6B35, #FF8E53, #FFB366);
  padding: 8px 30px;
  border-radius: 50px;
  white-space: nowrap;
  display: inline-block;
  text-align: center;
}

/* 两列网格布局 */
.food-list-content {
  display: grid !important;
  grid-template-columns: 1fr 1fr !important;
  gap: 6px;
  /* 进一步缩小行间距 */
  align-items: start;
  margin-top: 15px;
}

.food-item {
  display: flex;
  align-items: center;
  background: transparent;
  padding: 8px;
  /* 从12px改为8px，缩小内边距 */
  transition: all 0.3s ease;
  width: 100%;
  box-sizing: border-box;
}

.food-item:hover {
  background: transparent;
  transform: translateY(-2px);
}

.ranking-badge {
  width: 60px; /* 40px * 1.5 */
  height: 60px; /* 40px * 1.5 */
  background: linear-gradient(45deg, Tomato, OrangeRed);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900; /* 从bold改为900，更粗的字体 */
  color: #FFFFFF;
  font-size: 22px;
  margin-right: 15px;
  flex-shrink: 0;
}

.food-name {
  color: rgb(178, 34, 34);
  font-size: 20px;
  font-weight: 500;
  flex: 1;
  white-space: nowrap;
}

/* 响应式设计 */
@media (min-width: 1200px) {
  .food-list-container {
    max-width: 1000px;
    padding: 30px;
  }

  .food-list-header h2 {
    font-size: 28px;
  }
}

@media (max-width: 1199px) and (min-width: 992px) {
  .food-list-container {
    max-width: 900px;
  }

  .food-list-header h2 {
    font-size: 26px;
  }
}

@media (max-width: 991px) and (min-width: 768px) {
  .top10-food-section {
    padding: 0;
  }

  .food-list-header h2 {
    font-size: 22px;
  }
}

/* 移动端单列布局 */
@media (max-width: 768px) {
  .food-list-content {
    grid-template-columns: 1fr;
    /* 移动端改为单列 */
  }

  .food-list-header h2 {
    font-size: 20px;
  }

  .top10-food-section {
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
    width: 52.5px; /* 35px * 1.5 */
    height: 52.5px; /* 35px * 1.5 */
    margin-right: 12px;
    font-size: 14px;
    /* 移动端也相应增大 */
  }

  .food-name {
    font-size: 17.5px;
  }
}

@media (max-width: 480px) {
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
    font-size: 13px;
    /* 小屏幕也相应增大 */
  }

  .food-name {
    font-size: 16.25px;
  }
}
</style>