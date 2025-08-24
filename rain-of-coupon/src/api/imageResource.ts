import { request } from '@/utils/request'
import { getToken } from '@/utils/auth'

export interface ImageResource {
  id: number
  resourceName: string
  resourceKey: string
  fileName: string
  filePath: string
  usageScene: string
  description: string
}

export interface ImageResourceResponse {
  code: number
  msg: string
  data: ImageResource
}

export interface ImageResourceListResponse {
  code: number
  msg: string
  data: ImageResource[]
}

/**
 * 根据资源标识获取图片信息
 */
export const getImageByKey = async (resourceKey: string): Promise<ImageResourceResponse> => {
  const token = getToken()
  
  return request(`/public/redpacket/image/resource/${resourceKey}`, {
    method: 'GET',
    headers: token ? {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    } : {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 根据使用场景获取图片列表
 */
export const getImagesByScene = async (scene: string): Promise<ImageResourceListResponse> => {
  const token = getToken()
  
  return request(`/public/redpacket/image/scene/${scene}`, {
    method: 'GET',
    headers: token ? {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    } : {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 获取所有图片资源
 */
export const getAllImages = async (): Promise<ImageResourceListResponse> => {
  const token = getToken()
  
  return request('/public/redpacket/image/all', {
    method: 'GET',
    headers: token ? {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    } : {
      'Content-Type': 'application/json'
    }
  })
}