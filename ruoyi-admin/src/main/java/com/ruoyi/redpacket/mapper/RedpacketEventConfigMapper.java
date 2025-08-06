package com.ruoyi.redpacket.mapper;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketEventConfig;

/**
 * 活动配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface RedpacketEventConfigMapper 
{
    /**
     * 查询活动配置
     * 
     * @param id 活动配置主键
     * @return 活动配置
     */
    public RedpacketEventConfig selectRedpacketEventConfigById(Long id);

    /**
     * 查询活动配置列表
     * 
     * @param redpacketEventConfig 活动配置
     * @return 活动配置集合
     */
    public List<RedpacketEventConfig> selectRedpacketEventConfigList(RedpacketEventConfig redpacketEventConfig);

    /**
     * 新增活动配置
     * 
     * @param redpacketEventConfig 活动配置
     * @return 结果
     */
    public int insertRedpacketEventConfig(RedpacketEventConfig redpacketEventConfig);

    /**
     * 修改活动配置
     * 
     * @param redpacketEventConfig 活动配置
     * @return 结果
     */
    public int updateRedpacketEventConfig(RedpacketEventConfig redpacketEventConfig);

    /**
     * 删除活动配置
     * 
     * @param id 活动配置主键
     * @return 结果
     */
    public int deleteRedpacketEventConfigById(Long id);

    /**
     * 批量删除活动配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRedpacketEventConfigByIds(Long[] ids);
}
