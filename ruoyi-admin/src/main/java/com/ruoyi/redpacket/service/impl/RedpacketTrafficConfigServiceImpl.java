package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketTrafficConfigMapper;
import com.ruoyi.redpacket.domain.RedpacketTrafficConfig;
import com.ruoyi.redpacket.service.IRedpacketTrafficConfigService;

/**
 * 红包流量控制配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@Service
public class RedpacketTrafficConfigServiceImpl implements IRedpacketTrafficConfigService 
{
    @Autowired
    private RedpacketTrafficConfigMapper redpacketTrafficConfigMapper;

    /**
     * 查询红包流量控制配置
     * 
     * @param id 红包流量控制配置主键
     * @return 红包流量控制配置
     */
    @Override
    public RedpacketTrafficConfig selectRedpacketTrafficConfigById(Long id)
    {
        return redpacketTrafficConfigMapper.selectRedpacketTrafficConfigById(id);
    }

    /**
     * 查询红包流量控制配置列表
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 红包流量控制配置
     */
    @Override
    public List<RedpacketTrafficConfig> selectRedpacketTrafficConfigList(RedpacketTrafficConfig redpacketTrafficConfig)
    {
        return redpacketTrafficConfigMapper.selectRedpacketTrafficConfigList(redpacketTrafficConfig);
    }

    /**
     * 新增红包流量控制配置
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 结果
     */
    @Override
    public int insertRedpacketTrafficConfig(RedpacketTrafficConfig redpacketTrafficConfig)
    {
        return redpacketTrafficConfigMapper.insertRedpacketTrafficConfig(redpacketTrafficConfig);
    }

    /**
     * 修改红包流量控制配置
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 结果
     */
    @Override
    public int updateRedpacketTrafficConfig(RedpacketTrafficConfig redpacketTrafficConfig)
    {
        return redpacketTrafficConfigMapper.updateRedpacketTrafficConfig(redpacketTrafficConfig);
    }

    /**
     * 批量删除红包流量控制配置
     * 
     * @param ids 需要删除的红包流量控制配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTrafficConfigByIds(Long[] ids)
    {
        return redpacketTrafficConfigMapper.deleteRedpacketTrafficConfigByIds(ids);
    }

    /**
     * 删除红包流量控制配置信息
     * 
     * @param id 红包流量控制配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTrafficConfigById(Long id)
    {
        return redpacketTrafficConfigMapper.deleteRedpacketTrafficConfigById(id);
    }
}
