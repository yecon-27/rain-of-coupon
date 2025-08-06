package com.ruoyi.redpacket.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * "一镇一品"特色菜对象 redpacket_town_specialty_food
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public class RedpacketTownSpecialtyFood extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 特色菜ID */
    private Long id;

    /** 美食名称 */
    @Excel(name = "美食名称")
    private String foodName;

    /** 排名 */
    @Excel(name = "排名")
    private Long ranking;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setFoodName(String foodName) 
    {
        this.foodName = foodName;
    }

    public String getFoodName() 
    {
        return foodName;
    }

    public void setRanking(Long ranking) 
    {
        this.ranking = ranking;
    }

    public Long getRanking() 
    {
        return ranking;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("foodName", getFoodName())
            .append("ranking", getRanking())
            .toString();
    }
}
