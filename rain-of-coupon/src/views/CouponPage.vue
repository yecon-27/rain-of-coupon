<template>
  <div class="coupon-page">
    <!-- 头部导航 -->
    <div class="coupon-header">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h2>我的券包</h2>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 券包内容 -->
    <div v-else class="coupon-container">
      <!-- 用户信息 -->
      <div class="user-info">
        <h3>{{ authStore.currentUser?.nickname || '用户' }}，欢迎查看您的券包！</h3>
      </div>

      <!-- 优惠券列表 -->
      <div class="coupon-list">
        <!-- 未中奖状态 -->
        <div v-if="!hasWon" class="no-coupon">
          <img 
            :src="getCouponImageUrl('cytzhq.png')" 
            alt="参与挑战获取" 
            class="coupon-image"
            @error="handleImageError"
            @load="handleImageLoad"
          />
          <p class="coupon-hint">参与红包雨活动获取优惠券</p>
          <button @click="goToActivity" class="activity-btn">立即参与</button>
        </div>

        <!-- 中奖状态 - 显示获得的优惠券 -->
        <div v-else class="coupon-grid">
          <div 
            v-for="coupon in userCoupons" 
            :key="coupon.id" 
            class="coupon-item"
            :class="{ 'used': coupon.isUsed, 'expired': coupon.isExpired }"
          >
            <img 
              :src="getCouponImageUrl(coupon.image)" 
              :alt="coupon.name" 
              class="coupon-image"
              @error="handleImageError"
              @load="handleImageLoad"
            />
            <div class="coupon-info">
              <h4>{{ coupon.name }}</h4>
              <p class="coupon-desc">{{ coupon.description }}</p>
              <p class="coupon-expire">有效期至：{{ formatDate(coupon.expireDate) }}</p>
              <div class="coupon-status">
                <span v-if="coupon.isUsed" class="status-used">已使用</span>
                <span v-else-if="coupon.isExpired" class="status-expired">已过期</span>
                <span v-else class="status-available">可使用</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用说明 -->
      <div class="usage-tips">
        <h4>使用说明：</h4>
        <ul>
          <li>优惠券仅限在指定餐厅使用</li>
          <li>每张优惠券限用一次</li>
          <li>不可与其他优惠同时使用</li>
          <li>请在有效期内使用</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { API_CONFIG } from '@/config/api'

const router = useRouter()
const authStore = useAuthStore()

// 状态管理
const loading = ref(true)
const hasWon = ref(false)
const userCoupons = ref<Array<{
  id: number
  name: string
  description: string
  image: string
  expireDate: string
  isUsed: boolean
  isExpired: boolean
}>>([])

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

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 检查用户登录状态和中奖状态
const checkUserStatus = async () => {
  try {
    // 检查登录状态
    if (!authStore.isLoggedIn) {
      router.push('/login?redirect=/coupon')
      return
    }

    // 模拟API调用检查中奖状态
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟中奖状态
    const mockWinStatus = Math.random() > 0.3 // 70%概率中奖，便于测试
    hasWon.value = mockWinStatus
    
    if (hasWon.value) {
      // 模拟数据库返回的优惠券数据（包含完整路径）
      userCoupons.value = [
        {
          id: 1,
          name: '满500元优惠券',
          description: '满500元且消费一道特色菜可使用',
          image: '/image/coupon/满500元且消费一道特色菜可使用.png', // 数据库存储的路径
          expireDate: '2024-12-31',
          isUsed: false,
          isExpired: false
        },
        {
          id: 2,
          name: '满1500元优惠券',
          description: '满1500元且消费一道特色菜可使用',
          image: '/image/coupon/满1500元且消费一道特色菜可使用.png', // 数据库存储的路径
          expireDate: '2024-12-31',
          isUsed: false,
          isExpired: false
        },
        {
          id: 3,
          name: '满2500元优惠券',
          description: '满2500元且消费一道特色菜可使用',
          image: '/image/coupon/满2500元且消费一道特色菜可使用.png', // 数据库存储的路径
          expireDate: '2024-12-31',
          isUsed: true,
          isExpired: false
        }
      ]
    }
    
  } catch (error: unknown) {
    console.error('检查用户状态失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}

// 跳转到活动页面
const goToActivity = () => {
  router.push('/')
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error('图片加载失败:', img.src)
  // 可以设置一个默认图片
  // img.src = '/image/default-coupon.png'
}

// 图片加载成功处理
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('图片加载成功:', img.src)
}

// 页面初始化
onMounted(() => {
  checkUserStatus()
})
</script>

<style scoped>
.coupon-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #DC143C, #FF6B6B);
}

.coupon-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: rgba(0, 0, 0, 0.1);
  color: white;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.coupon-header h2 {
  margin: 0 0 0 20px;
  font-size: 18px;
  font-weight: bold;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 60px);
  color: white;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.coupon-container {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.user-info {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  text-align: center;
}

.user-info h3 {
  color: white;
  margin: 0;
  font-size: 18px;
}

.coupon-list {
  margin-bottom: 30px;
}

.no-coupon {
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  padding: 40px 20px;
  color: #333;
}

.coupon-image {
  max-width: 100%;
  height: auto;
  border-radius: 10px;
  margin-bottom: 20px;
}

.coupon-hint {
  font-size: 16px;
  margin-bottom: 20px;
  color: #666;
}

.activity-btn {
  background: #DC143C;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.activity-btn:hover {
  background: #B91C3C;
  transform: translateY(-2px);
}

.coupon-grid {
  display: grid;
  gap: 15px;
}

.coupon-item {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 15px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.coupon-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.coupon-item.used {
  opacity: 0.6;
  background: rgba(200, 200, 200, 0.9);
}

.coupon-item.expired {
  opacity: 0.5;
  background: rgba(150, 150, 150, 0.9);
}

.coupon-item .coupon-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
  margin-bottom: 0;
}

.coupon-info {
  flex: 1;
}

.coupon-info h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
}

.coupon-desc {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.coupon-expire {
  margin: 0 0 8px 0;
  color: #999;
  font-size: 12px;
}

.coupon-status {
  display: flex;
  align-items: center;
}

.status-available {
  background: #4CAF50;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-used {
  background: #9E9E9E;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-expired {
  background: #F44336;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.usage-tips {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  padding: 20px;
  color: white;
}

.usage-tips h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.usage-tips ul {
  margin: 0;
  padding-left: 20px;
}

.usage-tips li {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coupon-container {
    padding: 15px;
  }
  
  .coupon-item {
    flex-direction: column;
    text-align: center;
  }
  
  .coupon-item .coupon-image {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style>