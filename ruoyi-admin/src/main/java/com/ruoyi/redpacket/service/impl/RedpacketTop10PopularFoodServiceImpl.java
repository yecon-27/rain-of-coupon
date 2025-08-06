package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketTop10PopularFoodMapper;
import com.ruoyi.redpacket.domain.RedpacketTop10PopularFood;
import com.ruoyi.redpacket.service.IRedpacketTop10PopularFoodService;

/**
 * TOP10网络人气特色美食Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketTop10PopularFoodServiceImpl implements IRedpacketTop10PopularFoodService 
{
    @Autowired
    private RedpacketTop10PopularFoodMapper redpacketTop10PopularFoodMapper;

    /**
     * 查询TOP10网络人气特色美食
     * 
     * @param id TOP10网络人气特色美食主键
     * @return TOP10网络人气特色美食
     */
    @Override
    public RedpacketTop10PopularFood selectRedpacketTop10PopularFoodById(Long id)
    {
        return redpacketTop10PopularFoodMapper.selectRedpacketTop10PopularFoodById(id);
    }

    /**
     * 查询TOP10网络人气特色美食列表
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return TOP10网络人气特色美食
     */
    @Override
    public List<RedpacketTop10PopularFood> selectRedpacketTop10PopularFoodList(RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        return redpacketTop10PopularFoodMapper.selectRedpacketTop10PopularFoodList(redpacketTop10PopularFood);
    }

    /**
     * 新增TOP10网络人气特色美食
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return 结果
     */
    @Override
    public int insertRedpacketTop10PopularFood(RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        return redpacketTop10PopularFoodMapper.insertRedpacketTop10PopularFood(redpacketTop10PopularFood);
    }

    /**
     * 修改TOP10网络人气特色美食
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return 结果
     */
    @Override
    public int updateRedpacketTop10PopularFood(RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        return redpacketTop10PopularFoodMapper.updateRedpacketTop10PopularFood(redpacketTop10PopularFood);
    }

    /**
     * 批量删除TOP10网络人气特色美食
     * 
     * @param ids 需要删除的TOP10网络人气特色美食主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTop10PopularFoodByIds(Long[] ids)
    {
        return redpacketTop10PopularFoodMapper.deleteRedpacketTop10PopularFoodByIds(ids);
    }

    /**
     * 删除TOP10网络人气特色美食信息
     * 
     * @param id TOP10网络人气特色美食主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketTop10PopularFoodById(Long id)
    {
        return redpacketTop10PopularFoodMapper.deleteRedpacketTop10PopularFoodById(id);
    }
}
