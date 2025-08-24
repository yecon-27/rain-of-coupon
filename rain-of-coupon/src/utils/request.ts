/**
 * 简单的请求工具函数
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 通用请求函数
 */
export const request = async <T = any>(url: string, options?: RequestInit): Promise<ApiResponse<T>> => {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`
  
  const defaultOptions: RequestInit = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  }

  try {
    const response = await fetch(fullUrl, defaultOptions)
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Request failed:', error)
    throw error
  }
}

/**
 * GET 请求
 */
export const get = <T = any>(url: string): Promise<ApiResponse<T>> => {
  return request<T>(url, { method: 'GET' })
}

/**
 * POST 请求
 */
export const post = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request<T>(url, {
    method: 'POST',
    body: data ? JSON.stringify(data) : undefined,
  })
}

/**
 * PUT 请求
 */
export const put = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request<T>(url, {
    method: 'PUT',
    body: data ? JSON.stringify(data) : undefined,
  })
}

/**
 * DELETE 请求
 */
export const del = <T = any>(url: string): Promise<ApiResponse<T>> => {
  return request<T>(url, { method: 'DELETE' })
}