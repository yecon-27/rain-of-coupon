<template>
  <div class="prize-modal">
    <div v-if="gameStore.hasPrize" class="prize-content">
      <img 
        :src="gameStore.getPrizeImageUrl || ''"
        @click="navigateToHome"
        class="prize-image"
        alt="奖品"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameStore } from '@/stores/gameStore';
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';

const gameStore = useGameStore();
const router = useRouter();

// 在组件挂载时检查是否已经中奖

onMounted(() => {
  gameStore.loadPrizeRecord();
});

const navigateToHome = () => {
  router.push('/');
};
</script>

<style scoped>
.prize-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.prize-content {
  text-align: center;
}

.prize-image {
  max-width: 80%;
  max-height: 80vh;
  cursor: pointer;
  transition: transform 0.2s;
}

.prize-image:hover {
  transform: scale(1.05);
}
</style>
