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
        
        // 2. 检查用户是否已经中过奖（核心规则：一共只能中一次）
        boolean hasWon = hasEverWon(userId);
        logger.info("🏆 [抽奖资格检查] 用户是否已中奖: {}", hasWon);
        if (hasWon) {
            logger.warn("❌ [抽奖资格检查] 用户已中过奖，检查失败");
            return false;
        }
        
        // 3. 检查今日参与次数（每天3次机会）
        int remainingCount = getRemainingDrawCount(userId);
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
    public DrawResult executeDraw(Long userId, int clickedCount) {
        // 获取所有可用奖品
        List<RedpacketPrize> availablePrizes = getAvailablePrizes();
        
        if (availablePrizes.isEmpty()) {
            return new DrawResult(false, "奖品已发完，感谢参与！");
        }
        
        // 检查用户是否已经中过奖
        if (hasEverWon(userId)) {
            // 已中奖用户参与，但强制未中奖
            return new DrawResult(false, "感谢参与，继续体验红包雨吧！");
        }
        
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
    public void saveDrawRecord(Long userId, DrawResult result, String ipAddress, int clickedCount) {
        LoggerFactory.getLogger(LotteryServiceImpl.class).info("开始保存抽奖记录 - userId: {}, isWin: {}, clickedCount: {}", userId, result.isWin(), clickedCount);
        
        RedpacketUserParticipationLog log = new RedpacketUserParticipationLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setIsWin(result.isWin() ? 1 : 0);
        log.setParticipationTime(new Date());
        log.setClickedCount(clickedCount);
        
        if (result.isWin()) {
            log.setPrizeId(result.getPrizeId());
            log.setPrizeName(result.getPrizeName());
            LoggerFactory.getLogger(LotteryServiceImpl.class).info("中奖记录 - prizeId: {}, prizeName: {}", result.getPrizeId(), result.getPrizeName());
        }
        
        LoggerFactory.getLogger(LotteryServiceImpl.class).info("准备插入数据库 - log对象: {}", log.toString());
        
        try {
            int insertResult = participationLogMapper.insertRedpacketUserParticipationLog(log);
            LoggerFactory.getLogger(LotteryServiceImpl.class).info("数据库插入结果: {}, 生成的ID: {}", insertResult, log.getId());
        } catch (Exception e) {
            LoggerFactory.getLogger(LotteryServiceImpl.class).error("数据库插入失败", e);
            throw e;
        }
    }
    
    @Override
    public int getRemainingDrawCount(Long userId) {
        Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);
        
        // 获取活动配置的每日参与次数限制
        RedpacketEventConfig config = getEventConfig();
        int maxDrawsPerDay = config != null ? config.getMaxDrawsPerDay().intValue() : 3;
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
     * 执行基于点击数量的概率抽奖算法 - 红包雨模式
     * @param clickedCount 本轮游戏中点击的红包数量（1-100）
     * @param prizes 可用奖品列表
     * @return 中奖奖品，null表示未中奖
     */
    private RedpacketPrize executeClickBasedProbabilityDraw(int clickedCount, List<RedpacketPrize> prizes) {
        // 如果没有点击任何红包，直接返回未中奖
        if (clickedCount <= 10) {
            return null;
        }
        
        // 限制点击数量上限
        clickedCount = Math.min(100, clickedCount);
        
        // 基于点击数量计算概率加成系数
        // 使用对数函数，让概率增长更平滑且有上限
        // 点击1个：1.0倍，点击10个：约1.5倍，点击50个：约2.5倍，点击100个：约3.0倍
        double probabilityMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 0.8;
        
        // 额外的线性加成，鼓励多点击
        // 每点击10个红包，额外增加0.1倍概率
        double linearBonus = (clickedCount / 10.0) * 0.1;
        
        // 最终概率系数
        double finalMultiplier = probabilityMultiplier + linearBonus;
        
        // 设置概率上限，避免过高
        finalMultiplier = Math.min(finalMultiplier, 4.0);
        
        // 计算调整后的总概率权重
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (RedpacketPrize prize : prizes) {
            if (prize.getProbability() == null) {
                continue; // 跳过概率为null的奖品
            }
            BigDecimal adjustedProbability = prize.getProbability()
                    .multiply(BigDecimal.valueOf(finalMultiplier));
            totalWeight = totalWeight.add(adjustedProbability);
        }
        
        // 生成随机数
        double randomValue = random.nextDouble();
        BigDecimal randomWeight = totalWeight.multiply(BigDecimal.valueOf(randomValue));
        
        // 根据调整后的权重选择奖品
        BigDecimal currentWeight = BigDecimal.ZERO;
        for (RedpacketPrize prize : prizes) {
            BigDecimal adjustedProbability = prize.getProbability()
                    .multiply(BigDecimal.valueOf(finalMultiplier));
            currentWeight = currentWeight.add(adjustedProbability);
            if (randomWeight.compareTo(currentWeight) <= 0) {
                return prize;
            }
        }
        
        return null; // 未中奖
    }
    
    /**
     * 执行加权随机抽奖算法（原始版本，保留备用）
     */
    private RedpacketPrize executeWeightedRandom(List<RedpacketPrize> prizes) {
        // 计算总概率权重
        BigDecimal totalWeight = prizes.stream()
                .map(RedpacketPrize::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 生成随机数
        double randomValue = random.nextDouble();
        BigDecimal randomWeight = totalWeight.multiply(BigDecimal.valueOf(randomValue));
        
        // 根据权重选择奖品
        BigDecimal currentWeight = BigDecimal.ZERO;
        for (RedpacketPrize prize : prizes) {
            currentWeight = currentWeight.add(prize.getProbability());
            if (randomWeight.compareTo(currentWeight) <= 0) {
                return prize;
            }
        }
        
        return null; // 未中奖
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

}