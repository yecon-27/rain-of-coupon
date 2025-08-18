package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketActivityParticipants;

/**
 * 红包活动参与者记录Service接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface IRedpacketActivityParticipantsService 
{
    /**
     * 查询红包活动参与者记录
     * 
     * @param id 红包活动参与者记录主键
     * @return 红包活动参与者记录
     */
    public RedpacketActivityParticipants selectRedpacketActivityParticipantsById(Long id);

    /**
     * 查询红包活动参与者记录列表
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 红包活动参与者记录集合
     */
    public List<RedpacketActivityParticipants> selectRedpacketActivityParticipantsList(RedpacketActivityParticipants redpacketActivityParticipants);

    /**
     * 新增红包活动参与者记录
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 结果
     */
    public int insertRedpacketActivityParticipants(RedpacketActivityParticipants redpacketActivityParticipants);

    /**
     * 修改红包活动参与者记录
     * 
     * @param redpacketActivityParticipants 红包活动参与者记录
     * @return 结果
     */
    public int updateRedpacketActivityParticipants(RedpacketActivityParticipants redpacketActivityParticipants);

    /**
     * 批量删除红包活动参与者记录
     * 
     * @param ids 需要删除的红包活动参与者记录主键集合
     * @return 结果
     */
    public int deleteRedpacketActivityParticipantsByIds(Long[] ids);

    /**
     * 删除红包活动参与者记录信息
     * 
     * @param id 红包活动参与者记录主键
     * @return 结果
     */
    public int deleteRedpacketActivityParticipantsById(Long id);

    /**
     * 获取当前活跃用户数
     * 
     * @return 活跃用户数
     */
    public int getActiveUserCount();

    /**
     * 获取排队用户数
     * 
     * @return 排队用户数
     */
    public int getQueuedUserCount();

    /**
     * 用户加入活动
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean joinActivity(String userId, String sessionId);

    /**
     * 更新用户心跳时间
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean updateUserHeartbeat(String userId, String sessionId);

    /**
     * 用户离开活动
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean leaveActivity(String userId, String sessionId);
}
