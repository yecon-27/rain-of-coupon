# 项目结构

## 目录结构

```
├── .gitignore                    # Git 忽略文件
├── LICENSE                       # 开源许可证
├── README.md                     # 项目文档说明
├── pom.xml                       # Maven 根配置文件
├── ry.bat                        # 后端启动脚本（Windows）
├── ry.sh                         # 后端启动脚本（Linux/Mac）
├── bin/                          # Shell/批处理脚本目录
│   ├── clean.bat                 # 清理脚本
│   ├── package.bat               # 构建/打包脚本
│   └── run.bat                   # 启动脚本
├── docs/                         # 项目文档
│   ├── project-structure.md      # 项目结构说明
│   ├── api-specification.md      # API接口规范
│   ├── database-design.md        # 数据库设计
│   └── deployment-guide.md       # 部署指南
├── sql/                          # 数据库初始化 SQL 文件
│   ├── ry_20250522.sql           # 若依框架基础表
│   ├── coupon_activity_simplified.sql # 活动相关表
│   ├── add-clicked-count-session-fields.sql # 添加点击数量和会话ID字段
│   ├── add-traffic-function.sql  # 流量控制功能
│   ├── business_log.sql          # 业务日志表
│   ├── database-schema.md        # 数据库架构说明
│   ├── README.sql.md             # SQL 文件说明
│   └── quartz.sql                # Quartz 定时任务相关表
├── rain-of-coupon/               # 前端项目（Vue 3 + TypeScript + Pinia）
│   ├── public/                   # 公共静态资源
│   ├── src/
│   │   ├── api/                  # API 请求封装
│   │   │   ├── lottery.ts        # 红包雨API接口
│   │   │   ├── food.ts           # 美食相关API
│   │   │   ├── rules.ts          # 规则API
│   │   │   └── traffic.ts        # 流量控制API
│   │   ├── components/           # 可复用组件
│   │   │   ├── ActivitySection.vue      # 活动区域组件
│   │   │   ├── BackButton.vue           # 返回按钮
│   │   │   ├── CountDown.vue            # 倒计时动画
│   │   │   ├── CouponCard.vue           # 优惠券卡片
│   │   │   ├── CrowdingTip.vue          # 流量拥挤提示
│   │   │   ├── EncourageTip.vue         # 未中奖鼓励提示
│   │   │   ├── FoodDisplaySection.vue   # 美食展示区域
│   │   │   ├── LoadingAnim.vue          # 加载动画
│   │   │   ├── PrizeModal.vue           # 中奖弹窗
│   │   │   ├── PrizeStockTip.vue        # 奖品库存不足提示
│   │   │   ├── ProgressBar.vue          # 进度条
│   │   │   ├── RedPacketRain.vue        # 红包雨动画
│   │   │   ├── RulePopup.vue            # 规则弹窗
│   │   │   ├── SpecialityFoodSection.vue # 特色美食区域
│   │   │   ├── Top10FoodSection.vue     # 热门美食区域
│   │   │   └── WarningTip.vue           # 警告提示
│   │   ├── composables/          # 组合式函数
│   │   │   └── useLottery.ts     # 抽奖逻辑组合函数
│   │   ├── config/               # 配置文件
│   │   │   └── api.ts            # API配置
│   │   ├── examples/             # 使用示例
│   │   │   └── LotteryUsageExample.vue
│   │   ├── router/               # Vue Router 路由配置
│   │   │   ├── index.ts          # 路由定义
│   │   │   ├── redpacket-guards.ts # 路由守卫
│   │   │   └── README.router.md  # 路由说明
│   │   ├── services/             # 业务服务层
│   │   │   ├── lotteryService.ts # 抽奖服务
│   │   │   └── trafficService.ts # 流量服务
│   │   ├── stores/               # Pinia 状态管理
│   │   │   ├── auth.ts           # 认证状态
│   │   │   ├── counter.ts        # 计数器状态
│   │   │   ├── game.ts           # 游戏状态
│   │   │   ├── gameStore.ts      # 游戏存储
│   │   │   ├── lottery.ts        # 抽奖状态
│   │   │   ├── traffic.ts        # 流量状态
│   │   │   └── ui.ts             # UI状态
│   │   ├── types/                # TypeScript 类型定义
│   │   │   └── lottery.ts        # 抽奖相关类型
│   │   ├── utils/                # 工具函数
│   │   │   ├── auth.ts           # 认证工具
│   │   │   └── clearCache.js     # 缓存清理
│   │   ├── views/                # 页面级组件
│   │   │   ├── ClearCache.vue    # 缓存清理页
│   │   │   ├── CountDownPage.vue # 倒计时页
│   │   │   ├── CouponPage.vue    # 优惠券页
│   │   │   ├── HomePage.vue      # 首页
│   │   │   ├── LoadingPage.vue   # 加载页
│   │   │   ├── LoginPage.vue     # 登录页
│   │   │   ├── PrizePage.vue     # 中奖页面
│   │   │   ├── RedPacketPage.vue # 红包抽奖页
│   │   │   ├── RulePage.vue      # 活动规则页
│   │   │   ├── TrafficTest.vue   # 流量测试页
│   │   │   └── TrafficTestPage.vue # 流量测试页面
│   │   ├── __tests__/            # 测试文件
│   │   │   └── App.spec.ts       # 应用测试
│   │   ├── App.vue               # 根组件
│   │   └── main.ts               # 应用入口文件
│   ├── package.json              # NPM 配置文件
│   ├── vite.config.ts            # Vite 配置
│   ├── tsconfig.json             # TypeScript 配置
│   ├── .env                      # 环境变量
│   ├── .env.development          # 开发环境配置
│   └── .env.production           # 生产环境配置
├── ruoyi-ui/                     # 后台管理前端（Vue 2）
│   ├── src/
│   │   ├── api/                  # 后台API接口
│   │   │   └── redpacket/
│   │   │       └── lottery.js    # 红包雨API封装
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 通用组件
│   │   ├── layout/               # 页面布局
│   │   ├── router/               # 路由配置
│   │   ├── store/                # Vuex 状态管理
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面视图
│   ├── package.json              # NPM 配置
│   └── vue.config.js             # Vue CLI 配置
├── ruoyi-admin/                  # 后端主模块
│   ├── src/main/java/com/ruoyi/  # Java 源码
│   ├── src/main/resources/       # 配置文件与资源
│   │   └── image/redpacket/      # 红包图片资源
│   └── pom.xml                   # Maven 配置
├── ruoyi-common/                 # 通用工具模块
├── ruoyi-framework/              # 框架核心模块
├── ruoyi-generator/              # 代码生成器模块
├── ruoyi-quartz/                 # 定时任务模块
└── ruoyi-system/                 # 系统管理模块
```

