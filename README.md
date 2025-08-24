# Red Envelope Rain Mini Program

A interactive lottery mini-program built with Vue 3 and Spring Boot, featuring real-time red envelope rain animations and intelligent probability algorithms.

**Language**: [English](./README.md) | [中文](./README.cn.md)

## Features

- Real-time red envelope rain with click-based probability system
- Daily participation limits with anti-fraud mechanisms
- Traffic control and congestion detection
- Responsive design with smooth animations
- Admin dashboard for prize and user management
- Anonymous user access support

## Tech Stack

- **Frontend**: Vue 3, TypeScript, Pinia, Vite
- **Backend**: Spring Boot, MyBatis, Redis
- **Database**: MySQL 5.7+
- **Deployment**: Docker, Nginx

## Quick Start

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
