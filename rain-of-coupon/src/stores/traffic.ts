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
      
      // 优先使用后端API
      const request: UserActivityRequest = {
        action: 'join',
        userId: userId || `anonymous_${Date.now()}`,
        sessionId: state.value.sessionId
      }
      
      console.log('调用后端API，请求参数:', request)
      
      const response = await joinActivity(request)
      
      if (response.code === 200) {
        state.value.currentUsers = response.data.currentUsers
        state.value.userStatus = response.data.userStatus
        state.value.isInActivity = response.data.userStatus === 'active'
        
        if (response.data.userStatus === 'queued') {
          state.value.queuePosition = (response.data as any).queuePosition || null
        }
        
        // 如果成功加入，开始心跳
        if (response.data.userStatus === 'active') {
          startHeartbeat()
          return true
        }
        
        return false
      } else {
        throw new Error(response.msg || '加入活动失败')
      }
    } catch (err) {
      console.error('后端API调用失败:', error)
      error.value = error instanceof Error ? error.message : '网络错误'
      
      // 如果确实需要降级到模拟服务，可以在这里添加
      // 但根据您的要求，我们优先使用后端接口
      throw error
    } finally {
      loading.value = false
    }
  }

  const leaveActivitySession = async (): Promise<void> => {
    if (!state.value.sessionId) {
      return
    }
    
    try {
      const request: UserActivityRequest = {
        action: 'leave',
        sessionId: state.value.sessionId
      }
      
      const response = await leaveActivity(request)
      
      if (response.code === 200) {
        // 清理状态
        state.value.userStatus = 'idle'
        state.value.isInActivity = false
        state.value.queuePosition = null
        state.value.estimatedWaitTime = null
        state.value.currentUsers = response.data.currentUsers
      }
      
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
        const request: UserActivityRequest = {
          action: 'heartbeat',
          sessionId: state.value.sessionId
        }
        
        const response = await sendHeartbeat(request)
        
        if (response.code === 200) {
          state.value.lastHeartbeat = new Date()
          state.value.currentUsers = response.data.currentUsers
          
          // 如果心跳失败，用户可能被踢出
          if (response.data.userStatus !== 'active') {
            state.value.userStatus = response.data.userStatus
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

  // 智能流量检测（优先使用真实后端API）
  const smartTrafficCheck = async (): Promise<boolean> => {
    loading.value = true
    error.value = null
    
    try {
      console.log('开始调用后端流量检测API...')
      
      // 直接使用后端API，不降级到模拟服务
      const canJoin = await checkTraffic()
      console.log('后端API调用成功，结果:', canJoin)
      return canJoin
    } catch (err) {
      console.error('后端API调用失败:', err)
      error.value = err instanceof Error ? err.message : '网络错误'
      
      // 不使用模拟服务降级，直接抛出错误
      throw err
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
    smartTrafficCheck
  }
})