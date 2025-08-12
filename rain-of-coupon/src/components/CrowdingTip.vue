<template>
  <div class="crowded-tip-overlay">
    <div class="crowded-tip-modal">
      <div class="tip-icon">🔥</div>
      <h3>活动太火爆啦！</h3>
      <p>当前参与人数较多，请稍后再试</p>
      
      <div class="tip-actions">
        <button @click="$emit('retry')" class="retry-btn">
          重新尝试
        </button>
        <button @click="$emit('back')" class="back-btn">
          返回首页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits(['retry', 'back'])
</script>

<style scoped>
.crowded-tip-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.crowded-tip-modal {
  background: white;
  border-radius: 16px;
  padding: 40px 30px;
  text-align: center;
  max-width: 320px;
  margin: 20px;
}

.tip-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.crowded-tip-modal h3 {
  color: #333;
  font-size: 20px;
  margin-bottom: 12px;
}

.crowded-tip-modal p {
  color: #666;
  font-size: 14px;
  margin-bottom: 24px;
  line-height: 1.5;
}

.tip-actions {
  display: flex;
  gap: 12px;
}

.retry-btn, .back-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.retry-btn {
  background: #ff6b6b;
  color: white;
}

.retry-btn:hover {
  background: #ff5252;
}

.back-btn {
  background: #f5f5f5;
  color: #666;
}

.back-btn:hover {
  background: #e0e0e0;
}
</style>