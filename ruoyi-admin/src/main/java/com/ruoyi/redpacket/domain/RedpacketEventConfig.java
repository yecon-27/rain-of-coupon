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
 * @date 2025-08-06
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("maxUsers", getMaxUsers())
            .append("maxDrawsPerDay", getMaxDrawsPerDay())
            .toString();
    }
}
