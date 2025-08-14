import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { 
  checkTrafficStatus, 
  joinActivity, 
  leaveActivity, 
  sendHeartbeat,
  type TrafficCheckResponse,
  type UserActivityRequest 
} from '@/api/traffic'

export interface TrafficState {
  status: 'ok' | 'crowded' | 'maintenance' | 'checking'
  currentUsers: number
  maxUsers: number
  queuePosition: number | null
  estimatedWaitTime: number | null
  retryAfter: number | null
  userStatus: 'active' | 'queued' | 'blocked' | 'idle'
  sessionId: string | null
  lastHeartbeat: Date | null
  isInActivity: boolean
}

export const useTrafficStore = defineStore('traffic', () => {
  // 状态
  const state = ref<TrafficState>({
    status: 'checking',
    currentUsers: 0,
    maxUsers: 1000, // 默认最大用户数
    queuePosition: null,
    estimatedWaitTime: null,
    retryAfter: null,
    userStatus: 'idle',
    sessionId: null,
    lastHeartbeat: null,
    isInActivity: false
  })

  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // 心跳定时器
  let heartbeatTimer: NodeJS.Timeout | null = null
  
  // Computed
  const canJoinActivity = computed(() => {
    return state.value.status === 'ok' && state.value.userStatus !== 'blocked'
  })
  
  const isCrowded = computed(() => {
    return state.value.status === 'crowded'
  })
  
  const isInQueue = computed(() => {
    return state.value.userStatus === 'queued' && state.value.queuePosition !== null
  })
  
  const shouldShowCrowdingTip = computed(() => {
    return state.value.status === 'crowded' || state.value.userStatus === 'queued'
  })

  // Actions
  const checkTraffic = async (): Promise<boolean> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await checkTrafficStatus()
      
      if (response.code === 200) {
        state.value.status = response.data.status
        state.value.currentUsers = response.data.currentUsers
        state.value.maxUsers = response.data.maxUsers
        state.value.queuePosition = response.data.queuePosition || null
        state.value.estimatedWaitTime = response.data.estimatedWaitTime || null
        state.value.retryAfter = response.data.retryAfter || null
        
        return response.data.status === 'ok'
      } else {
        throw new Error(response.msg || '流量检测失败')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : '网络错误'
      state.value.status = 'maintenance'
      return false
    } finally {
      loading.value = false
    }
  }

  const attemptJoinActivity = async (userId?: string): Promise<boolean> => {
    loading.value = true
    error.value = null
    
    try {
      // 生成会话ID
      if (!state.value.sessionId) {
        state.value.sessionId = `session_${Date.now()}_${Math.random().toString(36).substring(2, 11)}`
      }
      
      // 开发阶段使用模拟服务
      const { trafficService } = await import('@/services/trafficService')
      
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 500 + Math.random() * 1000))
      
      const result = trafficService.joinActivity(state.value.sessionId, userId)
      
      state.value.currentUsers = result.currentUsers
      state.value.userStatus = result.userStatus
      state.value.isInActivity = result.userStatus === 'active'
      
      if (result.userStatus === 'queued') {
        state.value.queuePosition = (result as any).queuePosition || null
      }
      
      // 如果成功加入，开始心跳
      if (result.userStatus === 'active') {
        startHeartbeat()
        return true
      }
      
      return false
    } catch (err) {
      error.value = err instanceof Error ? err.message : '网络错误'
      return false
    } finally {
      loading.value = false
    }
  }

  const leaveActivitySession = async (): Promise<void> => {
    if (!state.value.sessionId) {
      return
    }
    
    try {
      // 开发阶段使用模拟服务
      const { trafficService } = await import('@/services/trafficService')
      
      const result = trafficService.leaveActivity(state.value.sessionId)
      
      // 清理状态
      state.value.userStatus = 'idle'
      state.value.isInActivity = false
      state.value.queuePosition = null
      state.value.estimatedWaitTime = null
      state.value.currentUsers = result.currentUsers
      
      // 停止心跳
      stopHeartbeat()
    } catch (err) {
      console.error('离开活动失败:', err)
    }
  }

  const startHeartbeat = () => {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
    }
    
    heartbeatTimer = setInterval(async () => {
      if (!state.value.sessionId || !state.value.isInActivity) {
        stopHeartbeat()
        return
      }
      
      try {
        // 开发阶段使用模拟服务
        const { trafficService } = await import('@/services/trafficService')
        
        const result = trafficService.handleHeartbeat(state.value.sessionId)
        
        if (result.success) {
          state.value.lastHeartbeat = new Date()
          state.value.currentUsers = result.currentUsers
          
          // 如果心跳失败，用户可能被踢出
          if (result.userStatus !== 'active') {
            state.value.userStatus = result.userStatus
            state.value.isInActivity = false
            stopHeartbeat()
          }
        }
      } catch (err) {
        console.error('心跳发送失败:', err)
        // 连续失败可能需要重新加入
      }
    }, 30000) // 每30秒发送一次心跳
  }

  const stopHeartbeat = () => {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
  }

  const resetState = () => {
    state.value = {
      status: 'checking',
      currentUsers: 0,
      maxUsers: 1000,
      queuePosition: null,
      estimatedWaitTime: null,
      retryAfter: null,
      userStatus: 'idle',
      sessionId: null,
      lastHeartbeat: null,
      isInActivity: false
    }
    error.value = null
    stopHeartbeat()
  }

  // 模拟后端限流逻辑（开发阶段使用）
  const simulateTrafficCheck = async (): Promise<boolean> => {
    loading.value = true
    
    try {
      // 动态导入服务
      const { trafficService } = await import('@/services/trafficService')
      
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 2000))
      
      // 使用真实的流量检测逻辑
      const result = trafficService.checkTrafficStatus()
      
      state.value.status = result.status
      state.value.currentUsers = result.currentUsers
      state.value.maxUsers = result.maxUsers
      state.value.queuePosition = result.queuePosition || null
      state.value.estimatedWaitTime = result.estimatedWaitTime || null
      state.value.retryAfter = result.retryAfter || null
      
      return result.status === 'ok'
    } catch (err) {
      console.error('流量检测模拟失败:', err)
      // 降级处理
      state.value.status = 'maintenance'
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    state,
    loading,
    error,
    
    // Computed
    canJoinActivity,
    isCrowded,
    isInQueue,
    shouldShowCrowdingTip,
    
    // Actions
    checkTraffic,
    attemptJoinActivity,
    leaveActivitySession,
    startHeartbeat,
    stopHeartbeat,
    resetState,
    simulateTrafficCheck
  }
})