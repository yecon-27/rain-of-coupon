package com.ruoyi.redpacket.mapper;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketTrafficConfig;

/**
 * 红包流量控制配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface RedpacketTrafficConfigMapper 
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
     * 删除红包流量控制配置
     * 
     * @param id 红包流量控制配置主键
     * @return 结果
     */
    public int deleteRedpacketTrafficConfigById(Long id);

    /**
     * 批量删除红包流量控制配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRedpacketTrafficConfigByIds(Long[] ids);
}
