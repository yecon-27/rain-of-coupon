🌐 语言切换 | Language Switch: [English](./README.md) | [中文](./README.cn.md)

# 红包雨小程序

基于 Vue 3 和若依框架开发了一个小程序。主要功能包括：

- **红包雨抢红包系统**：每位用户每天最多参与 3 次，获奖后自动结束。点击红包数量越多，中奖概率越大。
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

## 数据库表结构

### 核心业务表

1. **redpacket_prize**（奖品配置表）
   - 奖品名称、总数量、剩余数量、中奖概率
2. **redpacket_user_participation_log**（用户参与记录表）
   - 用户 ID、IP 地址、是否中奖、参与时间、奖品信息
3. **redpacket_event_config**（活动配置表）
   - 活动时间、并发限制、每日游戏次数限制
4. **image_resource**（图片资源表）
   - 红包雨相关的静态图片资源管理

### 业务特色

- **防刷机制**：IP 频率限制、每日次数限制、中奖后停止
- **概率算法**：基于点击数量的动态概率计算
- **并发控制**：Redis 缓存 + 数据库事务保证数据一致性

## 项目结构

```
├── .gitignore                    # Git 忽略文件
├── LICENSE                       # 开源许可证
├── README.md                     # 项目文档说明
├── pom.xml                       # Maven 根配置文件
├── ry.bat                        # 后端启动脚本（Windows）
├── ry.sh                         # 后端启动脚本（Linux/Mac）
├── backend-sql                   # MySQL 数据库文件
├── bin/                          # Shell/批处理脚本目录
│   ├── clean.bat                 # 清理脚本
│   ├── package.bat               # 构建/打包脚本
│   └── run.bat                   # 启动脚本
├── doc/                          # 文档目录
│   └── ruoyi-code-generation-config   # 代码生成器导出配置
├── sql/                          # 数据库初始化 SQL 文件
│   ├── ry_20250522.sql               # 若依框架基础表
│   ├── coupon_activity_simplified.sql # 活动相关表：奖品、日志、配置、图片资源、食品等
│   ├── add-participation-log-table.sql # 红包雨用户参与记录表
│   ├── business_log.sql              # 业务日志表
│   ├── README.sql.md                 # SQL 文件说明（包含红包雨表结构）
│   └── quartz.sql                    # Quartz 定时任务相关表
├── rain-of-coupon/              # 前端项目（Vue 3 + TypeScript + Pinia）
│   ├── public/                   # 公共静态资源
│   ├── src/
│   │   ├── api/                  # API 请求封装（TypeScript + Fetch）
│   │   │   ├── lottery.ts              # 🔹 API 层：HTTP 请求
│   │   ├── stores/lottery.ts           # 🔸 状态层：纯状态管理
│   │   ├── services/lotteryService.ts  # 🔹 服务层：业务逻辑
│   │   │── composables/useLottery.ts   # 🔸 组合层：组件接口
│   │   └── examples/                   # 📋 使用示例
│   │   │   └── LotteryUsageExample.vue
│   │   ├── assets/               # 静态资源（图片、样式等）
│   │   ├── components/           # 可复用 Vue 3 组件
│       │   ├── PrizeModal.vue          # 中奖弹窗
│       │   ├── EncourageTip.vue        # 未中奖时鼓励提示
│       │   ├── RedPacket.vue           # 红包动画逻辑
│       │   ├── CountDown.vue           # 倒计时动画（3,2,1）
│       │   ├── RulePopup.vue           # 活动规则弹窗或信息块
│       │   ├── CouponCard.vue          # 优惠券展示组件（在 /coupon 页面使用）
│       │   ├── CrowdingTip.vue         # “人数过多”提示组件
│       │   ├── LoadingAnim.vue         # 火箭 / 加载动画组件
│       │   ├── BackButton.vue          # 返回按钮组件
│       │   ├── LoginForm.vue           # 登录表单组件
│       ├── directives/           # 自定义指令（如自动聚焦等）
│       ├── router/               # Vue Router 路由配置
│       │   └── index.js              # 路由定义文件
│       ├── store/                # Vuex 状态管理目录
│       │   ├── index.js              # Vuex 主入口
│       │   └── modules/              # 模块化状态管理
│       ├── utils/                # 工具函数
│       │   └── request.js            # Axios 请求封装与拦截器
│       ├── views/                # 页面级组件（对应路由）
│       │   ├── HomePage.vue          # 首页
│       │   ├── LoginPage.vue         # 登录页
│       │   ├── LoadingPage.vue       # 加载动画页
│       │   ├── CountDownPage.vue     # 倒计时页
│       │   ├── RedPacketPage.vue     # 红包抽奖页
│       │   ├── RulePage.vue          # 活动规则页
│       │   └── CouponPage.vue        # 优惠券 / 奖品列表页
│       ├── App.vue               # 根组件
│       └── main.js               # 应用入口文件（Vue 2 + Vuex 初始化）
├── ruoyi-ui/                    # 后台管理前端（基于 Vue 2）
│   ├── src/
│   │   ├── api/                  # 后台API接口层定义
│   │   ├── redpacket/
│   │   │   └── lottery.js        # 红包雨API接口封装
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 通用组件
│   │   ├── directive/            # 自定义指令
│   │   ├── layout/               # 页面布局结构
│   │   ├── router/               # 路由设置
│   │   ├── store/                # Vuex 状态管理
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面视图（后台）
│   ├── bin/                      # 启动/构建脚本
│   ├── package.json              # NPM 配置文件
│   └── vue.config.js             # Vue CLI 配置文件
├── ruoyi-admin/                 # 后端主模块
│   ├── src/main/java/com/ruoyi/  # Java 源码
│   ├── src/main/resources/       # 配置文件与资源
│   │   ├──image/redpacket        # 红包图片资源目录
│   │   │   ├── README.image.md   # 图片资源说明
│   │   │   ├── ...
│   └── pom.xml                   # 模块专属 Maven 配置
├── ruoyi-common/                # 通用工具模块（Java）
├── ruoyi-framework/             # 框架核心模块（Java）
```

