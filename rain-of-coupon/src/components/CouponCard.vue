<template>
  <div class="coupon-card">
    <!-- 未中奖状态 - 只显示图片 -->
    <div v-if="!gameStore.hasPrize" class="no-coupon">
      <img :src="getCouponImageUrl('cytzhq.png')" alt="参与挑战获取" class="coupon-image" @error="handleImageError"
        @load="handleImageLoad" />
    </div>

    <!-- 中奖状态 - 显示获得的优惠券 -->
    <div v-else class="coupon-display">
      <div class="coupon-item">
        <div class="coupon-content">
          <img :src="getPrizeImageUrl()" :alt="gameStore.prizeRecord?.prizeName || '优惠券'" class="coupon-image" @error="handleImageError"
            @load="handleImageLoad" />
          <div class="coupon-expiry">
            使用期限：{{ formatExpireDate(getExpireDate()) }}前
          </div>
          <div class="coupon-status-btn">
            未使用
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useGameStore } from '@/stores/gameStore'
import { API_CONFIG } from '@/config/api'


const gameStore = useGameStore()

// 组件挂载时加载中奖记录
onMounted(async () => {
  console.log('🎫 [CouponCard] 组件挂载，加载中奖记录')
  await gameStore.loadPrizeRecord()
  console.log('🎫 [CouponCard] 中奖状态:', gameStore.hasPrize)
  console.log('🎫 [CouponCard] 中奖记录:', gameStore.prizeRecord)
})

// 根据中奖记录获取对应的图片URL
const getPrizeImageUrl = (): string => {
  if (!gameStore.prizeRecord) {
    return getCouponImageUrl('/image/coupon/满500元且消费一道特色菜可使用.png')
  }
  
  const amount = gameStore.prizeRecord.amount
  console.log('🎫 [CouponCard] 奖品金额:', amount)
  
  let imageUrl = ''
  if (amount >= 888) {
    imageUrl = '/image/coupon/满2500元且消费一道特色菜可使用.png'
    console.log('🎫 [CouponCard] 使用888元券图片')
  } else if (amount >= 588) {
    imageUrl = '/image/coupon/满1500元且消费一道特色菜可使用.png'
    console.log('🎫 [CouponCard] 使用588元券图片')
  } else {
    imageUrl = '/image/coupon/满500元且消费一道特色菜可使用.png'
    console.log('🎫 [CouponCard] 使用188元券图片')
  }
  
  return getCouponImageUrl(imageUrl)
}

// 获取过期日期
const getExpireDate = (): string => {
  // 默认30天后过期
  const expireDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
  return expireDate.toISOString().split('T')[0]
}

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

  console.log('🎫 [CouponCard] 优惠券图片URL:', filename, '->', imageUrl)
  return imageUrl
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('🎫 [CouponCard] 图片加载失败:', img.src)
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('🎫 [CouponCard] 图片加载成功:', img.src)
}

// 格式化有效期日期
const formatExpireDate = (dateString?: string) => {
  if (!dateString) return '永久有效'

  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    console.error('🎫 [CouponCard] 日期格式化失败:', error)
    return dateString
  }
}
</script>

<style scoped>
.coupon-card {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: 100%;
  min-height: 100%;
}

/* 未中奖状态 - 只显示图片，无padding */
.no-coupon {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: 100%;
}

.no-coupon .coupon-image {
  width: calc(100vw - 40px);
  max-height: 80vh;
  height: auto;
  object-fit: contain;
}

/* 中奖状态 - 显示优惠券，无padding */
.coupon-display {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  flex-wrap: wrap;
  width: 100%;
  gap: 0;
}

.coupon-item {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: 50%;
  box-sizing: border-box;
}

.coupon-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: relative;
}

.coupon-item .coupon-image {
  width: 100%;
  max-height: 60vh;
  height: auto;
  object-fit: contain;
}

.coupon-expiry {
  position: absolute;
  bottom: 30%;
  left: 50%;
  transform: translateX(-50%);
  color: rgb(178, 34, 34);
  font-size: 20px;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
  border-radius: 4px;
}

.coupon-status-btn {
  position: absolute;
  bottom: 8%;
  left: 50%;
  transform: translateX(-50%);
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  padding: 6px 16px;
  border-radius: 20px;
  background: #4CAF50;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  cursor: default;
  user-select: none;
}

.coupon-status-btn.used {
  background: #2196F3;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .no-coupon .coupon-image {
    width: calc(100vw - 30px);
    max-height: 75vh;
  }

  .coupon-item .coupon-image {
    width: 100%;
    max-height: 55vh;
  }

  .coupon-expiry {
    font-size: 13px;
    bottom: 30%;
    padding: 3px 6px;
    white-space: nowrap;
  }

  .coupon-status-btn {
    font-size: 12px;
    bottom: 8%;
    padding: 4px 12px;
  }
}
</style>