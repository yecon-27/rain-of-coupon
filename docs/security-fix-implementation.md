# 图片资源安全修复实施方案

## 🔧 具体修复实现

### 1. 后端：创建公开图片资源API

#### 新增Controller
```java
// 文件：ruoyi-admin/src/main/java/com/ruoyi/redpacket/controller/PublicImageResourceController.java
package com.ruoyi.redpacket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.redpacket.domain.RedpacketImageResource;
import com.ruoyi.redpacket.service.IRedpacketImageResourceService;
import java.util.List;

/**
 * 公开图片资源Controller（无需认证）
 */
@RestController
@RequestMapping("/public/redpacket/image")
public class PublicImageResourceController extends BaseController {
    
    @Autowired
    private IRedpacketImageResourceService imageResourceService;
    
    /**
     * 根据资源标识获取图片信息
     */
    @GetMapping("/resource/{resourceKey}")
    public AjaxResult getImageByKey(@PathVariable String resourceKey) {
        RedpacketImageResource query = new RedpacketImageResource();
        query.setResourceKey(resourceKey);
        
        List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(query);
        if (list.isEmpty()) {
            return error("图片资源不存在: " + resourceKey);
        }
        
        return success(list.get(0));
    }
    
    /**
     * 根据使用场景获取图片列表
     */
    @GetMapping("/scene/{scene}")
    public AjaxResult getImagesByScene(@PathVariable String scene) {
        RedpacketImageResource query = new RedpacketImageResource();
        query.setUsageScene(scene);
        
        List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(query);
        return success(list);
    }
    
    /**
     * 获取所有可用的图片资源
     */
    @GetMapping("/all")
    public AjaxResult getAllImages() {
        List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(new RedpacketImageResource());
        return success(list);
    }
}
```

#### 修改Service接口
```java
// 文件：ruoyi-admin/src/main/java/com/ruoyi/redpacket/service/IRedpacketImageResourceService.java
// 新增方法
public interface IRedpacketImageResourceService {
    // ... 现有方法 ...
    
    /**
     * 根据资源标识查询图片资源
     */
    public RedpacketImageResource selectByResourceKey(String resourceKey);
    
    /**
     * 根据使用场景查询图片资源列表
     */
    public List<RedpacketImageResource> selectByUsageScene(String usageScene);
}
```

### 2. 前端：创建图片资源API模块

#### 新增API文件
```typescript
// 文件：rain-of-coupon/src/api/imageResource.ts
import { request } from '@/utils/request'

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
  return request(`/public/redpacket/image/resource/${resourceKey}`)
}

/**
 * 根据使用场景获取图片列表
 */
export const getImagesByScene = async (scene: string): Promise<ImageResourceListResponse> => {
  return request(`/public/redpacket/image/scene/${scene}`)
}

/**
 * 获取所有图片资源
 */
export const getAllImages = async (): Promise<ImageResourceListResponse> => {
  return request('/public/redpacket/image/all')
}
```

#### 创建图片资源管理器
```typescript
// 文件：rain-of-coupon/src/utils/imageManager.ts
import { getImageByKey, getImagesByScene, type ImageResource } from '@/api/imageResource'

class ImageResourceManager {
  private cache = new Map<string, string>()
  private loading = new Set<string>()
  
  /**
   * 获取图片URL（带缓存）
   */
  async getImageUrl(resourceKey: string): Promise<string> {
    // 检查缓存
    if (this.cache.has(resourceKey)) {
      return this.cache.get(resourceKey)!
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
      const response = await getImageByKey(resourceKey)
      if (response.code === 200) {
        const imageUrl = this.buildFullUrl(response.data.filePath)
        this.cache.set(resourceKey, imageUrl)
        return imageUrl
      } else {
        console.error('获取图片资源失败:', response.msg)
        return this.getDefaultImage()
      }
    } catch (error) {
      console.error('获取图片资源异常:', error)
      return this.getDefaultImage()
    } finally {
      this.loading.delete(resourceKey)
    }
  }
  
  /**
   * 批量预加载图片
   */
  async preloadImages(resourceKeys: string[]): Promise<void> {
    const promises = resourceKeys.map(key => this.getImageUrl(key))
    await Promise.allSettled(promises)
  }
  
  /**
   * 根据场景预加载图片
   */
  async preloadByScene(scene: string): Promise<void> {
    try {
      const response = await getImagesByScene(scene)
      if (response.code === 200) {
        const resourceKeys = response.data.map(item => item.resourceKey)
        await this.preloadImages(resourceKeys)
      }
    } catch (error) {
      console.error('预加载场景图片失败:', error)
    }
  }
  
  /**
   * 构建完整URL
   */
  private buildFullUrl(filePath: string): string {
    const isDev = import.meta.env.DEV
    const baseUrl = isDev 
      ? `http://${window.location.hostname}:8080` 
      : 'https://your-production-domain.com'
    
    return filePath.startsWith('/') ? `${baseUrl}${filePath}` : filePath
  }
  
  /**
   * 获取默认图片
   */
  private getDefaultImage(): string {
    return '/image/default/placeholder.png'
  }
  
  /**
   * 清除缓存
   */
  clearCache(): void {
    this.cache.clear()
  }
}

