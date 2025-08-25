package com.ruoyi.redpacket.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketEventConfig;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;
import com.ruoyi.redpacket.mapper.RedpacketEventConfigMapper;
import com.ruoyi.redpacket.mapper.RedpacketPrizeMapper;
import com.ruoyi.redpacket.mapper.RedpacketUserParticipationLogMapper;
import com.ruoyi.redpacket.service.ILotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 抽奖服务实现类 - 混合规则：一共只能中一次 + 每天三次参与机会
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Service
public class LotteryServiceImpl implements ILotteryService {
    
    @Autowired
    private RedpacketUserParticipationLogMapper participationLogMapper;
    
    @Autowired
    private RedpacketPrizeMapper prizeMapper;
    
    @Autowired
    private RedpacketEventConfigMapper eventConfigMapper;
    
    private final Random random = new Random();
    
    @Override
    public boolean checkDrawEligibility(Long userId, String ipAddress) {
        Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
        logger.info("🔍 [抽奖资格检查] 开始检查 - userId: {}, ipAddress: {}", userId, ipAddress);
        
        // 1. 检查活动是否有效
        boolean activityValid = isActivityValid();
        logger.info("📅 [抽奖资格检查] 活动有效性检查: {}", activityValid);
        if (!activityValid) {
            logger.warn("❌ [抽奖资格检查] 活动无效，检查失败");
            return false;
        }
        
        // 2. 检查用户是否已经中过奖（核心规则：一共只能中一次） - 根据需求注释掉此限制
        // boolean hasWon = hasEverWon(userId);
        // logger.info("🏆 [抽奖资格检查] 用户是否已中奖: {}", hasWon);
        // if (hasWon) {
        //     logger.warn("❌ [抽奖资格检查] 用户已中过奖，检查失败");
        //     return false;
        // }
        
        // 3. 检查今日参与次数（每天3次机会）
        int remainingCount = getTodayRemainingCount(userId);
        logger.info("🎯 [抽奖资格检查] 剩余抽奖次数: {}", remainingCount);
        if (remainingCount <= 0) {
            logger.warn("❌ [抽奖资格检查] 今日抽奖次数已用完，检查失败");
            return false;
        }
        
        // 4. 检查IP频率限制（1小时内同一IP最多10次）
        boolean ipLimited = checkIpFrequencyLimit(ipAddress);
        logger.info("🌐 [抽奖资格检查] IP频率限制检查: {}", ipLimited);
        if (ipLimited) {
            logger.warn("❌ [抽奖资格检查] IP频率超限，检查失败");
            return false;
        }
        
        logger.info("✅ [抽奖资格检查] 所有检查通过，用户具备抽奖资格");
        return true;
    }
    
    /**
     * 执行抽奖 - 基于红包雨点击数量
     * @param userId 用户ID
     * @param clickedCount 本轮游戏中点击的红包数量（1-100）
     * @return 抽奖结果
     */
    @Override
    @Transactional
    public DrawResult draw(Long userId, String ipAddress, int clickedCount, String sessionId) {
        // 获取所有可用奖品
        List<RedpacketPrize> availablePrizes = getAvailablePrizes();
        
        if (availablePrizes.isEmpty()) {
            return new DrawResult(false, "奖品已发完，感谢参与！");
        }
        
        // 检查用户是否已经中过奖 - 根据需求注释掉此限制
        // if (hasEverWon(userId)) {
        //     // 已中奖用户参与，但强制未中奖
        //     return new DrawResult(false, "感谢参与，继续体验红包雨吧！");
        // }
        
        // 执行基于点击数量的概率抽奖算法
        RedpacketPrize wonPrize = executeClickBasedProbabilityDraw(clickedCount, availablePrizes);
        
        if (wonPrize != null) {
            // 扣减奖品库存
            wonPrize.setRemainingCount(wonPrize.getRemainingCount() - 1);
            prizeMapper.updateRedpacketPrize(wonPrize);
            
            return new DrawResult(true, wonPrize.getPrizeName(), wonPrize.getId(), 
                    wonPrize.getPrizeName(), "恭喜您中奖了！");
        } else {
            return new DrawResult(false, "很遗憾，未中奖，请再接再厉！");
        }
    }
    
