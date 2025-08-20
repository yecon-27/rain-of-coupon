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
import { useGameStore } from '@/stores/gameStore'

const router = useRouter()
const gameStore = useGameStore()
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

const handleGameFinished = async (result: { isWin: boolean; prize?: Prize }) => {
  console.log('🎮 [RedPacketPage] 游戏结束，结果:', result)
  
  gameState.value = 'finished'
  
  // 重新加载用户状态以获取最新的中奖信息
  try {
    await gameStore.loadPrizeRecord()
    console.log('🎮 [RedPacketPage] 重新加载后的中奖状态:', gameStore.hasPrize)
    console.log('🎮 [RedPacketPage] 重新加载后的中奖记录:', gameStore.prizeRecord)
  } catch (error) {
    console.error('🎮 [RedPacketPage] 重新加载中奖状态失败:', error)
  }
  
  // 根据最新的中奖状态决定显示什么
  if (gameStore.hasPrize) {
    console.log('🏆 [RedPacketPage] 显示中奖弹窗')
    prize.value = { amount: gameStore.prizeRecord?.amount || 0 }
    showPrize.value = true
    showEncourage.value = false
  } else {
    console.log('😔 [RedPacketPage] 显示鼓励弹窗')
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