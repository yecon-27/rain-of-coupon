-- 新增用户参与记录表
-- 在原有表结构基础上，新增这个表来记录所有参与行为

-- 创建用户参与记录表
DROP TABLE IF EXISTS `redpacket_user_participation_log`;
CREATE TABLE `redpacket_user_participation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `participation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
  `ip_address` varchar(45) NOT NULL COMMENT 'IP记录（防刷）',
  `is_win` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否中奖(0未中奖 1中奖)',
  `prize_id` bigint(20) DEFAULT NULL COMMENT '中奖奖品ID（未中奖时为空）',
  `prize_name` varchar(100) DEFAULT NULL COMMENT '奖品名称（未中奖时为空）',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用(0未使用 1已使用)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_participation_time` (`participation_time`),
  KEY `idx_user_date` (`user_id`, `participation_time`),
  KEY `idx_user_win_used` (`user_id`, `is_win`, `is_used`),
  KEY `idx_ip_address` (`ip_address`),
  FOREIGN KEY (`prize_id`) REFERENCES `redpacket_prize`(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户参与记录表（记录所有参与行为）';

-- 创建索引优化查询性能
CREATE INDEX idx_redpacket_user_participation_log_user_id ON redpacket_user_participation_log(user_id);

COMMIT;