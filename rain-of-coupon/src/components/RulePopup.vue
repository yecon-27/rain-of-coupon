<template>
  <div class="rule-popup" v-if="visible">
    <!-- 规则弹窗 -->
    <div class="rule-modal">
      <div class="rule-content">
        <!-- 关闭按钮 -->
        <div class="close-btn" @click="handleClose">
          <i class="close-icon">×</i>
        </div>

        <!-- 活动规则标题 -->
        <div class="rule-header">
          <h2 class="rule-title">活动规则</h2>
        </div>

        <!-- 发放规则 -->
        <div class="rule-section">
          <div class="section-header">
            <span class="section-title">发放规则</span>
          </div>
          <div class="section-content" v-html="distributionRules"></div>
        </div>

        <!-- 使用规则 -->
        <div class="rule-section">
          <div class="section-header">
            <span class="section-title">使用规则</span>
          </div>
          <div class="section-content" v-html="usageRules"></div>
        </div>

        <!-- 底部按钮 -->
        <div class="rule-footer">
          <button class="confirm-btn" @click="handleConfirm">
            我已阅读并同意
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getRulesForDisplay } from '@/api/rules'

// Props
interface Props {
  visible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
})

// Emits
const emit = defineEmits<{
  close: []
  confirm: []
}>()

// 响应式数据
const distributionRules = ref('') // 发放规则内容
const usageRules = ref('') // 使用规则内容
const loading = ref(false)

// 获取规则数据
const loadRules = async () => {
  loading.value = true
  try {
    const response = await getRulesForDisplay()
    if (response.code === 200) {
      const rulesData = response.data
      
      // 设置发放规则
      if (rulesData.distribution_rule && rulesData.distribution_rule.length > 0) {
        distributionRules.value = rulesData.distribution_rule[0].ruleContent
      }
      
      // 设置使用规则
      if (rulesData.usage_rule && rulesData.usage_rule.length > 0) {
        usageRules.value = rulesData.usage_rule[0].ruleContent
      }
    }
  } catch (error) {
    console.error('加载规则失败:', error)
    // 设置默认规则内容
    setDefaultRules()
  } finally {
    loading.value = false
  }
}

// 设置默认规则内容（后端不可用时的降级处理）
const setDefaultRules = () => {
  distributionRules.value = `
    <div class="rule-intro">消费券发放分两个阶段。</div>
    <div class="rule-stage">
      <span class="stage-title">第一阶段</span>发放时间为2025年1月22日早上10:00至25日早上10:00，按计划数发放，发完为止。
    </div>
    <div class="rule-stage">
      <span class="stage-title">第二阶段</span>发放时间为2025年2月6日10:00至18:00，按第一份段未使用的消费券回收数量发放，发完为止。
    </div>
  `
  
  usageRules.value = `
    <div class="usage-intro">消费者在符合条件的实体餐饮商家进行消费时，须满足所持消费券对应的使用要求方可核销，每桌限使用一张。</div>
    <div class="usage-detail">消费券使用分两个阶段。</div>
    <div class="rule-stage">
      <span class="stage-title">第一阶段</span>使用时间为2025年1月22日早上10:00至2月4日午夜12:00，期间未使用，消费券失效且被系统回收。
    </div>
    <div class="rule-stage">
      <span class="stage-title">第二阶段</span>使用时间为2025年2月6日早上10:00至2月12日午夜12:00，期间未使用，消费券失效。
    </div>
  `
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
}

// 确认按钮
const handleConfirm = () => {
  emit('confirm')
}

// 监听visible变化，当弹窗显示时加载规则
watch(() => props.visible, (newVisible) => {
  if (newVisible) {
    loadRules()
  }
}, { immediate: true })

// 组件挂载时如果visible为true则加载规则
onMounted(() => {
  if (props.visible) {
    loadRules()
  }
})
</script>

<style scoped>
.rule-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.rule-modal {
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  background: white;
  border-radius: 12px;
  padding: 20px;
  position: relative;
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.rule-content {
  width: 100%;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.close-btn:hover {
  background: #e0e0e0;
}

.close-icon {
  font-size: 20px;
  color: #666;
  font-weight: bold;
}

.rule-header {
  text-align: center;
  margin-bottom: 20px;
}

.rule-title {
  color: #f35917;
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.rule-section {
  margin-bottom: 25px;
}

.section-header {
  margin-bottom: 15px;
}

.section-title {
  background: #f35917;
  color: white;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 14px;
  font-weight: bold;
}

.section-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

/* 规则内容样式 */
.section-content :deep(.rule-intro),
.section-content :deep(.usage-intro),
.section-content :deep(.usage-detail) {
  margin-bottom: 10px;
  color: #666;
}

.section-content :deep(.rule-stage) {
  margin-bottom: 12px;
  padding-left: 10px;
}

.section-content :deep(.stage-title) {
  color: #f35917;
  font-weight: bold;
  margin-right: 5px;
}

.rule-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.confirm-btn {
  background: #f35917;
  color: white;
  border: none;
  border-radius: 25px;
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.confirm-btn:hover {
  background: #e04d0f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .rule-modal {
    width: 95%;
    padding: 15px;
    max-height: 85vh;
  }
  
  .rule-title {
    font-size: 18px;
  }
  
  .section-content {
    font-size: 13px;
  }
}
</style>