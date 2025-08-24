# API 接口规范

## 核心业务接口

### 红包雨抽奖接口

| 方法 | 接口地址 | 描述 |
|------|----------|------|
| POST | `/redpacket/lottery/draw` | 执行红包雨逻辑，基于点击数量计算概率 |
| GET | `/redpacket/lottery/records` | 获取用户参与历史记录，支持分页 |
| GET | `/redpacket/lottery/count` | 获取剩余抽奖次数和每日统计 |
| GET | `/redpacket/lottery/status` | 检查用户参与资格和状态 |
| GET | `/redpacket/lottery/prizes` | 获取可用奖品列表（自动过滤零库存） |
| POST | `/redpacket/lottery/claim/{logId}` | 领取/使用中奖记录中的优惠券 |
| GET | `/redpacket/lottery/config` | 获取活动配置和时间设置 |

### 抽奖接口详情

#### POST /redpacket/lottery/draw

执行红包雨抽奖逻辑

**请求参数:**
```json
{
  "clickedCount": 85,
  "sessionId": "user_session_123",
  "ipAddress": "192.168.1.100"
}
```

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "isWin": true,
    "prize": {
      "id": 1,
      "name": "10元优惠券",
      "description": "满50元可用"
    },
    "remainingAttempts": 2,
    "clickedCount": 85,
    "sessionId": "user_session_123"
  }
}
```

#### GET /redpacket/lottery/status

检查用户参与状态（支持并行调用）

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "canParticipate": true,
    "remainingAttempts": 3,
    "alreadyWon": false,
    "trafficStatus": "normal",
    "prizeStockStatus": "sufficient",
    "sessionStatus": "valid",
    "priority": "default"
  }
}
```

**状态优先级说明:**
- `priority: "already_won"` - 用户已中奖，跳转到 PrizePage
- `priority: "stock_insufficient"` - 奖品库存不足，显示 PrizeStockTip
- `priority: "attempts_exhausted"` - 抽奖次数用完或重复会话，显示 WarningTip  
- `priority: "traffic_crowded"` - 流量拥挤，显示 CrowdingTip
- `priority: "default"` - 可以正常抽奖，跳转到 LoadingPage

## 业务逻辑说明

### 中奖概率算法

基于用户点击红包数量动态计算中奖概率：

```java
int clickedCount = request.getClickedCount();
double baseProbability = 0.05; // 5% 基础概率
double probabilityMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 0.8;
double finalProbability = baseProbability * Math.min(probabilityMultiplier, 4.0);
```

### 新增接口

| 方法 | 接口地址 | 描述 |
|------|----------|------|
| GET | `/redpacket/lottery/parallel-check` | 并行状态检查接口 |
| POST | `/redpacket/lottery/session/validate` | 会话验证接口 |
| GET | `/redpacket/lottery/stock/check` | 奖品库存检查接口 |
| POST | `/redpacket/lottery/traffic/report` | 流量状态上报接口 |

#### GET /redpacket/lottery/parallel-check

并行检查用户状态，返回处理优先级

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功", 
  "data": {
    "checks": {
      "alreadyWon": false,
      "stockSufficient": true,
      "attemptsRemaining": 2,
      "sessionValid": true,
      "trafficNormal": true
    },
    "action": "proceed_to_lottery",
    "priority": "default",
    "redirectTo": "/loading"
  }
}
```

### 防刷机制

- 每日最多参与3次
- 中奖后停止参与（一人只能中一次）
- IP频率限制（1小时10次）
- Session ID防重复抽奖
- 活动时间控制

### 并发控制

- Redis缓存 + 数据库事务保证数据一致性
- 使用Redis进行库存扣减防止超发
- 并行API调用优化响应速度
- 状态检查优先级处理机制