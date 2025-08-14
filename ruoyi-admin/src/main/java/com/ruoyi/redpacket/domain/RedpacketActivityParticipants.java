package com.ruoyi.redpacket.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 红包活动参与者记录对象 redpacket_activity_participants
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public class RedpacketActivityParticipants extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户ID（可选，支持匿名用户） */
    @Excel(name = "用户ID", readConverterExp = "可=选，支持匿名用户")
    private String userId;

    /** 会话ID */
    @Excel(name = "会话ID")
    private String sessionId;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 加入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加入时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinTime;

    /** 离开时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "离开时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date leaveTime;

    /** 最后心跳时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后心跳时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastHeartbeat;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 队列位置（仅排队时有效） */
    @Excel(name = "队列位置", readConverterExp = "仅=排队时有效")
    private Long queuePosition;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }

    public void setSessionId(String sessionId) 
    {
        this.sessionId = sessionId;
    }

    public String getSessionId() 
    {
        return sessionId;
    }

    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }

    public void setJoinTime(Date joinTime) 
    {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() 
    {
        return joinTime;
    }

    public void setLeaveTime(Date leaveTime) 
    {
        this.leaveTime = leaveTime;
    }

    public Date getLeaveTime() 
    {
        return leaveTime;
    }

    public void setLastHeartbeat(Date lastHeartbeat) 
    {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Date getLastHeartbeat() 
    {
        return lastHeartbeat;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setQueuePosition(Long queuePosition) 
    {
        this.queuePosition = queuePosition;
    }

    public Long getQueuePosition() 
    {
        return queuePosition;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("sessionId", getSessionId())
            .append("ipAddress", getIpAddress())
            .append("joinTime", getJoinTime())
            .append("leaveTime", getLeaveTime())
            .append("lastHeartbeat", getLastHeartbeat())
            .append("status", getStatus())
            .append("queuePosition", getQueuePosition())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
