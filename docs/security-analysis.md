# 图片资源安全分析报告

## 🚨 发现的安全问题

### 1. 直接引用静态文件路径

**问题描述**: 前端组件直接使用硬编码的图片路径，而不是从数据库获取图片资源信息。

**风险等级**: 🔴 高风险

**具体问题**:

#### 硬编码路径引用

```typescript
// ❌ 安全问题：直接硬编码图片路径
const imagePath = "/image/coupon/福气+1.png";
const imageUrl = "/image/coupon/满500元且消费一道特色菜可使用.png";
```

#### 配置文件中的固定路径

```typescript
// ❌ 安全问题：配置文件中固定图片目录
imageURL: `http://${window.location.hostname}:8080/image/coupon/`;
```

### 2. 缺少图片资源 API 调用

**问题描述**: 虽然后端有完整的图片资源管理系统（`redpacket_image_resource` 表和对应的 Controller），但前端没有使用这些 API。

**风险分析**:

- 图片路径泄露服务器目录结构
- 无法动态管理图片资源
- 缺少访问权限控制
- 容易被恶意扫描和攻击

## 🛡️ 安全修复方案

### 1. 创建图片资源 API

#### 前端 API 接口

```typescript
// 新增：图片资源API
export interface ImageResourceAPI {
  getImageByKey(resourceKey: string): Promise<ImageResource>;
  getImagesByScene(scene: string): Promise<ImageResource[]>;
}

interface ImageResource {
  id: number;
  resourceName: string;
  resourceKey: string;
  fileName: string;
  filePath: string;
  usageScene: string;
}
```

#### 后端公开接口

```java
// 新增：公开的图片资源获取接口
@RestController
@RequestMapping("/public/redpacket/image")
public class PublicImageResourceController {

    @GetMapping("/resource/{resourceKey}")
    public AjaxResult getImageByKey(@PathVariable String resourceKey) {
        // 根据资源标识获取图片信息
    }

    @GetMapping("/scene/{scene}")
    public AjaxResult getImagesByScene(@PathVariable String scene) {
        // 根据使用场景获取图片列表
    }
}
```

### 2. 修复前端组件

#### 安全的图片获取方式

```typescript
// ✅ 安全修复：从数据库获取图片资源
const getImageUrl = async (resourceKey: string): Promise<string> => {
  try {
    const response = await imageResourceAPI.getImageByKey(resourceKey);
    return response.data.filePath;
  } catch (error) {
    console.error("获取图片资源失败:", error);
    return ""; // 返回默认图片或空字符串
  }
};
```

#### 组件修复示例

```vue
<!-- ✅ 修复后的组件 -->
<template>
  <img :src="imageUrl" :alt="imageAlt" @error="handleImageError" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { getImageByKey } from "@/api/imageResource";

const imageUrl = ref("");
const props = defineProps<{
  resourceKey: string;
  alt: string;
}>();

onMounted(async () => {
  try {
    const resource = await getImageByKey(props.resourceKey);
    imageUrl.value = resource.filePath;
  } catch (error) {
    console.error("加载图片失败:", error);
    // 使用默认图片或显示错误状态
  }
});
</script>
```

### 3. 数据库资源标识映射

#### 建议的资源标识规范

```sql
-- 主页相关图片
INSERT INTO redpacket_image_resource VALUES
(1, '主页背景', 'home_background', 'home.png', '/image/coupon/home.png', '主页', '主页背景图'),
(2, '挑战按钮', 'challenge_button', 'button.png', '/image/coupon/button.png', '主页', '立即挑战按钮'),
(3, '规则按钮', 'rules_button', 'gz.png', '/image/coupon/gz.png', '主页', '规则按钮'),
(4, '券包按钮', 'coupon_button', 'qb.png', '/image/coupon/qb.png', '主页', '券包按钮');

-- 游戏相关图片
INSERT INTO redpacket_image_resource VALUES
(5, '红包图片', 'red_packet', 'luckyBag.png', '/image/coupon/luckyBag.png', '游戏', '红包雨红包图片'),
(6, '倒计时背景', 'countdown_bg', 'ds.png', '/image/coupon/ds.png', '游戏', '倒计时背景'),
(7, '数量显示', 'packet_count', 'sl.png', '/image/coupon/sl.png', '游戏', '红包数量显示');

-- 优惠券相关图片
INSERT INTO redpacket_image_resource VALUES
(8, '188元券', 'coupon_188', '满500元且消费一道特色菜可使用.png', '/image/coupon/满500元且消费一道特色菜可使用.png', '优惠券', '188元优惠券'),
(9, '588元券', 'coupon_588', '满1500元且消费一道特色菜可使用.png', '/image/coupon/满1500元且消费一道特色菜可使用.png', '优惠券', '588元优惠券'),
(10, '888元券', 'coupon_888', '满2500元且消费一道特色菜可使用.png', '/image/coupon/满2500元且消费一道特色菜可使用.png', '优惠券', '888元优惠券');
```

## 🔧 实施步骤

### 第一阶段：创建安全 API

1. ✅ 创建公开的图片资源获取接口
2. ✅ 前端创建图片资源 API 调用模块
3. ✅ 实现资源缓存机制

### 第二阶段：组件重构

1. ✅ 重构所有使用硬编码路径的组件
2. ✅ 实现统一的图片加载组件
3. ✅ 添加图片加载失败的降级处理

### 第三阶段：安全加固

1. ✅ 添加图片访问权限控制
2. ✅ 实现图片防盗链机制
3. ✅ 添加图片访问日志记录

## 📋 需要修复的文件清单

### 前端组件文件

- `rain-of-coupon/src/components/ActivitySection.vue`
- `rain-of-coupon/src/components/RedPacketRain.vue`
- `rain-of-coupon/src/components/CountDown.vue`
- `rain-of-coupon/src/components/CouponCard.vue`
- `rain-of-coupon/src/components/EncourageTip.vue`
- `rain-of-coupon/src/components/CrowdingTip.vue`
- `rain-of-coupon/src/components/LoadingAnim.vue`
- `rain-of-coupon/src/components/FoodDisplaySection.vue`

### 前端视图文件

- `rain-of-coupon/src/views/RedPacketPage.vue`
- `rain-of-coupon/src/views/PrizePage.vue`

### 配置文件

- `rain-of-coupon/src/config/api.ts`

### 后端文件

- 需要创建 `PublicImageResourceController.java`
- 需要添加图片资源相关的公开 API 接口

## ⚠️ 紧急程度

**建议立即修复**，因为：

1. 当前实现存在目录遍历风险
2. 图片路径暴露了服务器结构
3. 无法进行动态资源管理
4. 缺少访问控制和审计日志

修复完成后，系统将具备：

- ✅ 动态图片资源管理
- ✅ 访问权限控制
- ✅ 防盗链保护
- ✅ 审计日志记录
- ✅ 更好的可维护性
