package com.ruoyi.redpacket.service.impl;

import java.util.List;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketTrafficStatsMapper;
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;
import com.ruoyi.redpacket.service.IRedpacketTrafficStatsService;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;

/**
 * 红包流量统计Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@Service
public class RedpacketTrafficStatsServiceImpl implements IRedpacketTrafficStatsService 
{
    private static final Logger logger = LoggerFactory.getLogger(RedpacketTrafficStatsServiceImpl.class);
    
    @Autowired
    private RedpacketTrafficStatsMapper redpacketTrafficStatsMapper;
    
    // 添加参与者服务注入
    @Autowired
    private IRedpacketActivityParticipantsService participantsService;

    /**
     * 查询红包流量统计
     * 
     * @param id 红包流量统计主键
     * @return 红包流量统计
     */
    @Override
    public RedpacketTrafficStats selectRedpacketTrafficStatsById(Long id)
    {
        return redpacketTrafficStatsMapper.selectRedpacketTrafficStatsById(id);
    }

    /**
     * 查询红包流量统计列表
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 红包流量统计
     */
    @Override
    public List<RedpacketTrafficStats> selectRedpacketTrafficStatsList(RedpacketTrafficStats redpacketTrafficStats)
    {
        return redpacketTrafficStatsMapper.selectRedpacketTrafficStatsList(redpacketTrafficStats);
    }

    /**
     * 新增红包流量统计
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 结果
     */
    @Override
    public int insertRedpacketTrafficStats(RedpacketTrafficStats redpacketTrafficStats)
    {
        return redpacketTrafficStatsMapper.insertRedpacketTrafficStats(redpacketTrafficStats);
    }

    /**
     * 修改红包流量统计
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 结果
     */
    @Override
    public int updateRedpacketTrafficStats(RedpacketTrafficStats redpacketTrafficStats)
    {
        return redpacketTrafficStatsMapper.updateRedpacketTrafficStats(redpacketTrafficStats);
    }

    /**
     * 批量删除红包流量统计
     * 
     * @param ids 需要删除的红包流量统计主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTrafficStatsByIds(Long[] ids)
    {
        return redpacketTrafficStatsMapper.deleteRedpacketTrafficStatsByIds(ids);
    }

    /**
     * 删除红包流量统计信息
     * 
     * @param id 红包流量统计主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTrafficStatsById(Long id)
    {
        return redpacketTrafficStatsMapper.deleteRedpacketTrafficStatsById(id);
    }

    /**
     * 批量插入流量统计数据
     * 
     * @param statsList 统计数据列表
     * @return 结果
     */
    @Override
    public int batchInsertRedpacketTrafficStats(List<RedpacketTrafficStats> statsList)
    {
        if (statsList == null || statsList.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (RedpacketTrafficStats stats : statsList) {
            try {
                int result = redpacketTrafficStatsMapper.insertRedpacketTrafficStats(stats);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他数据
                logger.error("批量插入统计数据失败: {}", stats, e);
            }
        }
        return successCount;
    }
    
    /**
     * 自动记录当前系统流量统计
     */
    @Override
    public void autoRecordCurrentTrafficStats()
    {
        try {
            // 获取真实的系统数据
            int activeUsers = participantsService.getActiveUserCount();
            int queuedUsers = participantsService.getQueuedUserCount();
            
            // 获取最近的统计记录来计算请求数
            Date now = new Date();
            Date oneHourAgo = new Date(now.getTime() - 3600000); // 一小时前
            List<RedpacketTrafficStats> recentStats = selectStatsByTimeRange(oneHourAgo, now);
            
            // 计算总请求数（基于最近的活动）
            long totalRequests = recentStats.isEmpty() ? activeUsers + queuedUsers : 
                recentStats.get(recentStats.size() - 1).getTotalRequests() + activeUsers;
            
            // 计算被拒绝的请求数（排队用户可视为暂时被拒绝）
            long rejectedRequests = queuedUsers;
            
            // 计算平均会话时间（基于活跃用户的估算）
            long averageSessionTime = activeUsers > 0 ? 300L : 180L; // 活跃时300秒，否则180秒
            
            RedpacketTrafficStats stats = new RedpacketTrafficStats();
            stats.setStatTime(now);
            stats.setActiveUsers((long) activeUsers);
            stats.setQueuedUsers((long) queuedUsers);
            stats.setTotalRequests(totalRequests);
            stats.setRejectedRequests(rejectedRequests);
            stats.setAverageSessionTime(averageSessionTime);
            
            insertRedpacketTrafficStats(stats);
            logger.info("自动记录流量统计成功: 活跃用户={}, 排队用户={}, 总请求={}", 
                activeUsers, queuedUsers, totalRequests);
        } catch (Exception e) {
            logger.error("自动记录流量统计失败", e);
        }
    }
    
    /**
     * 获取指定时间范围内的统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据列表
     */
    @Override
    public List<RedpacketTrafficStats> selectStatsByTimeRange(Date startTime, Date endTime)
    {
        RedpacketTrafficStats queryStats = new RedpacketTrafficStats();
        // 这里需要在Mapper中添加时间范围查询方法
        return redpacketTrafficStatsMapper.selectRedpacketTrafficStatsList(queryStats);
    }
}
