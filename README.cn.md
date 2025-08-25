# 红包雨小程序

基于 Vue 3 和 Spring Boot 开发的交互式抽奖小程序，具有实时红包雨动画和智能概率算法。采用前后端分离架构，基于若依框架构建。

**语言**: [English](./README.md) | [中文](./README.cn.md)

<img width="1199" height="756" alt="4204285decf504c5b4d78ba0faece377" src="https://github.com/user-attachments/assets/9f3ef837-6ee6-48dc-a1f4-84d06d6c46b7" />


## 功能特性

- 基于点击数量的实时红包雨概率系统
- 每日参与限制和防刷机制
- 流量控制和拥堵检测
- 多轮投票回流奖品
- 奖品和用户管理后台
- 匿名用户访问支持

## 界面预览
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d2e0c30d-af28-4b50-a8dd-5b409daeecb3" width="200"><br>
      主页面全览 
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8a841fce-f5bd-455d-aa9c-2be97f212620" width="200"><br>
      未登录状态下的规则，券包（需登录）
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/37017394-d83d-41ee-8a92-3f14516028fd" width="200"><br>
      注册用户
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/26a0d42e-abef-46ac-8d2d-e18f53c73a63" width="200"><br>
      中奖用户打开奖券页面
    </td>
  </tr>
  <tr>
     <td align="center">
      <img src="https://github.com/user-attachments/assets/59a6803d-c1f7-4926-956d-d71deb245e2a" width="200"><br>
      中奖用户打开立即挑战页面的奖券显示
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/307de466-9e0c-4413-b829-df89f2009d27" width="200"><br>
      同一个用户短期抽奖两次防止刷票
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/9a9982a0-19d2-4720-9285-1a6ae3bcfb89" width="200"><br>
      抢红包进入页面
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/39dbfcbc-f070-4846-af1d-a5f53460e142" width="200"><br>
      中奖页面
    </td>
  </tr>
  <tr>
   <td align="center">
      <img src="https://github.com/user-attachments/assets/3c08099d-8b61-44b1-b636-8e6c15d43dec" width="200"><br>
      不同用户同一个窗口id防刷票
    </td> 
    <td align="center">
      <img src="https://github.com/user-attachments/assets/08bf5932-b64c-41b2-aca9-e5217cbb60b6" width="200"><br>
      中奖后卡券同步状态
    </td>
  </tr>
</table>


## 技术栈

- **前端**: Vue 3, TypeScript, Pinia, Vite
- **后端**: Spring Boot, MyBatis, Redis, Spring Security, RuoyiFramework
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
- [安全分析](./docs/security-analysis.md) - 图片资源安全问题分析
- [安全修复](./docs/security-fix-implementation.md) - 安全问题修复实施方案

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
