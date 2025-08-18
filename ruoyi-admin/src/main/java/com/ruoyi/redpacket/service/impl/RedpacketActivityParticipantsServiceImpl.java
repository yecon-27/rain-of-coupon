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

    /**
     * 实现获取活跃用户数的逻辑
     * 
     * @return 活跃用户数
     */
    @Override
    public int getActiveUserCount() {
        RedpacketActivityParticipants query = new RedpacketActivityParticipants();
        query.setStatus("active"); // 只查询活跃状态的用户
        List<RedpacketActivityParticipants> activeUsers = redpacketActivityParticipantsMapper.selectRedpacketActivityParticipantsList(query);
        return activeUsers.size();
    }

    /**
     * 实现获取排队用户数的逻辑
     * 
     * @return 排队用户数
     */
    @Override
    public int getQueuedUserCount() {
        // 实现获取排队用户数的逻辑
        // 这里需要根据实际的业务逻辑来实现
        return 0; // 临时返回0
    }

    /**
     * 加入红包活动逻辑
     * 
     * @return 结果
     */
    @Override
    public boolean joinActivity(String userId, String sessionId) {
        try {
            RedpacketActivityParticipants participant = new RedpacketActivityParticipants();
            participant.setUserId(userId);
            participant.setSessionId(sessionId);
            participant.setJoinTime(new java.util.Date());
            participant.setStatus("active");  // 设置状态为活跃
            participant.setLastHeartbeat(new java.util.Date());  // 设置心跳时间
            
            int result = redpacketActivityParticipantsMapper.insertRedpacketActivityParticipants(participant);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 更新用户心跳时间
     */
    @Override
    public boolean updateUserHeartbeat(String userId, String sessionId) {
        try {
            // 查找现有的参与者记录
            RedpacketActivityParticipants query = new RedpacketActivityParticipants();
            query.setUserId(userId);
            query.setSessionId(sessionId);
            query.setStatus("active");
            
            List<RedpacketActivityParticipants> participants = 
                redpacketActivityParticipantsMapper.selectRedpacketActivityParticipantsList(query);
            
            if (!participants.isEmpty()) {
                RedpacketActivityParticipants participant = participants.get(0);
                participant.setLastHeartbeat(new java.util.Date());
                participant.setUpdatedAt(new java.util.Date());
                
                int result = redpacketActivityParticipantsMapper.updateRedpacketActivityParticipants(participant);
                return result > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用户离开活动
     */
    @Override
    public boolean leaveActivity(String userId, String sessionId) {
        try {
            // 查找现有的参与者记录
            RedpacketActivityParticipants query = new RedpacketActivityParticipants();
            query.setUserId(userId);
            query.setSessionId(sessionId);
            query.setStatus("active");
            
            List<RedpacketActivityParticipants> participants = 
                redpacketActivityParticipantsMapper.selectRedpacketActivityParticipantsList(query);
            
            if (!participants.isEmpty()) {
                RedpacketActivityParticipants participant = participants.get(0);
                participant.setStatus("left");
                participant.setLeaveTime(new java.util.Date());
                participant.setUpdatedAt(new java.util.Date());
                
                int result = redpacketActivityParticipantsMapper.updateRedpacketActivityParticipants(participant);
                return result > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
