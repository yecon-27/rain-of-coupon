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
import { useGameStore } from '@/stores/gameStore';
import { drawLottery } from '@/api/lottery';

const gameStore = useGameStore();

const emit = defineEmits<{
  (event: 'game-finished', payload: { isWin: boolean; prize?: { amount: number } }): void;
}>();

const rainContainer = ref<HTMLDivElement | null>(null);
const remainingTime = ref(30);
const packetCount = ref(500);
let timerInterval: number | null = null;
const rainInterval: number | null = null;
let activePackets = 0;
const maxActivePackets = 300;

const calculateRainInterval = () => {
  return Math.max(50, (30 * 1000) / 99);
};

// 生成随机飘落参数（确保在视图内）
function getRandomFallParams() {
  // 获取视口宽度
  const viewportWidth = window.innerWidth;
  // 红包宽度（固定为120px）
  const packetWidth = 120;
  // 计算红包可飘落的范围（确保红包完全在视口内）
  const minLeft = (packetWidth / 2) / viewportWidth * 100;
  const maxLeft = 100 - minLeft;
  
  // 动态调整水平位置，确保红包完全在视口内，并整体向左偏移10%
  const left = Math.max(minLeft, Math.min(maxLeft, Math.random() * (maxLeft - minLeft) + minLeft - 20));
  
  // 计算最大允许的水平偏移值
  const maxOffset = Math.min(
    viewportWidth * 0.1, // 最大偏移为视口宽度的10%
    (100 - left) * viewportWidth / 100 - packetWidth / 2, // 右侧剩余空间
    left * viewportWidth / 100 - packetWidth / 2 // 左侧剩余空间
  );
  
  return {
    left: left,
    duration: Math.random() * 1 + 2, // 缩短下落时间，加快速度
    horizontalOffset: (Math.random() - 0.5) * maxOffset * 0.8
  };
}

// 创建流星尾迹效果
function createMeteorTrail(packet: HTMLElement) {
  const trail = document.createElement('div');
  trail.className = 'meteor-trail';
  
  // 设置尾迹样式（更亮、更大、更明显）
  trail.style.position = 'absolute';
  trail.style.width = '6px';
  trail.style.height = '100px';
  trail.style.background = 'linear-gradient(to bottom, rgba(255, 215, 0, 1), rgba(255, 215, 0, 0.8), rgba(255, 215, 0, 0.5), transparent)';
  trail.style.borderRadius = '50%';
  trail.style.pointerEvents = 'none';
  trail.style.zIndex = '1';
  trail.style.filter = 'blur(2px)';
  
  // 位置跟随红包
  trail.style.left = packet.style.left;
  trail.style.top = packet.style.top;
  trail.style.transform = 'translateX(-50%) translateY(-100px)';
  
  return trail;
}

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
    
    // 获取随机参数
    const params = getRandomFallParams();
    
    // 固定红包大小为100px
    const fixedSize = 130;
    packet.style.width = `${fixedSize}px`;
    packet.style.height = `${fixedSize}px`;
    packet.style.maxWidth = `${fixedSize}px`;
    packet.style.maxHeight = `${fixedSize}px`;
    packet.style.objectFit = 'contain';
    packet.style.position = 'absolute';
    packet.style.cursor = 'pointer';
    packet.style.transition = 'transform 0.1s ease';
    packet.style.webkitTapHighlightColor = 'transparent';
    packet.style.touchAction = 'manipulation';
    packet.style.zIndex = '2';
    
    // 设置初始位置
    packet.style.left = `${params.left}%`;
    packet.style.top = '-150px';
    
    // 创建流星尾迹
    const trail = createMeteorTrail(packet, params);
    
    // 创建简化的垂直飘落动画（限制在视图内）
    const keyframes = [
      {
        transform: `translateY(-150px) translateX(0px)`,
        opacity: '0'
      },
      {
        transform: `translateY(50px) translateX(${Math.max(-50, Math.min(50, params.horizontalOffset * 0.2))}px)`,
        opacity: '1',
        offset: 0.1
      },
      {
        transform: `translateY(calc(100vh + 150px)) translateX(${Math.max(-50, Math.min(50, params.horizontalOffset))}px)`,
        opacity: '0.8'
      }
    ];
    
    // 尾迹动画
    const trailKeyframes = [
      {
        transform: `translateX(-50%) translateY(-210px)`,
        opacity: '0'
      },
      {
        transform: `translateX(-50%) translateY(-10px)`,
        opacity: '0.8',
        offset: 0.1
      },
      {
        transform: `translateX(-50%) translateY(calc(100vh + 90px))`,
        opacity: '0'
      }
    ];
    
    const animation = packet.animate(keyframes, {
      duration: params.duration * 1000,
      easing: 'linear',
      fill: 'forwards'
    });
    
    const trailAnimation = trail.animate(trailKeyframes, {
      duration: params.duration * 1000,
      easing: 'linear',
      fill: 'forwards'
    });

    activePackets++;
    packetCount.value = Math.max(0, packetCount.value - 1);

    // 点击事件
    packet.addEventListener('click', (event: MouseEvent) => {
      activePackets--;
      animation.cancel();
      trailAnimation.cancel();
      handleClick(event, packet, trail);
    });

    // 动画结束事件
    animation.addEventListener('finish', () => {
      activePackets--;
      if (packet.parentNode) {
        packet.remove();
      }
      if (trail.parentNode) {
        trail.remove();
      }
    });

    // 悬停效果
    packet.addEventListener('mouseenter', () => {
      packet.style.transform = 'scale(1.1)';
    });
    
    packet.addEventListener('mouseleave', () => {
      packet.style.transform = 'scale(1)';
    });

    rainContainer.value.appendChild(trail); // 先添加尾迹
    rainContainer.value.appendChild(packet); // 再添加红包

    if (packetCount.value > 0) {
      const nextInterval = calculateRainInterval() + Math.random() * 300;
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
      
      endGame();
    }
  }, 1000);
}

