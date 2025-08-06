package com.ruoyi.redpacket.mapper;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketImageResource;

/**
 * 图片资源Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface RedpacketImageResourceMapper 
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
     * 删除图片资源
     * 
     * @param id 图片资源主键
     * @return 结果
     */
    public int deleteRedpacketImageResourceById(Long id);

    /**
     * 批量删除图片资源
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRedpacketImageResourceByIds(Long[] ids);
}
