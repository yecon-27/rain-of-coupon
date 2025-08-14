package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketActivityParticipantsMapper;
import com.ruoyi.redpacket.domain.RedpacketActivityParticipants;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;

/**
 * 红包活动参与者记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@Service
public class RedpacketActivityParticipantsServiceImpl implements IRedpacketActivityParticipantsService 
{
    @Autowired
    private RedpacketActivityParticipantsMapper redpacketActivityParticipantsMapper;

    /**
     * 查询红包活动参与者记录
     * 
     * @param id 红包活动参与者记录主键
     * @return 红包活动参与者记录
     */
    @Override
    public RedpacketActivityParticipants selectRedpacketActivityParticipantsById(Long id)
    {
        return redpacketActivityParticipantsMapper.selectRedpacketActivityParticipantsById(id);
    }

    /**
     * 查询红包活动参与者记录列表
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 红包活动参与者记录
     */
    @Override
    public List<RedpacketActivityParticipants> selectRedpacketActivityParticipantsList(RedpacketActivityParticipants redpacketActivityParticipants)
    {
        return redpacketActivityParticipantsMapper.selectRedpacketActivityParticipantsList(redpacketActivityParticipants);
    }

    /**
     * 新增红包活动参与者记录
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 结果
     */
    @Override
    public int insertRedpacketActivityParticipants(RedpacketActivityParticipants redpacketActivityParticipants)
    {
        return redpacketActivityParticipantsMapper.insertRedpacketActivityParticipants(redpacketActivityParticipants);
    }

    /**
     * 修改红包活动参与者记录
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 结果
     */
    @Override
    public int updateRedpacketActivityParticipants(RedpacketActivityParticipants redpacketActivityParticipants)
    {
        return redpacketActivityParticipantsMapper.updateRedpacketActivityParticipants(redpacketActivityParticipants);
    }

    /**
     * 批量删除红包活动参与者记录
     * 
     * @param ids 需要删除的红包活动参与者记录主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketActivityParticipantsByIds(Long[] ids)
    {
        return redpacketActivityParticipantsMapper.deleteRedpacketActivityParticipantsByIds(ids);
    }

    /**
     * 删除红包活动参与者记录信息
     * 
     * @param id 红包活动参与者记录主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketActivityParticipantsById(Long id)
    {
        return redpacketActivityParticipantsMapper.deleteRedpacketActivityParticipantsById(id);
    }
}
