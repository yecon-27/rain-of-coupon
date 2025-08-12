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
  padding: 20px;
  margin: 0;
  background: #DC143C;
  /* 红色背景 */
}

.food-list-container {
  max-width: 800px;
  margin: 0 auto;
  background: #F5DEB3;
  /* 米黄色内部背景 */
  border-radius: 15px;
  padding: 20px;
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

/* 两列网格布局 */
.food-list-content {
  display: grid !important;
  grid-template-columns: 1fr 1fr !important;
  gap: 15px;
  align-items: start;
}

.food-item {
  display: flex;
  align-items: center;
  background: transparent;
  padding: 12px;
  transition: all 0.3s ease;
  width: 100%;
  box-sizing: border-box;
}

.food-item:hover {
  background: transparent;
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
    padding: 15px;
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
    width: 35px;
    height: 35px;
    margin-right: 12px;
  }

  .food-name {
    font-size: 14px;
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
    font-size: 12px;
  }

  .food-name {
    font-size: 13px;
  }
}
</style>