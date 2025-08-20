package com.ruoyi.redpacket.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户参与记录（记录所有参与行为）对象 redpacket_user_participation_log
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
public class RedpacketUserParticipationLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 参与时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "参与时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date participationTime;

    /** IP记录（防刷） */
    @Excel(name = "IP记录", readConverterExp = "防=刷")
    private String ipAddress;

    /** 是否中奖(0未中奖 1中奖) */
    @Excel(name = "是否中奖(0未中奖 1中奖)")
    private Integer isWin;

    /** 中奖奖品ID（未中奖时为空） */
    @Excel(name = "中奖奖品ID", readConverterExp = "未=中奖时为空")
    private Long prizeId;

    /** 奖品名称（未中奖时为空） */
    @Excel(name = "奖品名称", readConverterExp = "未=中奖时为空")
    private String prizeName;

    /** 是否使用(0未使用 1已使用) */
    @Excel(name = "是否使用(0未使用 1已使用)")
    private Integer isUsed;

    /** 点击次数 */
    @Excel(name = "点击次数")
    private Integer clickedCount;

    public void setClickedCount(Integer clickedCount) {
        this.clickedCount = clickedCount;
    }

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

    public void setParticipationTime(Date participationTime) 
    {
        this.participationTime = participationTime;
    }

    public Date getParticipationTime() 
    {
        return participationTime;
    }

    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }

    public void setIsWin(Integer isWin) 
    {
        this.isWin = isWin;
    }

    public Integer getIsWin() 
    {
        return isWin;
    }

    public void setPrizeId(Long prizeId) 
    {
        this.prizeId = prizeId;
    }

    public Long getPrizeId() 
    {
        return prizeId;
    }

    public void setPrizeName(String prizeName) 
    {
        this.prizeName = prizeName;
    }

    public String getPrizeName() 
    {
        return prizeName;
    }

    public void setIsUsed(Integer isUsed) 
    {
        this.isUsed = isUsed;
    }

    public Integer getIsUsed() 
    {
        return isUsed;
    }

    public Integer getClickedCount() {
        return clickedCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("participationTime", getParticipationTime())
            .append("ipAddress", getIpAddress())
            .append("isWin", getIsWin())
            .append("prizeId", getPrizeId())
            .append("prizeName", getPrizeName())
            .append("isUsed", getIsUsed())
            .toString();
    }
}
