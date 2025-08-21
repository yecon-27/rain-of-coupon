<template>
  <div v-if="visible" class="prize-stock-tip-overlay">
    <div class="prize-stock-tip-modal">
      <div class="tip-header">
        <h3>🎁 奖品发放提醒</h3>
      </div>
      
      <div class="tip-content">
        <div class="icon-container">
          <div class="gift-icon">🎁</div>
        </div>
        <p class="main-message">
          本轮奖品已发放完毕
        </p>
        <p class="sub-message">
          请等待优惠券回流后的第二次抽奖
        </p>
        
        <div class="prize-list" v-if="prizes && prizes.length > 0">
          <h4>奖品库存情况：</h4>
          <div class="prize-item" v-for="prize in prizes" :key="prize.id">
            <span class="prize-name">{{ prize.prizeName }}</span>
            <span class="prize-stock" :class="{ 'out-of-stock': prize.remainingCount === 0 }">
              {{ prize.remainingCount }}/{{ prize.totalCount }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="tip-actions">
        <button class="btn btn-primary" @click="handleKnow">
          我知道了
        </button>
        <button class="btn btn-secondary" @click="handleViewRules">
          查看规则
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

interface Prize {
  id: number
  prizeName: string
  totalCount: number
  remainingCount: number
}

interface Props {
  visible: boolean
  prizes?: Prize[]
}

defineProps<Props>()

const emit = defineEmits<{
  close: []
  viewRules: []
}>()

const handleKnow = () => {
  emit('close')
}

const handleViewRules = () => {
  emit('viewRules')
}
</script>

<style scoped>
.prize-stock-tip-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
}

.prize-stock-tip-modal {
  background: linear-gradient(135deg, #fff 0%, #fafafa 100%);
  border-radius: 20px;
  padding: 30px;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 8px 32px rgba(243, 89, 23, 0.1);
  border: 2px solid rgba(243, 89, 23, 0.1);
  animation: modalSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.tip-header {
  text-align: center;
  margin-bottom: 25px;
  position: relative;
}

.tip-header::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #f35917, #ff7b47);
  border-radius: 2px;
}

.tip-header h3 {
  margin: 0;
  background: linear-gradient(135deg, #f35917, #DC143C);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.tip-content {
  text-align: center;
  margin-bottom: 30px;
}

.icon-container {
  margin-bottom: 20px;
}

.gift-icon {
  font-size: 64px;
  animation: bounce 2s infinite;
  display: inline-block;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.main-message {
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #333, #555);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.sub-message {
  font-size: 15px;
  color: #666;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.prize-list {
  text-align: left;
  margin-top: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 15px;
  border: 1px solid rgba(243, 89, 23, 0.1);
}

.prize-list h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #f35917;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.prize-list h4::before {
  content: '📊';
  margin-right: 8px;
}

.prize-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 14px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.prize-item:last-child {
  border-bottom: none;
}

.prize-name {
  color: #333;
  font-weight: 500;
  flex: 1;
}

.prize-stock {
  color: #28a745;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  background: rgba(40, 167, 69, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 13px;
}

.prize-stock.out-of-stock {
  color: #dc3545;
  background: rgba(220, 53, 69, 0.1);
}

.tip-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  min-width: 120px;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #f35917, #ff7b47);
  color: white;
  box-shadow: 0 4px 15px rgba(243, 89, 23, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #e04d0f, #f35917);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(243, 89, 23, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #6c757d, #868e96);
  color: white;
  box-shadow: 0 4px 15px rgba(108, 117, 125, 0.3);
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #5a6268, #6c757d);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(108, 117, 125, 0.4);
}

.btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .prize-stock-tip-modal {
    width: 95%;
    padding: 25px 20px;
    margin: 20px;
  }
  
  .tip-header h3 {
    font-size: 20px;
  }
  
  .gift-icon {
    font-size: 56px;
  }
  
  .main-message {
    font-size: 16px;
  }
  
  .sub-message {
    font-size: 14px;
  }
  
  .btn {
    padding: 10px 20px;
    font-size: 14px;
    min-width: 100px;
  }
  
  .tip-actions {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .prize-stock-tip-modal {
    width: 98%;
    padding: 20px 15px;
  }
  
  .tip-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .btn {
    width: 100%;
  }
}
</style>