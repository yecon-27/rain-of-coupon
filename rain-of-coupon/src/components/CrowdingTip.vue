<template>
  <div class="crowded-message">
    <div class="message-content">
      <h3>🎉 活动火爆进行中！</h3>
      <p>当前参与人数较多，请稍后再试</p>
      <button @click="$emit('retry')" class="retry-btn">重新尝试</button>
      <button @click="$emit('back')" class="home-btn">返回首页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits(['retry', 'back'])
</script>

<style scoped>
.crowded-message {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.message-content {
  background: white;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  color: #333;
}

.message-content h3 {
  font-size: 24px;
  margin: 0 0 20px 0;
  color: orange;
}

.message-content p {
  font-size: 16px;
  margin: 0 0 30px 0;
  color: #666;
}

.retry-btn,
.home-btn {
  background: orange;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  margin: 0 10px;
  transition: all 0.3s ease;
}

.retry-btn:hover,
.home-btn:hover {
  background: #ff8c00;
  transform: translateY(-2px);
}

.home-btn {
  background: #666;
}

.home-btn:hover {
  background: #555;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-content {
    padding: 30px 20px;
  }

  .retry-btn,
  .home-btn {
    display: block;
    width: 100%;
    margin: 10px 0;
  }
}
</style>