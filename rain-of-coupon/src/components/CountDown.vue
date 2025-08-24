<template>
  <div class="countdown-container" v-if="isVisible">
    <div class="countdown-overlay"></div>
    <div class="countdown-content">
      <!-- 准备阶段：显示 zbh.png -->
      <DynamicImage 
        v-if="currentPhase === 'prepare'"
        resource-key="prepare_image"
        fallback-url="/src/assets/coupon/zbh.png"
        alt="准备图片"
        class-name="prepare-image"
        :class="{ 'slide-in': isSlideIn, 'slide-out': isSlideOut }"
      />
      
      <!-- 倒计时阶段：显示 3、2、1 -->
      <DynamicImage 
        v-if="currentPhase === 'countdown'"
        :resource-key="`countdown_${currentNumber}`"
        :fallback-url="`/src/assets/coupon/${currentNumber}.png`"
        :alt="`倒计时数字${currentNumber}`"
        class-name="countdown-image"
        :class="{ 'animate': isAnimating }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import DynamicImage from './DynamicImage.vue'

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
    
    // 延长闪烁动画时间，让用户能看到完整的1秒效果
    setTimeout(() => {
      isAnimating.value = false
      
      // 修复倒计时逻辑：确保每个数字显示完整的1秒
      setTimeout(() => {
        if (currentNumber.value > 1) {
          currentNumber.value--
          timer = setTimeout(countdown, 0) // 立即开始下一个数字
        } else {
          // 倒计时结束（显示完1后结束）
          setTimeout(() => {
            isVisible.value = false
            emit('finished')
          }, 500)
        }
      }, 500) // 静止显示500ms
    }, 500) // 闪烁动画500ms
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
  width: 800px;  /* 与倒计时数字一样大 */
  height: 800px; /* 与倒计时数字一样大 */
  object-fit: contain;
  transform: translateX(100vw);
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
  width: 400px;
  height: 400px;
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
    width: 600px;  /* 与倒计时数字一样大 */
    height: 600px;
  }
  
  .countdown-image {
    width: 300px;
    height: 300px;
  }
}

@media (max-width: 480px) {
  .prepare-image {
    width: 480px;  /* 与倒计时数字一样大 */
    height: 480px;
  }
  
  .countdown-image {
    width: 240px;
    height: 240px;
  }
}
</style>