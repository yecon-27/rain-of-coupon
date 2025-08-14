package com.ruoyi.redpacket.mapper;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;

/**
 * 红包流量统计Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface RedpacketTrafficStatsMapper 
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
     * 删除红包流量统计
     * 
     * @param id 红包流量统计主键
     * @return 结果
     */
    public int deleteRedpacketTrafficStatsById(Long id);

    /**
     * 批量删除红包流量统计
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRedpacketTrafficStatsByIds(Long[] ids);
}
