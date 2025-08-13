<template>
  <div class="coupon-card">
    <!-- 未中奖状态 - 只显示图片 -->
    <div v-if="!hasRewards" class="no-coupon">
      <img 
        :src="getCouponImageUrl('cytzhq.png')" 
        alt="参与挑战获取" 
        class="coupon-image" 
        @error="handleImageError" 
        @load="handleImageLoad" 
      />
    </div>

    <!-- 中奖状态 - 显示获得的优惠券 -->
    <div v-else class="coupon-display">
      <div v-for="reward in rewards" :key="reward.id" class="coupon-item">
        <img 
          :src="getCouponImageUrl(reward.image)" 
          :alt="reward.name" 
          class="coupon-image" 
          @error="handleImageError" 
          @load="handleImageLoad" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Reward } from '@/stores/game'
import { API_CONFIG } from '@/config/api'

// Props
interface Props {
  rewards: Reward[]
}

const props = defineProps<Props>()

// 计算属性：是否有奖励
const hasRewards = computed(() => {
  return props.rewards && props.rewards.length > 0
})

// 获取优惠券图片URL
const getCouponImageUrl = (filename: string) => {
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

  console.log('优惠券图片URL:', filename, '->', imageUrl)
  return imageUrl
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('图片加载失败:', img.src)
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('图片加载成功:', img.src)
}
</script>

<style scoped>
.coupon-card {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

/* 未中奖状态 - 只显示图片，无padding */
.no-coupon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.no-coupon .coupon-image {
  max-width: 90vw;
  max-height: 80vh;
  height: auto;
  object-fit: contain;
}

/* 中奖状态 - 显示优惠券，无padding */
.coupon-display {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  width: 100%;
  height: 100%;
}

.coupon-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.coupon-item .coupon-image {
  max-width: 90vw;
  max-height: 80vh;
  height: auto;
  object-fit: contain;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .no-coupon .coupon-image,
  .coupon-item .coupon-image {
    max-width: 95vw;
    max-height: 75vh;
  }
}
</style>