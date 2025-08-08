# 红包雨路由配置说明

## 🎯 **用户流程**

```
首页 (/) → 登录 (/login) → 加载 (/loading) → 倒计时 (/countdown) → 红包雨 (/redpacket) → 返回首页
                    ↓
                规则页面 (/rule)
                    ↓  
              我的优惠券 (/coupon)
```

## 📋 **路由配置**

| 路由 | 组件 | 说明 | 权限 |
|------|------|------|------|
| `/` | HomePage | 活动入口页面 | 公开 |
| `/login` | LoginPage | 用户登录 | 公开 |
| `/loading` | LoadingPage | 加载和拥挤检测 | 需登录 |
| `/countdown` | CountdownPage | 3秒倒计时 | 需登录 |
| `/redpacket` | RedPacketPage | 红包雨游戏 | 需登录+活动中 |
| `/rule` | RulePage | 活动规则 | 公开 |
| `/coupon` | CouponPage | 我的优惠券 | 需登录 |

## 🔄 **核心业务逻辑**

1. **登录检查**: 判断用户是否已中奖
2. **拥挤检测**: 检查服务器负载
3. **流程控制**: 严格按照 loading → countdown → redpacket 顺序
4. **中奖限制**: 每人只能中一次奖

## 🛡️ **路由守卫**

**文件位置**: `redpacket-guards.js`

- **beforeEnterLoading**: 检查登录状态和中奖状态
- **beforeEnterCountdown**: 检查活动状态和用户资格  
- **beforeEnterRedPacket**: 验证页面跳转顺序
- **beforeEnterCoupon**: 检查登录状态

## 🎮 **组件类型**

### 页面组件（路由）
- **HomePage**: 活动入口
- **LoginPage**: 用户登录
- **LoadingPage**: 加载检测
- **CountdownPage**: 倒计时
- **RedPacketPage**: 红包雨游戏
- **RulePage**: 活动规则
- **CouponPage**: 我的优惠券

### 弹窗组件（非路由）
- **PrizeModal**: 中奖弹窗
- **EncourageTip**: 未中奖提示
- **CrowdingTip**: 拥挤提示

## 🔧 **关键实现**

### 登录后状态判断
```javascript
const userStatus = await store.dispatch('lottery/fetchUserStatus')
if (userStatus.hasEverWon) {
  this.$router.push('/')  // 显示中奖弹窗
} else {
  this.$router.push('/loading')  // 进入游戏流程
}
```

### 拥挤检测
```javascript
if (userStatus.isCrowded) {
  // 显示拥挤提示
} else {
  this.$router.push('/countdown')
}
```

### 流程控制
- 严格按照页面顺序跳转
- 防止跳过中间步骤
- 异常情况统一处理