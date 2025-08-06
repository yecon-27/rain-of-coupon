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

## 项目结构

```
├── .gitignore                    # Git 忽略文件
├── LICENSE                       # 开源许可证
├── README.md                     # 项目文档
├── pom.xml                       # Maven 根配置文件
├── ry.bat                        # 后端启动脚本（Windows）
├── ry.sh                         # 后端启动脚本（Linux/Mac）
├── backend-sql                   # MySQL 数据库文件
├── bin/                          # 脚本目录
│   ├── clean.bat                 # 清理脚本
│   ├── package.bat               # 构建/打包脚本
│   └── run.bat                   # 运行脚本
├── doc/                          # 文档目录
│   └── RuoYi User Manual.docx    # 若依框架设置手册
├── sql/                          # 数据库初始化 SQL 文件
│   ├── ry_20250522.sql           # 若依基础表
│   ├──  
│   ├──  
│   ├──  
│   └── quartz.sql                # Quartz 定时任务
├── rain-of-coupon/
│   ├── public/                   # 公共静态文件
│   ├── docs/                     # 项目特定文档
│   ├── package.json              # NPM 依赖和脚本
│   ├── vite.config.ts            # Vite 配置文件
│   ├── vercel.json               # Vercel 部署配置
│   └── src/
│       ├── api/                  # API 抽象层（Axios）
│       ├── assets/               # 静态资源（模拟数据）
│       ├── components/           # 可复用 UI 组件
│       │   ├── PrizeModal.vue          # 中奖弹窗组件
│       │   ├── EncourageTip.vue        # 未中奖鼓励提示弹窗
│       │   ├── RedPacket.vue           # 红包动画与点击逻辑
│       │   ├── CountDown.vue           # 倒计时动画（3、2、1）
│       │   ├── RulePopup.vue           # 活动规则弹窗（或静态块）
│       │   ├── CouponCard.vue          # 单张优惠券展示组件（券包页使用）
│       │   ├── CrowdingTip.vue         # 活动拥挤提示组件
│       │   ├── LoadingAnim.vue         # 加载动效（如火箭动画）
│       │   ├── BackButton.vue          # 返回按钮（券包页使用）
│       │   ├── LoginForm.vue           # 登录表单封装组件
│       ├── composables/          # Vue 3 组合式函数（例如 useUser）
│       ├── directives/           # 自定义 Vue 指令
│       ├── router/               # Vue Router 设置
│       │   └── index.js              # 路由定义
│       ├── stores/               # 状态管理（Pinia）
│       ├── utils/                # 工具函数
│       │   └── request.js            # Axios 拦截器和配置
│       ├── views/                # 页面级组件（路由）
│       │   ├── HomePage.vue          # 首页
│       │   ├── LoginPage.vue         # 登录页面
│       │   ├── LoadingPage.vue       # 加载动画页面
│       │   ├── CountDownPage.vue     # 倒计时页面
│       │   ├── RedPacketPage.vue     # 红包抽奖页面
│       │   ├── RulePage.vue          # 活动规则页面
│       │   └── CouponPage.vue        # 优惠券/奖品列表页面
│       ├── App.vue               # Vue 根组件
│       └── main.js               # 应用入口
├── ruoyi-ui/
│   ├── src/
│   │   ├── api/                  # API 定义
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # UI 组件
│   │   ├── directive/            # 自定义指令
│   │   ├── layout/               # 页面布局系统
│   │   ├── router/               # Vue Router 配置
│   │   ├── store/                # Vuex 状态管理
│   │   ├── utils/                # 工具函数和辅助方法
│   │   └── views/                # 页面视图（管理端）
│   ├── bin/                      # 管理端启动/构建脚本
│   ├── package.json              # NPM 配置
│   └── vue.config.js             # Vue CLI 配置
├── ruoyi-admin/                  # 主后端模块
│   ├── src/main/java/com/ruoyi/  # Java 源代码
│   ├── src/main/resources/       # 应用配置
│   │   ├──image/redpacket        # 图片资源
|   │   │   ├── README.image.md   # 图片信息
|   │   │   ├── ...
│   └── pom.xml                   # 模块特定的 Maven 配置
├── ruoyi-common/                 # 共享工具模块
├── ruoyi-framework/             
```

## 开发进展

第一阶段和第二阶段可并行进行，优先完成 MVP。

### 第一阶段：数据库设计与 API 开发（进行中：2 天）

- **设计表**：`8.5 - present`
  1. prize – 奖品配置：奖品名称，奖品总数，剩余数量，中奖概率
  2. user_prize_log – 用户抽奖记录: 用户ID，奖品名称，中奖时间，使用状态
  3. image_resource - 其余静态资源：资源名称，资源标识，文件名，文件访问路径，使用场景，描述
  4. user_info -（若依内置）：用户名，密码，手机号
  5. TOP10 网络人气特色美食：美食名称，排名
  6. “一镇一品”特色菜：美食名称，排名

- 使用若依内置代码生成器生成：
  1. 实体类
  2. Mapper 接口
  3. Service 和 Controller 层

- 开发 API：
  - 抽奖：POST /api/draw
  - 查看抽奖记录：GET /api/user/records
  - 检查剩余抽奖次数：GET /api/user/drawCount
  - 获取奖池配置：GET /api/prizes

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
