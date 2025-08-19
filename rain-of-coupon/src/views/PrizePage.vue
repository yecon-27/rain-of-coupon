<template>
  <div class="prize-page">
    <div class="prize-container">
      <div class="prize-header">
        <h1>恭喜中奖！</h1>
        <p>您已成功获得优惠券</p>
      </div>
      
      <div class="prize-content" v-if="gameStore.hasPrize">
        <div class="prize-image-container" @click="navigateToHome">
          <img 
            :src="gameStore.getPrizeImageUrl" 
            :alt="`${gameStore.prizeRecord?.amount}元优惠券`"
            class="prize-image"
          />
        </div>
        
        <div class="prize-info">
          <h2>{{ gameStore.prizeRecord?.amount }}元优惠券</h2>
          <p class="prize-description">{{ gameStore.prizeRecord?.prizeName || `满${getPrizeThreshold(gameStore.prizeRecord?.amount)}元可用` }}</p>
          <p class="prize-time">获得时间：{{ formatTime(gameStore.prizeRecord?.timestamp) }}</p>
        </div>
        
        <div class="prize-actions">
          <button @click="navigateToHome" class="btn-primary">
            返回首页
          </button>
          <button @click="navigateToCoupon" class="btn-secondary">
            查看我的优惠券
          </button>
        </div>
      </div>
      
      <div class="no-prize" v-else>
        <p>暂无中奖记录</p>
        <button @click="navigateToHome" class="btn-primary">
          返回首页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useGameStore } from '@/stores/gameStore';

const router = useRouter();
const gameStore = useGameStore();

onMounted(() => {
  gameStore.loadPrizeRecord();
});

const navigateToHome = () => {
  router.push('/');
};

const navigateToCoupon = () => {
  router.push('/coupon');
};

const getPrizeThreshold = (amount?: number): number => {
  switch (amount) {
    case 188: return 500;
    case 588: return 1500;
    case 888: return 2500;
    default: return 500;
  }
};

const formatTime = (timestamp?: number): string => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleString('zh-CN');
};
</script>

<style scoped>
.prize-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.prize-container {
  background: white;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.prize-header h1 {
  color: #ff6b6b;
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: bold;
}

.prize-header p {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 30px;
}

.prize-image-container {
  margin: 30px 0;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.prize-image-container:hover {
  transform: scale(1.05);
}

.prize-image {
  width: 200px;
  height: 200px;
  object-fit: contain;
  border-radius: 15px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.prize-info h2 {
  color: #333;
  font-size: 1.8rem;
  margin: 20px 0 10px;
  font-weight: bold;
}

.prize-description {
  color: #666;
  font-size: 1rem;
  margin-bottom: 10px;
}

.prize-time {
  color: #999;
  font-size: 0.9rem;
  margin-bottom: 30px;
}

.prize-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary {
  padding: 12px 30px;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.btn-primary {
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
}

.btn-secondary {
  background: #f8f9fa;
  color: #333;
  border: 2px solid #dee2e6;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.no-prize {
  padding: 40px 0;
}

.no-prize p {
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .prize-container {
    padding: 30px 20px;
  }
  
  .prize-header h1 {
    font-size: 2rem;
  }
  
  .prize-image {
    width: 150px;
    height: 150px;
  }
  
  .prize-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .btn-primary, .btn-secondary {
    width: 100%;
    max-width: 200px;
  }
}
</style>