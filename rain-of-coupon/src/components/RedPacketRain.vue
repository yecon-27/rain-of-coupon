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
        <span class="count-text">x{{ packetCount }}</span>
      </div>
    </div>
    <div class="rain-container" ref="rainContainer"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { API_CONFIG } from '@/config/api';

const emit = defineEmits<{
  (event: 'game-finished', payload: { isWin: boolean; prize?: { amount: number } }): void;
}>();

const rainContainer = ref<HTMLDivElement | null>(null);
const remainingTime = ref(40);
const packetCount = ref(99);
const totalPackets = 99;
let timerInterval: number | null = null;
let rainInterval: number | null = null;
let activePackets = 0;
const maxActivePackets = 99;
const columnPositions = ['15%', '50%', '85%'];
let currentColumn = 0;

const calculateRainInterval = () => {
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
    void packet.offsetHeight; // 强制重绘

    // 调试：打印红包尺寸
    setTimeout(() => {
      const computedStyle = window.getComputedStyle(packet);
      console.log('Red Packet Size:', {
        width: computedStyle.width,
        height: computedStyle.height,
        viewportWidth: window.innerWidth,
      });
    }, 100);

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
  timerInterval = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--;
    } else {
      clearInterval(timerInterval as number);
      if (rainInterval) clearInterval(rainInterval);
      endGame(false);
    }
  }, 1000);
}

function handleClick(event: MouseEvent, packet: HTMLElement) {
  packet.remove();
  simulateLottery();
}

function simulateLottery() {
  const won = Math.random() > 0.5;
  if (won) {
    const prize = { amount: Math.floor(Math.random() * 1000) + 100 };
    endGame(true, prize);
  } else {
    endGame(false);
  }
}

function endGame(isWin: boolean, prize?: { amount: number }) {
  if (timerInterval) clearInterval(timerInterval);
  if (rainInterval) clearInterval(rainInterval);
  emit('game-finished', { isWin, prize });
}

onMounted(() => {
  const img = new Image();
  img.src = getImageUrl('luckyBag.png');
  img.onload = () => {
    startTimer();
    startRain();
  };
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

.rain-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 5;
  touch-action: manipulation;
}

.red-packet {
  position: absolute;
  width: clamp(80px, 15vw, 120px) !important; /* 强制覆盖内在尺寸 */
  height: auto !important; /* 按比例调整高度 */
  max-width: 120px !important;
  max-height: 120px !important;
  object-fit: contain !important; /* 确保图片按比例填充 */
  animation: fall linear forwards;
  cursor: pointer;
  transition: transform 0.1s ease;
  padding: 3px;
  margin: -3px;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
  border: 1px solid red; /* 调试用边框 */
}

.red-packet:hover,
.red-packet:active {
  transform: scale(1.2);
}

@keyframes fall {
  0% {
    transform: translateY(-100px) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(100vh) rotate(360deg);
    opacity: 0;
  }
}

@media (max-width: 768px) {
  .red-packet {
    width: clamp(60px, 12vw, 90px) !important;
    height: auto !important;
    max-width: 90px !important;
    max-height: 90px !important;
  }
}

@media (max-width: 480px) {
  .red-packet {
    width: clamp(50px, 10vw, 70px) !important;
    height: auto !important;
    max-width: 70px !important;
    max-height: 70px !important;
  }
}
</style>