<template>
  <div class="encourage-tip">
    <div class="modal-content">
      <img 
        :src="getEncourageImageUrl()" 
        alt="福气+1按钮" 
        class="encourage-image" 
        @click="goToHome"
        @error="handleImageError"
        @load="handleImageLoad"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 获取鼓励图片URL
const getEncourageImageUrl = (): string => {
  // 使用福气+1按钮图片
  const imagePath = '/image/coupon/福气+1.png'
  
  // 构造完整URL
  const isDev = import.meta.env.DEV
  const baseUrl = isDev ? `http://${window.location.hostname}:8080` : 'https://your-production-domain.com'
  const imageUrl = `${baseUrl}${imagePath}`
  
  console.log('🍀 [EncourageTip] 鼓励图片URL:', imagePath, '->', imageUrl)
  return imageUrl
}

// 点击图片返回主页
const goToHome = () => {
  console.log('🍀 [EncourageTip] 点击图片，返回主页')
  router.push('/')
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('🍀 [EncourageTip] 图片加载失败:', img.src)
  // 可以设置一个默认图片或者显示文字提示
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('🍀 [EncourageTip] 图片加载成功:', img.src)
}
</script>

<style scoped>
.encourage-tip {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.modal-content {
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
  max-width: none;
  width: auto;
}

.encourage-image {
  max-width: 80%;
  max-height: 80vh;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.encourage-image:hover {
  transform: scale(1.05);
}

.encourage-image:active {
  transform: scale(0.95);
}
</style>