    @Override
    public void saveDrawRecord(Long userId, DrawResult result, String ipAddress, String sessionId, int clickedCount) {
        Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
        logger.info("💾 [保存记录] 开始保存抽奖记录 - userId: {}, isWin: {}, clickedCount: {}, sessionId: {}", 
                   userId, result.isWin(), clickedCount, sessionId);
        
        RedpacketUserParticipationLog log = new RedpacketUserParticipationLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        // log.setSessionId(sessionId); // 数据库表暂无session_id字段，先注释
        log.setIsWin(result.isWin() ? 1 : 0);
        log.setParticipationTime(new Date());
        log.setClickedCount(clickedCount);
        log.setIsUsed(0); // 默认未使用
        
        if (result.isWin()) {
            log.setPrizeId(result.getPrizeId());
            log.setPrizeName(result.getPrizeName());
            logger.info("🏆 [保存记录] 中奖记录 - prizeId: {}, prizeName: {}", result.getPrizeId(), result.getPrizeName());
        }
        
        logger.info("💾 [保存记录] 准备插入数据库 - log对象详情:");
        logger.info("💾 [保存记录]   userId: {}", log.getUserId());
        logger.info("💾 [保存记录]   ipAddress: {}", log.getIpAddress());
        // logger.info("💾 [保存记录]   sessionId: {}", log.getSessionId()); // 暂时注释
        logger.info("💾 [保存记录]   isWin: {}", log.getIsWin());
        logger.info("💾 [保存记录]   clickedCount: {}", log.getClickedCount());
        logger.info("💾 [保存记录]   participationTime: {}", log.getParticipationTime());
        
        try {
            int insertResult = participationLogMapper.insertRedpacketUserParticipationLog(log);
            logger.info("✅ [保存记录] 数据库插入成功 - 影响行数: {}, 生成的ID: {}", insertResult, log.getId());
            
            if (insertResult <= 0) {
                logger.error("❌ [保存记录] 数据库插入失败 - 影响行数为0");
                throw new RuntimeException("数据库插入失败");
            }
        } catch (Exception e) {
            logger.error("❌ [保存记录] 数据库插入异常", e);
            logger.error("❌ [保存记录] 异常详情: {}", e.getMessage());
            throw e;
        }
    }
    @Override
    public int getTodayParticipationsCount(Long userId) {
        RedpacketUserParticipationLog query = new RedpacketUserParticipationLog();
        query.setUserId(userId);
        return participationLogMapper.countUserTodayParticipations(query);
    }

    @Override
    public int getTodayRemainingCount(Long userId) {
        Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
        
        // 获取活动配置的每日参与次数限制
        RedpacketEventConfig config = getEventConfig();
        int maxDrawsPerDay = config != null ? config.getMaxDrawsPerDay().intValue() : 0; // 从数据库配置读取，默认为3
        logger.info("📊 [剩余次数] 每日最大抽奖次数: {}", maxDrawsPerDay);
        
        // 查询今日已参与次数
        int todayDrawCount = getTodayParticipationCount(userId);
        logger.info("📊 [剩余次数] 今日已参与次数: {}", todayDrawCount);
        
        int remaining = Math.max(0, maxDrawsPerDay - todayDrawCount);
        logger.info("📊 [剩余次数] 计算结果: {}", remaining);
        
        return remaining;
    }
    
    @Override
    public boolean hasEverWon(Long userId) {
        // 核心方法：检查用户是否曾经中过奖（不限日期）
        RedpacketUserParticipationLog queryLog = new RedpacketUserParticipationLog();
        queryLog.setUserId(userId);
        queryLog.setIsWin(1);
        
        List<RedpacketUserParticipationLog> winLogs = 
            participationLogMapper.selectRedpacketUserParticipationLogList(queryLog);
        
        return !winLogs.isEmpty();
    }
    
    @Override
    public boolean hasWonToday(Long userId) {
        RedpacketUserParticipationLog queryLog = new RedpacketUserParticipationLog();
        queryLog.setUserId(userId);
        queryLog.setIsWin(1);
        
        List<RedpacketUserParticipationLog> logs = 
            participationLogMapper.selectRedpacketUserParticipationLogList(queryLog);
        
        String today = DateUtils.dateTimeNow("yyyy-MM-dd");
        return logs.stream()
                .anyMatch(log -> DateUtils.dateTime(log.getParticipationTime()).startsWith(today));
    }
    
