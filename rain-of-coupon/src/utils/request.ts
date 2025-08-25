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
    
    // 尝试解析响应数据，即使状态码不是200也要尝试获取错误信息
    let data;
    try {
      data = await response.json()
    } catch (parseError) {
      console.error('Response parse failed:', parseError)
      data = { code: response.status, msg: response.statusText, data: null }
    }
    
    // 如果HTTP状态码不是200，创建包含状态码的错误
    if (!response.ok) {
      const error = new Error(`HTTP error! status: ${response.status}`) as any
      error.status = response.status
      error.statusText = response.statusText
      error.response = data
      throw error
    }
    
    return data
  } catch (error: any) {
    console.error('Request failed:', error)
    
    // 确保错误对象包含状态码信息
    if (error.status) {
      error.message = `${error.message} (Status: ${error.status})`
    }
    
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