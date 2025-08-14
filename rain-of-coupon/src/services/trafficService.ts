// 流量管理服务 - 模拟后端限流逻辑
// 这个文件模拟后端的流量控制和数据库操作

interface UserSession {
  sessionId: string
  userId?: string
  joinTime: Date
  lastHeartbeat: Date
  status: 'active' | 'queued' | 'expired'
  ipAddress?: string
}

interface ActivityConfig {
  maxConcurrentUsers: number
  queueTimeout: number // 队列超时时间（秒）
  heartbeatTimeout: number // 心跳超时时间（秒）
  isMaintenanceMode: boolean
}

class TrafficService {
  private activeSessions: Map<string, UserSession> = new Map()
  private queuedSessions: Map<string, UserSession> = new Map()
  private config: ActivityConfig = {
    maxConcurrentUsers: 1000,
    queueTimeout: 300, // 5分钟
    heartbeatTimeout: 60, // 1分钟
    isMaintenanceMode: false
  }

  // 模拟数据库表：activity_participants
  private participantHistory: Array<{
    id: number
    userId?: string
    sessionId: string
    joinTime: Date
    leaveTime?: Date
    ipAddress?: string
    status: 'completed' | 'timeout' | 'left'
  }> = []

  constructor() {
    // 定期清理过期会话
    setInterval(() => {
      this.cleanupExpiredSessions()
    }, 30000) // 每30秒清理一次
  }

  // 检查流量状态
  checkTrafficStatus() {
    if (this.config.isMaintenanceMode) {
      return {
        status: 'maintenance' as const,
        currentUsers: 0,
        maxUsers: this.config.maxConcurrentUsers
      }
    }

    const activeCount = this.activeSessions.size
    const queuedCount = this.queuedSessions.size

    if (activeCount >= this.config.maxConcurrentUsers) {
      return {
        status: 'crowded' as const,
        currentUsers: activeCount,
        maxUsers: this.config.maxConcurrentUsers,
        queuePosition: queuedCount + 1,
        estimatedWaitTime: this.estimateWaitTime(),
        retryAfter: 60
      }
    }

    return {
      status: 'ok' as const,
      currentUsers: activeCount,
      maxUsers: this.config.maxConcurrentUsers
    }
  }

  // 用户尝试加入活动
  joinActivity(sessionId: string, userId?: string, ipAddress?: string) {
    // 检查是否已经在活动中
    if (this.activeSessions.has(sessionId)) {
      return {
        success: true,
        currentUsers: this.activeSessions.size,
        userStatus: 'active' as const
      }
    }

    // 检查是否在队列中
    if (this.queuedSessions.has(sessionId)) {
      const queuePosition = Array.from(this.queuedSessions.keys()).indexOf(sessionId) + 1
      return {
        success: false,
        currentUsers: this.activeSessions.size,
        userStatus: 'queued' as const,
        queuePosition
      }
    }

    const activeCount = this.activeSessions.size

    if (activeCount < this.config.maxConcurrentUsers) {
      // 可以直接加入
      const session: UserSession = {
        sessionId,
        userId,
        joinTime: new Date(),
        lastHeartbeat: new Date(),
        status: 'active',
        ipAddress
      }

      this.activeSessions.set(sessionId, session)

      // 记录到历史
      this.participantHistory.push({
        id: this.participantHistory.length + 1,
        userId,
        sessionId,
        joinTime: new Date(),
        ipAddress,
        status: 'completed'
      })

      return {
        success: true,
        currentUsers: this.activeSessions.size,
        userStatus: 'active' as const
      }
    } else {
      // 需要排队
      const session: UserSession = {
        sessionId,
        userId,
        joinTime: new Date(),
        lastHeartbeat: new Date(),
        status: 'queued',
        ipAddress
      }

      this.queuedSessions.set(sessionId, session)
      const queuePosition = this.queuedSessions.size

      return {
        success: false,
        currentUsers: activeCount,
        userStatus: 'queued' as const,
        queuePosition
      }
    }
  }

  // 用户离开活动
  leaveActivity(sessionId: string) {
    let removed = false

    if (this.activeSessions.has(sessionId)) {
      this.activeSessions.delete(sessionId)
      removed = true

      // 更新历史记录
      const record = this.participantHistory.find(p => p.sessionId === sessionId && !p.leaveTime)
      if (record) {
        record.leaveTime = new Date()
        record.status = 'left'
      }

      // 尝试从队列中提升用户
      this.promoteFromQueue()
    }

    if (this.queuedSessions.has(sessionId)) {
      this.queuedSessions.delete(sessionId)
      removed = true
    }

    return {
      success: removed,
      currentUsers: this.activeSessions.size,
      userStatus: 'idle' as const
    }
  }

