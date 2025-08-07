package com.ruoyi.redpacket.service;

import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import java.util.List;

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
     * @return 抽奖结果
     */
    DrawResult executeDraw(Long userId);
    
    /**
     * 保存抽奖记录到数据库
     * 
     * @param userId 用户ID
     * @param result 抽奖结果
     * @param ipAddress IP地址
     */
    void saveDrawRecord(Long userId, DrawResult result, String ipAddress);
    
    /**
     * 获取用户今日剩余抽奖次数
     * 
     * @param userId 用户ID
     * @return 剩余次数
     */
    int getRemainingDrawCount(Long userId);
    
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
     * 获取可用奖品列表
     * 
     * @return 奖品列表
     */
    List<RedpacketPrize> getAvailablePrizes();
    
    /**
     * 获取用户抽奖记录
     * 
     * @param userId 用户ID
     * @return 抽奖记录列表
     */
    List<Object> getUserDrawRecords(Long userId);
    
    /**
     * 检查活动是否有效
     * 
     * @return 是否有效
     */
    boolean isActivityValid();
}