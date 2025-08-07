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
        // 1. 检查活动是否有效
        if (!isActivityValid()) {
            return false;
        }
        
        // 2. 检查用户是否已经中过奖（核心规则：一共只能中一次）
        if (hasEverWon(userId)) {
            return false;
        }
        
        // 3. 检查今日参与次数（每天3次机会）
        if (getRemainingDrawCount(userId) <= 0) {
            return false;
        }
        
        // 4. 检查IP频率限制（1小时内同一IP最多10次）
        if (checkIpFrequencyLimit(ipAddress)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public DrawResult executeDraw(Long userId) {
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
        
        // 执行概率抽奖算法
        RedpacketPrize wonPrize = executeWeightedRandom(availablePrizes);
        
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
    public void saveDrawRecord(Long userId, DrawResult result, String ipAddress) {
        RedpacketUserParticipationLog log = new RedpacketUserParticipationLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setIsWin(result.isWin() ? 1 : 0);
        log.setParticipationTime(new Date());
        
        if (result.isWin()) {
            log.setPrizeId(result.getPrizeId());
            log.setPrizeName(result.getPrizeName());
        }
        
        participationLogMapper.insertRedpacketUserParticipationLog(log);
    }
    
    @Override
    public int getRemainingDrawCount(Long userId) {
        // 获取活动配置的每日参与次数限制
        RedpacketEventConfig config = getEventConfig();
        int maxDrawsPerDay = config != null ? config.getMaxDrawsPerDay().intValue() : 3;
        
        // 查询今日已参与次数
        int todayDrawCount = getTodayParticipationCount(userId);
        
        return Math.max(0, maxDrawsPerDay - todayDrawCount);
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
    public boolean isActivityValid() {
        RedpacketEventConfig config = getEventConfig();
        if (config == null) {
            return false;
        }
        
        Date now = new Date();
        return now.after(config.getStartTime()) && now.before(config.getEndTime());
    }
    
    /**
     * 执行加权随机抽奖算法
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
    private boolean checkIpFrequencyLimit(String ipAddress) {
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
}