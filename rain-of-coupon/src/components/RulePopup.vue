<template>
  <div class="rule-popup" v-if="visible">
    <!-- 规则弹窗 -->
    <div class="rule-modal">
      <div class="rule-content">
        <!-- 关闭按钮移到底部 -->

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

    <!-- 底部关闭按钮 -->
    <div class="bottom-close-btn" @click="handleClose">
      <i class="close-icon">×</i>
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
  console.log('🔍 开始加载规则数据...')

  try {
    const response = await getRulesForDisplay()
    console.log('✅ 成功从API加载规则数据:', response.data)

    // 处理API返回的规则数据
    if (response.data && Array.isArray(response.data)) {
      const rules = response.data

      // 按规则类型分类
      const distributionRule = rules.find(rule => rule.ruleType === 'distribution_rule')
      const usageRule = rules.find(rule => rule.ruleType === 'usage_rule')

      distributionRules.value = distributionRule?.ruleContent || ''
      usageRules.value = usageRule?.ruleContent || ''
    } else {
      console.warn('⚠️ API返回数据格式异常')
      distributionRules.value = ''
      usageRules.value = ''
      // setDefaultRules() // 注释掉默认规则
    }
  } catch (error) {
    console.error('❌ 加载规则失败:', error)
    console.log('📋 API调用失败，不使用默认规则')
    distributionRules.value = ''
    usageRules.value = ''
    // setDefaultRules() // 注释掉默认规则
  } finally {
    loading.value = false
  }
}

// 获取默认发放规则 (已注释，测试纯API调用)
// const getDefaultDistributionRules = () => {
//   return `
//     <div class="rule-intro">消费券发放分两个阶段。</div>
//     <div class="rule-stage">
//       <span class="stage-title">第一阶段</span>发放时间为2025年1月22日早上10:00至25日早上10:00，按计划数发放，发完为止。
//     </div>
//     <div class="rule-stage">
//       <span class="stage-title">第二阶段</span>发放时间为2025年2月6日10:00至18:00，按第一份段未使用的消费券回收数量发放，发完为止。
//     </div>
//   `
// }

// 获取默认使用规则 (已注释，测试纯API调用)
// const getDefaultUsageRules = () => {
//   return `
//     <div class="usage-intro">消费者在符合条件的实体餐饮商家进行消费时，须满足所持消费券对应的使用要求方可核销，每桌限使用一张。</div>
//     <div class="usage-detail">消费券使用分两个阶段。</div>
//     <div class="rule-stage">
//       <span class="stage-title">第一阶段</span>使用时间为2025年1月22日早上10:00至2月4日午夜12:00，期间未使用，消费券失效且被系统回收。
//     </div>
//     <div class="rule-stage">
//       <span class="stage-title">第二阶段</span>使用时间为2025年2月6日早上10:00至2月12日午夜12:00，期间未使用，消费券失效。
//     </div>
//   `
// }

// 设置默认规则内容（已注释，测试纯API调用）
// const setDefaultRules = () => {
//   distributionRules.value = getDefaultDistributionRules()
//   usageRules.value = getDefaultUsageRules()
// }

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

/* 底部关闭按钮 */
.bottom-close-btn {
  position: absolute;
  bottom: -60px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.bottom-close-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

.close-icon {
  font-size: 24px;
  color: white;
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