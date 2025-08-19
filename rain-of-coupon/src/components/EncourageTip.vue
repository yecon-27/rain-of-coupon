<template>
  <div class="encourage-tip">
    <div class="modal-content">
      <h2>💪 再接再厉！</h2>
      <div class="encourage-info">
        <p>这次没有中奖，但不要灰心！</p>
        <p>下次一定会有好运气的～</p>
      </div>
      <button class="close-btn" @click="$emit('close')">我知道了</button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits<{
  close: []
}>()
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
  background: white;
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-width: 300px;
  width: 90%;
}

.encourage-info {
  margin: 20px 0;
  color: #666;
}

.close-btn {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
}
</style>