package com.ruoyi.redpacket.service;

import java.util.List;
import com.ruoyi.redpacket.domain.RedpacketUserPrizeLog;

/**
 * 用户抽奖记录Service接口
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public interface IRedpacketUserPrizeLogService 
{
    /**
     * 查询用户抽奖记录
     * 
     * @param id 用户抽奖记录主键
     * @return 用户抽奖记录
     */
    public RedpacketUserPrizeLog selectRedpacketUserPrizeLogById(Long id);

    /**
     * 查询用户抽奖记录列表
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 用户抽奖记录集合
     */
    public List<RedpacketUserPrizeLog> selectRedpacketUserPrizeLogList(RedpacketUserPrizeLog redpacketUserPrizeLog);

    /**
     * 新增用户抽奖记录
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 结果
     */
    public int insertRedpacketUserPrizeLog(RedpacketUserPrizeLog redpacketUserPrizeLog);

    /**
     * 修改用户抽奖记录
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 结果
     */
    public int updateRedpacketUserPrizeLog(RedpacketUserPrizeLog redpacketUserPrizeLog);

    /**
     * 批量删除用户抽奖记录
     * 
     * @param ids 需要删除的用户抽奖记录主键集合
     * @return 结果
     */
    public int deleteRedpacketUserPrizeLogByIds(Long[] ids);

    /**
     * 删除用户抽奖记录信息
     * 
     * @param id 用户抽奖记录主键
     * @return 结果
     */
    public int deleteRedpacketUserPrizeLogById(Long id);
}
