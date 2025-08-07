-- 混合规则实现：一共只能中一次 + 每天三次参与机会
-- 用户总共只能中一次奖，但如果没中奖，每天都有3次参与机会

-- 1. 检查用户是否已经中过奖（历史任何时候）
-- 这是核心检查，决定用户是否还能中奖
SELECT COUNT(*) > 0 as hasEverWon
FROM redpacket_user_participation_log 
WHERE user_id = ? AND is_win = 1;

-- 2. 获取用户的中奖记录（应该最多只有一条）
SELECT id, prize_name, is_used, participation_time 
FROM redpacket_user_participation_log 
WHERE user_id = ? AND is_win = 1 
ORDER BY participation_time DESC
LIMIT 1;

-- 3. 统计今日参与次数（无论是否中奖都要统计）
SELECT COUNT(*) as todayDrawCount 
FROM redpacket_user_participation_log 
WHERE user_id = ? AND DATE(participation_time) = CURDATE();

-- 4. 统计历史总参与次数
SELECT COUNT(*) as totalDrawCount 
FROM redpacket_user_participation_log 
WHERE user_id = ?;

-- 5. 获取用户完整状态的查询
SELECT 
    COUNT(*) as totalParticipations,
    COUNT(CASE WHEN is_win = 1 THEN 1 END) as totalWins,
    COUNT(CASE WHEN DATE(participation_time) = CURDATE() THEN 1 END) as todayParticipations,
    MAX(CASE WHEN is_win = 1 THEN participation_time END) as winTime,
    MAX(CASE WHEN is_win = 1 THEN prize_name END) as wonPrizeName,
    (COUNT(CASE WHEN is_win = 1 THEN 1 END) = 0) as canStillWin,
    ((SELECT max_draws_per_day FROM redpacket_event_config WHERE id = 1) - 
     COUNT(CASE WHEN DATE(participation_time) = CURDATE() THEN 1 END)) as todayRemainingCount
FROM redpacket_user_participation_log 
WHERE user_id = ?;

-- 6. 检查用户今日是否还能参与（无论是否已中奖）
SELECT 
    (SELECT max_draws_per_day FROM redpacket_event_config WHERE id = 1) as maxDrawsPerDay,
    COUNT(*) as todayParticipations,
    ((SELECT max_draws_per_day FROM redpacket_event_config WHERE id = 1) - COUNT(*)) as remainingCount,
    (COUNT(*) < (SELECT max_draws_per_day FROM redpacket_event_config WHERE id = 1)) as canParticipateToday
FROM redpacket_user_participation_log 
WHERE user_id = ? 
AND DATE(participation_time) = CURDATE();

-- 7. 获取用户的参与历史（按日期分组）
SELECT 
    DATE(participation_time) as date,
    COUNT(*) as participations,
    COUNT(CASE WHEN is_win = 1 THEN 1 END) as wins,
    GROUP_CONCAT(CASE WHEN is_win = 1 THEN prize_name END) as won_prizes
FROM redpacket_user_participation_log 
WHERE user_id = ?
GROUP BY DATE(participation_time)
ORDER BY DATE(participation_time) DESC
LIMIT 30; -- 最近30天的记录

-- 8. 插入参与记录的逻辑

-- 未中奖的参与记录（正常情况）
INSERT INTO redpacket_user_participation_log 
(user_id, participation_time, ip_address, is_win, prize_id, prize_name, is_used) 
VALUES 
(?, NOW(), ?, 0, NULL, NULL, 0);

-- 中奖的参与记录（仅当用户从未中过奖时）
-- 后端需要先检查 hasEverWon = false
INSERT INTO redpacket_user_participation_log 
(user_id, participation_time, ip_address, is_win, prize_id, prize_name, is_used) 
VALUES 
(?, NOW(), ?, 1, 1, '188元优惠券', 0);

-- 9. 获取用户可用的优惠券（未使用的中奖记录）
SELECT 
    id,
    prize_name,
    participation_time as win_time,
    is_used
FROM redpacket_user_participation_log 
WHERE user_id = ? 
AND is_win = 1 
ORDER BY participation_time DESC;