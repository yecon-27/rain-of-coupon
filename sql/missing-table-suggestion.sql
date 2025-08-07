-- 建议添加：用户参与记录表
-- 用于记录每次用户参与红包雨的行为（无论是否中奖）

DROP TABLE IF EXISTS `redpacket_user_participation_log`;
CREATE TABLE `redpacket_user_participation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `participation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
  `ip_address` varchar(45) NOT NULL COMMENT 'IP记录（防刷）',
  `is_win` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否中奖(0未中奖 1中奖)',
  `prize_id` bigint(20) DEFAULT NULL COMMENT '中奖奖品ID（未中奖时为空）',
  `session_id` varchar(100) DEFAULT NULL COMMENT '会话ID（防刷）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_participation_time` (`participation_time`),
  KEY `idx_user_date` (`user_id`, `participation_time`),
  FOREIGN KEY (`prize_id`) REFERENCES `redpacket_prize`(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户参与记录表';

-- 这样就可以正确统计参与次数了：
-- 1. 今日参与次数
SELECT COUNT(*) FROM redpacket_user_participation_log 
WHERE user_id = ? AND DATE(participation_time) = CURDATE();

-- 2. 历史总参与次数  
SELECT COUNT(*) FROM redpacket_user_participation_log 
WHERE user_id = ?;

-- 3. 今日中奖次数
SELECT COUNT(*) FROM redpacket_user_participation_log 
WHERE user_id = ? AND DATE(participation_time) = CURDATE() AND is_win = 1;