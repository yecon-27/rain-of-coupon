<template>
  <div class="left-button">
  <button class="back-btn" @click="goBack" title="返回上一页">
    <span class="back-text">返回</span>
  </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
  const goBack = () => {
    router.go(-1)
  }
</script>

<style scoped>
.left-button {
  position: fixed;
  top: 50%;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 10;
}

.back-btn {
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border: none;
  border-radius: 8px 0 0 8px;
  padding: 8px 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  outline: none;
  color: white;
  font-size: 14px;
  font-weight: 500;
  box-shadow: -4px 0 15px rgba(74, 144, 226, 0.3);
  width: 48px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  position: relative;
  right: 0;
}

.back-text {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  writing-mode: vertical-rl;
  text-orientation: upright;
  line-height: 1.4;
  letter-spacing: 5px;
}
@media (max-width: 768px) {
  .back-btn {
    width: 44px;
    height: 72px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .back-btn {
    width: 40px;
    height: 64px;
    font-size: 12px;
  }
}
</style>