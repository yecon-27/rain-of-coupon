package com.ruoyi.redpacket.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 奖品配置对象 redpacket_prize
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public class RedpacketPrize extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 奖品ID */
    private Long id;

    /** 奖品名称 */
    @Excel(name = "奖品名称")
    private String prizeName;

    /** 奖品总数量 */
    @Excel(name = "奖品总数量")
    private Long totalCount;

    /** 剩余数量 */
    @Excel(name = "剩余数量")
    private Long remainingCount;

    /** 中奖概率(0-1) */
    @Excel(name = "中奖概率(0-1)")
    private BigDecimal probability;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPrizeName(String prizeName) 
    {
        this.prizeName = prizeName;
    }

    public String getPrizeName() 
    {
        return prizeName;
    }

    public void setTotalCount(Long totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Long getTotalCount() 
    {
        return totalCount;
    }

    public void setRemainingCount(Long remainingCount) 
    {
        this.remainingCount = remainingCount;
    }

    public Long getRemainingCount() 
    {
        return remainingCount;
    }

    public void setProbability(BigDecimal probability) 
    {
        this.probability = probability;
    }

    public BigDecimal getProbability() 
    {
        return probability;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("prizeName", getPrizeName())
            .append("totalCount", getTotalCount())
            .append("remainingCount", getRemainingCount())
            .append("probability", getProbability())
            .toString();
    }
}