    @Override
    public List<RedpacketPrize> getAvailablePrizes() {
        RedpacketPrize queryPrize = new RedpacketPrize();
        List<RedpacketPrize> allPrizes = prizeMapper.selectRedpacketPrizeList(queryPrize);
        
        // 过滤出有库存的奖品
        return allPrizes.stream()
                .filter(prize -> prize.getRemainingCount() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Object> getUserDrawRecords(Long userId) {
        RedpacketUserParticipationLog queryLog = new RedpacketUserParticipationLog();
        queryLog.setUserId(userId);
        
        return participationLogMapper.selectRedpacketUserParticipationLogList(queryLog)
                .stream()
                .map(log -> (Object) log)
                .collect(Collectors.toList());
    }
    
    @Override
    // 在 isActivityValid 方法中添加日志
    public boolean isActivityValid() {
    Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
    
    RedpacketEventConfig config = getEventConfig();
    logger.info("📋 [活动配置] 获取到的配置: {}", config);
    
    if (config == null) {
        logger.warn("❌ [活动配置] 配置为空");
        return false;
    }
    
    Date now = new Date();
    boolean isValid = now.after(config.getStartTime()) && now.before(config.getEndTime());
    logger.info("⏰ [活动配置] 当前时间: {}, 开始时间: {}, 结束时间: {}, 活动有效: {}", 
                now, config.getStartTime(), config.getEndTime(), isValid);
    
    return isValid;
    }
    
    /**
     * 执行基于点击数量的概率抽奖算法 - 红包雨模式（增强版）
     * @param clickedCount 本轮游戏中点击的红包数量（1-100）
     * @param prizes 可用奖品列表
     * @return 中奖奖品，null表示未中奖
     */
    private RedpacketPrize executeClickBasedProbabilityDraw(int clickedCount, List<RedpacketPrize> prizes) {
        Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
        logger.info("🎯 [抽奖算法] 开始执行抽奖 - 点击数量: {}, 可用奖品数: {}", clickedCount, prizes.size());
        
        if (clickedCount <= 0) {
            logger.info("🎯 [抽奖算法] 点击数量为0，返回未中奖");
            return null;
        }
        
        if (prizes.isEmpty()) {
            logger.info("🎯 [抽奖算法] 没有可用奖品，返回未中奖");
            return null;
        }
        
        // 打印所有奖品信息
        for (RedpacketPrize prize : prizes) {
            logger.info("🎁 [奖品信息] ID: {}, 名称: {}, 概率: {}, 剩余: {}", 
                       prize.getId(), prize.getPrizeName(), prize.getProbability(), prize.getRemainingCount());
        }
        
        // 限制点击数量上限
        clickedCount = Math.min(100, clickedCount);
        
        // 基于点击数量计算概率加成系数（提高倍率）
        // 使用对数函数 + 线性加成，让概率增长更明显
        // 点击1个：1.0倍，点击10个：约2.0倍，点击50个：约4.0倍，点击100个：约6.0倍
        double logMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 1.5; // 从0.8提高到1.5
        
        // 线性加成：每点击5个红包，额外增加0.2倍概率（从每10个0.1倍提高到每5个0.2倍）
        double linearBonus = (clickedCount / 5.0) * 0.2;
        
        // 额外的平方根加成，让高点击数量更有优势
        double sqrtBonus = Math.sqrt(clickedCount) * 0.3;
        
        // 最终概率系数
        double finalMultiplier = logMultiplier + linearBonus + sqrtBonus;
        
        // 设置概率上限，但比之前更高
        finalMultiplier = Math.min(finalMultiplier, 8.0); // 从4.0提高到8.0
        
        logger.info("🎯 [概率加成] 点击数: {}, 对数倍率: {:.2f}, 线性加成: {:.2f}, 平方根加成: {:.2f}, 最终倍率: {:.2f}", 
                   clickedCount, logMultiplier, linearBonus, sqrtBonus, finalMultiplier);
        
        // 计算调整后的总概率权重
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (RedpacketPrize prize : prizes) {
            if (prize.getProbability() != null && prize.getProbability().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal adjustedProbability = prize.getProbability()
                        .multiply(BigDecimal.valueOf(finalMultiplier));
                totalWeight = totalWeight.add(adjustedProbability);
                logger.info("🎯 [概率计算] 奖品: {}, 原始概率: {}, 调整后概率: {}, 累计权重: {}", 
                           prize.getPrizeName(), prize.getProbability(), adjustedProbability, totalWeight);
            }
        }
        
        logger.info("🎯 [概率计算] 总权重: {}", totalWeight);
        
        // 如果总权重为0，返回未中奖
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("🎯 [抽奖算法] 总权重为0，返回未中奖");
            return null;
        }
        
        // 生成0到总权重之间的随机数
        double randomValue = random.nextDouble();
        BigDecimal randomWeight = totalWeight.multiply(BigDecimal.valueOf(randomValue));
        
        logger.info("🎯 [随机抽取] 随机值: {:.4f}, 随机权重: {}", randomValue, randomWeight);
        
        // 根据调整后的权重选择奖品
        BigDecimal currentWeight = BigDecimal.ZERO;
        for (RedpacketPrize prize : prizes) {
            if (prize.getProbability() != null && prize.getProbability().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal adjustedProbability = prize.getProbability()
                        .multiply(BigDecimal.valueOf(finalMultiplier));
                currentWeight = currentWeight.add(adjustedProbability);
                logger.info("🎯 [权重判断] 奖品: {}, 当前权重: {}, 随机权重: {}", 
                           prize.getPrizeName(), currentWeight, randomWeight);
                
                if (randomWeight.compareTo(currentWeight) <= 0) {
                    logger.info("🏆 [中奖结果] 恭喜中奖！奖品: {}, 最终倍率: {:.2f}", prize.getPrizeName(), finalMultiplier);
                    return prize;
                }
            }
        }
        
        logger.info("😔 [抽奖结果] 未中奖，最终倍率: {:.2f}", finalMultiplier);
        return null; // 未中奖
    }
    
    @Override
    public List<RedpacketPrize> getAllPrizes() {
        return prizeMapper.selectRedpacketPrizeList(new RedpacketPrize());
    }
    
    /**
     * 获取今日参与次数
     */
    private int getTodayParticipationCount(Long userId) {
        RedpacketUserParticipationLog queryLog = new RedpacketUserParticipationLog();
        queryLog.setUserId(userId);
        
        List<RedpacketUserParticipationLog> logs = 
            participationLogMapper.selectRedpacketUserParticipationLogList(queryLog);
        
        String today = DateUtils.dateTimeNow("yyyy-MM-dd");
        long count = logs.stream()
                .filter(log -> DateUtils.dateTime(log.getParticipationTime()).startsWith(today))
                .count();
        
        return (int) count;
    }
    
    /**
     * 检查IP频率限制
     */
    // 如果这个方法存在，添加调试日志
    private boolean checkIpFrequencyLimit(String ipAddress) {
    Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
    logger.info("🌐 [IP限制] 检查IP: {}", ipAddress);
    
    // 这里添加具体的IP频率检查逻辑和日志
    RedpacketUserParticipationLog queryLog = new RedpacketUserParticipationLog();
    queryLog.setIpAddress(ipAddress);
    
    List<RedpacketUserParticipationLog> logs = 
        participationLogMapper.selectRedpacketUserParticipationLogList(queryLog);
    
    // 检查1小时内的请求次数
    Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
    long recentCount = logs.stream()
            .filter(log -> log.getParticipationTime().after(oneHourAgo))
            .count();
    
    logger.info("🌐 [IP限制] 1小时内请求次数: {}", recentCount);
    // 如果1小时内请求次数超过阈值（例如3次），则触发限制
    if (recentCount >= 3) {
        logger.info("🚫 [IP限制] 触发限制，IP: {}", ipAddress);
        return true;
    }
    
    return recentCount >= 10L; // 1小时内超过10次则限制
    }
    
    /**
     * 获取活动配置
     */
    private RedpacketEventConfig getEventConfig() {
        List<RedpacketEventConfig> configs = 
            eventConfigMapper.selectRedpacketEventConfigList(new RedpacketEventConfig());
        return configs.isEmpty() ? null : configs.get(0);
    }


    @Override
    public List<RedpacketUserParticipationLog> getUserParticipationLogs(Long userId) {
        RedpacketUserParticipationLog query = new RedpacketUserParticipationLog();
        query.setUserId(userId);
        return participationLogMapper.selectRedpacketUserParticipationLogList(query);
    }

    @Override
    public boolean isCrowded(String ipAddress) {
        // 实现流量检查逻辑，例如检查IP在一定时间内的请求次数
        // 这里假设使用Redis或其他方式计数，示例为简单实现
        // 实际中需要注入RedisTemplate或其他计数器
        // return checkIpFrequencyLimit(ipAddress); // 复用现有方法，如果有
        return false; // 临时返回false，需实现实际逻辑
    }

    @Override
    public Map<String, Object> checkPrizeStock() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有奖品
            RedpacketPrize queryPrize = new RedpacketPrize();
            List<RedpacketPrize> allPrizes = prizeMapper.selectRedpacketPrizeList(queryPrize);
            
            // 检查是否还有库存
            boolean hasStock = allPrizes.stream()
                    .anyMatch(prize -> prize.getRemainingCount() > 0);
            
            // 构建奖品列表
            List<Map<String, Object>> prizeList = allPrizes.stream()
                    .map(prize -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", prize.getId());
                        map.put("prizeName", prize.getPrizeName());
                        map.put("totalCount", prize.getTotalCount());
                        map.put("remainingCount", prize.getRemainingCount());
                        return map;
                    }).collect(Collectors.toList());
            
