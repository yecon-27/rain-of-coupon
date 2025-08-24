<template>
  <img 
    :src="imageUrl" 
    :alt="alt"
    :class="imageClass"
    @load="handleLoad"
    @error="handleError"
    v-bind="$attrs"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { imageManager } from '@/utils/imageManager'

interface Props {
  resourceKey: string
  alt: string
  imageClass?: string
  fallbackKey?: string
}

const props = withDefaults(defineProps<Props>(), {
  imageClass: '',
  fallbackKey: ''
})

const imageUrl = ref('')
const isLoading = ref(true)
const hasError = ref(false)

const loadImage = async (key: string) => {
  try {
    isLoading.value = true
    hasError.value = false
    imageUrl.value = await imageManager.getImageUrl(key)
  } catch (error) {
    console.error('加载图片失败:', error)
    hasError.value = true
    
    // 尝试加载备用图片
    if (props.fallbackKey && key !== props.fallbackKey) {
      await loadImage(props.fallbackKey)
    }
  } finally {
    isLoading.value = false
  }
}

const handleLoad = () => {
  isLoading.value = false
  hasError.value = false
}

const handleError = async () => {
  hasError.value = true
  
  // 尝试加载备用图片
  if (props.fallbackKey && props.resourceKey !== props.fallbackKey) {
    await loadImage(props.fallbackKey)
  }
}

// 监听resourceKey变化
watch(() => props.resourceKey, (newKey) => {
  if (newKey) {
    loadImage(newKey)
  }
}, { immediate: true })

onMounted(() => {
  if (props.resourceKey) {
    loadImage(props.resourceKey)
  }
})
</script>