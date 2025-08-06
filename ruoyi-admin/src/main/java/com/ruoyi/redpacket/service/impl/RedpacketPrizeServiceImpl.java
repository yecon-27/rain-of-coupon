package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketPrizeMapper;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.service.IRedpacketPrizeService;

/**
 * 奖品配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketPrizeServiceImpl implements IRedpacketPrizeService 
{
    @Autowired
    private RedpacketPrizeMapper redpacketPrizeMapper;

    /**
     * 查询奖品配置
     * 
     * @param id 奖品配置主键
     * @return 奖品配置
     */
    @Override
    public RedpacketPrize selectRedpacketPrizeById(Long id)
    {
        return redpacketPrizeMapper.selectRedpacketPrizeById(id);
    }

    /**
     * 查询奖品配置列表
     * 
     * @param redpacketPrize 奖品配置
     * @return 奖品配置
     */
    @Override
    public List<RedpacketPrize> selectRedpacketPrizeList(RedpacketPrize redpacketPrize)
    {
        return redpacketPrizeMapper.selectRedpacketPrizeList(redpacketPrize);
    }

    /**
     * 新增奖品配置
     * 
     * @param redpacketPrize 奖品配置
     * @return 结果
     */
    @Override
    public int insertRedpacketPrize(RedpacketPrize redpacketPrize)
    {
        return redpacketPrizeMapper.insertRedpacketPrize(redpacketPrize);
    }

    /**
     * 修改奖品配置
     * 
     * @param redpacketPrize 奖品配置
     * @return 结果
     */
    @Override
    public int updateRedpacketPrize(RedpacketPrize redpacketPrize)
    {
        return redpacketPrizeMapper.updateRedpacketPrize(redpacketPrize);
    }

    /**
     * 批量删除奖品配置
     * 
     * @param ids 需要删除的奖品配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketPrizeByIds(Long[] ids)
    {
        return redpacketPrizeMapper.deleteRedpacketPrizeByIds(ids);
    }

    /**
     * 删除奖品配置信息
     * 
     * @param id 奖品配置主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketPrizeById(Long id)
    {
        return redpacketPrizeMapper.deleteRedpacketPrizeById(id);
    }
}
