package com.ruoyi.redpacket.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.redpacket.mapper.RedpacketUserPrizeLogMapper;
import com.ruoyi.redpacket.domain.RedpacketUserPrizeLog;
import com.ruoyi.redpacket.service.IRedpacketUserPrizeLogService;

/**
 * 用户抽奖记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@Service
public class RedpacketUserPrizeLogServiceImpl implements IRedpacketUserPrizeLogService 
{
    @Autowired
    private RedpacketUserPrizeLogMapper redpacketUserPrizeLogMapper;

    /**
     * 查询用户抽奖记录
     * 
     * @param id 用户抽奖记录主键
     * @return 用户抽奖记录
     */
    @Override
    public RedpacketUserPrizeLog selectRedpacketUserPrizeLogById(Long id)
    {
        return redpacketUserPrizeLogMapper.selectRedpacketUserPrizeLogById(id);
    }

    /**
     * 查询用户抽奖记录列表
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 用户抽奖记录
     */
    @Override
    public List<RedpacketUserPrizeLog> selectRedpacketUserPrizeLogList(RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        return redpacketUserPrizeLogMapper.selectRedpacketUserPrizeLogList(redpacketUserPrizeLog);
    }

    /**
     * 新增用户抽奖记录
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 结果
     */
    @Override
    public int insertRedpacketUserPrizeLog(RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        return redpacketUserPrizeLogMapper.insertRedpacketUserPrizeLog(redpacketUserPrizeLog);
    }

    /**
     * 修改用户抽奖记录
     * 
     * @param redpacketUserPrizeLog 用户抽奖记录
     * @return 结果
     */
    @Override
    public int updateRedpacketUserPrizeLog(RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        return redpacketUserPrizeLogMapper.updateRedpacketUserPrizeLog(redpacketUserPrizeLog);
    }

    /**
     * 批量删除用户抽奖记录
     * 
     * @param ids 需要删除的用户抽奖记录主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketUserPrizeLogByIds(Long[] ids)
    {
        return redpacketUserPrizeLogMapper.deleteRedpacketUserPrizeLogByIds(ids);
    }

    /**
     * 删除用户抽奖记录信息
     * 
     * @param id 用户抽奖记录主键
     * @return 结果
     */
    @Override
    public int deleteRedpacketUserPrizeLogById(Long id)
    {
        return redpacketUserPrizeLogMapper.deleteRedpacketUserPrizeLogById(id);
    }
}