## 模块说明

### 前端模块

- **rain-of-coupon**: 用户端小程序，基于Vue 3 + TypeScript + Pinia
- **ruoyi-ui**: 后台管理系统，基于Vue 2 + Vuex

### 后端模块

- **ruoyi-admin**: 主应用模块，包含启动类和控制器
- **ruoyi-common**: 通用工具类和常量定义
- **ruoyi-framework**: 框架核心功能，包含安全、缓存等
- **ruoyi-generator**: 代码生成器
- **ruoyi-quartz**: 定时任务管理
- **ruoyi-system**: 系统管理功能

### 数据库文件

- **sql/**: 包含所有数据库初始化脚本和业务表结构
- **redpacket-rules-table-design.sql**: 红包雨业务表设计

### 文档目录

- **docs/**: 项目相关文档
  - [项目结构](./project-structure.md) - 项目组织结构
  - [API规范](./api-specification.md) - 接口文档
  - [数据库设计](./database-design.md) - 数据库架构
  - [部署指南](./deployment-guide.md) - 部署说明
  - [用户流程](./user-flow.md) - 页面流程和组件说明
  - [安全分析](./security-analysis.md) - 图片资源安全问题分析
  - [安全修复](./security-fix-implementation.md) - 安全问题修复实施方案
- **doc/**: 原有文档目录（待整理）