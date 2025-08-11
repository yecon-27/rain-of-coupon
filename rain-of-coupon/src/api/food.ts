// 美食列表API接口封装
import { API_CONFIG } from '@/config/api'

// 美食项接口定义
export interface FoodItem {
  id: number
  foodName: string
  ranking: number
}

// API响应接口
interface FoodListResponse {
  code: number
  msg: string
  rows: FoodItem[]
  total: number
}

// 通用请求函数
const request = async (url: string, options: RequestInit = {}) => {
  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    }
  }

  try {
    const response = await fetch(`${API_CONFIG.baseURL}${url}`, config)
    
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

// 获取TOP10网络人气特色美食列表
export const getTop10PopularFood = async (): Promise<FoodListResponse> => {
  return request('/redpacket/top10Food/list')
}

// 获取"一镇一品"特色菜列表
export const getTownSpecialtyFood = async (): Promise<FoodListResponse> => {
  return request('/redpacket/townFood/list')
}