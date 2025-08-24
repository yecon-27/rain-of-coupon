import { getImageByKey, getImagesByScene, type ImageResource } from '@/api/imageResource'

// 本地图片映射表（降级方案）
const LOCAL_IMAGE_MAP: Record<string, string> = {
  'prepare_image': '/src/assets/images/zbh.svg',
  'countdown_3': '/src/assets/images/3.svg',
  'countdown_2': '/src/assets/images/2.svg', 
  'countdown_1': '/src/assets/images/1.svg',
  'participate_coupon': '/src/assets/images/placeholder.svg',
  'prize_image': '/src/assets/images/placeholder.svg',
  'crowding_image': '/src/assets/images/placeholder.svg',
  'encourage_image': '/src/assets/images/placeholder.svg',
  'food_display': '/src/assets/images/placeholder.svg',
  'loading_gif': '/src/assets/images/placeholder.svg',
  'background_image': '/src/assets/images/placeholder.svg',
  'default_placeholder': '/src/assets/images/placeholder.svg'
}

class ImageResourceManager {
  private cache = new Map<string, string>()
  private loading = new Set<string>()
  private useLocalFallback = false
  
  /**
   * 获取图片URL（带缓存和降级）
   */
  async getImageUrl(resourceKey: string): Promise<string> {
    // 检查缓存
    if (this.cache.has(resourceKey)) {
      return this.cache.get(resourceKey)!
    }
    
    // 如果已经启用本地降级，直接返回本地图片
    if (this.useLocalFallback) {
      return this.getLocalImage(resourceKey)
    }
    
    // 防止重复请求
    if (this.loading.has(resourceKey)) {
      return new Promise((resolve) => {
        const checkCache = () => {
          if (this.cache.has(resourceKey)) {
            resolve(this.cache.get(resourceKey)!)
          } else {
            setTimeout(checkCache, 100)
          }
        }
        checkCache()
      })
    }
    
    this.loading.add(resourceKey)
    
    try {
      console.log(`🔍 [ImageManager] 尝试从数据库获取图片: ${resourceKey}`)
      const response = await getImageByKey(resourceKey)
      
      if (response.code === 200 && response.data?.filePath) {
        const imageUrl = this.buildFullUrl(response.data.filePath)
        console.log(`✅ [ImageManager] 数据库图片获取成功: ${resourceKey} -> ${imageUrl}`)
        this.cache.set(resourceKey, imageUrl)
        return imageUrl
      } else {
        console.warn(`⚠️ [ImageManager] 数据库图片获取失败: ${resourceKey}, 使用本地降级`)
        return this.getLocalImage(resourceKey)
      }
    } catch (error) {
      console.error(`❌ [ImageManager] 数据库请求异常: ${resourceKey}`, error)
      console.log(`🔄 [ImageManager] 启用本地降级模式`)
      this.useLocalFallback = true
      return this.getLocalImage(resourceKey)
    } finally {
      this.loading.delete(resourceKey)
    }
  }
  
  /**
   * 获取本地图片（降级方案）
   */
  private getLocalImage(resourceKey: string): string {
    const localPath = LOCAL_IMAGE_MAP[resourceKey] || LOCAL_IMAGE_MAP['default_placeholder']
    
    // 在开发环境中，Vite需要特殊处理静态资源
    let finalPath = localPath
    if (import.meta.env.DEV) {
      // 开发环境：直接使用相对路径，Vite会处理
      finalPath = localPath
    } else {
      // 生产环境：使用构建后的路径
      finalPath = localPath.replace('/src/assets/', '/assets/')
    }
    
    console.log(`📁 [ImageManager] 使用本地图片: ${resourceKey} -> ${finalPath}`)
    this.cache.set(resourceKey, finalPath)
    return finalPath
  }
  
  /**
   * 批量预加载图片
   */
  async preloadImages(resourceKeys: string[]): Promise<void> {
    console.log(`🚀 [ImageManager] 开始预加载 ${resourceKeys.length} 张图片`)
    const promises = resourceKeys.map(key => this.getImageUrl(key))
    const results = await Promise.allSettled(promises)
    
    const successful = results.filter(r => r.status === 'fulfilled').length
    console.log(`✅ [ImageManager] 预加载完成: ${successful}/${resourceKeys.length}`)
  }
  
  /**
   * 根据场景预加载图片
   */
  async preloadByScene(scene: string): Promise<void> {
    try {
      console.log(`🎬 [ImageManager] 预加载场景图片: ${scene}`)
      const response = await getImagesByScene(scene)
      if (response.code === 200) {
        const resourceKeys = response.data.map(item => item.resourceKey)
        await this.preloadImages(resourceKeys)
      }
    } catch (error) {
      console.error('预加载场景图片失败:', error)
      console.log('🔄 [ImageManager] 场景预加载失败，启用本地降级')
      this.useLocalFallback = true
    }
  }
  
  /**
   * 构建完整URL
   */
  private buildFullUrl(filePath: string): string {
    // 如果已经是完整URL，直接返回
    if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
      return filePath
    }
    
    // 构建API服务器URL
    const isDev = import.meta.env.DEV
    const baseUrl = isDev 
      ? `http://${window.location.hostname}:8080` 
      : (import.meta.env.VITE_API_BASE_URL || 'https://your-production-domain.com')
    
    return filePath.startsWith('/') ? `${baseUrl}${filePath}` : `${baseUrl}/${filePath}`
  }
  
  /**
   * 强制使用本地图片
   */
  enableLocalFallback(): void {
    console.log('🔄 [ImageManager] 手动启用本地降级模式')
    this.useLocalFallback = true
    this.cache.clear()
  }
  
  /**
   * 重置为数据库模式
   */
  resetToDatabase(): void {
    console.log('🔄 [ImageManager] 重置为数据库模式')
    this.useLocalFallback = false
    this.cache.clear()
  }
  
  /**
   * 清除缓存
   */
  clearCache(): void {
    this.cache.clear()
  }
  
  /**
   * 获取当前状态
   */
  getStatus() {
    return {
      useLocalFallback: this.useLocalFallback,
      cacheSize: this.cache.size,
      loadingCount: this.loading.size
    }
  }
}

export const imageManager = new ImageResourceManager()