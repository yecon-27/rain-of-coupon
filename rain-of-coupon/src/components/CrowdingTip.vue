<template>
  <div v-if="visible" class="crowding-overlay">
    <!-- BackButton组件 -->
    <BackButton />

    <img :src="getCrowdingImageUrl()" alt="活动拥挤" class="crowding-image" @error="handleImageError"
      @load="handleImageLoad" @click="handleImageClick" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { API_CONFIG } from '@/config/api'
import BackButton from '@/components/BackButton.vue'

// Props
interface Props {
  visible?: boolean
}

defineProps<Props>()

const router = useRouter()

// 获取活动拥挤图片URL
const getCrowdingImageUrl = () => {
  const filename = '活动拥挤.png'
  let imageUrl = ''

  // 如果数据库存储的是完整路径（以/开头）
  if (filename.startsWith('/')) {
    // 转换为完整URL
    const isDev = import.meta.env.DEV
    const baseUrl = isDev ? `http://${window.location.hostname}:8080` : 'https://your-production-domain.com'
    imageUrl = `${baseUrl}${filename}`
  } else {
    // 如果只是文件名，使用配置的路径
    imageUrl = `${API_CONFIG.couponImageURL}${filename}`
  }

  console.log('活动拥挤图片URL:', filename, '->', imageUrl)
  return imageUrl
}

// 点击图片处理
const handleImageClick = () => {
  // 点击图片后返回LoadingPage重新加载
  router.push('/loading')
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('活动拥挤图片加载失败:', img.src)
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('活动拥挤图片加载成功:', img.src)
}
</script>

<style scoped>
.crowding-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  cursor: pointer;
}

.crowding-image {
  max-width: 90vw;
  max-height: 90vh;
  width: auto;
  height: auto;
  object-fit: contain;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .crowding-image {
    max-width: 95vw;
    max-height: 85vh;
  }
}

@media (max-width: 480px) {
  .crowding-image {
    max-width: 98vw;
    max-height: 80vh;
  }
}
</style>
