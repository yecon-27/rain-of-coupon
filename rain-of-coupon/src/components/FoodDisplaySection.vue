<template>
  <div class="food-display-section">
    <DynamicImage resource-key="show_dishes" fallback-url="/src/assets/coupon/zscp.png" alt="展示菜品"
      class-name="food-display-img" />
  </div>
</template>

<script setup lang="ts">
import DynamicImage from './DynamicImage.vue'
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