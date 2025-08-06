package com.ruoyi.redpacket.mapper;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketPrize;

/**
 * 奖品配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface RedpacketPrizeMapper 
{
    /**
     * 查询奖品配置
     * 
     * @param id 奖品配置主键
     * @return 奖品配置
     */
    public RedpacketPrize selectRedpacketPrizeById(Long id);

    /**
     * 查询奖品配置列表
     * 
     * @param redpacketPrize 奖品配置
     * @return 奖品配置集合
     */
    public List<RedpacketPrize> selectRedpacketPrizeList(RedpacketPrize redpacketPrize);

    /**
     * 新增奖品配置
     * 
     * @param redpacketPrize 奖品配置
     * @return 结果
     */
    public int insertRedpacketPrize(RedpacketPrize redpacketPrize);

    /**
     * 修改奖品配置
     * 
     * @param redpacketPrize 奖品配置
     * @return 结果
     */
    public int updateRedpacketPrize(RedpacketPrize redpacketPrize);

    /**
     * 删除奖品配置
     * 
     * @param id 奖品配置主键
     * @return 结果
     */
    public int deleteRedpacketPrizeById(Long id);

    /**
     * 批量删除奖品配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRedpacketPrizeByIds(Long[] ids);
}
