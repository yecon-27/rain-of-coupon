<template>
  <div class="activity-section">
    <DynamicImage 
      resource-key="home_bg" 
      fallback-url="/src/assets/coupon/home.png"
      alt="首页背景" 
      class-name="activity-bg"
    />

    <div v-if="authStore.isLoggedIn" class="login-status">
      <span class="user-info">{{ authStore.currentUser?.nickname || '用户' }}</span>
      <button @click="authStore.logout" class="logout-btn">登出</button>
    </div>

    <DynamicImage 
      resource-key="rule_btn" 
      fallback-url="/src/assets/coupon/gz.png"
      alt="规则" 
      class-name="rule-btn" 
      @click="$emit('showRules')"
    />
    
    <DynamicImage 
      resource-key="coupon_btn" 
      fallback-url="/src/assets/coupon/qb.png"
      alt="券包" 
      class-name="coupon-btn" 
      @click="$emit('myCoupons')"
    />

    <div class="center-button">
      <DynamicImage 
        resource-key="challenge_btn" 
        fallback-url="/src/assets/coupon/button.png"
        alt="立即挑战" 
        class-name="challenge-btn" 
        @click="handleJoinActivity"
      />
    </div>
  </div>

  <PrizeStockTip 
    :visible="showPrizeStockTip" 
    :prizes="prizeStockData"
    @close="handlePrizeStockClose"
    @view-rules="handleViewRules"
  />
  <WarningTip
    :visible="showWarningTip"
    @close="handleWarningClose"
  />
  <CrowdingTip
    :visible="showCrowdingTip"
    @close="handleCrowdingClose"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useGameStore } from '@/stores/gameStore'
// 从你的 lottery.ts 文件中导入需要的 API
import { checkPrizeStock, getUserStatus, drawLottery } from '@/api/lottery'
import PrizeStockTip from './PrizeStockTip.vue'
import WarningTip from './WarningTip.vue'
import CrowdingTip from './CrowdingTip.vue'
import DynamicImage from './DynamicImage.vue'
import { imageManager } from '@/utils/imageManager'

// 定义事件
const emit = defineEmits<{
  showRules: []
  myCoupons: []
}>()

// 路由和store
const router = useRouter()
const authStore = useAuthStore()
const gameStore = useGameStore()

// 弹窗状态变量
const showPrizeStockTip = ref(false)
const showWarningTip = ref(false)
const showCrowdingTip = ref(false)
const prizeStockData = ref<Array<{ id: number; prizeName: string; totalCount: number; remainingCount: number }>>([])

// 预加载主页相关图片
const preloadImages = async () => {
  try {
    await imageManager.preloadByScene('主页')
    console.log('✅ [ActivitySection] 主页图片预加载完成')
  } catch (error) {
    console.error('❌ [ActivitySection] 图片预加载失败:', error)
  }
}

// 维护一个唯一的会话ID，用于后端判断同一个窗口的多次抽奖
const sessionId = ref(localStorage.getItem('sessionId') || Math.random().toString(36).substring(2, 15));

// 关闭弹窗的方法
const handlePrizeStockClose = () => {
  showPrizeStockTip.value = false
}

const handleWarningClose = () => {
  showWarningTip.value = false
}

const handleCrowdingClose = () => {
  showCrowdingTip.value = false
}

const handleViewRules = () => {
  showPrizeStockTip.value = false
  prizeStockData.value = []
  emit('showRules')
}

// 主要的点击处理函数
const handleJoinActivity = async () => {
  console.log('🚀 [ActivitySection] 用户点击立即挑战按钮');
  
  // 每次点击时，先重置所有弹窗状态
  showPrizeStockTip.value = false;
  showWarningTip.value = false;
  showCrowdingTip.value = false;

  // 优先级1: 检查是否已登录
  if (!authStore.isLoggedIn) {
    console.log('🚀 [ActivitySection] 用户未登录，跳转到登录页面');
    router.push('/login?redirect=/');
    return;
  }
  
  try {
    // 并行获取所有必要数据，减少等待时间
    console.log('🔍 [ActivitySection] 并行调用 getUserStatus 和 checkPrizeStock API...');
const [statusRes, stockRes] = await Promise.all([getUserStatus({ sessionId: sessionId.value }), checkPrizeStock()]);

    let userStatus = statusRes?.data;
    let stockResponse = stockRes?.data;

    console.log('🔍 [ActivitySection] getUserStatus 响应:', userStatus);
    console.log('🎁 [ActivitySection] checkPrizeStock 响应:', stockResponse);
    
    // 加载中奖记录，这是最高优先级
    await gameStore.loadPrizeRecord();
    console.log('🏆 [ActivitySection] 当前中奖状态:', gameStore.hasPrize);

    // --- 优先级判断链 ---

    // 优先级1: 已中奖
    if (gameStore.hasPrize) {
      console.log('🏆 [ActivitySection] 用户已中奖，跳转到中奖页面');
      router.push('/prize');
      return;
    }

    // 优先级2: 奖品库存不足
    if (stockResponse && stockResponse.hasStock === false) {
      console.log('🎁 [ActivitySection] 奖品已发放完毕，显示 PrizeStockTip');
      prizeStockData.value = stockResponse.prizes || [];
      showPrizeStockTip.value = true;
      return;
    }

    // 优先级3: 今日抽奖次数已用完或在同一会话中多次抽奖
    if (userStatus && userStatus.canDraw === false) {
      console.log('⚠️ [ActivitySection] 用户今日抽奖次数已用完，显示 WarningTip');
      showWarningTip.value = true;
      return;
    }
    
    // 优先级4: 服务器流量拥挤
    if (userStatus && userStatus.isCrowded === true) {
      console.log('🚦 [ActivitySection] 活动流量拥挤，显示 CrowdingTip');
      showCrowdingTip.value = true;
      return;
    }
    
    // 默认情况: 所有检查通过，直接执行抽奖逻辑
    console.log('✅ [ActivitySection] 所有检查通过，开始执行抽奖');
    const drawResponse = await drawLottery({ sessionId: sessionId.value });

    if (drawResponse.code === 200 && drawResponse.data?.isWin) {
      console.log('🏆 [ActivitySection] 抽奖成功并中奖，跳转到 PrizePage');
      await gameStore.loadPrizeRecord(); // 重新加载中奖记录
      router.push('/prize');
    } else {
      console.log('💔 [ActivitySection] 抽奖成功但未中奖，跳转到 LoadingPage');
      // 未中奖，跳转到 LoadingPage，这部分逻辑可以根据你的需求调整
      router.push('/loading');
    }

  } catch (error) {
    console.error('❌ [ActivitySection] API 调用或抽奖失败:', error);
    // 抽奖失败，可以显示拥挤提示或其他通用错误提示
    showWarningTip.value = true;
    return;
  }
}

