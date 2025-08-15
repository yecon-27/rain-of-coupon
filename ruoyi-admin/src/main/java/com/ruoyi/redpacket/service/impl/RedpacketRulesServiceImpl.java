package com.ruoyi.redpacket.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketRulesMapper;
import com.ruoyi.redpacket.domain.RedpacketRules;
import com.ruoyi.redpacket.service.IRedpacketRulesService;

/**
 * 红包活动规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-15
 */
@Service
public class RedpacketRulesServiceImpl implements IRedpacketRulesService 
{
    @Autowired
    private RedpacketRulesMapper redpacketRulesMapper;

    /**
     * 查询红包活动规则
     * 
     * @param id 红包活动规则主键
     * @return 红包活动规则
     */
    @Override
    public RedpacketRules selectRedpacketRulesById(Long id)
    {
        return redpacketRulesMapper.selectRedpacketRulesById(id);
    }

    /**
     * 查询红包活动规则列表
     * 
     * @param redpacketRules 红包活动规则
     * @return 红包活动规则
     */
    @Override
    public List<RedpacketRules> selectRedpacketRulesList(RedpacketRules redpacketRules)
    {
        return redpacketRulesMapper.selectRedpacketRulesList(redpacketRules);
    }

    /**
     * 新增红包活动规则
     * 
     * @param redpacketRules 红包活动规则
     * @return 结果
     */
    @Override
    public int insertRedpacketRules(RedpacketRules redpacketRules)
    {
        redpacketRules.setCreateTime(DateUtils.getNowDate());
        return redpacketRulesMapper.insertRedpacketRules(redpacketRules);
    }

    /**
     * 修改红包活动规则
     * 
     * @param redpacketRules 红包活动规则
     * @return 结果
     */
    @Override
    public int updateRedpacketRules(RedpacketRules redpacketRules)
    {
        redpacketRules.setUpdateTime(DateUtils.getNowDate());
        return redpacketRulesMapper.updateRedpacketRules(redpacketRules);
    }

    /**
     * 批量删除红包活动规则
     * 
     * @param ids 需要删除的红包活动规则主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketRulesByIds(Long[] ids)
    {
        return redpacketRulesMapper.deleteRedpacketRulesByIds(ids);
    }

    /**
     * 删除红包活动规则信息
     * 
     * @param id 红包活动规则主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketRulesById(Long id)
    {
        return redpacketRulesMapper.deleteRedpacketRulesById(id);
    }
}
