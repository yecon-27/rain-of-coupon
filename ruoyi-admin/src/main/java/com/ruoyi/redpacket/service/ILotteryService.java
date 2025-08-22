package com.ruoyi.redpacket.service;
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;
import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import java.util.List;
import java.util.Map;

/**
 * 抽奖服务接口
 *
 * @author ruoyi
 * @date 2025-08-06
 */
public interface ILotteryService {
    
    /**
     * 检查用户抽奖资格（每日限制、已中奖、IP限制）
     *
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @return 是否有抽奖资格
     */
    boolean checkDrawEligibility(Long userId, String ipAddress);

    /**
     * 执行抽奖算法，概率控制
     *
     * @param userId 用户ID
     * @param clickedCount 点击次数
     * @param sessionId 会话ID
     * @return 抽奖结果
     */
    DrawResult draw(Long userId, String ipAddress, int clickedCount, String sessionId);
    
    /**
     * 保存抽奖记录到数据库
     * @param userId 用户ID
     * @param result 抽奖结果
     * @param ipAddress IP地址
     * @param sessionId 会话ID
     * @param clickedCount 点击次数
     */
    void saveDrawRecord(Long userId, DrawResult result, String ipAddress, String sessionId, int clickedCount);
    
    /**
     * 检查用户今日是否已中奖
     *
     * @param userId 用户ID
     * @return 是否已中奖
     */
    boolean hasWonToday(Long userId);

    /**
     * 检查用户是否曾经中过奖（混合规则核心方法）
     *
     * @param userId 用户ID
     * @return 是否曾经中奖
     */
    boolean hasEverWon(Long userId);

    /**
     * 检查用户是否在当前会话中已参与过
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否已参与
     */
    boolean hasParticipatedInSession(Long userId, String sessionId);

    /**
     * 获取可用奖品列表
     *
     * @return 奖品列表
     */
    List<RedpacketPrize> getAvailablePrizes();
    
    /**
     * 获取所有奖品列表
     *
     * @return 奖品列表
     */
    List<RedpacketPrize> getAllPrizes();
    
    /**
     * 获取用户今日参与次数
     *
     * @param userId 用户ID
     * @return 今日参与次数
     */
    int getTodayParticipationsCount(Long userId);

    /**
     * 获取用户今日剩余抽奖次数
     *
     * @param userId 用户ID
     * @return 剩余次数
     */
    int getTodayRemainingCount(Long userId);

    /**
     * 获取用户抽奖记录
     *
     * @param userId 用户ID
     * @return 抽奖记录列表
     */
    List<Object> getUserDrawRecords(Long userId);
    
    /**
     * 获取用户参与日志
     * @param userId 用户ID
     * @return 参与日志列表
     */
    List<RedpacketUserParticipationLog> getUserParticipationLogs(Long userId);

      /**
     * 检查是否拥挤（流量控制）
     * @param ipAddress IP地址
     * @return 是否拥挤
     */
    boolean isCrowded(String ipAddress);
    
    /**
     * 检查活动是否有效
     * 
     * @return 是否有效
     */
    boolean isActivityValid();

    /**
     * 检查奖品库存
     * 
     * @return 库存检查结果
     */
    Map<String, Object> checkPrizeStock();
    /**
     * 获取当前活跃轮次信息
     *
     * @return 轮次信息
     */
    Map<String, Object> getCurrentActiveRound();
}