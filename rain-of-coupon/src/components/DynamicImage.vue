<template>
  <img 
    :src="currentImageUrl" 
    :alt="alt"
    :class="className"
    @error="handleImageError"
    @load="handleImageLoad"
    v-bind="$attrs"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getImageByKey } from '@/api/imageResource'

interface Props {
  resourceKey: string
  fallbackUrl: string
  alt?: string
  className?: string
}

const props = withDefaults(defineProps<Props>(), {
  alt: '',
  className: ''
})

const currentImageUrl = ref(props.fallbackUrl)
const isLoading = ref(false)
const hasError = ref(false)

/**
 * 从数据库获取图片URL
 */
const fetchImageFromDatabase = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  hasError.value = false
  
  try {
    console.log(`🔍 [DynamicImage] 尝试从数据库获取图片: ${props.resourceKey}`)
    const response = await getImageByKey(props.resourceKey)
    
    if (response.code === 200 && response.data?.filePath) {
      const imageUrl = buildFullUrl(response.data.filePath)
      console.log(`✅ [DynamicImage] 数据库图片获取成功: ${props.resourceKey} -> ${imageUrl}`)
      currentImageUrl.value = imageUrl
    } else {
      console.warn(`⚠️ [DynamicImage] 数据库图片获取失败: ${props.resourceKey}`)
      console.warn(`   响应码: ${response.code}`)
      console.warn(`   响应消息: ${response.msg}`)
      console.warn(`   响应数据:`, response.data)
      console.warn(`   使用降级图片: ${props.fallbackUrl}`)
      useFallback()
    }
  } catch (error: any) {
    console.error(`❌ [DynamicImage] 数据库请求异常: ${props.resourceKey}`)
    console.error(`   错误类型: ${error.name}`)
    console.error(`   错误消息: ${error.message}`)
    console.error(`   完整错误:`, error)
    
    // 检查是否是认证错误
    if (error.message && error.message.includes('认证失败')) {
      console.warn(`🔐 [DynamicImage] 认证失败，后端服务可能未启动`)
    } else if (error.name === 'TypeError' && error.message.includes('fetch')) {
      console.warn(`🌐 [DynamicImage] 网络请求失败，后端服务可能未启动`)
    }
    
    console.log(`📁 [DynamicImage] 使用降级图片: ${props.fallbackUrl}`)
    useFallback()
  } finally {
    isLoading.value = false
  }
}

/**
 * 构建完整的图片URL
 */
const buildFullUrl = (filePath: string): string => {
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
 * 使用降级图片
 */
const useFallback = () => {
  console.log(`📁 [DynamicImage] 使用降级图片: ${props.resourceKey} -> ${props.fallbackUrl}`)
  currentImageUrl.value = props.fallbackUrl
  hasError.value = true
}

/**
 * 处理图片加载错误
 */
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.error(`❌ [DynamicImage] 图片加载失败: ${img.src}`)
  
  // 如果当前显示的不是降级图片，则切换到降级图片
  if (currentImageUrl.value !== props.fallbackUrl) {
    console.log(`🔄 [DynamicImage] 切换到降级图片: ${props.fallbackUrl}`)
    useFallback()
  }
}

/**
 * 处理图片加载成功
 */
const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log(`✅ [DynamicImage] 图片加载成功: ${img.src}`)
}

// 监听resourceKey变化
watch(() => props.resourceKey, () => {
  fetchImageFromDatabase()
})

// 组件挂载时尝试从数据库获取图片
onMounted(() => {
  fetchImageFromDatabase()
})
</script>

<style scoped>
/* 可以添加加载状态的样式 */
img {
  transition: opacity 0.3s ease;
}
</style>