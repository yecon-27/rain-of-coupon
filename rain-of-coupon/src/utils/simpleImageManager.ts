/**
 * 简化的图片管理器 - 专门解决当前的图片显示问题
 */

// 直接使用Vite的静态资源导入
import zbhImage from '@/assets/images/zbh.svg'
import countdown3 from '@/assets/images/3.svg'
import countdown2 from '@/assets/images/2.svg'
import countdown1 from '@/assets/images/1.svg'
import placeholder from '@/assets/images/placeholder.svg'

// 本地图片映射
const LOCAL_IMAGES: Record<string, string> = {
  // 倒计时相关
  'redpacket_rain': zbhImage,
  'countdown_3': countdown3,
  'countdown_2': countdown2,
  'countdown_1': countdown1,
  
  // 主页相关
  'home_bg': '/src/assets/coupon/home.png',
  'challenge_btn': '/src/assets/coupon/button.png',
  'rule_btn': '/src/assets/coupon/gz.png',
  'coupon_btn': '/src/assets/coupon/qb.png',
  'show_dishes': '/src/assets/coupon/zscp.png',
  
  // 游戏相关
  'timer_btn': '/src/assets/coupon/ds.png',
  'packet_count': '/src/assets/coupon/sl.png',
  'red_packet_bag': '/src/assets/coupon/luckyBag.png',
  
  // 优惠券相关
  'challenge_get': '/src/assets/coupon/cytzhq.png',
  'coupon_188': '/src/assets/coupon/满500元且消费一道特色菜可使用.png',
  'coupon_588': '/src/assets/coupon/满1500元且消费一道特色菜可使用.png',
  'coupon_888': '/src/assets/coupon/满2500元且消费一道特色菜可使用.png',
  
  // 提示相关
  'luck_plus_btn': '/src/assets/coupon/福气+1.png',
  'crowding_tip': '/src/assets/coupon/活动拥挤.png',
  'loading_gif': '/src/assets/coupon/加载.gif',
  
  // 奖品相关
  'prize_188': '/src/assets/coupon/188.png',
  'prize_588': '/src/assets/coupon/588.png',
  'prize_888': '/src/assets/coupon/888.png',
  
  // 默认占位符
  'default_placeholder': placeholder
}

class SimpleImageManager {
  private cache = new Map<string, string>()
  private useLocalOnly = false

  /**
   * 获取图片URL
   */
  async getImageUrl(resourceKey: string): Promise<string> {
    // 检查缓存
    if (this.cache.has(resourceKey)) {
      return this.cache.get(resourceKey)!
    }

    let imageUrl: string

    if (this.useLocalOnly) {
      // 直接使用本地图片
      imageUrl = this.getLocalImage(resourceKey)
    } else {
      // 尝试从数据库获取，失败则使用本地图片
      try {
        imageUrl = await this.fetchFromDatabase(resourceKey)
      } catch (error) {
        console.warn(`数据库图片获取失败，使用本地图片: ${resourceKey}`, error)
        imageUrl = this.getLocalImage(resourceKey)
      }
    }

    // 缓存结果
    this.cache.set(resourceKey, imageUrl)
    return imageUrl
  }

  /**
   * 从数据库获取图片
   */
  private async fetchFromDatabase(resourceKey: string): Promise<string> {
    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    
    const response = await fetch(`${API_BASE_URL}/public/redpacket/image/resource/${resourceKey}`)
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }
    
    const data = await response.json()
    
    if (data.code !== 200 || !data.data?.filePath) {
      throw new Error(`API Error: ${data.msg}`)
    }
    
    // 构建完整URL
    const filePath = data.data.filePath
    if (filePath.startsWith('http')) {
      return filePath
    }
    
    return filePath.startsWith('/') ? `${API_BASE_URL}${filePath}` : `${API_BASE_URL}/${filePath}`
  }

  /**
   * 获取本地图片
   */
  private getLocalImage(resourceKey: string): string {
    return LOCAL_IMAGES[resourceKey] || LOCAL_IMAGES['default_placeholder']
  }

  /**
   * 启用仅本地模式
   */
  enableLocalOnly(): void {
    this.useLocalOnly = true
    this.cache.clear()
  }

  /**
   * 启用数据库模式
   */
  enableDatabase(): void {
    this.useLocalOnly = false
    this.cache.clear()
  }

  /**
   * 清除缓存
   */
  clearCache(): void {
    this.cache.clear()
  }

  /**
   * 批量预加载
   */
  async preloadImages(keys: string[]): Promise<void> {
    const promises = keys.map(key => this.getImageUrl(key))
    await Promise.allSettled(promises)
  }
}

export const simpleImageManager = new SimpleImageManager()