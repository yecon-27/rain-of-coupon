package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketImageResourceMapper;
import com.ruoyi.redpacket.domain.RedpacketImageResource;
import com.ruoyi.redpacket.service.IRedpacketImageResourceService;

/**
 * 图片资源Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketImageResourceServiceImpl implements IRedpacketImageResourceService 
{
    @Autowired
    private RedpacketImageResourceMapper redpacketImageResourceMapper;

    /**
     * 查询图片资源
     * 
     * @param id 图片资源主键
     * @return 图片资源
     */
    @Override
    public RedpacketImageResource selectRedpacketImageResourceById(Long id)
    {
        return redpacketImageResourceMapper.selectRedpacketImageResourceById(id);
    }

    /**
     * 查询图片资源列表
     * 
     * @param redpacketImageResource 图片资源
     * @return 图片资源
     */
    @Override
    public List<RedpacketImageResource> selectRedpacketImageResourceList(RedpacketImageResource redpacketImageResource)
    {
        return redpacketImageResourceMapper.selectRedpacketImageResourceList(redpacketImageResource);
    }

    /**
     * 新增图片资源
     * 
     * @param redpacketImageResource 图片资源
     * @return 结果
     */
    @Override
    public int insertRedpacketImageResource(RedpacketImageResource redpacketImageResource)
    {
        return redpacketImageResourceMapper.insertRedpacketImageResource(redpacketImageResource);
    }

    /**
     * 修改图片资源
     * 
     * @param redpacketImageResource 图片资源
     * @return 结果
     */
    @Override
    public int updateRedpacketImageResource(RedpacketImageResource redpacketImageResource)
    {
        return redpacketImageResourceMapper.updateRedpacketImageResource(redpacketImageResource);
    }

    /**
     * 批量删除图片资源
     * 
     * @param ids 需要删除的图片资源主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketImageResourceByIds(Long[] ids)
    {
        return redpacketImageResourceMapper.deleteRedpacketImageResourceByIds(ids);
    }

    /**
     * 删除图片资源信息
     * 
     * @param id 图片资源主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketImageResourceById(Long id)
    {
        return redpacketImageResourceMapper.deleteRedpacketImageResourceById(id);
    }
}
