# 前端开发指南

## 红包雨小程序前端开发步骤详解

---

#### 🎯 **前端开发步骤：**

**第一步：项目基础搭建** (0.5 天)

1. **API 接口封装**

   ```javascript
   // src/api/lottery.js
   -drawLottery() - // 执行抽奖
     getUserRecords() - // 获取抽奖记录
     getDrawCount() - // 获取剩余次数
     getPrizes() - // 获取奖品列表
     getUserStatus() - // 获取用户状态
     getActivityConfig(); // 获取活动配置
   ```

2. **状态管理设置** (Vuex)

   ```javascript
   // src/stores/lottery.js
   -用户抽奖状态 - 奖品信息 - 抽奖记录 - 活动配置;
   ```

3. **路由配置**
   ```javascript
   // src/router/index.js
   - 7个页面路由配置
   - 路由守卫（登录检查）
   ```

**第二步：核心组件开发** (1 天)

1. **通用组件**

   ```vue
   - BackButton.vue // 返回按钮 - LoadingAnim.vue // 加载动画 - LoginForm.vue //
   登录表单
   ```

2. **业务组件**
   ```vue
   - PrizeModal.vue // 中奖弹窗 - EncourageTip.vue // 未中奖鼓励提示 -
   RedPacket.vue // 红包动画 - CountDown.vue // 倒计时(3,2,1) - CrowdingTip.vue
   // 拥挤通知 - RulePopup.vue // 规则弹窗 - CouponCard.vue // 优惠券卡片
   ```

**第三步：页面开发** (1.5 天)

_基础页面：_

```vue
1. HomePage.vue // 首页 (0.3天) - 背景图片展示 -
三个主要按钮：加入活动、活动规则、我的优惠券 - 美食展示图片 2. LoginPage.vue //
登录页 (0.2天) - 用户登录表单 - 登录后状态检查 - 自动跳转逻辑 3. RulePage.vue //
规则页 (0.2天) - 静态规则内容 - 返回按钮
```

_核心功能页面：_

```vue
4. LoadingPage.vue // 加载页 (0.2天) - 加载动画 - 拥挤检测逻辑 - 自动跳转 5.
CountDownPage.vue // 倒计时页 (0.3天) - 3,2,1倒计时动画 - 倒计时结束后自动跳转
6. RedPacketPage.vue // 红包抽奖页 (0.3天) - 红包雨动画效果 - 点击抽奖逻辑 -
中奖/未中奖弹窗 7. CouponPage.vue // 优惠券页 (0.2天) - 优惠券列表展示 -
使用状态管理 - 参与挑战获取提示
```

**第四步：动画效果实现** (1 天)

1. **红包雨动画**

   ```javascript
   let totalRedPackets = 100;
   let interval = setInterval(() => {
     if (totalRedPackets <= 0) clearInterval(interval);
     generateRedPacket(); // 每次生成1个红包动画
     totalRedPackets--;
   }, 200);
   ```

2. **倒计时动画**

   - 3,2,1 数字切换特效
   - 缩放和渐变动画

3. **加载动画**

   - 火箭动画或其他加载效果

4. **弹窗动画**
   - 中奖弹窗出现动画
   - 鼓励提示动画

**第五步：业务逻辑集成** (1 天)

1. **抽奖流程整合**

   ```javascript
   // 完整抽奖流程
   登录 → 状态检查 → 加载页 → 倒计时 → 抽奖 → 结果展示 → 返回首页
   ```

2. **状态管理**

   - 用户登录状态
   - 抽奖资格检查
   - 中奖状态管理
   - 剩余次数更新

3. **错误处理**
   - 网络请求失败
   - 抽奖资格不足
   - 活动时间限制

**第六步：样式和响应式** (0.5 天)

1. **移动端适配**

   - 响应式布局
   - 触摸事件优化

2. **视觉效果**
   - 美食图片展示
   - 优惠券样式
   - 按钮交互效果

**第七步：测试和优化** (0.5 天)

1. **功能测试**

   - 抽奖流程测试
   - 边界情况测试
   - 多设备兼容性

2. **性能优化**
   - 动画性能优化
   - 图片懒加载
   - 请求缓存

