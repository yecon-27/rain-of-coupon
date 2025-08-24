/**
 * API测试工具
 */
import { getImageByKey } from '@/api/imageResource'
import { getToken } from '@/utils/auth'

export const testImageAPI = async () => {
  console.log('🧪 [API测试] 开始测试图片API...')
  
  const testKeys = ['home_bg', 'challenge_btn', 'rule_btn', 'coupon_btn']
  
  for (const key of testKeys) {
    try {
      console.log(`🔍 [API测试] 测试资源键: ${key}`)
      const response = await getImageByKey(key)
      console.log(`✅ [API测试] ${key} 响应:`, response)
    } catch (error) {
      console.error(`❌ [API测试] ${key} 失败:`, error)
    }
  }
  
  console.log('🧪 [API测试] 测试完成')
}

// 测试基础网络连接
export const testNetworkConnection = async () => {
  console.log('🌐 [网络测试] 开始测试后端连接...')
  
  const baseUrl = import.meta.env.DEV 
    ? `http://${window.location.hostname}:8080` 
    : (import.meta.env.VITE_API_BASE_URL || 'https://your-production-domain.com')
  
  try {
    const response = await fetch(`${baseUrl}/public/redpacket/image/resource/home_bg`)
    console.log(`🌐 [网络测试] 响应状态: ${response.status}`)
    console.log(`🌐 [网络测试] 响应头:`, Object.fromEntries(response.headers.entries()))
    
    if (response.ok) {
      const data = await response.json()
      console.log(`✅ [网络测试] 响应数据:`, data)
    } else {
      console.warn(`⚠️ [网络测试] HTTP错误: ${response.status} ${response.statusText}`)
    }
  } catch (error) {
    console.error(`❌ [网络测试] 网络连接失败:`, error)
  }
}