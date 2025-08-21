package com.ruoyi.redpacket.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 活动配置对象 redpacket_event_config
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
public class RedpacketEventConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 活动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "活动开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 活动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "活动结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 并发用户上限 */
    @Excel(name = "并发用户上限")
    private Long maxUsers;

    /** 每日最大抽奖次数 */
    @Excel(name = "每日最大抽奖次数")
    private Long maxDrawsPerDay;

    /** 抽奖轮次类型 */
    @Excel(name = "抽奖轮次类型")
    private String roundType;

    /** 第一轮开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "第一轮开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date firstRoundStart;

    /** 第一轮结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "第一轮结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date firstRoundEnd;

    /** 第二轮开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "第二轮开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date secondRoundStart;

    /** 第二轮结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "第二轮结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date secondRoundEnd;

    /** 是否启用优惠券回流 */
    private Integer couponRecycleEnabled;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setMaxUsers(Long maxUsers) 
    {
        this.maxUsers = maxUsers;
    }

    public Long getMaxUsers() 
    {
        return maxUsers;
    }

    public void setMaxDrawsPerDay(Long maxDrawsPerDay) 
    {
        this.maxDrawsPerDay = maxDrawsPerDay;
    }

    public Long getMaxDrawsPerDay() 
    {
        return maxDrawsPerDay;
    }

    public void setRoundType(String roundType) 
    {
        this.roundType = roundType;
    }

    public String getRoundType() 
    {
        return roundType;
    }

    public void setFirstRoundStart(Date firstRoundStart) 
    {
        this.firstRoundStart = firstRoundStart;
    }

    public Date getFirstRoundStart() 
    {
        return firstRoundStart;
    }

    public void setFirstRoundEnd(Date firstRoundEnd) 
    {
        this.firstRoundEnd = firstRoundEnd;
    }

    public Date getFirstRoundEnd() 
    {
        return firstRoundEnd;
    }

    public void setSecondRoundStart(Date secondRoundStart) 
    {
        this.secondRoundStart = secondRoundStart;
    }

    public Date getSecondRoundStart() 
    {
        return secondRoundStart;
    }

    public void setSecondRoundEnd(Date secondRoundEnd) 
    {
        this.secondRoundEnd = secondRoundEnd;
    }

    public Date getSecondRoundEnd() 
    {
        return secondRoundEnd;
    }

    public void setCouponRecycleEnabled(Integer couponRecycleEnabled) 
    {
        this.couponRecycleEnabled = couponRecycleEnabled;
    }

    public Integer getCouponRecycleEnabled() 
    {
        return couponRecycleEnabled;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("maxUsers", getMaxUsers())
            .append("maxDrawsPerDay", getMaxDrawsPerDay())
            .append("roundType", getRoundType())
            .append("firstRoundStart", getFirstRoundStart())
            .append("firstRoundEnd", getFirstRoundEnd())
            .append("secondRoundStart", getSecondRoundStart())
            .append("secondRoundEnd", getSecondRoundEnd())
            .append("couponRecycleEnabled", getCouponRecycleEnabled())
            .toString();
    }
}
