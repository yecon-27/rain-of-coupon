-- 添加 clicked_count 和 session_id 字段到 redpacket_user_participation_log 表
-- 执行时间：2024年8月

-- 添加点击数量字段
ALTER TABLE `redpacket_user_participation_log` 
ADD COLUMN `clicked_count` int(11) DEFAULT 0 COMMENT '点击红包数量' AFTER `is_used`;

-- 添加会话ID字段
ALTER TABLE `redpacket_user_participation_log` 
ADD COLUMN `session_id` varchar(100) DEFAULT NULL COMMENT '会话ID（防重复抽奖）' AFTER `clicked_count`;

-- 添加新的索引
CREATE INDEX idx_session_id ON redpacket_user_participation_log(session_id);
CREATE INDEX idx_clicked_count ON redpacket_user_participation_log(clicked_count);
CREATE INDEX idx_user_win_used ON redpacket_user_participation_log(user_id, is_win, is_used);

-- 添加数据完整性约束
ALTER TABLE redpacket_user_participation_log 
ADD CONSTRAINT chk_clicked_count 
CHECK (clicked_count >= 0 AND clicked_count <= 100);

-- 添加会话ID格式约束（可选，根据实际需求启用）
-- ALTER TABLE redpacket_user_participation_log 
-- ADD CONSTRAINT chk_session_format 
-- CHECK (session_id IS NULL OR session_id REGEXP '^user_[0-9]+_[0-9]+_[a-zA-Z0-9]+$');

-- 更新现有记录的默认值（如果有数据的话）
UPDATE redpacket_user_participation_log 
SET clicked_count = 0 
WHERE clicked_count IS NULL;

COMMIT;