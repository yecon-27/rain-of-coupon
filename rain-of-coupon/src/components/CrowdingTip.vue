<template>
  <div v-if="visible" class="crowding-overlay">
    <div class="crowding-content">
      <h3>🎉 活动火爆进行中！</h3>
      <p>当前参与人数较多，请稍后再试</p>
      <button @click="retry" class="retry-btn">重新尝试</button>
      <button @click="close" class="close-btn">返回首页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
// Props
interface Props {
  visible?: boolean
}

defineProps<Props>()

// Emits
const emit = defineEmits<{
  retry: []
  close: []
}>()

// 重新尝试
const retry = () => {
  emit('retry')
}

// 关闭提示
const close = () => {
  emit('close')
}
</script>

<style scoped>
.crowding-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.crowding-content {
  background: white;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  color: #333;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.crowding-content h3 {
  font-size: 24px;
  margin: 0 0 20px 0;
  color: orange;
}

.crowding-content p {
  font-size: 16px;
  margin: 0 0 30px 0;
  color: #666;
}

.retry-btn,
.close-btn {
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
.close-btn:hover {
  background: #ff8c00;
  transform: translateY(-2px);
}

.close-btn {
  background: #666;
}

.close-btn:hover {
  background: #555;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .crowding-content {
    padding: 30px 20px;
  }

  .retry-btn,
  .close-btn {
    display: block;
    width: 100%;
    margin: 10px 0;
  }
}
</style>