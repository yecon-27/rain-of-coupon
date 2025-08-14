
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

CREATE TABLE redpacket_traffic_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(200) NOT NULL COMMENT '配置值',
    description VARCHAR(500) NULL COMMENT '配置描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='红包流量控制配置表';

-- 修改流量控制配置的插入语句，使用不同的默认值
INSERT INTO redpacket_traffic_config (config_key, config_value, description) VALUES
('max_concurrent_users', '800', '流量控制最大并发用户数（技术层面限制）'),
('queue_timeout_seconds', '300', '队列超时时间（秒）'),
('heartbeat_timeout_seconds', '60', '心跳超时时间（秒）'),
('maintenance_mode', 'false', '维护模式开关'),
('rush_hour_start', '11', '高峰期开始时间（小时）'),
('rush_hour_end', '20', '高峰期结束时间（小时）'),
('rush_hour_max_users', '600', '高峰期最大用户数');

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