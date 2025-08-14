import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUIStore = defineStore('ui', () => {
  // UI状态
  const showOverlay = ref(false)
  const showCrowdingTip = ref(false)
  
  // 设置overlay状态
  const setOverlay = (show: boolean) => {
    showOverlay.value = show
  }
  
  // 设置拥挤提示状态
  const setCrowdingTip = (show: boolean) => {
    showCrowdingTip.value = show
  }
  
  // 重置所有overlay状态
  const resetAllOverlays = () => {
    showOverlay.value = false
    showCrowdingTip.value = false
  }
  
  return {
    // 状态
    showOverlay,
    showCrowdingTip,
    
    // 方法
    setOverlay,
    setCrowdingTip,
    resetAllOverlays
  }
})