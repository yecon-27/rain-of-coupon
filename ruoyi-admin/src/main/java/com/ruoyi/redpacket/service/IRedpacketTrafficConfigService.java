package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketTrafficConfig;

/**
 * 红包流量控制配置Service接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface IRedpacketTrafficConfigService 
{
    /**
     * 查询红包流量控制配置
     * 
     * @param id 红包流量控制配置主键
     * @return 红包流量控制配置
     */
    public RedpacketTrafficConfig selectRedpacketTrafficConfigById(Long id);

    /**
     * 查询红包流量控制配置列表
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 红包流量控制配置集合
     */
    public List<RedpacketTrafficConfig> selectRedpacketTrafficConfigList(RedpacketTrafficConfig redpacketTrafficConfig);

    /**
     * 新增红包流量控制配置
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 结果
     */
    public int insertRedpacketTrafficConfig(RedpacketTrafficConfig redpacketTrafficConfig);

    /**
     * 修改红包流量控制配置
     * 
     * @param redpacketTrafficConfig 红包流量控制配置
     * @return 结果
     */
    public int updateRedpacketTrafficConfig(RedpacketTrafficConfig redpacketTrafficConfig);

    /**
     * 批量删除红包流量控制配置
     * 
     * @param ids 需要删除的红包流量控制配置主键集合
     * @return 结果
     */
    public int deleteRedpacketTrafficConfigByIds(Long[] ids);

    /**
     * 删除红包流量控制配置信息
     * 
     * @param id 红包流量控制配置主键
     * @return 结果
     */
    public int deleteRedpacketTrafficConfigById(Long id);

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    public String getConfigValueByKey(String configKey);

    /**
     * 根据配置键获取配置值，如果不存在则返回默认值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getConfigValueByKey(String configKey, String defaultValue);
}
