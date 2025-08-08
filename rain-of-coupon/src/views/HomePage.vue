<template>
  <div class="home-page">
    <!-- 背景内容 -->
    <div class="background-content">
      <!-- 美食展示图片等背景内容 -->
      <div class="food-display">
        <!-- 背景图片和内容 -->
      </div>
      
      <!-- 主要按钮 -->
      <div class="main-buttons">
        <button @click="joinActivity" class="join-btn">加入活动</button>
        <button @click="showRules" class="rules-btn">活动规则</button>
        <button @click="myCoupons" class="coupons-btn">我的优惠券</button>
      </div>
    </div>
    
    <!-- 蒙版层（当有overlay时显示） -->
    <div v-if="showOverlay" class="overlay-mask"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const showOverlay = ref(false)

const joinActivity = () => {
  // 跳转到倒计时页面或直接显示倒计时overlay
}

const showRules = () => {
  // 显示规则
}

const myCoupons = () => {
  // 跳转到我的优惠券
}
</script>

<style scoped>
.home-page {
  position: relative;
  width: 100vw;
  height: 100vh;
}

.background-content {
  width: 100%;
  height: 100%;
  /* 背景样式 */
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

.main-buttons {
  position: absolute;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 20px;
}
</style>