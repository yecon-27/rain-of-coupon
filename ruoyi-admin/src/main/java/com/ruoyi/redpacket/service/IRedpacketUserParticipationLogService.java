package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;

/**
 * 用户参与记录（记录所有参与行为）Service接口
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
public interface IRedpacketUserParticipationLogService 
{
    /**
     * 查询用户参与记录（记录所有参与行为）
     * 
     * @param id 用户参与记录（记录所有参与行为）主键
     * @return 用户参与记录（记录所有参与行为）
     */
    public RedpacketUserParticipationLog selectRedpacketUserParticipationLogById(Long id);

    /**
     * 查询用户参与记录（记录所有参与行为）列表
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 用户参与记录（记录所有参与行为）集合
     */
    public List<RedpacketUserParticipationLog> selectRedpacketUserParticipationLogList(RedpacketUserParticipationLog redpacketUserParticipationLog);

    /**
     * 新增用户参与记录（记录所有参与行为）
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 结果
     */
    public int insertRedpacketUserParticipationLog(RedpacketUserParticipationLog redpacketUserParticipationLog);

    /**
     * 修改用户参与记录（记录所有参与行为）
     * 
     * @param redpacketUserParticipationLog 用户参与记录（记录所有参与行为）
     * @return 结果
     */
    public int updateRedpacketUserParticipationLog(RedpacketUserParticipationLog redpacketUserParticipationLog);

    /**
     * 批量删除用户参与记录（记录所有参与行为）
     * 
     * @param ids 需要删除的用户参与记录（记录所有参与行为）主键集合
     * @return 结果
     */
    public int deleteRedpacketUserParticipationLogByIds(Long[] ids);

    /**
     * 删除用户参与记录（记录所有参与行为）信息
     * 
     * @param id 用户参与记录（记录所有参与行为）主键
     * @return 结果
     */
    public int deleteRedpacketUserParticipationLogById(Long id);
}
