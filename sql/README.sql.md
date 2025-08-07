# 红包雨抽奖活动数据库设计文档

## 表结构说明

### 1. prize（奖品配置）
- 奖品名称、奖品总数、剩余数量、中奖概率

### 2. user_prize_log（用户抽奖记录）
- 用户ID、奖品名称、是否中奖、是否使用、抽奖时间、IP记录

### 3. redpacket_event_config（活动配置）
- 活动开始时间、活动结束时间、并发用户上限、每日最大抽奖次数

### 4. image_resource（图片静态资源）
- 资源名称、资源标识、文件名、文件访问路径、使用场景、描述

### 5. user_info（若依内置）
- 用户名、密码、手机号（使用若依系统自带的sys_user表）

### 6. top10_popular_food（TOP10网络人气特色美食）
- 美食名称、排名

### 7. town_specialty_food（"一镇一品"特色菜）
- 美食名称、排名

### 8. redpacket_user_participation_log（红包雨用户参与记录）
- 用户ID、IP地址、是否中奖、参与时间、奖品ID、奖品名称
- 记录每次用户参与红包雨游戏的详细信息
- 支持基于点击数量的中奖概率计算

## 图片文件部署说明

**图片存储目录：** `ruoyi-admin/src/main/resources/static/images/coupon/`

**需要的图片文件（共22个）：**
- home.png, button.png, gz.png, hdgz.png, qb.png, zbh.png
- 1.png, 2.png, 3.png, ds.png, sl.png, 福气+1.png
- 活动拥挤.png, 加载.gif, 188.png, 588.png, 888.png
- 满500且消费一道特色菜可使用.png, 满1500且消费一道特色菜可使用.png, 满2500且消费一道特色菜可使用.png
- cytzhq.png, zscp.png

## 防刷票业务逻辑说明

### 1. 活动时间检查
```sql
SELECT start_time, end_time FROM redpacket_event_config WHERE id = 1;
```

### 2. 每日抽奖次数限制（从配置表获取限制）
```sql
SELECT max_draws_per_day FROM redpacket_event_config WHERE id = 1;
SELECT COUNT(*) FROM user_prize_log 
WHERE user_id = ? AND DATE(created_at) = CURDATE();
```

### 3. 并发用户限制检查
```sql
SELECT max_users FROM redpacket_event_config WHERE id = 1;
SELECT COUNT(DISTINCT user_id) FROM user_prize_log 
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE);
```

### 4. IP地址限制（防止同一IP大量抽奖）
```sql
SELECT COUNT(*) FROM user_prize_log 
WHERE ip_address = ? AND created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR);
```

### 5. 用户中奖检查（一旦中奖就不能再抽奖）
```sql
SELECT COUNT(*) FROM user_prize_log 
WHERE user_id = ? AND is_win = 1;
```

### 6. 券包页面显示逻辑（只显示未使用的优惠券）
```sql
SELECT prize_name FROM user_prize_log 
WHERE user_id = ? AND is_win = 1 AND is_used = 0;
```

### 7. 优惠券使用状态更新
```sql
UPDATE user_prize_log SET is_used = 1 
WHERE user_id = ? AND prize_name = ? AND is_win = 1 AND is_used = 0;
```

### 8. 时间间隔限制（防止频繁请求）
```sql
SELECT created_at FROM user_prize_log 
WHERE user_id = ? ORDER BY created_at DESC LIMIT 1;
```

### 9. 性能优化建议
建议在Redis中缓存活动配置和用户状态，提高查询性能

## 红包雨抢红包业务流程

1. **检查活动是否在有效时间内** → 活动未开始或已结束则禁止参与
2. **检查并发用户数** → 超过上限显示拥挤提示
3. **检查用户是否已中奖** → 如果已中奖，禁止继续参与（一人只能中一次）
4. **检查今日游戏次数** → 如果超过配置限制（每日3次），禁止参与
5. **检查IP频率限制** → 防止恶意刷票（1小时内同IP最多10次）
6. **执行红包雨游戏** → 50秒内100个红包飘落，用户点击累积计数
7. **基于点击数量计算中奖概率** → 点击越多，中奖概率越大
8. **记录参与结果** → 保存到 `redpacket_user_participation_log` 表
9. **券包页面显示** → 只显示 `is_win=1 AND is_used=0` 的记录

### 红包雨概率算法
```java
// 基于点击数量的动态概率计算
double probabilityMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 0.8;
double finalProbability = baseProbability * Math.min(probabilityMultiplier, 4.0);
```

**示例**：
- 点击1个红包：基础概率 × 1.0倍
- 点击10个红包：基础概率 × 1.6倍  
- 点击50个红包：基础概率 × 2.7倍
- 点击100个红包：基础概率 × 4.0倍（上限）