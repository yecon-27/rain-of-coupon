<template>
  <div class="countdown-page">
    <!-- 引入HomePage作为背景 -->
    <HomePage />
    
    <!-- 倒计时overlay组件 -->
    <CountDown ref="countdownRef" @finished="goToRedPacketPage" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CountDown from '@/components/CountDown.vue'
import HomePage from '@/views/HomePage.vue'

const router = useRouter()
const countdownRef = ref()

// 页面加载时自动开始倒计时
onMounted(() => {
  countdownRef.value?.startCountdown()
})

const goToRedPacketPage = () => {
  router.push('/red-packet')
}
</script>

<style scoped>
.countdown-page {
  position: relative;
  width: 100vw;
  height: 100vh;
}
</style>