## 用户流 & 路由页面结构（Page 层级）

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

### 第一阶段：数据库设计与 API 开发

**数据库设计** ` 8/5 - 8/6``8.7上午补充了参与活动的表数据库 `

**代码生成** `8/6`

- 实体类已生成
- Mapper 接口已生成
- 基础 CRUD Service 和 Controller 层已生成
- 菜单配置 SQL 文件已生成

  **红包雨抢红包控制器和服务层的核心业务逻辑**`8/6`

- 每日 3 次游戏限制
- 中奖后停止参与（一人只能中一次）
- IP 频率限制（1 小时 10 次）
- 活动时间控制
- 基于点击数量的概率算法（点击越多，中奖概率越大）
- 红包雨交互模式（50 秒内 100 个红包飘落）
- 自动库存扣减
- 零库存自动排除
- 事务保证数据一致性

## API 接口规范 `8/17`

### 核心业务接口

| 方法 | 接口地址                           | 描述                                 |
| ---- | ---------------------------------- | ------------------------------------ |
| POST | `/redpacket/lottery/draw`          | 执行红包雨逻辑，基于点击数量计算概率 |
| GET  | `/redpacket/lottery/records`       | 获取用户参与历史记录，支持分页       |
| GET  | `/redpacket/lottery/count`         | 获取剩余抽奖次数和每日统计           |
| GET  | `/redpacket/lottery/status`        | 检查用户参与资格和状态               |
| GET  | `/redpacket/lottery/prizes`        | 获取可用奖品列表（自动过滤零库存）   |
| POST | `/redpacket/lottery/claim/{logId}` | 领取/使用中奖记录中的优惠券          |
| GET  | `/redpacket/lottery/config`        | 获取活动配置和时间设置               |

_另有管理后台接口用于奖品配置、用户管理和数据分析。_

**身份验证与数据安全**`8.17`

#### 密码保护措施

为防止浏览器密码泄露警告并增强安全性，已实施以下措施：

1. **蜜罐字段**：隐藏的假用户名和密码字段，使用标准自动完成属性来分散浏览器注意力
2. **自定义字段名**：真实输入字段使用非标准名称（如 `security_field`、`user_login_field`）而非常见的密码字段名
3. **密码管理器排除**：添加 `data-lpignore="true"` 属性防止 LastPass 等密码管理器检测这些字段
4. **表单类型伪装**：使用 `data-form-type="search"` 将登录表单伪装成搜索表单
5. **禁用自动完成**：在所有敏感字段上设置 `autocomplete="off"` 防止浏览器自动填充
6. **字段类型混淆**：策略性使用字段类型和属性来避免浏览器密码检测算法

这些措施协同工作，防止浏览器将字段识别为登录凭据，从而避免密码泄露警告，同时保持完整功能。

### 第二阶段：前端页面结构与 API 集成

[README.frontend.md](doc\frontend-development-guide.md)

- **LoginPage.vue**

  1. 用户状态检测（通过 token 登录）
  2. 配置 Axios 请求拦截器

- **红包雨逻辑**：_50 秒内 100 个红包下落动画_，_点击红包累积计数_，_游戏结束后根据点击数量计算中奖概率_，_弹窗显示中奖或未中奖_

```javascript
let totalRedPackets = 100;
let interval = setInterval(() => {
  if (totalRedPackets <= 0) clearInterval(interval);
  generateRedPacket(); // 每次生成 1 个红包动画
  totalRedPackets--;
}, 200);
```

- **流量检测**：“限流”问题 -> 后端限流 + 状态响应 + 前端加载判断，返回 `{ "status": "crowded" } // 或 "ok"`

- **基于点击数量判断用户是否抢到红包**：

```java
// 查询今日参与记录
List<UserPrizeLog> logs = userPrizeLogMapper.queryToday(userId);
if (logs.size() >= 3) return fail("次数用尽");

boolean alreadyWon = logs.stream().anyMatch(log -> log.isWin());
if (alreadyWon) return fail("已中奖");

// 基于点击数量计算中奖概率
int clickedCount = request.getClickedCount(); // 用户点击的红包数量
double baseProbability = 0.05; // 5% 基础概率
double probabilityMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 0.8;
double finalProbability = baseProbability * Math.min(probabilityMultiplier, 4.0);

boolean isWin = Math.random() < finalProbability;
if (isWin) {
    // 随机分配奖品
    Prize prize = prizeService.getRandomAvailablePrize();
    saveUserPrize(userId, prize);
}
```

**可配置的奖品分配算法**：

- 使用 Redis 进行库存扣减（防止并发超发）
- 奖品概率存储在数据库的可配置字段中

### 第三阶段：测试

- 连接前端和后端抽奖逻辑
- 验证抽奖限制和中奖逻辑

## 许可证

本项目采用 MIT 许可证授权。详情见 [LICENSE](./LICENSE) 文件。
