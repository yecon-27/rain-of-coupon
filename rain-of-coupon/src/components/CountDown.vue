<template>
  <div class="countdown-container" v-if="isVisible">
    <div class="countdown-overlay"></div>
    <div class="countdown-content">
      <!-- 准备阶段：显示 zbh.png -->
      <img 
        v-if="currentPhase === 'prepare'"
        :src="getImageUrl('zbh.png')"
        alt="准备图片"
        class="prepare-image"
        :class="{ 'slide-in': isSlideIn, 'slide-out': isSlideOut }"
        @error="handleImageError"
      />
      
      <!-- 倒计时阶段：显示 3、2、1 -->
      <img 
        v-if="currentPhase === 'countdown'"
        :src="getImageUrl(`${currentNumber}.png`)"
        :alt="`倒计时数字${currentNumber}`"
        class="countdown-image"
        :class="{ 'animate': isAnimating }"
        @error="handleImageError"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'

type Phase = 'prepare' | 'countdown'

const currentPhase = ref<Phase>('prepare')
const currentNumber = ref(3)
const isVisible = ref(false)
const isAnimating = ref(false)
const isSlideIn = ref(false)
const isSlideOut = ref(false)
let timer: number | null = null

const emit = defineEmits<{
  finished: []
}>()

// 获取图片URL - 修复后端图片路径
const getImageUrl = (filename: string) => {
  // 尝试多个可能的路径
  return `http://localhost:8080/image/coupon/${filename}`
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('图片加载失败:', img.src)
  // 可以设置一个默认图片或显示错误信息
}

const startCountdown = () => {
  isVisible.value = true
  currentPhase.value = 'prepare'
  
  // 第一阶段：zbh.png 从右滑入
  setTimeout(() => {
    isSlideIn.value = true
  }, 100)
  
  // 停留2秒后向左滑走（修改为2秒）
  timer = setTimeout(() => {
    isSlideOut.value = true
    
    // 滑走动画完成后开始倒计时
    setTimeout(() => {
      startCountdownPhase()
    }, 500) // 等待滑走动画完成
  }, 2000) // 修改为2秒
}

const startCountdownPhase = () => {
  currentPhase.value = 'countdown'
  currentNumber.value = 3
  isSlideIn.value = false
  isSlideOut.value = false
  
  const countdown = () => {
    isAnimating.value = true
    
    setTimeout(() => {
      isAnimating.value = false
      
      // 修复倒计时逻辑：只在大于1时继续倒计时
      if (currentNumber.value > 1) {
        currentNumber.value--
        timer = setTimeout(countdown, 1000)
      } else {
        // 倒计时结束（显示完1后结束）
        setTimeout(() => {
          isVisible.value = false
          emit('finished')
        }, 1000)
      }
    }, 500)
  }
  
  countdown()
}

const stopCountdown = () => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  isVisible.value = false
  currentPhase.value = 'prepare'
  isSlideIn.value = false
  isSlideOut.value = false
}

// 暴露方法供父组件调用
defineExpose({
  startCountdown,
  stopCountdown
})

onUnmounted(() => {
  stopCountdown()
})
</script>

<style scoped>
.countdown-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999; /* 确保在最顶层 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.countdown-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8); /* 这个蒙版会覆盖HomePage */
}

.countdown-content {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 准备阶段图片样式 */
.prepare-image {
  width: 300px;
  height: 200px;
  object-fit: contain;
  transform: translateX(100vw); /* 初始位置在屏幕右侧外 */
  transition: transform 0.5s ease-in-out;
}

.prepare-image.slide-in {
  transform: translateX(0); /* 滑入到中心 */
}

.prepare-image.slide-out {
  transform: translateX(-100vw); /* 向左滑出 */
}

/* 倒计时图片样式 */
.countdown-image {
  width: 200px;
  height: 200px;
  object-fit: contain;
  transition: all 0.5s ease;
}

.countdown-image.animate {
  transform: scale(1.2);
  filter: drop-shadow(0 0 20px rgba(255, 255, 255, 0.8));
}

/* 响应式设计 */
@media (max-width: 768px) {
  .prepare-image {
    width: 250px;
    height: 150px;
  }
  
  .countdown-image {
    width: 150px;
    height: 150px;
  }
}

@media (max-width: 480px) {
  .prepare-image {
    width: 200px;
    height: 120px;
  }
  
  .countdown-image {
    width: 120px;
    height: 120px;
  }
}
</style>