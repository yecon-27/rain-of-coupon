package com.ruoyi.redpacket.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 红包活动规则对象 redpacket_rules
 * 
 * @author ruoyi
 * @date 2025-08-15
 */
public class RedpacketRules extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long id;

    /** 规则类型：activity_rule(活动规则), distribution_rule(发放规则), usage_rule(使用规则) */
    @Excel(name = "规则类型：activity_rule(活动规则), distribution_rule(发放规则), usage_rule(使用规则)")
    private String ruleType;

    /** 规则标题 */
    @Excel(name = "规则标题")
    private String ruleTitle;

    /** 规则内容（支持HTML格式） */
    @Excel(name = "规则内容", readConverterExp = "支=持HTML格式")
    private String ruleContent;

    /** 显示顺序 */
    private Long ruleOrder;

    /** 状态：0正常，1停用 */
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setRuleType(String ruleType) 
    {
        this.ruleType = ruleType;
    }

    public String getRuleType() 
    {
        return ruleType;
    }

    public void setRuleTitle(String ruleTitle) 
    {
        this.ruleTitle = ruleTitle;
    }

    public String getRuleTitle() 
    {
        return ruleTitle;
    }

    public void setRuleContent(String ruleContent) 
    {
        this.ruleContent = ruleContent;
    }

    public String getRuleContent() 
    {
        return ruleContent;
    }

    public void setRuleOrder(Long ruleOrder) 
    {
        this.ruleOrder = ruleOrder;
    }

    public Long getRuleOrder() 
    {
        return ruleOrder;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("ruleType", getRuleType())
            .append("ruleTitle", getRuleTitle())
            .append("ruleContent", getRuleContent())
            .append("ruleOrder", getRuleOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
