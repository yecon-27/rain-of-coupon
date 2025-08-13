<template>
  <div class="loading-animation">
    <img :src="getLoadingGifUrl()" alt="加载中..." class="loading-gif" @error="handleImageError" @load="handleImageLoad" />
  </div>
</template>

<script setup lang="ts">
import { API_CONFIG } from '@/config/api'

// 获取加载gif图片URL
const getLoadingGifUrl = () => {
  const filename = '加载.gif'
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

  console.log('加载动画URL:', filename, '->', imageUrl)
  return imageUrl
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('加载动画图片加载失败:', img.src)
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('加载动画图片加载成功:', img.src)
}
</script>

<style scoped>
.loading-animation {
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-gif {
  width: 500px; /* 从350px增加到500px */
  height: 500px; /* 从350px增加到500px */
  object-fit: contain;
  display: block;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-gif {
    width: 400px; /* 从280px增加到400px */
    height: 400px; /* 从280px增加到400px */
  }
}

@media (max-width: 480px) {
  .loading-gif {
    width: 320px; /* 从220px增加到320px */
    height: 320px; /* 从220px增加到320px */
  }
}
</style>