  // 处理心跳
  handleHeartbeat(sessionId: string) {
    const session = this.activeSessions.get(sessionId)
    
    if (session) {
      session.lastHeartbeat = new Date()
      return {
        success: true,
        currentUsers: this.activeSessions.size,
        userStatus: 'active' as const
      }
    }

    // 检查是否在队列中
    const queuedSession = this.queuedSessions.get(sessionId)
    if (queuedSession) {
      queuedSession.lastHeartbeat = new Date()
      const queuePosition = Array.from(this.queuedSessions.keys()).indexOf(sessionId) + 1
      
      return {
        success: true,
        currentUsers: this.activeSessions.size,
        userStatus: 'queued' as const,
        queuePosition
      }
    }

    return {
      success: false,
      currentUsers: this.activeSessions.size,
      userStatus: 'blocked' as const
    }
  }

  // 获取活动统计
  getActivityStats() {
    return {
      activeUsers: this.activeSessions.size,
      queuedUsers: this.queuedSessions.size,
      maxUsers: this.config.maxConcurrentUsers,
      totalParticipants: this.participantHistory.length,
      averageSessionTime: this.calculateAverageSessionTime()
    }
  }

  // 清理过期会话
  private cleanupExpiredSessions() {
    const now = new Date()
    const heartbeatTimeout = this.config.heartbeatTimeout * 1000
    const queueTimeout = this.config.queueTimeout * 1000

    // 清理活跃会话中的过期用户
    for (const [sessionId, session] of this.activeSessions.entries()) {
      if (now.getTime() - session.lastHeartbeat.getTime() > heartbeatTimeout) {
        this.activeSessions.delete(sessionId)
        
        // 更新历史记录
        const record = this.participantHistory.find(p => p.sessionId === sessionId && !p.leaveTime)
        if (record) {
          record.leaveTime = new Date()
          record.status = 'timeout'
        }
      }
    }

    // 清理队列中的过期用户
    for (const [sessionId, session] of this.queuedSessions.entries()) {
      if (now.getTime() - session.joinTime.getTime() > queueTimeout) {
        this.queuedSessions.delete(sessionId)
      }
    }

    // 尝试从队列中提升用户
    this.promoteFromQueue()
  }

  // 从队列中提升用户到活跃状态
  private promoteFromQueue() {
    const availableSlots = this.config.maxConcurrentUsers - this.activeSessions.size
    
    if (availableSlots > 0 && this.queuedSessions.size > 0) {
      const sessionsToPromote = Array.from(this.queuedSessions.entries())
        .slice(0, availableSlots)

      for (const [sessionId, session] of sessionsToPromote) {
        session.status = 'active'
        session.lastHeartbeat = new Date()
        
        this.activeSessions.set(sessionId, session)
        this.queuedSessions.delete(sessionId)

        // 添加到历史记录
        this.participantHistory.push({
          id: this.participantHistory.length + 1,
          userId: session.userId,
          sessionId,
          joinTime: new Date(),
          ipAddress: session.ipAddress,
          status: 'completed'
        })
      }
    }
  }

  // 估算等待时间
  private estimateWaitTime(): number {
    const averageSessionTime = this.calculateAverageSessionTime()
    const queuePosition = this.queuedSessions.size + 1
    const activeUsers = this.activeSessions.size
    
    // 简单估算：基于平均会话时间和队列位置
    const estimatedTime = Math.ceil((queuePosition / activeUsers) * averageSessionTime)
    return Math.max(60, Math.min(estimatedTime, 600)) // 最少1分钟，最多10分钟
  }

  // 计算平均会话时间
  private calculateAverageSessionTime(): number {
    const completedSessions = this.participantHistory.filter(p => p.leaveTime)
    
    if (completedSessions.length === 0) {
      return 180 // 默认3分钟
    }

    const totalTime = completedSessions.reduce((sum, session) => {
      const duration = session.leaveTime!.getTime() - session.joinTime.getTime()
      return sum + duration
    }, 0)

    return Math.ceil(totalTime / completedSessions.length / 1000) // 转换为秒
  }

  // 设置维护模式
  setMaintenanceMode(enabled: boolean) {
    this.config.isMaintenanceMode = enabled
  }

  // 更新配置
  updateConfig(newConfig: Partial<ActivityConfig>) {
    this.config = { ...this.config, ...newConfig }
  }
}

// 单例实例
export const trafficService = new TrafficService()

// 导出类型
export type { UserSession, ActivityConfig }