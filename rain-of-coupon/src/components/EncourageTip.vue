<template>
  <div class="encourage-tip">
    <div class="modal-content">
      <DynamicImage 
        resource-key="luck_plus_btn" 
        fallback-url="/src/assets/coupon/福气+1.png"
        alt="福气+1按钮" 
        class-name="encourage-image" 
        @click="goToHome"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import DynamicImage from './DynamicImage.vue'

const router = useRouter()

// 点击图片返回主页
const goToHome = () => {
  console.log('🍀 [EncourageTip] 点击图片，返回主页')
  router.push('/')
}
</script>

<style scoped>
.encourage-tip {
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
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
  max-width: none;
  width: auto;
}

.encourage-image {
  max-width: 80%;
  max-height: 80vh;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.encourage-image:hover {
  transform: scale(1.05);
}

.encourage-image:active {
  transform: scale(0.95);
}
</style>
