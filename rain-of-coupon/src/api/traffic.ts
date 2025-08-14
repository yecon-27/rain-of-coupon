// 流量检测和限流API接口封装
import { getToken } from '@/utils/auth'

// HTTP请求配置 - 使用若依后端的API地址
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 流量检测响应接口
export interface TrafficCheckResponse {
  code: number
  msg: string
  data: {
    status: 'ok' | 'crowded' | 'maintenance'
    currentUsers: number
    maxUsers: number
    queuePosition?: number
    estimatedWaitTime?: number
    retryAfter?: number
  }
}

// 用户活动状态接口
export interface UserActivityRequest {
  action: 'join' | 'leave' | 'heartbeat'
  userId?: string
  sessionId?: string
}

export interface UserActivityResponse {
  code: number
  msg: string
  data: {
    success: boolean
    currentUsers: number
    userStatus: 'active' | 'queued' | 'blocked'
  }
}

// 通用请求函数
const request = async (url: string, options: RequestInit = {}) => {
  const token = getToken()
  
  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    }
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Traffic API Request Error:', error)
    throw error
  }
}

// 检查流量状态
export const checkTrafficStatus = async (): Promise<TrafficCheckResponse> => {
  return request('/api/traffic/check', {
    method: 'GET'
  }) as Promise<TrafficCheckResponse>
}

// 用户加入活动队列
export const joinActivity = async (data: UserActivityRequest): Promise<UserActivityResponse> => {
  return request('/api/traffic/join', {
    method: 'POST',
    body: JSON.stringify(data)
  }) as Promise<UserActivityResponse>
}

// 用户离开活动
export const leaveActivity = async (data: UserActivityRequest): Promise<UserActivityResponse> => {
  return request('/api/traffic/leave', {
    method: 'POST',
    body: JSON.stringify(data)
  }) as Promise<UserActivityResponse>
}

// 发送心跳保持活跃状态
export const sendHeartbeat = async (data: UserActivityRequest): Promise<UserActivityResponse> => {
  return request('/api/traffic/heartbeat', {
    method: 'POST',
    body: JSON.stringify(data)
  }) as Promise<UserActivityResponse>
}

// 获取当前活动统计
export const getActivityStats = async () => {
  return request('/api/traffic/stats', {
    method: 'GET'
  })
}