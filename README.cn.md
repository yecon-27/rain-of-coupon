# 红包雨小程序

基于 Vue 3 和 Spring Boot 开发的交互式抽奖小程序，具有实时红包雨动画和智能概率算法。

**语言**: [English](./README.md) | [中文](./README.cn.md)

## 功能特性

- 基于点击数量的实时红包雨概率系统
- 每日参与限制和防刷机制
- 流量控制和拥堵检测
- 响应式设计和流畅动画
- 奖品和用户管理后台
- 匿名用户访问支持

## 技术栈

- **前端**: Vue 3, TypeScript, Pinia, Vite
- **后端**: Spring Boot, MyBatis, Redis
- **数据库**: MySQL 5.7+
- **部署**: Docker, Nginx

## 快速开始

### 环境要求

- Java 8+
- Node.js 14+
- MySQL 5.7+
- Redis 5.0+

### 安装步骤

1. 克隆仓库

```bash
git clone https://github.com/your-username/red-envelope-rain.git
cd red-envelope-rain
```

2. 初始化数据库

```bash
mysql -u root -p red_envelope_rain < sql/ry_20250522.sql
mysql -u root -p red_envelope_rain < sql/coupon_activity_simplified.sql
```

3. 启动后端服务

```bash
# Windows
ry.bat

# Linux/Mac
./ry.sh
```

4. 启动前端

```bash
cd rain-of-coupon
npm install && npm run dev
```

访问 `http://localhost:3000` 查看应用。

## 项目文档

- [项目结构](./docs/project-structure.md) - 详细的项目组织结构
- [API 规范](./docs/api-specification.md) - REST API 接口文档
- [数据库设计](./docs/database-design.md) - 数据库架构和设计
- [部署指南](./docs/deployment-guide.md) - 生产环境部署说明
- [用户流程](./docs/user-flow.md) - 页面流程和组件说明

## 核心功能

### 红包雨系统

- 30 秒内 100 个红包下落
- 基于点击数量的概率算法（点击越多中奖率越高）
- 每用户每日最多 3 次参与机会
- 中奖后自动停止

### 防刷机制

- IP 频率限制（每小时 4 次）
- 基于唯一会话 ID 的用户跟踪
- 实时流量监控和优先级处理
- 数据库事务一致性
- 点击数量验证和追踪
- 蜜罐字段密码保护

### 用户体验

- 流畅的动画和过渡效果
- 移动设备响应式设计
- 实时反馈和通知
- 直观的导航流程

## 贡献指南

欢迎为红包雨系统贡献代码。

### 开发环境设置

1. Fork 仓库
2. 创建功能分支: `git checkout -b feature/amazing-feature`
3. 提交更改: `git commit -m 'Add amazing feature'`
4. 推送到分支: `git push origin feature/amazing-feature`
5. 提交 Pull Request

### 代码规范

- 后端遵循 Java 编码规范
- 前端使用 TypeScript 和 Vue 3 Composition API
- 为新功能编写单元测试
- 根据需要更新文档

## 许可证

本项目采用 MIT 许可证。详情见 [LICENSE](./LICENSE) 文件。
