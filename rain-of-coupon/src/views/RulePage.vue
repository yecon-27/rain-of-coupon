<template>
  <div class="rule-page">
    <!-- 使用RulePopup组件 -->
    <RulePopup 
      :visible="showRules" 
      @close="handleClose"
      @confirm="handleConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RulePopup from '@/components/RulePopup.vue'

const router = useRouter()

// 控制规则弹窗显示
const showRules = ref(true)

// 关闭规则弹窗
const handleClose = () => {
  showRules.value = false
  // 返回首页
  router.push('/')
}

// 确认规则
const handleConfirm = () => {
  showRules.value = false
  // 可以在这里添加确认后的逻辑，比如记录用户已阅读规则
  console.log('用户已确认阅读规则')
  // 返回首页
  router.push('/')
}

// 页面挂载时显示规则
onMounted(() => {
  showRules.value = true
})
</script>

<style scoped>
.rule-page {
  width: 100vw;
  height: 100vh;
}
</style>