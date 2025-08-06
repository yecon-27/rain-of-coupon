package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketImageResource;

/**
 * 图片资源Service接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface IRedpacketImageResourceService 
{
    /**
     * 查询图片资源
     * 
     * @param id 图片资源主键
     * @return 图片资源
     */
    public RedpacketImageResource selectRedpacketImageResourceById(Long id);

    /**
     * 查询图片资源列表
     * 
     * @param redpacketImageResource 图片资源
     * @return 图片资源集合
     */
    public List<RedpacketImageResource> selectRedpacketImageResourceList(RedpacketImageResource redpacketImageResource);

    /**
     * 新增图片资源
     * 
     * @param redpacketImageResource 图片资源
     * @return 结果
     */
    public int insertRedpacketImageResource(RedpacketImageResource redpacketImageResource);

    /**
     * 修改图片资源
     * 
     * @param redpacketImageResource 图片资源
     * @return 结果
     */
    public int updateRedpacketImageResource(RedpacketImageResource redpacketImageResource);

    /**
     * 批量删除图片资源
     * 
     * @param ids 需要删除的图片资源主键集合
     * @return 结果
     */
    public int deleteRedpacketImageResourceByIds(Long[] ids);

    /**
     * 删除图片资源信息
     * 
     * @param id 图片资源主键
     * @return 结果
     */
    public int deleteRedpacketImageResourceById(Long id);
}
