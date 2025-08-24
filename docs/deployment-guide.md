# 部署指南

## 环境要求

### 基础环境

- **Java**: JDK 1.8+
- **Node.js**: 14.x+
- **MySQL**: 5.7+
- **Redis**: 5.0+
- **Maven**: 3.6+

### 可选环境

- **Docker**: 20.10+
- **Nginx**: 1.18+

## 安装步骤

### 1. 克隆项目

```bash
git clone https://github.com/your-username/red-envelope-rain.git
cd red-envelope-rain
```

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE red_envelope_rain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入基础表结构
mysql -u root -p red_envelope_rain < sql/ry_20250522.sql

# 导入业务表结构
mysql -u root -p red_envelope_rain < sql/coupon_activity_simplified.sql
mysql -u root -p red_envelope_rain < sql/add-participation-log-table.sql
```

### 3. 后端配置

编辑 `ruoyi-admin/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/red_envelope_rain?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0
```

### 4. 启动后端服务

```bash
# Windows
ry.bat

# Linux/Mac
chmod +x ry.sh
./ry.sh
```

### 5. 前端配置与启动

#### 用户端前端（rain-of-coupon）

```bash
cd rain-of-coupon
npm install

# 开发环境
npm run dev

# 生产构建
npm run build
```

#### 管理后台前端（ruoyi-ui）

```bash
cd ruoyi-ui
npm install

# 开发环境
npm run dev

# 生产构建
npm run build:prod
```

## Docker 部署

### 1. 构建镜像

```bash
# 后端镜像
docker build -t red-envelope-backend .

# 前端镜像
cd rain-of-coupon
docker build -t red-envelope-frontend .
```

### 2. 使用 Docker Compose

创建 `docker-compose.yml`:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: red_envelope_rain
    ports:
      - "3306:3306"
    volumes:
      - ./sql:/docker-entrypoint-initdb.d

  redis:
    image: redis:5.0
    ports:
      - "6379:6379"

  backend:
    image: red-envelope-backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

  frontend:
    image: red-envelope-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

启动服务：

```bash
docker-compose up -d
```

## Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/red-envelope-frontend;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 生产环境优化

### 1. JVM 参数优化

```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -jar ruoyi-admin.jar
```

### 2. Redis 配置优化

```conf
# redis.conf
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

### 3. MySQL 配置优化

```ini
# my.cnf
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
query_cache_size = 64M
```

## 监控与日志

### 应用监控

- 使用 Spring Boot Actuator 监控应用健康状态
- 集成 Micrometer + Prometheus 进行指标收集

### 日志管理

- 使用 Logback 进行日志管理
- 配置日志轮转和压缩
- 重要业务操作记录到数据库

## 常见问题

### 1. 数据库连接失败

检查数据库配置和网络连接，确保MySQL服务正常运行。

### 2. Redis 连接超时

检查Redis配置和防火墙设置，确保端口6379可访问。

### 3. 前端页面空白

检查前端构建是否成功，确保静态资源路径正确。