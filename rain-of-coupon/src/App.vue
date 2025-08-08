<script setup lang="ts"></script>

<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
/* 全局样式重置 - 消除浏览器默认间距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

#app {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
}
</style>
<style scoped>
#app {
  width: 100%;
  height: 100vh;
}
</style>