---

#### 📋 **开发优先级：**

**高优先级** (MVP 核心功能)

1. ✅ API 接口封装
2. ✅ 基础页面开发
3. ✅ 核心抽奖逻辑
4. ✅ 简单动画效果

**中优先级** (用户体验)

1. 🔄 复杂动画效果
2. 🔄 样式美化
3. 🔄 响应式适配

**低优先级** (锦上添花)

1. ⏳ 高级动画特效
2. ⏳ 性能优化
3. ⏳ 额外功能

---

#### 🚀 **建议开发顺序：**

**第 1 天**: API 封装 + 基础页面 + 简单路由
**第 2 天**: 核心组件 + 抽奖逻辑集成  
**第 3 天**: 动画效果 + 样式优化
**第 4 天**: 测试调试 + 细节完善

---

#### 🎮 **用户流程图：**

```
首页 (/)
├─ 点击"加入活动" → 登录页面 (/login)
│   ├─ 登录成功后 → 判断中奖状态
│   │   ├─ 如果已中奖 → 显示 PrizeModal 弹窗
│   │   ├─ 如果未中奖 → 自动跳转 → 加载页面 (/loading)
│   │   │   ├─ 如果人数拥挤 → 拥挤提示组件
│   │   │   ├─ 如果不拥挤 → 倒计时页面 (/countdown)
│   │   │   │   ├─ 倒计时结束 → 红包页面 (/redpacket)
│   │   │   │   │   ├─ 用户中奖 → 显示 PrizeModal 弹窗
│   │   │   │   │   └─ 用户未中奖 → 显示 EncourageTip 弹窗
│   │   │   └─ 抽奖后 → 返回首页 (/)
├─ 点击"活动规则" → 规则页面 (/rule)
└─ 抽奖结束或用户登录后 → 优惠券页面 (/coupon)
```

---

#### 📦 **页面级路由结构：**

```
/
├── HomePage (/)
│   ├── 加入活动 → LoginPage (/login)
│   ├── 查看活动规则 → RulePage (/rule)
│   └── 查看我的优惠券 → CouponPage (/coupon)

├── LoginPage (/login)
│   └── 登录成功 → 检查中奖状态
│       ├── 如果已中奖 → 显示 PrizeModal (组件)
│       └── 如果未中奖 → 跳转到 LoadingPage (/loading)

├── LoadingPage (/loading)
│   ├── 如果拥挤 → 显示 CrowdingTip (组件)
│   └── 如果正常 → 跳转到 CountdownPage (/countdown)

├── CountdownPage (/countdown)
│   └── 倒计时结束 → 跳转到 RedPacketPage (/redpacket)

├── RedPacketPage (/redpacket)
│   ├── 用户点击红包后：
│   │   ├── 如果中奖 → 显示 PrizeModal (组件)
│   │   └── 如果未中奖 → 显示 EncourageTip (组件)
│   └── 抽奖后 → 自动返回 HomePage

├── RulePage (/rule)
│   └── 显示静态活动规则内容

├── CouponPage (/coupon)
│   └── 显示已获得的优惠券 + 返回按钮
```

---

#### 🛠️ **技术栈：**

- **前端框架**: Vue 3
- **状态管理**: Pinia
- **构建工具**: Vite
- **路由**: Vue Router
- **HTTP 客户端**: Axios
- **动画库**: CSS3 + JavaScript
- **样式**: SCSS/CSS3
- **移动端适配**: Viewport + Media Queries

---

#### 📝 **开发注意事项：**

1. **性能优化**

   - 图片资源压缩和懒加载
   - 动画使用 CSS3 硬件加速
   - 避免频繁的 DOM 操作

2. **用户体验**

   - 加载状态提示
   - 网络异常处理
   - 触摸反馈优化

3. **兼容性**

   - 移动端浏览器兼容
   - 不同屏幕尺寸适配
   - 网络环境适应

4. **安全性**
   - 用户输入验证
   - API 请求防护
   - 敏感信息保护

---

这个开发指南提供了完整的前端开发路线图，可以按照步骤逐步实现红包雨小程序的前端功能。
