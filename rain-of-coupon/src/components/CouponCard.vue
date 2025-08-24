<template>
  <div class="coupon-card">
    <!-- 未中奖状态 - 只显示图片 -->
    <div v-if="!gameStore.hasPrize" class="no-coupon">
      <DynamicImage 
        resource-key="participate_coupon" 
        fallback-url="/src/assets/coupon/cytzhq.png"
        alt="参与挑战获取" 
        class-name="coupon-image" 
      />
    </div>

    <!-- 中奖状态 - 显示获得的优惠券 -->
    <div v-else class="coupon-display">
      <div class="coupon-item">
        <div class="coupon-content">
          <DynamicImage 
            :resource-key="getPrizeImageKey()" 
            :fallback-url="getPrizeFallbackUrl()"
            :alt="gameStore.prizeRecord?.prizeName || '优惠券'" 
            class-name="coupon-image" 
          />
          <div class="coupon-expiry">
            使用期限：{{ formatExpireDate(getExpireDate()) }}前
          </div>
          <div class="coupon-status-btn">
            可使用
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useGameStore } from '@/stores/gameStore'
import DynamicImage from './DynamicImage.vue'

const gameStore = useGameStore()

// 组件挂载时加载中奖记录
onMounted(async () => {
  console.log('🎫 [CouponCard] 组件挂载，加载中奖记录')
  await gameStore.loadPrizeRecord()
  console.log('🎫 [CouponCard] 中奖状态:', gameStore.hasPrize)
  console.log('🎫 [CouponCard] 中奖记录:', gameStore.prizeRecord)
})

// 获取奖品图片资源键
const getPrizeImageKey = (): string => {
  if (!gameStore.prizeRecord) {
    return 'coupon_188'
  }
  
  // 根据奖品金额选择对应图片
  const amount = gameStore.prizeRecord.amount
  
  if (amount >= 888) {
    console.log('🎫 [CouponCard] 使用888元券图片')
    return 'coupon_888'
  } else if (amount >= 588) {
    console.log('🎫 [CouponCard] 使用588元券图片')
    return 'coupon_588'
  } else {
    console.log('🎫 [CouponCard] 使用188元券图片')
    return 'coupon_188'
  }
}

// 获取奖品图片降级URL
const getPrizeFallbackUrl = (): string => {
  if (!gameStore.prizeRecord) {
    return '/src/assets/coupon/188.png'
  }
  
  // 根据奖品金额选择对应图片
  const amount = gameStore.prizeRecord.amount
  
  if (amount >= 888) {
    return '/src/assets/coupon/888.png'
  } else if (amount >= 588) {
    return '/src/assets/coupon/588.png'
  } else {
    return '/src/assets/coupon/188.png'
  }
}

// 获取过期日期
const getExpireDate = (): string => {
  // 默认30天后过期
  const expireDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
  return expireDate.toISOString().split('T')[0]
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
  width: calc(100vw - 40px);
  max-height: 80vh;
  gap: 0;
}

.coupon-item {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: calc(100vw - 40px);
  max-height: 80vh;
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
  color: #f35917;
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
  font-size: 30px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  padding: 6px 16px;
  border-radius: 20px;
  background: rgb(82, 175, 5);
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  cursor: default;
  user-select: none;
}

.coupon-status-btn.used {
  background: red;
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
    font-size: 30px;
    bottom: 25%;
    padding: 3px 6px;
    white-space: nowrap;
  }

  .coupon-status-btn {
    font-size: 20px;
    bottom: 8%;
    padding: 4px 12px;
  }
}
</style>