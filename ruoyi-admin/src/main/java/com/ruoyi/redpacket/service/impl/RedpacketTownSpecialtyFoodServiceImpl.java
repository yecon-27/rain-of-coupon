package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketTownSpecialtyFoodMapper;
import com.ruoyi.redpacket.domain.RedpacketTownSpecialtyFood;
import com.ruoyi.redpacket.service.IRedpacketTownSpecialtyFoodService;

/**
 * "一镇一品"特色菜Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketTownSpecialtyFoodServiceImpl implements IRedpacketTownSpecialtyFoodService 
{
    @Autowired
    private RedpacketTownSpecialtyFoodMapper redpacketTownSpecialtyFoodMapper;

    /**
     * 查询"一镇一品"特色菜
     * 
     * @param id "一镇一品"特色菜主键
     * @return "一镇一品"特色菜
     */
    @Override
    public RedpacketTownSpecialtyFood selectRedpacketTownSpecialtyFoodById(Long id)
    {
        return redpacketTownSpecialtyFoodMapper.selectRedpacketTownSpecialtyFoodById(id);
    }

    /**
     * 查询"一镇一品"特色菜列表
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return "一镇一品"特色菜
     */
    @Override
    public List<RedpacketTownSpecialtyFood> selectRedpacketTownSpecialtyFoodList(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        return redpacketTownSpecialtyFoodMapper.selectRedpacketTownSpecialtyFoodList(redpacketTownSpecialtyFood);
    }

    /**
     * 新增"一镇一品"特色菜
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return 结果
     */
    @Override
    public int insertRedpacketTownSpecialtyFood(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        return redpacketTownSpecialtyFoodMapper.insertRedpacketTownSpecialtyFood(redpacketTownSpecialtyFood);
    }

    /**
     * 修改"一镇一品"特色菜
     * 
     * @param redpacketTownSpecialtyFood "一镇一品"特色菜
     * @return 结果
     */
    @Override
    public int updateRedpacketTownSpecialtyFood(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        return redpacketTownSpecialtyFoodMapper.updateRedpacketTownSpecialtyFood(redpacketTownSpecialtyFood);
    }

    /**
     * 批量删除"一镇一品"特色菜
     * 
     * @param ids 需要删除的"一镇一品"特色菜主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTownSpecialtyFoodByIds(Long[] ids)
    {
        return redpacketTownSpecialtyFoodMapper.deleteRedpacketTownSpecialtyFoodByIds(ids);
    }

    /**
     * 删除"一镇一品"特色菜信息
     * 
     * @param id "一镇一品"特色菜主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTownSpecialtyFoodById(Long id)
    {
        return redpacketTownSpecialtyFoodMapper.deleteRedpacketTownSpecialtyFoodById(id);
    }
}
