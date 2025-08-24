# 用户流程

## 重构后的抽奖流程处理逻辑

### 状态检查优先级（并行API调用）

登录成功后，系统会并行调用多个API进行状态检查，按以下优先级处理：

1. **最高优先级**: 如果用户已中奖 → 跳转到 `/prize` (PrizePage)
2. **次高优先级**: 如果奖品库存不足 → 显示 PrizeStockTip 组件
3. **第三优先级**: 如果用户抽奖次数已用完或同一sessionId重复抽奖 → 显示 WarningTip 组件
4. **最低优先级**: 如果服务器流量拥挤 → 显示 CrowdingTip 组件
5. **默认情况**: 用户未中奖、库存充足、抽奖次数未用完 → 跳转到 `/loading` 进行抽奖

### 页面路由结构

```
首页 (/)
├─ 点击"加入活动" → 登录页面 (/login)
│   ├─ 登录成功后 → 并行API调用状态检查
│   │   ├─ 【最高优先级】已中奖 → 中奖页面 (/prize)
│   │   ├─ 【次高优先级】库存不足 → PrizeStockTip 组件
│   │   ├─ 【第三优先级】次数用完/重复抽奖 → WarningTip 组件
│   │   ├─ 【最低优先级】流量拥挤 → CrowdingTip 组件
│   │   └─ 【默认情况】可以抽奖 → 加载页面 (/loading)
│   │       └─ 倒计时页面 (/countdown)
│   │           └─ 红包页面 (/redpacket)
│   │               ├─ 中奖 → 中奖页面 (/prize)
│   │               └─ 未中奖 → EncourageTip 组件
├─ 点击"活动规则" → 规则页面 (/rule)
├─ 点击"我的优惠券" → 优惠券页面 (/coupon)
├─ 流量测试 → 流量测试页面 (/traffic-test)
└─ 缓存清理 → 缓存清理页面 (/clear-cache)
```

## 页面组件说明

### 首页 (HomePage.vue)

- 活动入口和导航
- 显示活动状态和剩余次数

### 登录页 (LoginPage.vue)

- 用户身份验证
- 防密码泄露保护措施

### 加载页 (LoadingPage.vue)

- 流量检测和排队机制
- 加载动画展示

### 倒计时页 (CountDownPage.vue)

- 3 秒倒计时动画
- 增加用户期待感

### 红包页 (RedPacketPage.vue)

- 红包雨动画核心逻辑
- 点击计数和概率计算

### 规则页 (RulePage.vue)

- 活动规则说明
- 静态内容展示

### 中奖页 (PrizePage.vue)

- 中奖结果展示页面
- 奖品详情和使用说明

### 优惠券页 (CouponPage.vue)

- 用户奖品展示
- 优惠券使用状态

### 流量测试页 (TrafficTestPage.vue)

- 流量控制测试
- 系统负载监控

### 缓存清理页 (ClearCache.vue)

- 清理应用缓存
- 重置用户状态

## 核心组件

### 状态提示组件

#### PrizeStockTip.vue
- 奖品库存不足提示
- 引导用户关注后续活动

#### WarningTip.vue
- 抽奖次数用完提示
- 防重复抽奖警告

#### CrowdingTip.vue
- 流量拥挤提示
- 引导用户稍后再试

#### EncourageTip.vue
- 未中奖鼓励提示
- 引导用户继续参与

### 功能组件

#### PrizeModal.vue
- 中奖结果弹窗
- 奖品信息展示

#### RedPacketRain.vue
- 红包雨动画逻辑
- 点击计数和交互处理

#### ActivitySection.vue
- 活动区域展示
- 参与入口管理

#### ProgressBar.vue
- 抽奖进度显示
- 视觉反馈增强

### 美食相关组件

#### FoodDisplaySection.vue
- 美食展示区域
- 活动奖品预览

#### SpecialityFoodSection.vue
- 特色美食推荐
- 地方特产展示

#### Top10FoodSection.vue
- 热门美食排行
- 用户喜好统计

## 安全特性

### 密码保护措施

为防止浏览器密码泄露警告并增强安全性，登录页面实施了以下保护措施：

#### 蜜罐字段技术
- 添加隐藏的假用户名和密码字段
- 使用标准的 `autocomplete` 属性吸引浏览器注意力
- 分散浏览器对真实登录字段的识别

#### 字段名混淆
- 真实输入框使用非标准 `name` 属性（如 `user_login_field`、`user_pass_field`）
- 避免使用常见的 `username`、`password` 等标准字段名
- 降低浏览器自动识别概率

#### 密码管理器排除
- 添加 `data-lpignore="true"` 属性
- 告诉 LastPass 等密码管理器忽略这些字段
- 防止第三方工具干扰

#### 表单类型伪装
- 使用 `data-form-type="other"` 标记
- 标识这不是标准的登录表单
- 避免浏览器安全检查机制

#### 自动完成禁用
- 所有敏感字段设置 `autocomplete="off"`
- 防止浏览器自动填充历史数据
- 增强用户隐私保护

### 会话管理

#### Session ID 防重复抽奖
- 为每个用户会话生成唯一 Session ID
- 防止同一会话多次抽奖
- 记录在 `participation_log` 表的 `session_id` 字段

#### 点击计数追踪
- 新增 `clicked_count` 字段记录用户点击红包数量
- 用于概率算法计算和防刷验证
- 提供更精确的用户行为分析