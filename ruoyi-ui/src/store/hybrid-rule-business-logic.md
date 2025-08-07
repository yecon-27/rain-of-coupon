# 混合规则业务逻辑详解：一共只能中一次 + 每天三次参与机会

## 🎯 **混合规则的核心逻辑**

- **总中奖限制**: 用户一共只能中一次奖
- **每日参与**: 如果没中奖，每天都有3次参与机会
- **持续激励**: 中奖前保持用户参与积极性

## 📊 **关键状态字段**

### 核心字段
```javascript
userStatus: {
  hasEverWon: false,        // 是否曾经中过奖（核心判断字段）
  winRecord: null,          // 中奖记录（最多只有一条）
  canStillWin: true,        // 是否还能中奖（!hasEverWon）
  remainingCount: 3,        // 今日剩余参与次数
  canParticipateToday: true // 今日是否还能参与
}
```

## 🔄 **业务流程**

### 1. 用户登录后的状态检查
```javascript
async initUserStatus() {
  const status = await getUserStatus();
  
  if (status.hasEverWon) {
    // 已经中过奖了
    this.showWinnerStatus(status.winRecord);
    this.canStillWin = false;
  } else {
    // 还没中过奖
    this.canStillWin = true;
    
    if (status.remainingCount > 0) {
      // 今日还有参与机会
      this.canDraw = true;
    } else {
      // 今日次数用完，明天再来
      this.showTomorrowMessage();
    }
  }
}
```

### 2. 抽奖逻辑
```javascript
async performDraw() {
  // 前端检查
  if (this.hasEverWon) {
    this.$message.warning('您已经中过奖了，感谢参与！');
    return;
  }
  
  if (this.remainingCount <= 0) {
    this.$message.warning('今日参与次数已用完，明天再来！');
    return;
  }
  
  // 执行抽奖
  const result = await drawLottery();
  
  if (result.isWin) {
    // 中奖了！
    this.hasEverWon = true;
    this.winRecord = result;
    this.canStillWin = false;
    this.showPrizeModal(result);
    
    // 中奖后用户可以选择继续参与（但不会再中奖）或者结束
    this.showCongratulationsAndOptions();
    
  } else {
    // 未中奖
    this.showEncourageTip();
  }
  
  // 更新参与次数
  this.remainingCount--;
  this.todayDrawCount++;
  
  // 检查今日是否还能参与
  this.canParticipateToday = this.remainingCount > 0;
}
```

### 3. 后端抽奖逻辑
```java
@PostMapping("/draw")
public AjaxResult drawLottery() {
    Long userId = getCurrentUserId();
    String ipAddress = getClientIpAddress();
    
    // 1. 检查是否已经中过奖
    boolean hasEverWon = participationService.hasEverWon(userId);
    
    // 2. 检查今日参与次数
    int todayCount = participationService.getTodayCount(userId);
    int maxDrawsPerDay = configService.getMaxDrawsPerDay(); // 3
    if (todayCount >= maxDrawsPerDay) {
        return AjaxResult.error("今日参与次数已用完，明天再来！");
    }
    
    // 3. 执行抽奖逻辑
    DrawResult result;
    if (hasEverWon) {
        // 已经中过奖，强制设为未中奖
        result = DrawResult.notWin();
    } else {
        // 还没中过奖，正常抽奖
        result = lotteryService.performDraw();
    }
    
    // 4. 记录参与
    ParticipationLog log = new ParticipationLog();
    log.setUserId(userId);
    log.setIpAddress(ipAddress);
    log.setIsWin(result.isWin());
    
    if (result.isWin()) {
        log.setPrizeId(result.getPrizeId());
        log.setPrizeName(result.getPrizeName());
    }
    
    participationService.save(log);
    
    return AjaxResult.success(result);
}
```

## 🎮 **不同用户状态的体验设计**

### 状态1: 新用户（从未参与）
```javascript
showMessage: "欢迎参与红包雨！今日有3次机会"
showButton: "立即参与" (高亮)
canWin: true
```

### 状态2: 参与中用户（未中奖，还有次数）
```javascript
showMessage: "今日还有 2 次机会，加油！"
showButton: "继续参与" (高亮)
canWin: true
```

### 状态3: 今日次数用完（未中奖）
```javascript
showMessage: "今日次数已用完，明天继续加油！"
showButton: "明天再来" (置灰)
canWin: true // 明天还有机会中奖
```

### 状态4: 已中奖用户
```javascript
showMessage: "恭喜！您已获得 188元优惠券"
showButton: "查看我的优惠券" (绿色)
canWin: false // 永远不能再中奖

// 可选：允许继续参与但不会中奖
extraButton: "继续参与（体验）" (灰色)
```

## 📅 **每日重置机制**

### 自动重置（每天0点）
```javascript
// 重置的状态
todayDrawCount: 0
remainingCount: 3
canParticipateToday: true

// 不重置的状态
hasEverWon: 保持原值
winRecord: 保持原值
canStillWin: 保持原值
```

### 前端检查
```javascript
checkDateChange() {
  const today = new Date().toDateString();
  const lastDate = localStorage.getItem('lastParticipationDate');
  
  if (lastDate !== today) {
    // 跨天了，重置今日状态（但不重置中奖状态）
    this.resetTodayParticipation();
    localStorage.setItem('lastParticipationDate', today);
  }
}

resetTodayParticipation() {
  this.todayDrawCount = 0;
  this.remainingCount = this.maxDrawsPerDay;
  this.canParticipateToday = true;
  // 注意：不重置 hasEverWon 和 winRecord
}
```

## 📊 **数据统计示例**

### 用户A的参与历程
```
第1天: 参与3次，未中奖 → 明天再来
第2天: 参与3次，未中奖 → 明天再来  
第3天: 参与2次，第2次中奖 → 获得优惠券，活动结束
第4天: 可以参与但不会中奖（可选功能）
```

### 用户B的参与历程
```
第1天: 参与1次，中奖 → 获得优惠券，活动结束
第2天: 可以参与但不会中奖（可选功能）
```

## 🎯 **混合规则的优势**

1. **成本可控** - 每人最多一张优惠券
2. **持续参与** - 未中奖用户每天都有希望
3. **用户体验** - 中奖前保持积极性
4. **公平性** - 每个用户都有中奖机会
5. **防刷效果** - 总收益有上限

## 🤔 **中奖后的选择**

### 选项A: 中奖后停止参与
```javascript
if (hasEverWon) {
  showMessage: "您已中奖，感谢参与！"
  disableParticipation: true
}
```

### 选项B: 中奖后可继续参与（体验模式）
```javascript
if (hasEverWon) {
  showMessage: "您已中奖！可继续体验红包雨"
  enableParticipation: true
  guaranteeNoWin: true // 但不会再中奖
}
```

你倾向于哪种方式？我建议选择**选项A**，让中奖用户专注于使用优惠券，给其他用户更多机会。