<template>
  <div class="loading-animation">
    <DynamicImage 
      resource-key="loading_gif" 
      fallback-url="/src/assets/coupon/加载.gif"
      alt="加载中..." 
      class-name="loading-gif" 
    />
  </div>
</template>

<script setup lang="ts">
import DynamicImage from './DynamicImage.vue'
</script>

<style scoped>
.loading-animation {
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-gif {
  width: 500px;
  /* 从350px增加到500px */
  height: 500px;
  /* 从350px增加到500px */
  object-fit: contain;
  display: block;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-gif {
    width: 400px;
    /* 从280px增加到400px */
    height: 400px;
    /* 从280px增加到400px */
  }
}

@media (max-width: 480px) {
  .loading-gif {
    width: 320px;
    /* 从220px增加到320px */
    height: 320px;
    /* 从220px增加到320px */
  }
}
</style>