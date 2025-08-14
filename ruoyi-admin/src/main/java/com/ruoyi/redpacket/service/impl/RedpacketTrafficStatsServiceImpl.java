package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketTrafficStatsMapper;
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;
import com.ruoyi.redpacket.service.IRedpacketTrafficStatsService;

/**
 * 红包流量统计Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@Service
public class RedpacketTrafficStatsServiceImpl implements IRedpacketTrafficStatsService 
{
    @Autowired
    private RedpacketTrafficStatsMapper redpacketTrafficStatsMapper;

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
}
