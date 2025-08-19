<template>
  <div class="red-packet-page">
    <!-- 使用img标签替代CSS背景，参考ActivitySection的实现 -->
    <img :src="getImageUrl('home.png')" alt="背景" class="background-img" />
    
    <!-- 灰色蒙版层 -->
    <div class="overlay"></div>
    
    <!-- 游戏进行时显示红包雨 -->
    <div v-if="gameState === 'playing'" class="game-layer">
      <RedPacketRain @game-finished="handleGameFinished" />
    </div>
    
    <!-- 游戏结束后显示结果弹窗 -->
    <div v-if="gameState === 'finished'" class="result-layer">
      <PrizeModal v-if="showPrize" :prize="prize" @close="closeModal" />
      <EncourageTip v-if="showEncourage" @close="closeModal" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import RedPacketRain from '@/components/RedPacketRain.vue'
import PrizeModal from '@/components/PrizeModal.vue'
import EncourageTip from '@/components/EncourageTip.vue'
import { API_CONFIG } from '@/config/api'

const router = useRouter()
const gameState = ref<'playing' | 'finished'>('playing')
const showPrize = ref(false)
const showEncourage = ref(false)

// 获取图片URL - 与ActivitySection保持一致
const getImageUrl = (filename: string) => {
  return `${API_CONFIG.imageURL}${filename}`
}

interface Prize {
  amount: number
}
const prize = ref<Prize | null>(null)

const handleGameFinished = (result: { isWin: boolean; prize?: Prize }) => {
  gameState.value = 'finished'
  
  if (result.isWin) {
    prize.value = result.prize as Prize
    showPrize.value = true
    showEncourage.value = false
  } else {
    showPrize.value = false
    showEncourage.value = true
  }
}

const closeModal = () => {
  showPrize.value = false
  showEncourage.value = false
  router.push('/')
}
</script>

<style scoped>
.red-packet-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景图片 - 参考ActivitySection的设置 */
.background-img {
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
  z-index: 1;
}

/* 灰色蒙版层 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  z-index: 10;
  pointer-events: none;
}

/* 游戏内容层 */
.game-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 20;
}

/* 结果弹窗层 */
.result-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 20;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>