function handleClick(event: MouseEvent, packet: HTMLElement, trail?: HTMLElement) {
  event.stopPropagation();
  event.preventDefault();

  gameStore.incrementClickedPacketCount();
  
  // 添加点击效果
  packet.style.transform = 'scale(1.3)';
  packet.style.opacity = '0';
  
  if (trail) {
    trail.style.opacity = '0';
  }
  
  setTimeout(() => {
    packet.remove();
    if (trail && trail.parentNode) {
      trail.remove();
    }
  }, 100);
}

async function endGame() {
  console.log('🎮 [RedPacketRain] 游戏结束，开始处理结果');
  console.log('🎮 [RedPacketRain] 点击红包数量:', gameStore.clickedPacketCount);
  
  try {
    console.log('🌐 [RedPacketRain] 准备调用drawLottery API');
    console.log('🌐 [RedPacketRain] 请求参数:', {
      clickedCount: gameStore.clickedPacketCount
    });
    
    const startTime = Date.now();
    
    const result = await drawLottery({
      clickedCount: gameStore.clickedPacketCount
    });
    
    const endTime = Date.now();
    console.log(`🌐 [RedPacketRain] API调用完成，耗时: ${endTime - startTime}ms`);
    console.log('🌐 [RedPacketRain] API响应:', result);
    console.log('🌐 [RedPacketRain] 响应状态码:', result?.code);
    console.log('🌐 [RedPacketRain] 响应消息:', result?.msg);
    console.log('🌐 [RedPacketRain] 响应数据:', result?.data);

    const isWin = result?.data?.isWin === true;
    console.log('🎯 [RedPacketRain] 是否中奖:', isWin);

    if (isWin) {
      console.log('🏆 [RedPacketRain] 用户中奖，设置奖品记录');
      console.log('🏆 [RedPacketRain] 奖品信息:', {
        isWin: result.data.isWin,
        prizeName: result.data.prizeName,
        id: result.data.id
      });
      
      await gameStore.setPrizeRecord(gameStore.clickedPacketCount, {
        isWin: result.data.isWin,
        prizeName: result.data.prizeName || undefined,
        id: result.data.id
      });
    } else {
      console.log('😔 [RedPacketRain] 用户未中奖');
    }

    console.log('✅ [RedPacketRain] 游戏结果处理完成，发送事件给父组件');
    emit('game-finished', {
      isWin: !!isWin,
      prize: isWin ? { amount: gameStore.prizeRecord?.amount || 0 } : undefined
    });
  } catch (error) {
    console.error('❌ [RedPacketRain] 游戏结束处理失败:', error);
    console.error('❌ [RedPacketRain] 错误详情:', {
      name: (error as Error).name,
      message:(error as Error).message,
      stack: (error as Error).stack
    });
    
    if ((error as Error).message.includes('fetch')) {
      console.error('🌐 [RedPacketRain] 网络请求失败，请检查后端服务');
    }
    
    emit('game-finished', { isWin: false });
  }
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
  overflow: hidden; /* 确保内容不会溢出 */
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
  overflow: hidden; /* 防止红包超出容器 */
}

/* 红包样式 */
.red-packet {
  pointer-events: auto;
  filter: drop-shadow(0 0 8px rgba(255, 215, 0, 0.3)); /* 添加金色光晕 */
}

/* 流星尾迹样式（更亮、更大） */
.meteor-trail {
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.8);
  filter: blur(2px);
}
</style>