<template>
  <div class="red-packet-container">
    <div class="timer" :style="{ backgroundImage: `url(${getImageUrl('ds.png')})` }">
      <div class="text-container">
        <span class="timer-text">倒计时</span>
        <span class="timer-seconds">{{ remainingTime }}秒</span>
      </div>
    </div>
    <div class="packet-count" :style="{ backgroundImage: `url(${getImageUrl('sl.png')})` }">
      <div class="text-container">
        <span class="count-text">x{{ gameStore.clickedPacketCount }}</span>
      </div>
    </div>
    <div class="rain-container" ref="rainContainer"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { API_CONFIG } from '@/config/api';
import { useGameStore } from '@/stores/gameStore'; // 引入 game store

const gameStore = useGameStore(); // 初始化 store

const emit = defineEmits<{
  (event: 'game-finished', payload: { isWin: boolean; prize?: { amount: number } }): void;
}>();

const rainContainer = ref<HTMLDivElement | null>(null);
const remainingTime = ref(15);
const packetCount = ref(99);
// const clickedPacketCount = ref(0); // 不再需要本地的 ref
const PROB_OF_NOT_WINNING_PER_PACKET = 0.95;
let timerInterval: number | null = null;
const rainInterval: number | null = null;
let activePackets = 0;
const maxActivePackets = 99;
const columnPositions = ['15%', '60%', '85%'];
let currentColumn = 0;

const calculateRainInterval = () => {
  // 确保50秒内掉落99个红包
  return Math.max(100, (50 * 1000) / 99);
};

function startRain() {
  const generatePacket = () => {
    if (packetCount.value <= 0 || activePackets >= maxActivePackets) {
      if (packetCount.value > 0 && activePackets >= maxActivePackets) {
        setTimeout(generatePacket, 200);
      }
      return;
    }
    
    if (!rainContainer.value) return;

    const packet = document.createElement('img');
    packet.src = getImageUrl('luckyBag.png');
    packet.className = 'red-packet';
    
    // 调整红包尺寸为100x100px
    packet.width = 120;
    packet.height = 120;
    packet.style.width = '140px';
    packet.style.height = '140px';
    packet.style.maxWidth = '140px';
    packet.style.maxHeight = '140px';
    packet.style.objectFit = 'contain';

    // 调整红包位置（20%, 50%, 80%）
    packet.style.left = columnPositions[currentColumn];
    currentColumn = (currentColumn + 1) % columnPositions.length;
    packet.style.animationDuration = `${Math.random() * 2 + 3}s`;

    activePackets++;
    packetCount.value = Math.max(0, packetCount.value - 1);

    packet.addEventListener('click', (event: MouseEvent) => {
      activePackets--;
      handleClick(event, packet);
    });

    packet.addEventListener('animationend', () => {
      activePackets--;
      packet.remove();
    });

    rainContainer.value.appendChild(packet);

    void packet.offsetHeight;

    if (packetCount.value > 0) {
      const nextInterval = calculateRainInterval();
      setTimeout(generatePacket, nextInterval);
    }
  };

  generatePacket();
}

const getImageUrl = (filename: string): string => {
  return `${API_CONFIG.imageURL}${filename}`;
};

function startTimer() {
  gameStore.resetClickedPacketCount();

  timerInterval = setInterval(async () => {
    if (remainingTime.value > 0) {
      remainingTime.value--;
    } else {
      clearInterval(timerInterval as number);
      if (rainInterval) clearInterval(rainInterval);
      
      // 游戏结束时，根据点击数动态计算中奖概率
      const finalProbOfNotWinning = Math.pow(PROB_OF_NOT_WINNING_PER_PACKET, gameStore.clickedPacketCount);
      const isWin = Math.random() > finalProbOfNotWinning;

      if (isWin) {
        try {
          // 异步设置中奖记录并调用API
          await gameStore.setPrizeRecord(gameStore.clickedPacketCount);
        } catch (error) {
          console.error('设置中奖记录失败:', error);
        }
      }
      
      endGame(isWin);
    }
  }, 1000);
}

function handleClick(event: MouseEvent, packet: HTMLElement) {
  // 阻止事件冒泡，防止意外触发
  event.stopPropagation();
  event.preventDefault();

  // 调用 store 中的 action 来增加点击计数
  gameStore.incrementClickedPacketCount();

  // 移除被点击的红包
  packet.remove();
}

function endGame(isWin: boolean, prize?: { amount: number }) {
  if (timerInterval) clearInterval(timerInterval);
  if (rainInterval) clearInterval(rainInterval);
  emit('game-finished', { isWin, prize });
}

onMounted(() => {
  startTimer();
  startRain();
});

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval);
  if (rainInterval) clearInterval(rainInterval);
});
</script>

<style scoped>
.red-packet-container {
  position: relative;
  width: 100%;
  height: 100%;
  touch-action: manipulation;
  user-select: none;
  -webkit-user-select: none;
}

.timer,
.packet-count {
  position: absolute;
  top: 0px;
  z-index: 10;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  width: min(150px, 30vw);
  height: min(150px, 30vw);
  display: flex;
  justify-content: center;
  align-items: center;
}

.timer {
  left: 0px;
  justify-content: flex-end;
  padding-right: min(16px, 4vw);
  padding-top: min(12px, 3vw);
}

.packet-count {
  right: 0px;
  justify-content: flex-end;
  padding-right: min(25px, 5vw);
}

.text-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.timer-text,
.timer-seconds,
.count-text {
  color: #fff;
  font-weight: bold;
  line-height: 1.2;
  margin: 1px 0;
  user-select: none;
  -webkit-user-select: none;
}

.timer-text {
  font-size: clamp(12px, 4vw, 18px);
}

.timer-seconds {
  font-size: clamp(14px, 4.5vw, 20px);
}

.count-text {
  font-size: clamp(18px, 5vw, 26px);
  color: #fff;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

/* 红包雨容器 */
.rain-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 5;
  touch-action: manipulation;
}

/* 红包样式（缩小范围） */
.red-packet {
  position: absolute;
  animation: fall linear infinite;
  cursor: pointer;
  transition: transform 0.1s ease;
  padding: 3px;
  margin: -3px;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.red-packet:hover,
.red-packet:active {
  transform: scale(1.2);
}

@keyframes fall {
  0% { transform: translateY(-100px) rotate(0deg); }
  100% { transform: translateY(100vh) rotate(360deg); }
}
</style>