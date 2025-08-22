<template>
  <div class="left-button">
    <button class="back-btn" @click="goBack" title="返回首页">
      <span class="back-text">返回</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUIStore } from '@/stores/ui'

const router = useRouter()
const uiStore = useUIStore()

const goBack = async () => {
  console.log('返回按钮被点击了，开始执行 goBack 函数。');
  uiStore.resetAllOverlays();
    await router.push('/').then(() => {
    window.location.reload();
    console.log('路由导航成功，页面应该已跳转到首页。');
  }).catch(err => {
    console.error('Navigation failed:', err);
    window.location.reload();
  });
}
</script>

<style scoped>
.left-button {
  position: fixed;
  top: 20px;
  left: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 1001;
  pointer-events:auto;
}

.back-btn {
  background: linear-gradient(135deg, #f35917, #f7761f);
  border: none;
  border-radius: 0 40px 40px 0;
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  outline: none;
  color: white;
  font-size: 28px;
  font-weight: 500;
  box-shadow: 4px 0 15px rgba(74, 144, 226, 0.3);
  width: 120px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  position: relative;
  pointer-events: auto;
}

.back-text {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  line-height: 1.4;
  letter-spacing: 1px;
}

/* 移除hover特效 */

@media (max-width: 768px) {
  .back-btn {
    width: 95px;
    height: 60px;
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .back-btn {
    width: 80px;
    height: 52px;
    font-size: 14px;
  }
}
</style>