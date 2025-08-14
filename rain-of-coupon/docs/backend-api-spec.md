# 后端API接口规范

## 数据库表结构

### redpacket_user_participation_log

统一的用户参与记录表，包含所有参与行为、中奖信息和防刷检测数据。

## API接口规范

### 1. 用户抽奖接口

**接口地址：** `POST /redpacket/lottery/draw`

**请求参数：**

```json
{
  "clickedCount": 50,
  "sessionId": "session_1234567890_abc123"
}
```

**响应数据：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1001,
    "userId": 12345,
    "participationTime": "2025-01-14 10:30:00",
    "ipAddress": "192.168.1.100",
    "isWin": 1,
    "prizeId": 2,
    "prizeName": "588元优惠券",
    "isUsed": 0
  }
}
```

**业务逻辑：**

1. 写入参与记录到 `redpacket_user_participation_log` 表
2. 返回参与记录数据

### 2. 用户参与记录查询

**接口地址：** `GET /redpacket/lottery/records`

**请求参数：**

```
pageNum=1&pageSize=10&isWin=1&isUsed=0&startDate=2025-01-01&endDate=2025-01-31
```

**响应数据：**

```json
{
  "code": 200,
  "msg": "success",
  "rows": [
    {
      "id": 1001,
      "userId": 12345,
      "participationTime": "2025-01-14 10:30:00",
      "ipAddress": "192.168.1.100",
      "isWin": 1,
      "prizeId": 2,
      "prizeName": "588元优惠券",
      "isUsed": 0
    }
  ],
  "total": 25
}
```

**数据来源：** `redpacket_user_participation_log` 表

### 3. 优惠券使用接口

**接口地址：** `POST /redpacket/lottery/claim/{participationId}`

**路径参数：**

- `participationId`: 参与记录ID

**响应数据：**

```json
{
  "code": 200,
  "msg": "优惠券使用成功",
  "data": {
    "participationId": 1001,
    "isUsed": 1,
    "usedTime": "2025-01-14 15:30:00"
  }
}
```

**业务逻辑：**

1. 根据 `participationId` 查询 `redpacket_user_participation_log` 表
2. 更新 `is_used` 字段为 1

### 4. 用户状态查询

**接口地址：** `GET /redpacket/lottery/status`

**响应数据：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "canDraw": true,
    "hasEverWon": true,
    "isCrowded": false,
    "remainingCount": 2,
    "todayParticipations": [
      {
        "id": 1001,
        "participationTime": "2025-01-14 10:30:00",
        "isWin": 1,
        "clickedCount": 50,
        "winProbability": 0.05
      }
    ],
    "winRecords": [
      {
        "id": 1001,
        "participationTime": "2025-01-14 10:30:00",
        "prizeName": "588元优惠券",
        "isUsed": 0,
        "clickedCount": 50
      }
    ]
  }
}
```

**数据来源：** `redpacket_user_participation_log` 表

## 数据库操作建议

### 查询优化

1. **参与记录查询**：利用已建立的索引进行高效查询
2. **防刷检测**：使用IP和时间维度的复合索引
3. **统计查询**：根据业务需要建立合适的索引

### 事务处理

```java
@Transactional
public DrawResult drawLottery(DrawRequest request) {
    try {
        // 1. 插入参与记录
        ParticipationRecord participation = insertParticipationLog(request);

        // 2. 更新奖品库存等其他操作
        if (participation.isWin()) {
            updatePrizeStock(participation.getPrizeId());
        }

        return DrawResult.success(participation);
    } catch (Exception e) {
        // 事务回滚
        throw new BusinessException("抽奖失败", e);
    }
}
```

### 索引使用建议

- `idx_user_id` - 用户查询
- `idx_participation_time` - 时间范围查询
- `idx_user_date` - 用户+时间复合查询
- `idx_user_win_used` - 用户中奖状态查询
- `idx_ip_address` - IP防刷检测

## 前端调用示例

```typescript
// 参与抽奖
const result = await drawLottery({
  clickedCount: 50,
  sessionId: 'session_123',
})

// 查询参与记录
const records = await getUserRecords({
  pageNum: 1,
  pageSize: 10,
  isWin: 1,
})

// 使用优惠券
const claimResult = await claimPrize(1001) // 使用参与记录ID
```
