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
    isWin: boolean
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
  code: number
  msg: string
  data: {
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
}

import { getToken } from '@/utils/auth'

// HTTP请求配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 通用请求函数
const request = async (url: string, options: RequestInit = {}) => {
  const token = getToken();
  
  console.log('🔗 [Request] 开始HTTP请求');
  console.log('🔗 [Request] URL:', `${API_BASE_URL}${url}`);
  console.log('🔗 [Request] Token存在:', !!token);
  console.log('🔗 [Request] Token值:', token ? `${token.substring(0, 20)}...` : 'null');
  
  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    }
  };
  
  console.log('🔗 [Request] 请求配置:', {
    method: config.method || 'GET',
    headers: config.headers,
    body: config.body ? 'Present' : 'None'
  });

  try {
    console.log('🔗 [Request] 发送请求...');
    const response = await fetch(`${API_BASE_URL}${url}`, config);
    
    console.log('🔗 [Request] 收到响应');
    console.log('🔗 [Request] 响应状态:', response.status, response.statusText);
    console.log('🔗 [Request] 响应头:', Object.fromEntries(response.headers.entries()));
    
    // 尝试解析响应数据，即使状态码不是200也要尝试获取错误信息
    let data;
    try {
      data = await response.json();
      console.log('✅ [Request] 响应数据:', data);
    } catch (parseError) {
      console.error('❌ [Request] 响应解析失败:', parseError);
      data = { code: response.status, msg: response.statusText };
    }
    
    // 如果HTTP状态码不是200，创建包含状态码的错误
    if (!response.ok) {
      console.error('❌ [Request] HTTP错误:', response.status, response.statusText);
      const error = new Error(`HTTP error! status: ${response.status}`) as any;
      error.status = response.status;
      error.statusText = response.statusText;
      error.response = data;
      throw error;
    }
    
    return data;
  } catch (error: any) {
    console.error('❌ [Request] 请求失败:', error);
    console.error('❌ [Request] 错误类型:', error.constructor.name);
    console.error('❌ [Request] 错误消息:', error.message);
    console.error('❌ [Request] 错误状态码:', error.status);
    
    // 确保错误对象包含状态码信息
    if (error.status) {
      error.message = `${error.message} (Status: ${error.status})`;
    }
    
    throw error;
  }
}

// 参与红包雨（领取优惠券）
// 后端写入 redpacket_user_participation_log 表
export const drawLottery = async (data: DrawLotteryRequest = {}): Promise<DrawLotteryResponse> => {
  console.log('🚀 [API] drawLottery 开始调用');
  console.log('🚀 [API] 请求数据:', data);
  
  const requestBody = {
    clickedCount: data.clickedCount || 1,
    sessionId: data.sessionId || null,
    ...data
  };
  
  console.log('🚀 [API] 最终请求体:', requestBody);
  console.log('🚀 [API] 请求URL:', '/redpacket/lottery/draw');
  
  try {
    const result = await request('/redpacket/lottery/draw', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'repeatSubmit': 'false'
      },
      body: JSON.stringify(requestBody)
    }) as Promise<DrawLotteryResponse>;
    
    console.log('✅ [API] drawLottery 调用成功');
    console.log('✅ [API] 返回结果:', result);
    
    return result;
  } catch (error) {
    console.error('❌ [API] drawLottery 调用失败:', error);
    throw error;
  }
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
// 获取用户状态
export const getUserStatus = async (data: { sessionId?: string | null } = {}): Promise<UserStatusResponse> => {
  const params = new URLSearchParams({
    ...(data.sessionId && { sessionId: data.sessionId })
  })
  return request(`/redpacket/lottery/status?${params}`) as Promise<UserStatusResponse>
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


// 检查奖品库存
export const checkPrizeStock = async (): Promise<{
  code: number
  data: {
    hasStock: boolean
    prizes: Array<{
      id: number
      prizeName: string
      totalCount: number
      remainingCount: number
    }>
  }
  msg: string
}> => {
  return request('/redpacket/lottery/stock')
}

// 获取当前活跃轮次信息
export const getCurrentActiveRound = async (): Promise<{
  code: number
  data: {
    id: number
    roundNumber: number
    isRecycleRound: boolean
    startTime: string
    endTime: string
    isActive: boolean
    message?: string
  }
  msg: string
}> => {
  return request('/redpacket/lottery/current-round')
}