// 组件挂载时检查登录状态，并保存 sessionId
onMounted(async () => {
  if (!localStorage.getItem('sessionId')) {
    localStorage.setItem('sessionId', sessionId.value);
  }
  authStore.checkAuthStatus();
  
  // 预加载图片
  await preloadImages();
});
</script>

<style scoped>
.activity-section {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.login-status {
  position: fixed;
  top: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px 15px;
  border-radius: 20px;
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  background: #DC143C;
  color: white;
  border: none;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #B91C3C;
  transform: translateY(-1px);
}

.activity-bg {
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  object-position: center top;
  display: block;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;
}

.rule-btn {
  position: fixed;
  top: calc(50vh - 40px);
  right: -2px;
  width: 96px;
  height: 96px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
}

.coupon-btn {
  position: fixed;
  top: calc(50vh + 15px);
  right: -2px;
  width: 110px;
  height: 110px;
  cursor: pointer;
  display: block;
  object-fit: contain;
  z-index: 5; /* 降低z-index，确保被蒙版覆盖 */
}

.center-button {
  position: absolute;
  bottom: 5%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.challenge-btn {
  width: 375px;
  /* 300px * 1.25 */
  height: 125px;
  /* 100px * 1.25 */
  cursor: pointer;
  display: block;
  animation: breathe 1.2s ease-in-out infinite;
}

@keyframes breathe {

  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

.challenge-btn:hover {
  animation-play-state: paused;
  transform: scale(1.08);
}

/* 响应式设计 */
@media (max-width: 1199px) and (min-width: 992px) {
  .challenge-btn {
    width: 337.5px;
    /* 270px * 1.25 */
    height: 112.5px;
    /* 90px * 1.25 */
  }
}

@media (max-width: 991px) and (min-width: 768px) {
  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }

  .rule-btn {
    width: 90px;
    /* 84px * 1.5 */
    height: 90px;
    top: calc(50vh - 45px);
  }

  .coupon-btn {
    width: 100px;
    height: 100px;
    top: calc(50vh + 10px);
  }

  .challenge-btn {
    width: 337.5px;
    /* 270px * 1.25 */
    height: 112.5px;
    /* 90px * 1.25 */
  }
}

@media (max-width: 768px) {
  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }

  .rule-btn {
    width: 85px;
    /* 78px * 1.5 */
    height: 85px;
    top: calc(50vh - 48px);
  }

  .coupon-btn {
    width: 95px;
    height: 95px;
    top: calc(50vh + 8px);
  }

  .challenge-btn {
    width: 300px;
    /* 240px * 1.25 */
    height: 100px;
    /* 80px * 1.25 */
  }
}

@media (max-width: 480px) {
  .activity-section {
    min-height: 600px;
  }

  .activity-bg {
    object-fit: cover;
    object-position: center top;
  }

  .rule-btn {
    width: 80px;
    /* 72px * 1.5 */
    height: 80px;
    top: calc(50vh - 50px);
  }

  .coupon-btn {
    width: 90px;
    height: 90px;
    top: calc(50vh + 5px);
  }

  .challenge-btn {
    width: 262.5px;
    /* 210px * 1.25 */
    height: 87.5px;
    /* 70px * 1.25 */
  }

  .center-button {
    bottom: 5%;
  }
}

@media (max-height: 500px) and (orientation: landscape) {
  .activity-section {
    height: 100vh;
  }

  .center-button {
    bottom: 5%;
  }

  .challenge-btn {
    width: 225px;
    /* 180px * 1.25 */
    height: 75px;
    /* 60px * 1.25 */
  }
}
</style>

<!-- 奖品库存提示 -->
<PrizeStockTip 
  :visible="showPrizeStockTip" 
  :prizes="prizeStockData"
  @close="handlePrizeStockClose"
  @view-rules="handleViewRules"
/>