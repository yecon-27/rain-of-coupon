package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketUserParticipationLogMapper;
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;
import com.ruoyi.redpacket.service.IRedpacketUserParticipationLogService;

/**
 * 用户参与记录（记录所有参与行为）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Service
public class RedpacketUserParticipationLogServiceImpl implements IRedpacketUserParticipationLogService 
{
    @Autowired
    private RedpacketUserParticipationLogMapper redpacketUserParticipationLogMapper;

    /**
     * 查询用户参与记录（记录所有参与行为）
     * 
     * @param id 用户参与记录（记录所有参与行为）主键
     * @return 用户参与记录（记录所有参与行为）
     */
    @Override
    public RedpacketUserParticipationLog selectRedpacketUserParticipationLogById(Long id)
    {
        return redpacketUserParticipationLogMapper.selectRedpacketUserParticipationLogById(id);
    }

    /**
     * 查询用户参与记录（记录所有参与行为）列表
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 用户参与记录（记录所有参与行为）
     */
    @Override
    public List<RedpacketUserParticipationLog> selectRedpacketUserParticipationLogList(RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        return redpacketUserParticipationLogMapper.selectRedpacketUserParticipationLogList(redpacketUserParticipationLog);
    }

    /**
     * 新增用户参与记录（记录所有参与行为）
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 结果
     */
    @Override
    public int insertRedpacketUserParticipationLog(RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        return redpacketUserParticipationLogMapper.insertRedpacketUserParticipationLog(redpacketUserParticipationLog);
    }

    /**
     * 修改用户参与记录（记录所有参与行为）
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 结果
     */
    @Override
    public int updateRedpacketUserParticipationLog(RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        return redpacketUserParticipationLogMapper.updateRedpacketUserParticipationLog(redpacketUserParticipationLog);
    }

    /**
     * 批量删除用户参与记录（记录所有参与行为）
     * 
     * @param ids 需要删除的用户参与记录（记录所有参与行为）主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketUserParticipationLogByIds(Long[] ids)
    {
        return redpacketUserParticipationLogMapper.deleteRedpacketUserParticipationLogByIds(ids);
    }

    /**
     * 删除用户参与记录（记录所有参与行为）信息
     * 
     * @param id 用户参与记录（记录所有参与行为）主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketUserParticipationLogById(Long id)
    {
        return redpacketUserParticipationLogMapper.deleteRedpacketUserParticipationLogById(id);
    }
}
