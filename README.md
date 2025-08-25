# Red Envelope Rain Mini Program

A interactive lottery mini-program built with Vue 3 and Spring Boot, featuring real-time red envelope rain animations and intelligent probability algorithms. Built on RuoYi framework with front-end and back-end separation architecture.

**Language**: [English](./README.md) | [中文](./README.cn.md)

<img width="1199" height="756" alt="4204285decf504c5b4d78ba0faece377" src="https://github.com/user-attachments/assets/9f3ef837-6ee6-48dc-a1f4-84d06d6c46b7" />

## Features

- Real-time red envelope rain with click-based probability system
- Daily participation limits with anti-fraud mechanisms
- Traffic control and congestion detection
- Multiple rounds of voting rewards
- Admin dashboard for prize and user management
- Anonymous user access support

## Pages

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d2e0c30d-af28-4b50-a8dd-5b409daeecb3" width="200"><br>
      Main Dashboard
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8a841fce-f5bd-455d-aa9c-2be97f212620" width="200"><br>
      Guest View – Rules & Coupon Pack
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/37017394-d83d-41ee-8a92-3f14516028fd" width="200"><br>
      Registered User View
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/26a0d42e-abef-46ac-8d2d-e18f53c73a63" width="200"><br>
      Coupon Page – Winning User
    </td>
  </tr>
  <tr>
     <td align="center">
      <img src="https://github.com/user-attachments/assets/59a6803d-c1f7-4926-956d-d71deb245e2a" width="200"><br>
      Challenge Page – Coupon Display
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/307de466-9e0c-4413-b829-df89f2009d27" width="200"><br>
      Anti-Abuse – Limit Multiple Draws
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/9a9982a0-19d2-4720-9285-1a6ae3bcfb89" width="200"><br>
      Red Packet Entry
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/39dbfcbc-f070-4846-af1d-a5f53460e142" width="200"><br>
      Winning Confirmation
    </td>
  </tr>
  <tr>
   <td align="center">
      <img src="https://github.com/user-attachments/assets/3c08099d-8b61-44b1-b636-8e6c15d43dec" width="200"><br>
      Anti-Fraud – Same Session ID
    </td> 
    <td align="center">
      <img src="https://github.com/user-attachments/assets/08bf5932-b64c-41b2-aca9-e5217cbb60b6" width="200"><br>
      Post-Win Coupon Sync
    </td>
  </tr>
</table>

## Tech Stack

- **Frontend**: Vue 3, TypeScript, Pinia, Vite
- **Backend**: Spring Boot, MyBatis, Redis, Spring Security, Ruoyi Framework
- **Database**: MySQL 5.7+
- **Deployment**: Docker, Nginx

## Quick Start

### Edit the files
1. [line 11: Set your own SQL password](./ruoyi-admin/src/main/resources/application-druid.yml)
2. [line 34: Set your IPv4 address for Management Frontend](./ruoyi-ui/vue.config.js)
3. [line 3: Set your IPv4 address for Main Frontend](./rain-of-coupon/.env.development)
   
### Prerequisites

- Java 8+
- Node.js 14+
- MySQL 5.7+
- Redis 5.0+

### Installation

1. Clone the repository
```bash
git clone https://github.com/your-username/red-envelope-rain.git
cd red-envelope-rain
```

2. Initialize database
```bash
mysql -u root -p red_envelope_rain < sql/ry_20250522.sql
mysql -u root -p red_envelope_rain < sql/coupon_activity_simplified.sql
```

3. Start backend service
```bash
# Windows
ry.bat

# Linux/Mac
./ry.sh
```

4. Start frontend
```bash
cd rain-of-coupon
npm install && npm run dev
```

Visit `http://localhost:3000` to access the application.

## Documentation

- [Project Structure](./docs/project-structure.md) - Detailed project organization
- [API Specification](./docs/api-specification.md) - REST API documentation
- [Database Design](./docs/database-design.md) - Database schema and design
- [Deployment Guide](./docs/deployment-guide.md) - Production deployment instructions
- [User Flow](./docs/user-flow.md) - Page flow and component guide
- [Security Analysis](./docs/security-analysis.md) - Image resource security analysis
- [Security Fix](./docs/security-fix-implementation.md) - Security fix implementation guide

## Core Features

### Red Envelope Rain System
- 100 red envelopes fall in 30 seconds
- Click-based probability algorithm (more clicks = higher win rate)
- Daily limit of 3 attempts per user
- Automatic stop after winning

### Anti-Fraud Mechanisms
- IP frequency limits (4 times per hour)
- Session-based user tracking with unique Session ID
- Real-time traffic monitoring with priority handling
- Database transaction consistency
- Click count validation and tracking
- Honeypot fields for password protection

### User Experience
- Smooth animations and transitions
- Responsive design for mobile devices
- Real-time feedback and notifications
- Intuitive navigation flow

## Contributing

We welcome contributions to improve the Red Envelope Rain system.

### Development Setup

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Standards

- Follow Java coding conventions for backend
- Use TypeScript and Vue 3 Composition API for frontend
- Write unit tests for new features
- Update documentation as needed

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
