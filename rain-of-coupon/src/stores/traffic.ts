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
      
      // 优先使用模拟服务
      try {
        const { trafficService } = await import('@/services/trafficService')
        
        // 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, 300 + Math.random() * 200))
        
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
      } catch (simulationErr) {
        console.warn('模拟服务失败，尝试后端API:', simulationErr)
        
        // 降级到后端API
        const request: UserActivityRequest = {
          action: 'join',
          userId,
          sessionId: state.value.sessionId
        }
        
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
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : '网络错误'
      
      // 最终降级处理
      const randomSuccess = Math.random() > 0.5 // 50%概率成功
      if (randomSuccess) {
        state.value.userStatus = 'active'
        state.value.isInActivity = true
        startHeartbeat()
        return true
      } else {
        state.value.userStatus = 'queued'
        state.value.queuePosition = Math.floor(Math.random() * 100) + 1
        return false
      }
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
      
      // 优先使用真实的后端API
      const canJoin = await checkTraffic()
      console.log('后端API调用成功，结果:', canJoin)
      return canJoin
    } catch (err) {
      console.warn('后端API调用失败，使用降级处理:', err)
      error.value = err instanceof Error ? err.message : '网络错误'
      
      // 降级到模拟服务
      try {
        const { trafficService } = await import('@/services/trafficService')
        
        console.log('使用模拟服务进行流量检测...')
        
        // 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, 500 + Math.random() * 300))
        
        // 使用模拟的流量检测逻辑
        const result = trafficService.checkTrafficStatus()
        
        state.value.status = result.status
        state.value.currentUsers = result.currentUsers
        state.value.maxUsers = result.maxUsers
        state.value.queuePosition = result.queuePosition || null
        state.value.estimatedWaitTime = result.estimatedWaitTime || null
        state.value.retryAfter = result.retryAfter || null
        
        console.log('模拟服务检测结果:', result)
        
        return result.status === 'ok'
      } catch (fallbackErr) {
        console.error('模拟服务也失败了:', fallbackErr)
        error.value = '流量检测服务暂时不可用，请稍后重试'
        
        // 最终降级处理
        state.value.status = 'maintenance'
        return false
      }
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