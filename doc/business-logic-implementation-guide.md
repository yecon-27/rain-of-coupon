# 业务逻辑实现指南

## 🎯 **若依代码生成 vs 自定义业务逻辑**

### ✅ **若依生成的（基础CRUD）**
```java
// RedpacketUserParticipationLogService.java
public List<RedpacketUserParticipationLog> selectList(RedpacketUserParticipationLog log);
public RedpacketUserParticipationLog selectById(Long id);
public int insert(RedpacketUserParticipationLog log);
public int update(RedpacketUserParticipationLog log);
public int deleteById(Long id);
```

### 🔥 **你需要自己实现的（核心业务逻辑）**

#### 1. 后端Service层新增方法
```java
// IRedpacketUserParticipationLogService.java 中添加
public interface IRedpacketUserParticipationLogService {
    // 若依生成的基础方法...
    
    // 🔥 你需要添加的业务方法：
    
    /**
     * 检查用户是否曾经中过奖
     */
    boolean hasEverWon(Long userId);
    
    /**
     * 获取用户今日参与次数
     */
    int getTodayParticipationCount(Long userId);
    
    /**
     * 获取用户剩余参与次数
     */
    int getRemainingCount(Long userId);
    
    /**
     * 获取用户完整状态
     */
    UserParticipationStatus getUserStatus(Long userId);
    
    /**
     * 执行抽奖并记录参与
     */
    DrawResult performDrawAndRecord(Long userId, String ipAddress);
    
    /**
     * 获取用户中奖记录
     */
    RedpacketUserParticipationLog getUserWinRecord(Long userId);
}
```

#### 2. Service实现类
```java
// RedpacketUserParticipationLogServiceImpl.java 中实现
@Service
public class RedpacketUserParticipationLogServiceImpl implements IRedpacketUserParticipationLogService {
    
    @Autowired
    private RedpacketUserParticipationLogMapper participationMapper;
    
    // 若依生成的基础方法实现...
    
    // 🔥 你需要实现的业务方法：
    
    @Override
    public boolean hasEverWon(Long userId) {
        // 参考 hybrid-rule-implementation.sql 中的查询
        return participationMapper.countWinRecords(userId) > 0;
    }
    
    @Override
    public int getTodayParticipationCount(Long userId) {
        // 参考 hybrid-rule-implementation.sql 中的查询
        return participationMapper.countTodayParticipations(userId);
    }
    
    @Override
    public UserParticipationStatus getUserStatus(Long userId) {
        // 实现复杂的状态查询逻辑
        UserParticipationStatus status = new UserParticipationStatus();
        
        // 检查是否曾经中奖
        status.setHasEverWon(hasEverWon(userId));
        
        // 获取今日参与次数
        int todayCount = getTodayParticipationCount(userId);
        status.setTodayDrawCount(todayCount);
        
        // 计算剩余次数
        int maxDrawsPerDay = configService.getMaxDrawsPerDay();
        status.setRemainingCount(maxDrawsPerDay - todayCount);
        
        // 判断是否还能中奖
        status.setCanStillWin(!status.getHasEverWon());
        
        return status;
    }
    
    @Override
    public DrawResult performDrawAndRecord(Long userId, String ipAddress) {
        // 🔥 核心抽奖逻辑
        
        // 1. 检查是否已中奖
        if (hasEverWon(userId)) {
            // 已中奖，强制未中奖但仍记录参与
            DrawResult result = DrawResult.notWin();
            recordParticipation(userId, ipAddress, result);
            return result;
        }
        
        // 2. 检查今日次数
        if (getRemainingCount(userId) <= 0) {
            throw new ServiceException("今日参与次数已用完");
        }
        
        // 3. 执行抽奖
        DrawResult result = lotteryService.performDraw();
        
        // 4. 记录参与
        recordParticipation(userId, ipAddress, result);
        
        return result;
    }
    
    private void recordParticipation(Long userId, String ipAddress, DrawResult result) {
        RedpacketUserParticipationLog log = new RedpacketUserParticipationLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setIsWin(result.isWin() ? 1 : 0);
        
        if (result.isWin()) {
            log.setPrizeId(result.getPrizeId());
            log.setPrizeName(result.getPrizeName());
        }
        
        participationMapper.insert(log);
    }
}
```

