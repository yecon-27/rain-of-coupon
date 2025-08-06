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

#### 🔄 **进行中任务：**

**核心业务逻辑实现** `8/6 - present`

1. **抽奖控制器开发**
   ```java
   @RestController
   @RequestMapping("/api/lottery")
   public class LotteryController {
       @PostMapping("/draw")           // 执行抽奖
       @GetMapping("/records")         // 获取用户抽奖记录
       @GetMapping("/drawCount")       // 检查剩余抽奖次数
       @GetMapping("/prizes")          // 获取可用奖品
       @GetMapping("/status")          // 检查用户抽奖资格
   }
   ```

2. **抽奖服务层**
   ```java
   @Service
   public class LotteryService {
       // 检查用户抽奖资格（每日限制、已中奖、IP限制）
       boolean checkDrawEligibility(Long userId, String ipAddress);
       
       // 执行抽奖算法，概率控制
       DrawResult executeDraw(Long userId);
       
       // 保存抽奖记录到数据库
       void saveDrawRecord(Long userId, DrawResult result, String ipAddress);
       
       // 获取用户今日剩余抽奖次数
       int getRemainingDrawCount(Long userId);
       
       // 检查用户今日是否已中奖
       boolean hasWonToday(Long userId);
   }
   ```

3. **防刷票机制**
   - **每日限制控制**：每用户每天最多3次抽奖
   - **中奖者限制**：中奖后停止抽奖
   - **IP频率限制**：防止同一IP过度抽奖
   - **并发控制**：基于Redis的库存管理
   - **请求节流**：抽奖间隔最小时间限制

4. **概率算法**
   ```java
   // 基于奖品概率的加权随机选择
   // Redis原子操作进行库存扣减
   // 奖品用完时的降级机制
   ```

5. **配置管理**
   - 活动时间控制（开始/结束时间）
   - 并发用户数限制
   - 奖品概率调整
   - 每日抽奖次数配置

#### 📋 **API 接口规范：**

| 接口地址 | 请求方式 | 功能描述 | 状态 |
|----------|----------|----------|------|
| `/api/lottery/draw` | POST | 执行抽奖 | 🔄 进行中 |
| `/api/lottery/records` | GET | 获取用户抽奖历史 | 🔄 进行中 |
| `/api/lottery/drawCount` | GET | 获取剩余抽奖次数 | 🔄 进行中 |
| `/api/lottery/prizes` | GET | 获取奖品配置 | 🔄 进行中 |
| `/api/lottery/status` | GET | 检查用户抽奖资格 | 🔄 进行中 |
| `/api/activity/config` | GET | 获取活动设置 | 🔄 进行中 |

### 第二阶段：前端页面结构与 API 集成（进行中：3 天）

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
```
📦 路由页面结构（Page层级）
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

### 第三阶段：集成与部署准备（进行中：1 天）

- 连接前端和后端抽奖逻辑
- 验证抽奖限制和中奖逻辑

## 许可证

本项目采用 MIT 许可证授权。详情见 [LICENSE](./LICENSE) 文件。
