package com.ruoyi.redpacket.service;

import java.util.List;
import java.util.Date;  // 添加这行导入
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;

/**
 * 红包流量统计Service接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface IRedpacketTrafficStatsService 
{
    /**
     * 查询红包流量统计
     * 
     * @param id 红包流量统计主键
     * @return 红包流量统计
     */
    public RedpacketTrafficStats selectRedpacketTrafficStatsById(Long id);

    /**
     * 查询红包流量统计列表
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 红包流量统计集合
     */
    public List<RedpacketTrafficStats> selectRedpacketTrafficStatsList(RedpacketTrafficStats redpacketTrafficStats);

    /**
     * 新增红包流量统计
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 结果
     */
    public int insertRedpacketTrafficStats(RedpacketTrafficStats redpacketTrafficStats);

    /**
     * 修改红包流量统计
     * 
     * @param redpacketTrafficStats 红包流量统计
     * @return 结果
     */
    public int updateRedpacketTrafficStats(RedpacketTrafficStats redpacketTrafficStats);

    /**
     * 批量删除红包流量统计
     * 
     * @param ids 需要删除的红包流量统计主键集合
     * @return 结果
     */
    public int deleteRedpacketTrafficStatsByIds(Long[] ids);

    /**
     * 删除红包流量统计信息
     * 
     * @param id 红包流量统计主键
     * @return 结果
     */
    public int deleteRedpacketTrafficStatsById(Long id);
    
    /**
     * 批量插入流量统计数据
     * 
     * @param statsList 统计数据列表
     * @return 成功插入的数量
     */
    public int batchInsertRedpacketTrafficStats(List<RedpacketTrafficStats> statsList);
    
    /**
     * 自动记录当前系统流量统计
     */
    public void autoRecordCurrentTrafficStats();
    
    /**
     * 获取指定时间范围内的统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据列表
     */
    public List<RedpacketTrafficStats> selectStatsByTimeRange(Date startTime, Date endTime);
}
