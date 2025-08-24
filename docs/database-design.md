# 数据库设计

## 核心业务表

### 1. redpacket_prize（奖品配置表）

存储活动奖品信息和库存管理。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| name | varchar(100) | 奖品名称 |
| description | varchar(500) | 奖品描述 |
| total_quantity | int | 总数量 |
| remaining_quantity | int | 剩余数量 |
| win_probability | decimal(5,4) | 中奖概率 |
| status | tinyint | 状态（0停用 1启用） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### 2. redpacket_user_participation_log（用户参与记录表）

记录用户每次参与活动的详细信息。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| user_id | bigint | 用户ID |
| participation_time | datetime | 参与时间 |
| ip_address | varchar(45) | IP地址 |
| is_win | tinyint | 是否中奖（0否 1是） |
| prize_id | bigint | 中奖奖品ID |
| prize_name | varchar(100) | 奖品名称 |
| is_used | tinyint | 是否使用（0未使用 1已使用） |
| clicked_count | int | 点击红包数量（新增字段） |
| session_id | varchar(100) | 会话ID（防重复抽奖） |

### 3. redpacket_event_config（活动配置表）

管理活动的全局配置参数。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| event_name | varchar(100) | 活动名称 |
| start_time | datetime | 开始时间 |
| end_time | datetime | 结束时间 |
| daily_limit | int | 每日参与次数限制 |
| ip_hourly_limit | int | IP每小时限制次数 |
| max_concurrent_users | int | 最大并发用户数 |
| status | tinyint | 活动状态（0停用 1启用） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### 4. image_resource（图片资源表）

管理红包雨相关的静态图片资源。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| resource_name | varchar(100) | 资源名称 |
| resource_path | varchar(500) | 资源路径 |
| resource_type | varchar(20) | 资源类型 |
| file_size | bigint | 文件大小 |
| status | tinyint | 状态（0停用 1启用） |
| create_time | datetime | 创建时间 |

## 索引设计

### redpacket_user_participation_log 表索引

```sql
-- 用户ID + 参与时间索引（查询用户今日参与记录）
CREATE INDEX idx_user_time ON redpacket_user_participation_log(user_id, participation_time);

-- IP地址 + 参与时间索引（IP频率限制）
CREATE INDEX idx_ip_time ON redpacket_user_participation_log(ip_address, participation_time);

-- 中奖状态索引（统计中奖记录）
CREATE INDEX idx_is_win ON redpacket_user_participation_log(is_win);

-- 会话ID索引（防重复抽奖）
CREATE INDEX idx_session_id ON redpacket_user_participation_log(session_id);

-- 点击数量索引（概率分析）
CREATE INDEX idx_clicked_count ON redpacket_user_participation_log(clicked_count);

-- 复合索引（用户 + 中奖状态 + 使用状态）
CREATE INDEX idx_user_win_used ON redpacket_user_participation_log(user_id, is_win, is_used);
```

### redpacket_prize 表索引

```sql
-- 状态 + 剩余数量索引（查询可用奖品）
CREATE INDEX idx_status_remaining ON redpacket_prize(status, remaining_quantity);
```

## 数据一致性保证

### 库存扣减

使用Redis分布式锁 + 数据库事务确保库存扣减的原子性：

1. 获取Redis分布式锁
2. 检查数据库库存
3. 扣减库存
4. 记录中奖日志（包含clickedCount）
5. 更新会话状态
6. 释放锁

### 会话管理

#### Session ID 生成规则
```sql
-- 会话ID格式：user_{userId}_{timestamp}_{random}
-- 示例：user_12345_1640995200_abc123
```

#### 防重复抽奖检查
```sql
-- 检查同一会话是否已抽奖
SELECT COUNT(*) FROM redpacket_user_participation_log 
WHERE session_id = ? AND user_id = ?;
```

### 并发控制

- 使用数据库乐观锁防止库存超卖
- Redis缓存用户参与状态，减少数据库压力
- 事务隔离级别设置为READ_COMMITTED
- 并行API调用状态检查，提升响应速度
- Session ID唯一性约束防止重复提交

### 数据完整性

#### 点击数量验证
```sql
-- 点击数量合理性检查（1-100之间）
ALTER TABLE redpacket_user_participation_log 
ADD CONSTRAINT chk_clicked_count 
CHECK (clicked_count >= 1 AND clicked_count <= 100);
```

#### 会话有效性验证
```sql
-- 会话ID格式验证
ALTER TABLE redpacket_user_participation_log 
ADD CONSTRAINT chk_session_format 
CHECK (session_id REGEXP '^user_[0-9]+_[0-9]+_[a-zA-Z0-9]+$');
```