export const imageManager = new ImageResourceManager()
```

### 3. 创建安全图片组件

#### 通用图片组件
```vue
<!-- 文件：rain-of-coupon/src/components/SecureImage.vue -->
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
```

### 4. 修复现有组件

#### ActivitySection.vue 修复示例
```vue
<!-- 修复后的 ActivitySection.vue -->
<template>
  <div class="activity-section">
    <SecureImage 
      resource-key="home_background" 
      alt="首页背景" 
      image-class="activity-bg"
    />

    <div v-if="authStore.isLoggedIn" class="login-status">
      <span>{{ authStore.user?.nickName || '用户' }}</span>
    </div>

    <SecureImage 
      resource-key="rules_button" 
      alt="规则" 
      image-class="rule-btn" 
      @click="$emit('showRules')"
    />
    
    <SecureImage 
      resource-key="coupon_button" 
      alt="券包" 
      image-class="coupon-btn" 
      @click="$emit('myCoupons')"
    />

    <div class="center-button">
      <SecureImage 
        resource-key="challenge_button" 
        alt="立即挑战" 
        image-class="challenge-btn" 
        @click="handleJoinActivity"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import SecureImage from './SecureImage.vue'
import { useAuthStore } from '@/stores/auth'
import { imageManager } from '@/utils/imageManager'

const authStore = useAuthStore()

// 预加载主页相关图片
onMounted(async () => {
  await imageManager.preloadByScene('主页')
})

const handleJoinActivity = () => {
  // 处理加入活动逻辑
}
</script>
```

### 5. 数据库资源标识完善

#### 完整的资源数据
```sql
-- 文件：sql/update-image-resources.sql
-- 清空现有数据并插入完整的图片资源信息

TRUNCATE TABLE redpacket_image_resource;

INSERT INTO redpacket_image_resource (resource_name, resource_key, file_name, file_path, usage_scene, description) VALUES
-- 主页相关
('主页背景', 'home_background', 'home.png', '/image/coupon/home.png', '主页', '主页背景图'),
('挑战按钮', 'challenge_button', 'button.png', '/image/coupon/button.png', '主页', '立即挑战按钮'),
('规则按钮', 'rules_button', 'gz.png', '/image/coupon/gz.png', '主页', '规则按钮'),
('券包按钮', 'coupon_button', 'qb.png', '/image/coupon/qb.png', '主页', '券包按钮'),

-- 游戏相关
('红包图片', 'red_packet', 'luckyBag.png', '/image/coupon/luckyBag.png', '游戏', '红包雨红包图片'),
('倒计时背景', 'countdown_timer', 'ds.png', '/image/coupon/ds.png', '游戏', '倒计时背景'),
('数量显示', 'packet_count', 'sl.png', '/image/coupon/sl.png', '游戏', '红包数量显示'),
('准备图片', 'prepare_image', 'zbh.png', '/image/coupon/zbh.png', '游戏', '准备阶段图片'),
('倒计时1', 'countdown_1', '1.png', '/image/coupon/1.png', '游戏', '倒计时数字1'),
('倒计时2', 'countdown_2', '2.png', '/image/coupon/2.png', '游戏', '倒计时数字2'),
('倒计时3', 'countdown_3', '3.png', '/image/coupon/3.png', '游戏', '倒计时数字3'),

-- 优惠券相关
('188元券', 'coupon_188', '满500元且消费一道特色菜可使用.png', '/image/coupon/满500元且消费一道特色菜可使用.png', '优惠券', '188元优惠券'),
('588元券', 'coupon_588', '满1500元且消费一道特色菜可使用.png', '/image/coupon/满1500元且消费一道特色菜可使用.png', '优惠券', '588元优惠券'),
('888元券', 'coupon_888', '满2500元且消费一道特色菜可使用.png', '/image/coupon/满2500元且消费一道特色菜可使用.png', '优惠券', '888元优惠券'),
('参与挑战券', 'participate_coupon', 'cytzhq.png', '/image/coupon/cytzhq.png', '优惠券', '参与挑战获取券'),

-- 提示相关
('福气+1', 'luck_plus', '福气+1.png', '/image/coupon/福气+1.png', '提示', '福气+1按钮'),
('活动拥挤', 'crowding_tip', '活动拥挤.png', '/image/coupon/活动拥挤.png', '提示', '活动拥挤提示'),
('加载动画', 'loading_gif', '加载.gif', '/image/coupon/加载.gif', '提示', '加载动画'),

-- 美食相关
('展示菜品', 'food_display', 'zscp.png', '/image/coupon/zscp.png', '美食', '展示菜品图片'),

-- 默认图片
('默认占位图', 'default_placeholder', 'placeholder.png', '/image/default/placeholder.png', '默认', '默认占位图片');

COMMIT;
```

## 📋 实施检查清单

### 后端修改
- [ ] 创建 `PublicImageResourceController.java`
- [ ] 修改 `IRedpacketImageResourceService.java` 接口
- [ ] 实现新增的Service方法
- [ ] 执行数据库更新脚本

### 前端修改
- [ ] 创建 `imageResource.ts` API文件
- [ ] 创建 `imageManager.ts` 工具类
- [ ] 创建 `SecureImage.vue` 组件
- [ ] 修复所有使用硬编码路径的组件
- [ ] 更新 `api.ts` 配置文件

### 测试验证
- [ ] 测试图片资源API接口
- [ ] 验证图片缓存机制
- [ ] 测试图片加载失败降级
- [ ] 验证所有页面图片正常显示

修复完成后，系统将实现真正的动态图片资源管理，消除安全隐患。