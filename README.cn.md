🌐 语言切换 | Language Switch: [English](./README.md) | [中文](./README.cn.md)

# 红包雨小程序

基于 Vue 3 和若依框架开发了一个小程序。主要功能包括：

- **抽奖系统**：每位用户每天最多抽奖 3 次，获奖后自动结束。
- **流量控制**：检测高用户流量并显示拥堵通知。
- **交互动画**：下落的红包动画和可点击的奖励触发。
- **优惠券页面**：设计了响应式布局，包含返回导航。

![红包雨截图](https://github.com/user-attachments/assets/ee03551b-73a5-4565-862b-7016bc432df9)

## 技术栈

- **前端**：Vue 3, Pinia, Vite
- **后端**：若依框架（Spring Boot）
- **数据库**：MySQL
- **API**：RESTful API
- **部署**：Nginx + Docker

## 用户流 & 路由页面结构（Page层级）
1. 
```
首页 (/)
├─ 点击“加入活动” → 登录页面 (/login)
│   ├─ 登录成功后 → 判断中奖状态
│   │   ├─ 如果已中奖 → 显示 PrizeModal 弹窗
|   │   ├─ 如果未中奖 → 自动跳转 → 加载页面 (/loading)
|   |   │   ├─ 如果人数拥挤 → 拥挤提示组件
|   |   │   ├─ 如果不拥挤 → 倒计时页面 (/countdown)
|   │   │   │   ├─ 倒计时结束 → 红包页面 (/redpacket)
|   │   │   │   │   ├─ 用户中奖 → 显示 PrizeModal 弹窗
|   │   │   │   │   └─ 用户未中奖 → 显示 EncourageTip 弹窗
│   │   │   └─ 抽奖后 → 返回首页 (/)
├─ 点击“活动规则” → 规则页面 (/rule)
└─ 抽奖结束或用户登录后 → 优惠券页面 (/coupon)
```
2. 

```
/
├── HomePage (/)
│   ├── 加入活动 → 登录页（/login）
│   └── 活动规则页（/rule）
│   └── 查看我的优惠券（/coupon）

├── LoginPage (/login)
│   ├── 登录成功 → 判断是否中奖
│   │   ├── 已中奖 → PrizeModal（组件）
│   │   └── 未中奖 → /loading

├── LoadingPage (/loading)
│   ├── 拥挤 → CrowdingTip（组件）
│   └── 正常 → 倒计时页面 /countdown

├── CountdownPage (/countdown)
│   └── 倒计时结束 → /redpacket

├── RedPacketPage (/redpacket)
│   ├── 抽奖完成（点击红包）
│   │   ├── 中奖 → PrizeModal（组件）
│   │   └── 未中奖 → EncourageTip（组件）
│   └── 抽奖后自动返回 HomePage

├── RulePage (/rule)
│   └── 显示活动规则静态内容

├── CouponPage (/coupon)
│   └── 显示已领取的券 + 返回按钮

```


## 开发进展

第一阶段和第二阶段可并行进行，优先完成 MVP。

### 第一阶段：数据库设计与 API 开发（进行中：3 天）

#### ✅ **已完成任务：**

**数据库设计** `8/5 - 8/6`

**代码生成** `8/6`
- ✅ 实体类已生成
- ✅ Mapper 接口已生成
- ✅ 基础 CRUD Service 和 Controller 层已生成
- ✅ 菜单配置 SQL 文件已生成

 **抽奖控制器和服务层的核心业务逻辑**`8/6`
- ✅ 每日3次限制
- ✅ 中奖后停止抽奖
- ✅ IP频率限制（1小时10次）
- ✅ 活动时间控制
- ✅ 加权随机概率算法
- ✅ 自动库存扣减
- ✅ 零库存自动排除
- ✅ 事务保证数据一致性

  **API 接口规范**`8/6`
1. POST /api/lottery/draw ✅
- 执行抽奖逻辑
- 检查用户资格
- 保存抽奖记录
- 返回抽奖结果

2. GET /api/lottery/records ✅
- 获取用户历史抽奖记录
- 需要用户登录

3. GET /api/lottery/drawCount ✅
- 获取剩余抽奖次数
- 检查是否已中奖
- 返回是否可以抽奖

4. GET /api/lottery/prizes ✅
- 获取所有可用奖品列表
- 自动过滤库存为0的奖品

5. GET /api/lottery/status ✅
- 检查用户抽奖资格
- 返回详细的状态信息
- 包含不能抽奖的原因

6. GET /api/activity/config ✅
- 获取活动配置信息
- 活动时间、限制等
- 活动状态判断


### 第二阶段：前端页面结构与 API 集成（进行中：3 天）

[README.frontend.md](doc\frontend-development-guide.md)

- **LoginPage.vue**
  1. 用户状态检测（通过 token 登录）
  2. 配置 Axios 请求拦截器

- **红包逻辑**：*下落红包动画*，*点击触发抽奖请求*，*弹窗显示中奖或未中奖*

```javascript
let totalRedPackets = 100;
let interval = setInterval(() => {
  if (totalRedPackets <= 0) clearInterval(interval);
  generateRedPacket(); // 每次生成 1 个红包动画
  totalRedPackets--;
}, 200);
```

- **流量检测**：“限流”问题 -> 后端限流 + 状态响应 + 前端加载判断，返回 `{ "status": "crowded" } // 或 "ok"`

- **随机判断用户是否抢到红包**：

```java
// 查询今日抽奖记录
List<UserPrizeLog> logs = userPrizeLogMapper.queryToday(userId);
if (logs.size() >= 3) return fail("次数用尽");

boolean alreadyWon = logs.stream().anyMatch(log -> log.isWin());
if (alreadyWon) return fail("已中奖");

boolean isWin = Math.random() < 0.2; // 20% 中奖概率，后台可配置
if (isWin) {
    // 随机分配奖品
    Prize prize = prizeService.getRandomAvailablePrize();
    saveUserPrize(userId, prize);
}
```

**可配置的奖品分配算法**：
- 使用 Redis 进行库存扣减（防止并发超发）
- 奖品概率存储在数据库的可配置字段中

### 第三阶段：测试（1天）

- 连接前端和后端抽奖逻辑
- 验证抽奖限制和中奖逻辑


## 许可证

本项目采用 MIT 许可证授权。详情见 [LICENSE](./LICENSE) 文件。