            result.put("hasStock", hasStock);
            result.put("prizes", prizeList);
            
        } catch (Exception e) {
            LoggerFactory.getLogger(LotteryServiceImpl.class).error("检查奖品库存失败", e);
            result.put("hasStock", true); // 出错时默认有库存，避免阻塞用户
            result.put("prizes", new ArrayList<>());
        }
        
        return result;
    }
    @Override
    public boolean hasParticipatedInSession(Long userId, String sessionId) {
        // 暂时注释，因为数据库表没有session_id字段
        // RedpacketUserParticipationLog query = new RedpacketUserParticipationLog();
        // query.setUserId(userId);
        // query.setSessionId(sessionId);
        // return participationLogMapper.countUserParticipationsBySessionId(query) > 0;
        return false; // 暂时返回false，表示未参与过
    }
    @Override
    public Map<String, Object> getCurrentActiveRound() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RedpacketEventConfig config = getEventConfig();
            if (config == null) {
                result.put("id", 1);
                result.put("roundNumber", 1);
                result.put("isRecycleRound", false);
                result.put("startTime", new Date());
                result.put("endTime", new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
                result.put("isActive", true);
                result.put("message", "默认轮次");
                return result;
            }
            
            Date now = new Date();
            
            // 检查第一轮是否活跃
            if (config.getFirstRoundStart() != null && config.getFirstRoundEnd() != null) {
                if (now.after(config.getFirstRoundStart()) && now.before(config.getFirstRoundEnd())) {
                    result.put("id", 1);
                    result.put("roundNumber", 1);
                    result.put("isRecycleRound", false);
                    result.put("startTime", config.getFirstRoundStart());
                    result.put("endTime", config.getFirstRoundEnd());
                    result.put("isActive", true);
                    result.put("message", "第一轮抽奖进行中");
                    return result;
                }
            }
            
            // 检查第二轮是否活跃
            if (config.getSecondRoundStart() != null && config.getSecondRoundEnd() != null) {
                if (now.after(config.getSecondRoundStart()) && now.before(config.getSecondRoundEnd())) {
                    result.put("id", 2);
                    result.put("roundNumber", 2);
                    result.put("isRecycleRound", true);
                    result.put("startTime", config.getSecondRoundStart());
                    result.put("endTime", config.getSecondRoundEnd());
                    result.put("isActive", true);
                    result.put("message", "第二轮回流抽奖进行中");
                    return result;
                }
            }
            
            // 如果都不在活跃时间内，返回最近的轮次信息
            if (config.getFirstRoundStart() != null && now.before(config.getFirstRoundStart())) {
                result.put("id", 1);
                result.put("roundNumber", 1);
                result.put("isRecycleRound", false);
                result.put("startTime", config.getFirstRoundStart());
                result.put("endTime", config.getFirstRoundEnd());
                result.put("isActive", false);
                result.put("message", "第一轮抽奖尚未开始");
            } else if (config.getSecondRoundStart() != null && now.before(config.getSecondRoundStart())) {
                result.put("id", 2);
                result.put("roundNumber", 2);
                result.put("isRecycleRound", true);
                result.put("startTime", config.getSecondRoundStart());
                result.put("endTime", config.getSecondRoundEnd());
                result.put("isActive", false);
                result.put("message", "第二轮抽奖尚未开始");
            } else {
                result.put("id", 1);
                result.put("roundNumber", 1);
                result.put("isRecycleRound", false);
                result.put("startTime", config.getFirstRoundStart());
                result.put("endTime", config.getFirstRoundEnd());
                result.put("isActive", false);
                result.put("message", "活动已结束");
            }
            
        } catch (Exception e) {
            LoggerFactory.getLogger(LotteryServiceImpl.class).error("获取当前轮次失败", e);
            // 返回默认轮次信息
            result.put("id", 1);
            result.put("roundNumber", 1);
            result.put("isRecycleRound", false);
            result.put("startTime", new Date());
            result.put("endTime", new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
            result.put("isActive", true);
            result.put("message", "默认轮次");
        }
        
        return result;
    }

}