package com.ruoyi.redpacket.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户抽奖记录对象 redpacket_user_prize_log
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public class RedpacketUserPrizeLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 奖品名称（未中奖时为空） */
    @Excel(name = "奖品名称", readConverterExp = "未=中奖时为空")
    private String prizeName;

    /** 是否中奖(0未中奖 1中奖) */
    @Excel(name = "是否中奖(0未中奖 1中奖)")
    private Integer isWin;

    /** 是否使用(0未使用 1已使用) */
    @Excel(name = "是否使用(0未使用 1已使用)")
    private Integer isUsed;

    /** 抽奖时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "抽奖时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** IP记录（支持IPv6） */
    @Excel(name = "IP记录", readConverterExp = "支=持IPv6")
    private String ipAddress;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setPrizeName(String prizeName) 
    {
        this.prizeName = prizeName;
    }

    public String getPrizeName() 
    {
        return prizeName;
    }

    public void setIsWin(Integer isWin) 
    {
        this.isWin = isWin;
    }

    public Integer getIsWin() 
    {
        return isWin;
    }

    public void setIsUsed(Integer isUsed) 
    {
        this.isUsed = isUsed;
    }

    public Integer getIsUsed() 
    {
        return isUsed;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("prizeName", getPrizeName())
            .append("isWin", getIsWin())
            .append("isUsed", getIsUsed())
            .append("createdAt", getCreatedAt())
            .append("ipAddress", getIpAddress())
            .toString();
    }
}
