<template>
  <div class="food-display-section">
    <img :src="getImageUrl('zscp.png')" alt="展示菜品" class="food-display-img" />
  </div>
</template>

<script setup lang="ts">
import { API_CONFIG } from '@/config/api'

// 获取图片URL
const getImageUrl = (filename: string) => {
  return `${API_CONFIG.imageURL}${filename}`
}
</script>

<style scoped>
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
</style>