# 数据库表结构更新说明

## 表结构简化

删除旧的 `redpacket_user_prize_log` 表，统一使用新的 `redpacket_user_participation_log` 表来记录所有用户参与行为。

## 新表结构

### redpacket_user_participation_log
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint(20) | 主键 |
| user_id | bigint(20) | 用户ID |
| participation_time | datetime | 参与时间 |
| ip_address | varchar(45) | IP记录（防刷） |
| is_win | tinyint(1) | 是否中奖 |
| prize_id | bigint(20) | 中奖奖品ID |
| prize_name | varchar(100) | 奖品名称 |
| is_used | tinyint(1) | 是否使用 |

这个表包含了所有必要的信息：
- 用户参与记录
- 中奖信息
- 防刷检测（IP + 时间）
- 优惠券使用状态

## 业务逻辑说明

### 数据写入流程
用户参与抽奖时，只需要写入 `redpacket_user_participation_log` 表，包含：
- 参与时间和IP（防刷检测）
- 中奖状态和奖品信息
- 优惠券使用状态

### 后端API更新要点

#### 1. 抽奖接口 `/redpacket/lottery/draw`
```java
// 伪代码示例
public DrawResult drawLottery(DrawRequest request) {
    // 写入参与记录
    ParticipationRecord participation = insertParticipationLog(request);
    
    // 返回结果
    return new DrawResult(participation);
}
```

#### 2. 记录查询接口 `/redpacket/lottery/records`
```java
// 查询用户参与记录
public PageResult<ParticipationRecord> getUserRecords(UserRecordsQuery query) {
    return participationLogMapper.selectByUserId(query);
}
```

#### 3. 优惠券使用接口 `/redpacket/lottery/claim/{participationId}`
```java
// 使用参与记录ID
public ClaimResult claimPrize(Long participationId) {
    ParticipationRecord record = participationLogMapper.selectById(participationId);
    // 更新使用状态
    record.setIsUsed(1);
    participationLogMapper.updateById(record);
    return ClaimResult.success();
}
```

## 前端代码更新

### API函数更新
- ✅ `claimPrize` 函数参数从 `logId` 改为 `participationId`
- ✅ 相关类型定义已使用 `ParticipationRecord` 接口

### 需要后端配合的更新
1. 更新 `/redpacket/lottery/claim/{id}` 端点使用新表
2. 确保所有查询参与记录的接口使用新表结构
3. 更新中奖记录查询逻辑

## 回滚计划

如果迁移出现问题，可以通过以下步骤回滚：

```sql
-- 从备份表恢复数据
DROP TABLE redpacket_user_prize_log;
CREATE TABLE redpacket_user_prize_log AS 
SELECT * FROM redpacket_user_prize_log_backup;

-- 删除迁移的数据
DELETE FROM activity_participants 
WHERE session_id LIKE 'migrated_%';
```

## 注意事项

1. **执行顺序**：先完成后端API更新，再执行数据迁移
2. **业务影响**：建议在低峰期执行迁移
3. **数据备份**：迁移前务必备份相关数据
4. **测试验证**：在测试环境先完整执行一遍迁移流程
5. **监控告警**：迁移后监控相关功能是否正常