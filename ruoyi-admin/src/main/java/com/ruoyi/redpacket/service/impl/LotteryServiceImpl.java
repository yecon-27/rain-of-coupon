package com.ruoyi.redpacket.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketEventConfig;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.domain.RedpacketUserPrizeLog;
import com.ruoyi.redpacket.mapper.RedpacketEventConfigMapper;
import com.ruoyi.redpacket.mapper.RedpacketPrizeMapper;
import com.ruoyi.redpacket.mapper.RedpacketUserPrizeLogMapper;
import com.ruoyi.redpacket.service.ILotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 抽奖服务实现类
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class LotteryServiceImpl implements ILotteryService {
    
    @Autowired
    private RedpacketUserPrizeLogMapper userPrizeLogMapper;
    
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
        
        // 2. 检查用户今日是否已中奖
        if (hasWonToday(userId)) {
            return false;
        }
        
        // 3. 检查今日抽奖次数
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
        RedpacketUserPrizeLog log = new RedpacketUserPrizeLog();
        log.setUserId(userId);
        log.setPrizeName(result.isWin() ? result.getPrizeName() : null);
        log.setIsWin(result.isWin() ? 1 : 0);
        log.setIsUsed(0); // 默认未使用
        log.setCreatedAt(new Date());
        log.setIpAddress(ipAddress);
        
        userPrizeLogMapper.insertRedpacketUserPrizeLog(log);
    }
    
    @Override
    public int getRemainingDrawCount(Long userId) {
        // 获取活动配置的每日抽奖次数限制
        RedpacketEventConfig config = getEventConfig();
        int maxDrawsPerDay = config != null ? config.getMaxDrawsPerDay() : 3;
        
        // 查询今日已抽奖次数
        int todayDrawCount = getTodayDrawCount(userId);
        
        return Math.max(0, maxDrawsPerDay - todayDrawCount);
    }
    
    @Override
    public boolean hasWonToday(Long userId) {
        RedpacketUserPrizeLog queryLog = new RedpacketUserPrizeLog();
        queryLog.setUserId(userId);
        queryLog.setIsWin(1);
        
        List<RedpacketUserPrizeLog> logs = userPrizeLogMapper.selectRedpacketUserPrizeLogList(queryLog);
        
        // 检查今日是否有中奖记录
        String today = DateUtils.dateTimeNow("yyyy-MM-dd");
        return logs.stream().anyMatch(log -> 
            DateUtils.dateTime(log.getCreatedAt()).startsWith(today));
    }
    
    @Override
    public List<RedpacketPrize> getAvailablePrizes() {
        RedpacketPrize queryPrize = new RedpacketPrize();
        List<RedpacketPrize> allPrizes = prizeMapper.selectRedpacketPrizeList(queryPrize);
        
        // 过滤出有库存的奖品
        return allPrizes.stream()
                .filter(prize -> prize.getRemainingCount() > 0)
                .toList();
    }
    
    @Override
    public List<Object> getUserDrawRecords(Long userId) {
        RedpacketUserPrizeLog queryLog = new RedpacketUserPrizeLog();
        queryLog.setUserId(userId);
        
        return userPrizeLogMapper.selectRedpacketUserPrizeLogList(queryLog)
                .stream()
                .map(log -> (Object) log)
                .toList();
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
     * 获取今日抽奖次数
     */
    private int getTodayDrawCount(Long userId) {
        RedpacketUserPrizeLog queryLog = new RedpacketUserPrizeLog();
        queryLog.setUserId(userId);
        
        List<RedpacketUserPrizeLog> logs = userPrizeLogMapper.selectRedpacketUserPrizeLogList(queryLog);
        
        String today = DateUtils.dateTimeNow("yyyy-MM-dd");
        return (int) logs.stream()
                .filter(log -> DateUtils.dateTime(log.getCreatedAt()).startsWith(today))
                .count();
    }
    
    /**
     * 检查IP频率限制
     */
    private boolean checkIpFrequencyLimit(String ipAddress) {
        // 这里可以使用Redis实现更精确的频率限制
        // 暂时使用数据库查询实现
        RedpacketUserPrizeLog queryLog = new RedpacketUserPrizeLog();
        queryLog.setIpAddress(ipAddress);
        
        List<RedpacketUserPrizeLog> logs = userPrizeLogMapper.selectRedpacketUserPrizeLogList(queryLog);
        
        // 检查1小时内的请求次数
        Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        long recentCount = logs.stream()
                .filter(log -> log.getCreatedAt().after(oneHourAgo))
                .count();
        
        return recentCount >= 10; // 1小时内超过10次则限制
    }
    
    /**
     * 获取活动配置
     */
    private RedpacketEventConfig getEventConfig() {
        List<RedpacketEventConfig> configs = eventConfigMapper.selectRedpacketEventConfigList(new RedpacketEventConfig());
        return configs.isEmpty() ? null : configs.get(0);
    }
}