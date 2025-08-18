<template>
  <div class="red-packet-page">
    <HomePage />
    <div class="overlay"></div>
    <div class="timer" :style="{ backgroundImage: `url(${getImageUrl('ds.png')})` }">
      <span class="timer-text">倒计时</span>
      <span class="timer-seconds">{{ remainingTime }}秒</span>
    </div>
    <div class="packet-count" :style="{ backgroundImage: `url(${getImageUrl('sl.png')})` }">
      <span class="count-text">x{{ packetCount }}</span>
    </div>
    <div class="rain-container" ref="rainContainer"></div>
    <PrizeModal v-if="showPrize" :prize="prize" @close="closeModal" />
    <EncourageTip v-if="showEncourage" @close="closeModal" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import PrizeModal from '@/components/PrizeModal.vue';
import EncourageTip from '@/components/EncourageTip.vue';
import { API_CONFIG } from '@/config/api'; // 引入API配置

// 声明一个获取图片的公共函数
const getImageUrl = (filename: string): string => {
  // 假设你的API_CONFIG.imageURL已经包含了完整的路径，例如 'https://your-cdn.com/assets/image/coupon/'
  return `${API_CONFIG.imageURL}${filename}`;
};

const router = useRouter();

const rainContainer = ref<HTMLDivElement | null>(null);
const showPrize = ref(false);
const showEncourage = ref(false);

interface Prize {
  amount: number;
}
const prize = ref<Prize | null>(null);

const remainingTime = ref(40);
const packetCount = ref(99);
let timerInterval: number | null = null;
let rainInterval: number | null = null;

// 开始倒计时
function startTimer() {
  timerInterval = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--;
    } else {
      clearInterval(timerInterval as number);
      if (rainInterval) {
        clearInterval(rainInterval);
      }
      router.push('/');
    }
  }, 1000);
}

// 生成红包雨
function startRain() {
  rainInterval = setInterval(() => {
    if (packetCount.value <= 0) {
      if (rainInterval) {
        clearInterval(rainInterval);
      }
      return;
    }
    
    if (!rainContainer.value) {
      return;
    }

    const packet = document.createElement('img');
    // 使用 getImageUrl 函数来设置图片路径
    packet.src = getImageUrl('luckyBag.png');
    packet.className = 'red-packet';
    packet.style.left = `${Math.random() * 90 + 5}%`;
    packet.style.animationDuration = `${Math.random() * 3 + 4}s`;
    
    packet.addEventListener('click', (event: MouseEvent) => {
      packetCount.value = Math.max(0, packetCount.value - 1);
      handleClick(event, packet);
    });

    packet.addEventListener('animationend', () => {
      packet.remove();
    });

    rainContainer.value.appendChild(packet);
  }, 500);
}

// 处理点击红包
function handleClick(event: MouseEvent, packet: HTMLElement) {
  packet.remove();
  simulateLottery();
}

// 模拟抽奖
function simulateLottery() {
  const won = Math.random() > 0.5;
  if (won) {
    prize.value = { amount: Math.floor(Math.random() * 1000) + 100 };
    showPrize.value = true;
  } else {
    showEncourage.value = true;
  }
}

function closeModal() {
  showPrize.value = false;
  showEncourage.value = false;
  router.push('/');
}

onMounted(() => {
  startTimer();
  startRain();
});

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
  }
  if (rainInterval) {
    clearInterval(rainInterval);
  }
});
</script>

<style scoped>
/* 样式部分保持不变 */
.red-packet-page {
  position: relative;
  height: 100vh;
  overflow: hidden;
}
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}
.timer,
.packet-count {
  position: absolute;
  top: 10px;
  z-index: 2;
  background-size: contain;
  background-repeat: no-repeat;
  width: 80px;
  height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.timer {
  left: 10px;
}
.packet-count {
  right: 10px;
}
.timer-text,
.timer-seconds,
.count-text {
  text-align: center;
  color: #fff;
  font-weight: bold;
}

.rain-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.red-packet {
  position: absolute;
  width: 50px;
  animation: fall linear infinite;
  cursor: pointer;
}
@keyframes fall {
  0% { transform: translateY(-50px) rotate(0deg); }
  100% { transform: translateY(100vh) rotate(360deg); }
}
</style>