<template>
  <div class="lottery-example">
    <!-- 用户状态显示 -->
    <div class="user-status">
      <p>剩余次数: {{ userStatus.remainingCount }}</p>
      <p>是否可以抽奖: {{ canDraw ? '是' : '否' }}</p>
      <p>活动状态: {{ isActivityActive ? '进行中' : '未开始/已结束' }}</p>
    </div>

    <!-- 游戏控制 -->
    <div class="game-controls">
      <button @click="handleStartGame" :disabled="!canDraw">
        开始红包雨
      </button>
      <button @click="handleClickRedPacket" :disabled="gameSession.gameStatus !== 'playing'">
        点击红包 ({{ gameSession.clickedCount }})
      </button>
      <button @click="handleEndGame" :disabled="gameSession.gameStatus !== 'playing'">
        结束游戏
      </button>
    </div>

    <!-- 抽奖结果 -->
    <div v-if="showResult" class="draw-result">
      <h3>{{ drawResult?.isWin ? '恭喜中奖!' : '未中奖' }}</h3>
      <p v-if="drawResult?.prizeName">奖品: {{ drawResult.prizeName }}</p>
      <button @click="hideDrawResult">关闭</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="drawing" class="loading">
      抽奖中...
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useLottery } from '@/composables/useLottery'

// 使用 composable
const {
  // 状态
  userStatus,
  gameSession,
  drawing,
  drawResult,
  showResult,
  
  // 计算属性
  canDraw,
  isActivityActive,
  
  // 方法
  initLotteryData,
  startGameSession,
  clickRedPacket,
  endGameSession,
  performDraw,
  hideDrawResult
} = useLottery()

// 初始化数据
onMounted(async () => {
  try {
    await initLotteryData()
  } catch (error) {
    console.error('初始化失败:', error)
  }
})

// 开始游戏
const handleStartGame = () => {
  startGameSession()
}

// 点击红包
const handleClickRedPacket = () => {
  clickRedPacket()
}

// 结束游戏并抽奖
const handleEndGame = async () => {
  try {
    const sessionResult = endGameSession()
    await performDraw(sessionResult.clickedCount)
  } catch (error) {
    console.error('抽奖失败:', error)
    alert(error instanceof Error ? error.message : 'Unknown error')
  }
}
</script>

<style scoped>
.lottery-example {
  padding: 20px;
}

.user-status, .game-controls, .draw-result {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.game-controls button {
  margin-right: 10px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #409eff;
  color: white;
  cursor: pointer;
}

.game-controls button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.draw-result {
  background: #f0f9ff;
  border-color: #409eff;
}

.loading {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #409eff;
}
</style>