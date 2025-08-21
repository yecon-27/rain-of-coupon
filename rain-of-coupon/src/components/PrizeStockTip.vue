<template>
  <div v-if="visible" class="prize-stock-tip-overlay">
    <div class="prize-stock-tip-modal">
      <div class="tip-header">
        <h3>奖品发放提醒</h3>
      </div>
      
      <div class="tip-content">
        <div class="icon-container">
          <i class="icon-gift">🎁</i>
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
            <span class="prize-stock">{{ prize.remainingCount }}/{{ prize.totalCount }}</span>
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
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.prize-stock-tip-modal {
  background: white;
  border-radius: 12px;
  padding: 24px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.tip-header {
  text-align: center;
  margin-bottom: 20px;
}

.tip-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.tip-content {
  text-align: center;
  margin-bottom: 24px;
}

.icon-container {
  font-size: 48px;
  margin-bottom: 16px;
}

.main-message {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.sub-message {
  font-size: 14px;
  color: #666;
  margin: 0 0 16px 0;
}

.prize-list {
  text-align: left;
  margin-top: 16px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

.prize-list h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.prize-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 12px;
}

.prize-name {
  color: #333;
}

.prize-stock {
  color: #999;
}

.tip-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #545b62;
}
</style>