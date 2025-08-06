package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketEventConfigMapper;
import com.ruoyi.redpacket.domain.RedpacketEventConfig;
import com.ruoyi.redpacket.service.IRedpacketEventConfigService;

/**
 * 活动配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketEventConfigServiceImpl implements IRedpacketEventConfigService 
{
    @Autowired
    private RedpacketEventConfigMapper redpacketEventConfigMapper;

    /**
     * 查询活动配置
     * 
     * @param id 活动配置主键
     * @return 活动配置
     */
    @Override
    public RedpacketEventConfig selectRedpacketEventConfigById(Long id)
    {
        return redpacketEventConfigMapper.selectRedpacketEventConfigById(id);
    }

    /**
     * 查询活动配置列表
     * 
     * @param redpacketEventConfig 活动配置
     * @return 活动配置
     */
    @Override
    public List<RedpacketEventConfig> selectRedpacketEventConfigList(RedpacketEventConfig redpacketEventConfig)
    {
        return redpacketEventConfigMapper.selectRedpacketEventConfigList(redpacketEventConfig);
    }

    /**
     * 新增活动配置
     * 
     * @param redpacketEventConfig 活动配置
     * @return 结果
     */
    @Override
    public int insertRedpacketEventConfig(RedpacketEventConfig redpacketEventConfig)
    {
        return redpacketEventConfigMapper.insertRedpacketEventConfig(redpacketEventConfig);
    }

    /**
     * 修改活动配置
     * 
     * @param redpacketEventConfig 活动配置
     * @return 结果
     */
    @Override
    public int updateRedpacketEventConfig(RedpacketEventConfig redpacketEventConfig)
    {
        return redpacketEventConfigMapper.updateRedpacketEventConfig(redpacketEventConfig);
    }

    /**
     * 批量删除活动配置
     * 
     * @param ids 需要删除的活动配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketEventConfigByIds(Long[] ids)
    {
        return redpacketEventConfigMapper.deleteRedpacketEventConfigByIds(ids);
    }

    /**
     * 删除活动配置信息
     * 
     * @param id 活动配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketEventConfigById(Long id)
    {
        return redpacketEventConfigMapper.deleteRedpacketEventConfigById(id);
    }
}
