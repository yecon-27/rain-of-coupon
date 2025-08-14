
## 1. 红包活动参与者表 (redpacket_activity_participants)

用于记录用户参与活动的历史记录和当前状态。

```sql
CREATE TABLE redpacket_activity_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NULL COMMENT '用户ID（可选，支持匿名用户）',
    session_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID',
    ip_address VARCHAR(45) NULL COMMENT 'IP地址',
    join_time DATETIME NOT NULL COMMENT '加入时间',
    leave_time DATETIME NULL COMMENT '离开时间',
    last_heartbeat DATETIME NOT NULL COMMENT '最后心跳时间',
    status ENUM('active', 'queued', 'expired', 'left') NOT NULL DEFAULT 'active' COMMENT '状态',
    queue_position INT NULL COMMENT '队列位置（仅排队时有效）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status_join_time (status, join_time),
    INDEX idx_last_heartbeat (last_heartbeat)
) COMMENT='红包活动参与者记录表';
```

## 2. 红包流量控制配置表 (redpacket_traffic_config)

用于存储流量控制的配置参数。

```sql
CREATE TABLE redpacket_traffic_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(200) NOT NULL COMMENT '配置值',
    description VARCHAR(500) NULL COMMENT '配置描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='红包流量控制配置表';

-- 插入默认配置
INSERT INTO redpacket_traffic_config (config_key, config_value, description) VALUES
('max_concurrent_users', '1000', '最大并发用户数'),
('queue_timeout_seconds', '300', '队列超时时间（秒）'),
('heartbeat_timeout_seconds', '60', '心跳超时时间（秒）'),
('maintenance_mode', 'false', '维护模式开关'),
('rush_hour_start', '11', '高峰期开始时间（小时）'),
('rush_hour_end', '20', '高峰期结束时间（小时）'),
('rush_hour_max_users', '800', '高峰期最大用户数');
```

## 3. 红包流量统计表 (redpacket_traffic_stats)

用于记录流量统计数据，便于分析和监控。

```sql
CREATE TABLE redpacket_traffic_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_time DATETIME NOT NULL COMMENT '统计时间',
    active_users INT NOT NULL DEFAULT 0 COMMENT '活跃用户数',
    queued_users INT NOT NULL DEFAULT 0 COMMENT '排队用户数',
    total_requests INT NOT NULL DEFAULT 0 COMMENT '总请求数',
    rejected_requests INT NOT NULL DEFAULT 0 COMMENT '被拒绝的请求数',
    average_session_time INT NULL COMMENT '平均会话时间（秒）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_stat_time (stat_time)
) COMMENT='红包流量统计表';
```

## 后端API实现建议

### 1. 流量检测接口 `/api/traffic/check`

```java
@GetMapping("/api/traffic/check")
public ResponseEntity<TrafficCheckResponse> checkTraffic() {
    // 1. 获取当前活跃用户数
    int activeUsers = participantService.getActiveUserCount();
    
    // 2. 获取配置的最大用户数
    int maxUsers = configService.getMaxConcurrentUsers();
    
    // 3. 判断是否维护模式
    if (configService.isMaintenanceMode()) {
        return ResponseEntity.ok(TrafficCheckResponse.maintenance());
    }
    
    // 4. 判断是否拥挤
    if (activeUsers >= maxUsers) {
        int queueCount = participantService.getQueuedUserCount();
        int estimatedWaitTime = calculateEstimatedWaitTime(queueCount, activeUsers);
        
        return ResponseEntity.ok(TrafficCheckResponse.crowded(
            activeUsers, maxUsers, queueCount + 1, estimatedWaitTime
        ));
    }
    
    return ResponseEntity.ok(TrafficCheckResponse.ok(activeUsers, maxUsers));
}
```

### 2. 加入活动接口 `/api/traffic/join`

```java
@PostMapping("/api/traffic/join")
public ResponseEntity<UserActivityResponse> joinActivity(@RequestBody UserActivityRequest request) {
    // 1. 检查会话是否已存在
    Optional<ActivityParticipant> existing = participantService.findBySessionId(request.getSessionId());
    if (existing.isPresent()) {
        return ResponseEntity.ok(UserActivityResponse.fromParticipant(existing.get()));
    }
    
    // 2. 检查当前活跃用户数
    int activeUsers = participantService.getActiveUserCount();
    int maxUsers = configService.getMaxConcurrentUsers();
    
    if (activeUsers < maxUsers) {
        // 可以直接加入
        ActivityParticipant participant = participantService.createActiveParticipant(
            request.getUserId(), request.getSessionId(), getClientIP()
        );
        return ResponseEntity.ok(UserActivityResponse.active(participant));
    } else {
        // 需要排队
        ActivityParticipant participant = participantService.createQueuedParticipant(
            request.getUserId(), request.getSessionId(), getClientIP()
        );
        return ResponseEntity.ok(UserActivityResponse.queued(participant));
    }
}
```

### 3. 心跳接口 `/api/traffic/heartbeat`

```java
@PostMapping("/api/traffic/heartbeat")
public ResponseEntity<UserActivityResponse> heartbeat(@RequestBody UserActivityRequest request) {
    Optional<ActivityParticipant> participant = participantService.findBySessionId(request.getSessionId());
    
    if (participant.isEmpty()) {
        return ResponseEntity.ok(UserActivityResponse.blocked());
    }
    
    // 更新心跳时间
    participantService.updateHeartbeat(participant.get());
    
    return ResponseEntity.ok(UserActivityResponse.fromParticipant(participant.get()));
}
```

### 4. 定时任务

```java
@Scheduled(fixedRate = 30000) // 每30秒执行一次
public void cleanupExpiredSessions() {
    // 1. 清理过期的活跃会话
    participantService.expireInactiveSessions();
    
    // 2. 从队列中提升用户
    participantService.promoteQueuedUsers();
    
    // 3. 记录统计数据
    statsService.recordCurrentStats();
}
```

## 关键实现要点

1. **并发控制**: 使用数据库锁或Redis分布式锁确保并发安全
2. **心跳机制**: 定期清理无心跳的会话，释放资源
3. **队列管理**: FIFO队列，优先处理等待时间最长的用户
4. **统计监控**: 记录关键指标，便于分析和调优
5. **配置热更新**: 支持动态调整限流参数
6. **IP限制**: 可选的IP级别限流，防止单个IP占用过多资源