package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketRules;

/**
 * 红包活动规则Service接口
 * 
 * @author ruoyi
 * @date 2025-08-15
 */
public interface IRedpacketRulesService 
{
    /**
     * 查询红包活动规则
     * 
     * @param id 红包活动规则主键
     * @return 红包活动规则
     */
    public RedpacketRules selectRedpacketRulesById(Long id);

    /**
     * 查询红包活动规则列表
     * 
     * @param redpacketRules 红包活动规则
     * @return 红包活动规则集合
     */
    public List<RedpacketRules> selectRedpacketRulesList(RedpacketRules redpacketRules);

    /**
     * 新增红包活动规则
     * 
     * @param redpacketRules 红包活动规则
     * @return 结果
     */
    public int insertRedpacketRules(RedpacketRules redpacketRules);

    /**
     * 修改红包活动规则
     * 
     * @param redpacketRules 红包活动规则
     * @return 结果
     */
    public int updateRedpacketRules(RedpacketRules redpacketRules);

    /**
     * 批量删除红包活动规则
     * 
     * @param ids 需要删除的红包活动规则主键集合
     * @return 结果
     */
    public int deleteRedpacketRulesByIds(Long[] ids);

    /**
     * 删除红包活动规则信息
     * 
     * @param id 红包活动规则主键
     * @return 结果
     */
    public int deleteRedpacketRulesById(Long id);
}
