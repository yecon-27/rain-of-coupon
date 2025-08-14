package com.ruoyi.redpacket.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 红包流量统计对象 redpacket_traffic_stats
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public class RedpacketTrafficStats extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 统计时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "统计时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date statTime;

    /** 活跃用户数 */
    @Excel(name = "活跃用户数")
    private Long activeUsers;

    /** 排队用户数 */
    @Excel(name = "排队用户数")
    private Long queuedUsers;

    /** 总请求数 */
    @Excel(name = "总请求数")
    private Long totalRequests;

    /** 被拒绝的请求数 */
    @Excel(name = "被拒绝的请求数")
    private Long rejectedRequests;

    /** 平均会话时间（秒） */
    @Excel(name = "平均会话时间", readConverterExp = "秒=")
    private Long averageSessionTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setStatTime(Date statTime) 
    {
        this.statTime = statTime;
    }

    public Date getStatTime() 
    {
        return statTime;
    }

    public void setActiveUsers(Long activeUsers) 
    {
        this.activeUsers = activeUsers;
    }

    public Long getActiveUsers() 
    {
        return activeUsers;
    }

    public void setQueuedUsers(Long queuedUsers) 
    {
        this.queuedUsers = queuedUsers;
    }

    public Long getQueuedUsers() 
    {
        return queuedUsers;
    }

    public void setTotalRequests(Long totalRequests) 
    {
        this.totalRequests = totalRequests;
    }

    public Long getTotalRequests() 
    {
        return totalRequests;
    }

    public void setRejectedRequests(Long rejectedRequests) 
    {
        this.rejectedRequests = rejectedRequests;
    }

    public Long getRejectedRequests() 
    {
        return rejectedRequests;
    }

    public void setAverageSessionTime(Long averageSessionTime) 
    {
        this.averageSessionTime = averageSessionTime;
    }

    public Long getAverageSessionTime() 
    {
        return averageSessionTime;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("statTime", getStatTime())
            .append("activeUsers", getActiveUsers())
            .append("queuedUsers", getQueuedUsers())
            .append("totalRequests", getTotalRequests())
            .append("rejectedRequests", getRejectedRequests())
            .append("averageSessionTime", getAverageSessionTime())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
