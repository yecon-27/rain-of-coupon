<template>
  <div class="prize-modal">
    <div class="modal-content">
      <h2>恭喜！</h2>
      <p>您点击了 {{ clickedCount }} 个红包！</p>
      <p>获得奖励：{{ prizeTier }}</p>
      <button class="close-btn" @click="$emit('close')">确定</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameStore } from '@/stores/gameStore';

const gameStore = useGameStore();

const clickedCount = gameStore.clickedPacketCount;

function getPrizeTier(count: number) {
  if (count >= 20) {
    return '一等奖';
  } else if (count >= 10) {
    return '二等奖';
  } else {
    return '三等奖';
  }
}

const prizeTier = getPrizeTier(clickedCount);
</script>

<style scoped>
.prize-modal {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-width: 300px;
  width: 90%;
}

.prize-info {
  margin: 20px 0;
}

.prize-amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.close-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
}
</style>