-- 1. 奖品配置表
DROP TABLE IF EXISTS `redpacket_prize`;
CREATE TABLE `prize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `prize_name` varchar(100) NOT NULL COMMENT '奖品名称',
  `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '奖品总数量',
  `remaining_count` int(11) NOT NULL DEFAULT '0' COMMENT '剩余数量',
  `probability` decimal(5,4) NOT NULL COMMENT '中奖概率(0-1)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='奖品配置表';

-- 2. 用户抽奖记录表
DROP TABLE IF EXISTS `redpacket_user_prize_log`;
CREATE TABLE `user_prize_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `prize_name` varchar(100) DEFAULT NULL COMMENT '奖品名称',
  `draw_time` datetime NOT NULL COMMENT '中奖时间',
  `use_status` char(1) DEFAULT '0' COMMENT '使用状态(0未使用 1已使用)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户抽奖记录表';

-- 3. 图片资源表
DROP TABLE IF EXISTS `redpacket_image_resource`;
CREATE TABLE `image_resource` (
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

-- 4. TOP10网络人气特色美食表
DROP TABLE IF EXISTS `redpacket_top10_popular_food`;
CREATE TABLE `top10_popular_food` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '美食ID',
  `food_name` varchar(100) NOT NULL COMMENT '美食名称',
  `ranking` int(11) NOT NULL COMMENT '排名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ranking` (`ranking`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='TOP10网络人气特色美食表';

-- 5. "一镇一品"特色菜表
DROP TABLE IF EXISTS `redpacket_town_specialty_food`;
CREATE TABLE `town_specialty_food` (
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
CREATE INDEX idx_user_prize_log_user_id ON user_prize_log(user_id);
CREATE INDEX idx_image_resource_key ON image_resource(resource_key);
CREATE INDEX idx_top10_food_ranking ON top10_popular_food(ranking);
CREATE INDEX idx_town_food_ranking ON town_specialty_food(ranking);

COMMIT;

-- ========== 表结构说明 ==========
-- 
-- 1. prize（奖品配置）
--    - 奖品名称、奖品总数、剩余数量、中奖概率
-- 
-- 2. user_prize_log（用户抽奖记录）
--    - 用户ID、奖品名称、中奖时间、使用状态
-- 
-- 3. image_resource（图片静态资源）
--    - 资源名称、资源标识、文件名、文件访问路径、使用场景、描述
-- 
-- 4. user_info（若依内置）
--    - 用户名、密码、手机号（使用若依系统自带的sys_user表）
-- 
-- 5. top10_popular_food（TOP10网络人气特色美食）
--    - 美食名称、排名
-- 
-- 6. town_specialty_food（"一镇一品"特色菜）
--    - 美食名称、排名
-- 
-- ========== 图片文件部署说明 ==========
-- 
-- 图片存储目录：ruoyi-admin/src/main/resources/static/images/coupon/
-- 
-- 需要的图片文件（共22个）：
-- home.png, button.png, gz.png, hdgz.png, qb.png, zbh.png
-- 1.png, 2.png, 3.png, ds.png, sl.png, 福气+1.png
-- 活动拥挤.png, 加载.gif, 188.png, 588.png, 888.png
-- 满500且消费一道特色菜可使用.png, 满1500且消费一道特色菜可使用.png, 满2500且消费一道特色菜可使用.png
-- cytzhq.png, zscp.png