#### 3. Mapper层新增方法
```java
// RedpacketUserParticipationLogMapper.java 中添加
public interface RedpacketUserParticipationLogMapper {
    // 若依生成的基础方法...
    
    // 🔥 你需要添加的查询方法：
    
    /**
     * 统计用户中奖次数
     */
    int countWinRecords(@Param("userId") Long userId);
    
    /**
     * 统计用户今日参与次数
     */
    int countTodayParticipations(@Param("userId") Long userId);
    
    /**
     * 获取用户完整状态
     */
    UserParticipationStatus selectUserStatus(@Param("userId") Long userId);
    
    /**
     * 获取用户中奖记录
     */
    RedpacketUserParticipationLog selectWinRecord(@Param("userId") Long userId);
}
```

#### 4. Mapper XML实现
```xml
<!-- RedpacketUserParticipationLogMapper.xml 中添加 -->
<mapper namespace="com.ruoyi.redpacket.mapper.RedpacketUserParticipationLogMapper">
    
    <!-- 若依生成的基础SQL... -->
    
    <!-- 🔥 你需要添加的SQL： -->
    
    <!-- 统计用户中奖次数 -->
    <select id="countWinRecords" parameterType="Long" resultType="int">
        SELECT COUNT(*) 
        FROM redpacket_user_participation_log 
        WHERE user_id = #{userId} AND is_win = 1
    </select>
    
    <!-- 统计用户今日参与次数 -->
    <select id="countTodayParticipations" parameterType="Long" resultType="int">
        SELECT COUNT(*) 
        FROM redpacket_user_participation_log 
        WHERE user_id = #{userId} 
        AND DATE(participation_time) = CURDATE()
    </select>
    
    <!-- 获取用户完整状态 -->
    <select id="selectUserStatus" parameterType="Long" resultType="UserParticipationStatus">
        SELECT 
            COUNT(*) as totalParticipations,
            COUNT(CASE WHEN is_win = 1 THEN 1 END) as totalWins,
            COUNT(CASE WHEN DATE(participation_time) = CURDATE() THEN 1 END) as todayParticipations,
            (COUNT(CASE WHEN is_win = 1 THEN 1 END) = 0) as canStillWin
        FROM redpacket_user_participation_log 
        WHERE user_id = #{userId}
    </select>
    
</mapper>
```

#### 5. Controller层新增接口
```java
// RedpacketUserParticipationLogController.java 中添加
@RestController
@RequestMapping("/redpacket/participation")
public class RedpacketUserParticipationLogController {
    
    @Autowired
    private IRedpacketUserParticipationLogService participationService;
    
    // 若依生成的基础接口...
    
    // 🔥 你需要添加的业务接口：
    
    /**
     * 获取用户状态
     */
    @GetMapping("/status")
    public AjaxResult getUserStatus() {
        Long userId = SecurityUtils.getUserId();
        UserParticipationStatus status = participationService.getUserStatus(userId);
        return AjaxResult.success(status);
    }
    
    /**
     * 执行抽奖
     */
    @PostMapping("/draw")
    public AjaxResult performDraw() {
        Long userId = SecurityUtils.getUserId();
        String ipAddress = IpUtils.getIpAddr(ServletUtils.getRequest());
        
        try {
            DrawResult result = participationService.performDrawAndRecord(userId, ipAddress);
            return AjaxResult.success(result);
        } catch (ServiceException e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
```

## 🎯 **总结**

### 若依代码生成提供：
- ✅ 基础的表结构映射
- ✅ 简单的CRUD操作
- ✅ 基础的Controller、Service、Mapper框架

### 你需要自己实现：
- 🔥 **核心业务逻辑**（混合规则的抽奖逻辑）
- 🔥 **复杂查询**（参考hybrid-rule-implementation.sql）
- 🔥 **状态管理**（用户参与状态、中奖状态等）
- 🔥 **前端交互**（Vue组件、状态管理、用户界面）

**所以是的，真正的业务逻辑需要你在前后端都要实现！若依只是提供了基础框架。**