package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketTop10PopularFood;

/**
 * TOP10网络人气特色美食Service接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface IRedpacketTop10PopularFoodService 
{
    /**
     * 查询TOP10网络人气特色美食
     * 
     * @param id TOP10网络人气特色美食主键
     * @return TOP10网络人气特色美食
     */
    public RedpacketTop10PopularFood selectRedpacketTop10PopularFoodById(Long id);

    /**
     * 查询TOP10网络人气特色美食列表
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return TOP10网络人气特色美食集合
     */
    public List<RedpacketTop10PopularFood> selectRedpacketTop10PopularFoodList(RedpacketTop10PopularFood redpacketTop10PopularFood);

    /**
     * 新增TOP10网络人气特色美食
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return 结果
     */
    public int insertRedpacketTop10PopularFood(RedpacketTop10PopularFood redpacketTop10PopularFood);

    /**
     * 修改TOP10网络人气特色美食
     * 
     * @param redpacketTop10PopularFood TOP10网络人气特色美食
     * @return 结果
     */
    public int updateRedpacketTop10PopularFood(RedpacketTop10PopularFood redpacketTop10PopularFood);

    /**
     * 批量删除TOP10网络人气特色美食
     * 
     * @param ids 需要删除的TOP10网络人气特色美食主键集合
     * @return 结果
     */
    public int deleteRedpacketTop10PopularFoodByIds(Long[] ids);

    /**
     * 删除TOP10网络人气特色美食信息
     * 
     * @param id TOP10网络人气特色美食主键
     * @return 结果
     */
    public int deleteRedpacketTop10PopularFoodById(Long id);
}
