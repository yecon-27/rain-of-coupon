-- 1. 奖品配置表
DROP TABLE IF EXISTS `redpacket_prize`;
CREATE TABLE `redpacket_prize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `prize_name` varchar(100) NOT NULL COMMENT '奖品名称',
  `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '奖品总数量',
  `remaining_count` int(11) NOT NULL DEFAULT '0' COMMENT '剩余数量',
  `probability` decimal(5,4) NOT NULL COMMENT '中奖概率(0-1)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='奖品配置表';

-- 2. 用户抽奖记录表（防刷票设计）
DROP TABLE IF EXISTS `redpacket_user_prize_log`;
CREATE TABLE `redpacket_user_prize_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `prize_name` varchar(100) DEFAULT NULL COMMENT '奖品名称（未中奖时为空）',
  `is_win` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否中奖(0未中奖 1中奖)',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用(0未使用 1已使用)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '抽奖时间',
  `ip_address` varchar(45) NOT NULL COMMENT 'IP记录（支持IPv6）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_user_ip_time` (`user_id`, `ip_address`, `created_at`),
  KEY `idx_user_win_used` (`user_id`, `is_win`, `is_used`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户抽奖记录表';

-- 3. 活动配置表
DROP TABLE IF EXISTS `redpacket_event_config`;
CREATE TABLE `redpacket_event_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `max_users` int(11) NOT NULL DEFAULT '1000' COMMENT '并发用户上限',
  `max_draws_per_day` int(11) NOT NULL DEFAULT '3' COMMENT '每日最大抽奖次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='活动配置表';

-- 4. 图片资源表
DROP TABLE IF EXISTS `redpacket_image_resource`;
CREATE TABLE `redpacket_image_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(100) NOT NULL COMMENT '资源名称',
  `resource_key` varchar(100) NOT NULL COMMENT '资源标识',
  `file_name` varchar(200) NOT NULL COMMENT '文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件访问路径',
  `usage_scene` varchar(100) DEFAULT NULL COMMENT '使用场景',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_key` (`resource_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='图片资源表';

-- 5. TOP10网络人气特色美食表
DROP TABLE IF EXISTS `redpacket_top10_popular_food`;
CREATE TABLE `redpacket_top10_popular_food` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '美食ID',
  `food_name` varchar(100) NOT NULL COMMENT '美食名称',
  `ranking` int(11) NOT NULL COMMENT '排名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ranking` (`ranking`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='TOP10网络人气特色美食表';

-- 6. "一镇一品"特色菜表
DROP TABLE IF EXISTS `redpacket_town_specialty_food`;
CREATE TABLE `redpacket_town_specialty_food` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '特色菜ID',
  `food_name` varchar(100) NOT NULL COMMENT '美食名称',
  `ranking` int(11) NOT NULL COMMENT '排名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='"一镇一品"特色菜表';

-- ========== 数据插入 ==========

-- 插入奖品配置数据
INSERT INTO `redpacket_prize` (`prize_name`, `total_count`, `remaining_count`, `probability`) VALUES
('188元优惠券', 100, 100, 0.1000),
('588元优惠券', 50, 50, 0.0500),
('888元优惠券', 20, 20, 0.0100);

-- 插入活动配置数据
INSERT INTO `redpacket_event_config` (`start_time`, `end_time`, `max_users`, `max_draws_per_day`) VALUES
('2025-02-06 00:00:00', '2025-02-28 23:59:59', 1000, 3);

-- 插入图片资源数据
INSERT INTO `redpacket_image_resource` (`resource_name`, `resource_key`, `file_name`, `file_path`, `usage_scene`, `description`) VALUES
('背景图', 'home_bg', 'home.png', '/images/coupon/home.png', '主页', '主页背景图'),
('立即挑战按钮', 'challenge_btn', 'button.png', '/images/coupon/button.png', '主页', '立即挑战按钮'),
('规则按钮', 'rule_btn', 'gz.png', '/images/coupon/gz.png', '主页面', '规则按钮'),
('通用规则按钮', 'common_rule_btn', 'hdgz.png', '/images/coupon/hdgz.png', '通用', '通用规则按钮'),
('券包按钮', 'coupon_btn', 'qb.png', '/images/coupon/qb.png', '通用', '券包按钮'),
('红包雨来袭', 'redpacket_rain', 'zbh.png', '/images/coupon/zbh.png', '红包雨组件', '一大波红包雨即将来袭'),
('一秒特效', 'countdown_1', '1.png', '/images/coupon/1.png', '倒计时组件', '一秒特效'),
('二秒特效', 'countdown_2', '2.png', '/images/coupon/2.png', '倒计时组件', '二秒特效'),
('三秒特效', 'countdown_3', '3.png', '/images/coupon/3.png', '倒计时组件', '三秒特效'),
('计时按钮', 'timer_btn', 'ds.png', '/images/coupon/ds.png', '红包雨组件', '计时按钮'),
('红包个数', 'packet_count', 'sl.png', '/images/coupon/sl.png', '红包雨组件', '红包个数显示'),
('福气+1按钮', 'luck_plus_btn', '福气+1.png', '/images/coupon/福气+1.png', '鼓励提示组件', '福气+1按钮'),
('活动拥挤', 'crowding_tip', '活动拥挤.png', '/images/coupon/活动拥挤.png', '拥挤提示组件', '活动拥挤提示图'),
('加载动画', 'loading_gif', '加载.gif', '/images/coupon/加载.gif', '加载页面', '加载动画'),
('188元中奖图', 'prize_188', '188.png', '/images/coupon/188.png', '中奖弹窗', '188元中奖图'),
('588元中奖图', 'prize_588', '588.png', '/images/coupon/588.png', '中奖弹窗', '588元中奖图'),
('888元中奖图', 'prize_888', '888.png', '/images/coupon/888.png', '中奖弹窗', '888元中奖图'),
('188优惠券', 'coupon_188', '满500且消费一道特色菜可使用.png', '/images/coupon/满500且消费一道特色菜可使用.png', '券包页面', '188优惠券展示'),
('588优惠券', 'coupon_588', '满1500且消费一道特色菜可使用.png', '/images/coupon/满1500且消费一道特色菜可使用.png', '券包页面', '588优惠券展示'),
('888优惠券', 'coupon_888', '满2500且消费一道特色菜可使用.png', '/images/coupon/满2500且消费一道特色菜可使用.png', '券包页面', '888优惠券展示'),
('参与挑战获取', 'challenge_get', 'cytzhq.png', '/images/coupon/cytzhq.png', '券包页面', '参与挑战获取提示图'),
('展示菜品', 'show_dishes', 'zscp.png', '/images/coupon/zscp.png', '主页面', '展示菜品图');

-- 插入TOP10网络人气特色美食数据
INSERT INTO `redpacket_top10_popular_food` (`food_name`, `ranking`) VALUES
('榴莲炖鸡', 1),
('白切牛肉', 2),
('葱油鸭', 3),
('瑞安传统鱼丸汤', 4),
('石斛鸡汤', 5),
('湖岭精品牛肉', 6),
('盘香鳝鱼', 7),
('鹿木卖贴锅', 8),
('江蟹八宝饭', 9),
('炭烤蒜蓉生蚝', 10);

-- 插入"一镇一品"特色菜数据
INSERT INTO `redpacket_town_specialty_food` (`food_name`, `ranking`) VALUES
('陈年花雕沙蒜网潮炖软壳猷蠓', 1),
('虾饼', 2),
('桐浦山外牛骨', 3),
('滋补黄牛排粉干', 4),
('鲜炒黄牛拆骨肉', 5),
('红烧田鱼', 6),
('猪肚仙人球', 7),
('曹村进士姜酒索面', 8),
('高楼炒双粉', 9),
('瑞安传统鱼丸汤', 10),
('海苔白虾', 11),
('麻油鸭', 12),
('滋补牛尾巴', 13),
('金丝榴莲虾球', 14),
('海鲜饼', 15),
('鳗胶甜汤', 16),
('陈恒发什锦馅肉炊糕', 17),
('石斛鸡汤', 18),
('盘香鳝鱼', 19),
('汤臣一品招牌炭烤羊排', 20),
('酒炖猷蠓', 21);

-- 创建索引
CREATE INDEX idx_redpacket_user_prize_log_user_id ON redpacket_user_prize_log(user_id);
CREATE INDEX idx_redpacket_image_resource_key ON redpacket_image_resource(resource_key);
CREATE INDEX idx_redpacket_top10_food_ranking ON redpacket_top10_popular_food(ranking);
CREATE INDEX idx_redpacket_town_food_ranking ON redpacket_town_specialty_food(ranking);

COMMIT;
