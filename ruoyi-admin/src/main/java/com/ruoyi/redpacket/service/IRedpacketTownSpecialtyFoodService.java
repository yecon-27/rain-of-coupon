package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketTownSpecialtyFood;

/**
 * "一镇一品"特色菜Service接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface IRedpacketTownSpecialtyFoodService 
{
    /**
     * 查询"一镇一品"特色菜
     * 
     * @param id "一镇一品"特色菜主键
     * @return "一镇一品"特色菜
     */
    public RedpacketTownSpecialtyFood selectRedpacketTownSpecialtyFoodById(Long id);

    /**
     * 查询"一镇一品"特色菜列表
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return "一镇一品"特色菜集合
     */
    public List<RedpacketTownSpecialtyFood> selectRedpacketTownSpecialtyFoodList(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood);

    /**
     * 新增"一镇一品"特色菜
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return 结果
     */
    public int insertRedpacketTownSpecialtyFood(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood);

    /**
     * 修改"一镇一品"特色菜
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return 结果
     */
    public int updateRedpacketTownSpecialtyFood(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood);

    /**
     * 批量删除"一镇一品"特色菜
     * 
     * @param ids 需要删除的"一镇一品"特色菜主键集合
     * @return 结果
     */
    public int deleteRedpacketTownSpecialtyFoodByIds(Long[] ids);

    /**
     * 删除"一镇一品"特色菜信息
     * 
     * @param id "一镇一品"特色菜主键
     * @return 结果
     */
    public int deleteRedpacketTownSpecialtyFoodById(Long id);
}
