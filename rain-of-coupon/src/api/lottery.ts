// 红包雨API接口封装 (TypeScript版本)

interface DrawLotteryRequest {
  clickedCount?: number
  sessionId?: string | null
}

interface DrawLotteryResponse {
  code: number
  msg: string
  data: {
    id: number
    userId: number
    participationTime: string
    ipAddress: string
    isWin: number
    prizeId: number | null
    prizeName: string | null
    isUsed: number
  }
}

interface UserRecordsQuery {
  pageNum?: number
  pageSize?: number
  isWin?: number | null
  isUsed?: number | null
  startDate?: string | null
  endDate?: string | null
}

interface UserRecordsResponse {
  code: number
  msg: string
  rows: Array<{
    id: number
    userId: number
    participationTime: string
    ipAddress: string
    isWin: number
    prizeId: number | null
    prizeName: string | null
    isUsed: number
  }>
  total: number
}

interface DrawCountResponse {
  remainingCount: number
  todayDrawCount: number
  maxDrawsPerDay: number
  totalDrawCount: number
  todayWinCount: number
  totalWinCount: number
  hasEverWon: boolean
}

interface UserStatusResponse {
  canDraw: boolean
  hasEverWon: boolean
  isCrowded: boolean
  remainingCount: number
  todayParticipations: Array<{
    id: number
    participationTime: string
    isWin: number
    clickedCount: number
    winProbability: number
  }>
  winRecords: Array<{
    id: number
    participationTime: string
    prizeName: string
    isUsed: number
    clickedCount: number
  }>
}

import { getToken } from '@/utils/auth'

// HTTP请求配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

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
    console.error('API Request Error:', error)
    throw error
  }
}

// 参与红包雨（领取优惠券）
// 后端写入 redpacket_user_participation_log 表
export const drawLottery = async (data: DrawLotteryRequest = {}): Promise<DrawLotteryResponse> => {
  return request('/redpacket/lottery/draw', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'repeatSubmit': 'false'
    },
    body: JSON.stringify({
      clickedCount: data.clickedCount || 1,
      sessionId: data.sessionId || null,
      ...data
    })
  }) as Promise<DrawLotteryResponse>
}

// 获取用户参与记录
export const getUserRecords = async (query: UserRecordsQuery = {}): Promise<UserRecordsResponse> => {
  const params = new URLSearchParams({
    pageNum: String(query.pageNum || 1),
    pageSize: String(query.pageSize || 10),
    ...(query.isWin !== null && { isWin: String(query.isWin) }),
    ...(query.isUsed !== null && { isUsed: String(query.isUsed) }),
    ...(query.startDate && { startDate: query.startDate }),
    ...(query.endDate && { endDate: query.endDate })
  })

  return request(`/redpacket/lottery/records?${params}`) as Promise<UserRecordsResponse>
}

// 获取用户参与次数信息
export const getDrawCount = async (): Promise<DrawCountResponse> => {
  return request('/redpacket/lottery/count') as Promise<DrawCountResponse>
}

// 获取优惠券列表
export const getPrizes = async () => {
  return request('/redpacket/lottery/prizes')
}

// 获取用户状态
export const getUserStatus = async (): Promise<UserStatusResponse> => {
  return request('/redpacket/lottery/status') as Promise<UserStatusResponse>
}

// 获取活动配置
export const getActivityConfig = async () => {
  return request('/redpacket/lottery/config')
}

// 获取中奖公告列表
export const getWinningAnnouncements = async (query: UserRecordsQuery = {}) => {
  const params = new URLSearchParams()
  params.append('pageNum', String(query.pageNum || 1))
  params.append('pageSize', String(query.pageSize || 20))
  
  return request(`/redpacket/lottery/announcements?${params}`)
}

// 使用优惠券
export const claimPrize = async (participationId: number) => {
  return request(`/redpacket/lottery/claim/${participationId}`, {
    method: 'POST',
    headers: {
      'repeatSubmit': 'false'
    }
  })
}

// 获取用户今日参与统计
export const getTodayStats = async () => {
  return request('/redpacket/lottery/today-stats')
}

// 检查活动是否进行中
export const checkActivityStatus = async () => {
  return request('/redpacket/lottery/activity-status')
}

// 用户登录
export const login = async (username: string, password: string) => {
  return request('/login', {
    method: 'POST',
    body: JSON.stringify({
      username,
      password
    })
  })
}


// 用户登出
export const logout = async () => {
  return request('/logout', {
    method: 'POST'
  })
}