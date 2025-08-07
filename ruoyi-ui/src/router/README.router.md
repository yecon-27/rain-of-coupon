# 红包雨活动正确的用户流程和路由配置

## 🎯 **用户流程对比**

### 你的设计流程 ✅
```
首页 (/)
├─ 点击"加入活动" → 登录页面 (/login)
│   ├─ 登录成功后 → 判断中奖状态
│   │   ├─ 如果已中奖 → 显示 PrizeModal 弹窗
│   │   └─ 如果未中奖 → 自动跳转 → 加载页面 (/loading)
│   │       ├─ 如果人数拥挤 → 拥挤提示组件
│   │       └─ 如果不拥挤 → 倒计时页面 (/countdown)
│   │           └─ 倒计时结束 → 红包页面 (/redpacket)
│   │               ├─ 用户中奖 → 显示 PrizeModal 弹窗
│   │               └─ 用户未中奖 → 显示 EncourageTip 弹窗
│   │               └─ 抽奖后 → 返回首页 (/)
├─ 点击"活动规则" → 规则页面 (/rule)
└─ 抽奖结束或用户登录后 → 优惠券页面 (/coupon)
```

### 我之前的错误配置 ❌
```
/redpacket          - 红包雨主页
/redpacket/result   - 领取结果  
/redpacket/records  - 我的优惠券
/redpacket/rules    - 活动规则
/redpacket/coupons  - 优惠券展示
/redpacket/winners  - 获奖名单
/redpacket/share    - 分享页面
```

## 📋 **正确的路由配置**

| 路由 | 组件 | 页面名称 | 权限要求 | 说明 |
|------|------|----------|----------|------|
| `/` | HomePage | 首页 | 无 | 活动入口，包含"加入活动"、"活动规则"、"查看我的优惠券"按钮 |
| `/login` | LoginPage | 登录页 | 无 | 用户登录，成功后判断中奖状态 |
| `/loading` | LoadingPage | 加载页 | 需登录 | 检查人数拥挤情况，决定是否进入倒计时 |
| `/countdown` | CountdownPage | 倒计时页 | 需登录 | 倒计时结束后进入红包雨 |
| `/redpacket` | RedPacketPage | 红包雨页 | 需登录+活动中 | 用户点击红包参与活动 |
| `/rule` | RulePage | 活动规则 | 公开 | 显示活动规则静态内容 |
| `/coupon` | CouponPage | 我的优惠券 | 需登录 | 显示已领取的券 + 返回按钮 |

## 🔄 **状态管理调整**

### 新增状态字段
```javascript
userStatus: {
  // 原有字段...
  hasWon: false,            // 是否已中奖（登录后检查）
  isCrowded: false,         // 当前是否拥挤
  canEnterCountdown: false  // 是否可以进入倒计时
}
```

### 关键业务逻辑
1. **登录后检查**: 判断用户是否已中奖
2. **拥挤检测**: 在loading页面检查服务器负载
3. **流程控制**: 严格按照 loading → countdown → redpacket 的顺序

## 🛡️ **路由守卫逻辑**

### 1. 加载页面守卫 (`beforeEnterLoading`)
- 检查登录状态
- 检查用户是否已中奖
- 如果已中奖，跳转首页显示弹窗
- 如果未中奖，允许进入加载页面

### 2. 倒计时页面守卫 (`beforeEnterCountdown`)
- 检查登录状态
- 必须从loading页面跳转而来
- 检查活动状态

### 3. 红包雨页面守卫 (`beforeEnterRedPacket`)
- 检查登录状态
- 必须从countdown页面跳转而来
- 检查活动状态和用户状态
- 初始化红包雨数据

### 4. 优惠券页面守卫 (`beforeEnterCoupon`)
- 检查登录状态
- 预加载用户优惠券记录

## 🎮 **组件设计**

### 弹窗组件（非路由）
- **PrizeModal**: 中奖弹窗，在首页或红包雨页面显示
- **EncourageTip**: 未中奖鼓励弹窗，在红包雨页面显示
- **CrowdingTip**: 拥挤提示组件，在loading页面显示

### 页面组件（路由）
- **HomePage**: 活动入口页面
- **LoginPage**: 登录页面
- **LoadingPage**: 加载和拥挤检测页面
- **CountdownPage**: 倒计时页面
- **RedPacketPage**: 红包雨主页面
- **RulePage**: 活动规则页面
- **CouponPage**: 我的优惠券页面

## 🔧 **关键实现点**

### 1. 登录后的状态判断
```javascript
// 在登录成功后
const userStatus = await store.dispatch('lottery/fetchUserStatus')
if (userStatus.hasWon) {
  // 跳转首页并显示中奖弹窗
  this.$router.push('/?showPrize=true')
} else {
  // 跳转到加载页面
  this.$router.push('/loading')
}
```

### 2. 拥挤检测逻辑
```javascript
// 在loading页面
const crowdStatus = await checkServerLoad()
if (crowdStatus.isCrowded) {
  // 显示拥挤提示组件
  this.showCrowdingTip = true
} else {
  // 跳转到倒计时页面
  this.$router.push('/countdown')
}
```

### 3. 严格的流程控制
- 每个页面都检查前置条件
- 不允许跳过中间步骤
- 异常情况统一处理

## ✅ **修正总结**

1. **路由结构**: 完全按照你的用户流程重新设计
2. **权限控制**: 简化为必要的登录检查和流程控制
3. **状态管理**: 添加支持用户流程的关键状态
4. **组件设计**: 区分页面组件和弹窗组件
5. **用户体验**: 严格按照设计的流程引导用户

现在的配置完全符合你的用户流程设计！