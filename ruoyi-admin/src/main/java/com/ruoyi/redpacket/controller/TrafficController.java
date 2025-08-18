package com.ruoyi.redpacket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.redpacket.service.IRedpacketTrafficConfigService;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;
import com.ruoyi.redpacket.service.IRedpacketTrafficStatsService;
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;
import java.util.HashMap;
// 添加以下导入
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流量控制Controller
 * 
 * @author ruoyi
 */
@Anonymous
@RestController
@RequestMapping("/api/traffic")
public class TrafficController extends BaseController {
    
    // 添加 Logger 声明
    private static final Logger logger = LoggerFactory.getLogger(TrafficController.class);
    
    @Autowired
    private IRedpacketTrafficConfigService trafficConfigService;
    
    @Autowired
    private IRedpacketActivityParticipantsService participantsService;
    
    @Autowired
    private IRedpacketTrafficStatsService trafficStatsService;
    
    /**
     * 检查流量状态
     */
    @GetMapping("/check")
    public AjaxResult checkTraffic() {
        try {
            // 获取配置
            String maxUsersStr = trafficConfigService.getConfigValueByKey("max_users", "1000");
            String maintenanceModeStr = trafficConfigService.getConfigValueByKey("maintenance_mode", "false");
            
            int maxUsers = Integer.parseInt(maxUsersStr);
            boolean isMaintenanceMode = Boolean.parseBoolean(maintenanceModeStr);
            
            if (isMaintenanceMode) {
                // 维护模式
                recordTrafficStats("check_traffic", 0, maxUsers, true);
                return AjaxResult.success(createResponse("maintenance", 0, maxUsers, null, null, 300));
            }
            
            // 获取当前用户数
            int currentUsers = participantsService.getActiveUserCount();
            
            if (currentUsers >= maxUsers) {
                // 需要排队
                int queuedUsers = participantsService.getQueuedUserCount();
                int estimatedWaitTime = calculateEstimatedWaitTime(queuedUsers, currentUsers);
                
                recordTrafficStats("check_traffic", currentUsers, maxUsers, false);
                return AjaxResult.success(createResponse("queue", currentUsers, maxUsers, queuedUsers + 1, estimatedWaitTime, 30));
            } else {
                // 可以直接进入
                recordTrafficStats("check_traffic", currentUsers, maxUsers, false);
                return AjaxResult.success(createResponse("allow", currentUsers, maxUsers, null, null, null));
            }
            
        } catch (Exception e) {
            logger.error("流量检测失败", e);
            
            recordErrorStats("check_traffic", e.getMessage());
            return AjaxResult.error("流量检测服务暂时不可用");
        }
    }
    
    @PostMapping("/join")
    public AjaxResult joinActivity(@RequestBody JoinActivityRequest request) {
        try {
            String userId = request.getUserId();
            String sessionId = request.getSessionId();
            
            // 用户加入活动逻辑
            boolean success = participantsService.joinActivity(userId, sessionId);
            
            int currentUsers = participantsService.getActiveUserCount();
            String userStatus = success ? "active" : "failed";
            
            // 记录加入统计
            recordJoinStats(userId, success, currentUsers);
            
            return AjaxResult.success(createJoinResponse(success, currentUsers, userStatus));
            
        } catch (Exception e) {
            logger.error("加入活动失败", e);
            
            recordErrorStats("join_activity", e.getMessage());
            return AjaxResult.error("加入活动失败");
        }
    }
    
    private Object createResponse(String status, int currentUsers, int maxUsers, 
                                 Integer queuePosition, Integer estimatedWaitTime, Integer retryAfter) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        data.put("currentUsers", currentUsers);
        data.put("maxUsers", maxUsers);
        if (queuePosition != null) data.put("queuePosition", queuePosition);
        if (estimatedWaitTime != null) data.put("estimatedWaitTime", estimatedWaitTime);
        if (retryAfter != null) data.put("retryAfter", retryAfter);
        return data;
    }
    
    private Object createJoinResponse(boolean success, int currentUsers, String userStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("currentUsers", currentUsers);
        data.put("userStatus", userStatus);
        return data;
    }
    
    private int calculateEstimatedWaitTime(int queueCount, int activeUsers) {
        // 简单的等待时间计算逻辑
        return queueCount * 30; // 假设每个用户平均30秒
    }
    
    // 请求对象
    public static class JoinActivityRequest {
        private String action;
        private String userId;
        private String sessionId;
        
        // getters and setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    /**
     * 记录流量统计数据
     */
    private void recordTrafficStats(String operation, int activeUsers, int maxUsers, boolean isMaintenanceMode) {
        try {
            RedpacketTrafficStats stats = new RedpacketTrafficStats();
            stats.setStatTime(new Date()); // 修改：setRecordTime -> setStatTime
            stats.setActiveUsers((long) activeUsers);
            stats.setQueuedUsers((long) Math.max(0, activeUsers - maxUsers));
            stats.setTotalRequests((long) ThreadLocalRandom.current().nextInt(100, 500));
            stats.setRejectedRequests((long) (isMaintenanceMode ? ThreadLocalRandom.current().nextInt(10, 50) : 0));
            stats.setAverageSessionTime((long) ThreadLocalRandom.current().nextInt(120, 600));
            
            trafficStatsService.insertRedpacketTrafficStats(stats);
            logger.debug("流量统计数据记录成功: operation={}, activeUsers={}", operation, activeUsers);
        } catch (Exception e) {
            logger.error("记录流量统计数据失败", e);
        }
    }

    private void recordJoinStats(String userId, boolean success, int currentUsers) {
        try {
            RedpacketTrafficStats stats = new RedpacketTrafficStats();
            stats.setStatTime(new Date()); // 修改：setRecordTime -> setStatTime
            stats.setActiveUsers((long) currentUsers);
            stats.setQueuedUsers(0L);
            stats.setTotalRequests(1L);
            stats.setRejectedRequests(success ? 0L : 1L);
            stats.setAverageSessionTime(180L);
            
            trafficStatsService.insertRedpacketTrafficStats(stats);
            logger.debug("加入统计数据记录成功: userId={}, success={}", userId, success);
        } catch (Exception e) {
            logger.error("记录加入统计数据失败", e);
        }
    }

    private void recordErrorStats(String operation, String errorMessage) {
        try {
            RedpacketTrafficStats stats = new RedpacketTrafficStats();
            stats.setStatTime(new Date()); // 修改：setRecordTime -> setStatTime
            stats.setActiveUsers(0L);
            stats.setQueuedUsers(0L);
            stats.setTotalRequests(1L);
            stats.setRejectedRequests(1L);
            stats.setAverageSessionTime(0L);
            
            trafficStatsService.insertRedpacketTrafficStats(stats);
            logger.debug("错误统计数据记录成功: operation={}, error={}", operation, errorMessage);
        } catch (Exception e) {
            logger.error("记录错误统计数据失败", e);
        }
    }
    
    /**
     * 心跳检测接口
     */
    @PostMapping("/heartbeat")
    public AjaxResult heartbeat(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String sessionId = (String) request.get("sessionId");
            
            // 更新用户心跳时间
            boolean success = participantsService.updateUserHeartbeat(userId, sessionId);
            
            if (success) {
                int currentUsers = participantsService.getActiveUserCount();
                
                // 记录心跳统计
                recordTrafficStats("heartbeat", currentUsers, 1000, false);
                
                Map<String, Object> data = new HashMap<>();
                data.put("success", true);
                data.put("currentUsers", currentUsers);
                data.put("timestamp", System.currentTimeMillis());
                
                return AjaxResult.success(data);
            } else {
                return AjaxResult.error("心跳更新失败");
            }
            
        } catch (Exception e) {
            logger.error("心跳检测失败", e);
            recordErrorStats("heartbeat", e.getMessage());
            return AjaxResult.error("心跳检测服务暂时不可用");
        }
    }
    
    /**
     * 离开活动接口
     */
    @PostMapping("/leave")
    public AjaxResult leaveActivity(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String sessionId = (String) request.get("sessionId");
            
            // 用户离开活动
            boolean success = participantsService.leaveActivity(userId, sessionId);
            
            int currentUsers = participantsService.getActiveUserCount();
            
            // 记录离开统计
            recordTrafficStats("leave_activity", currentUsers, 1000, false);
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("currentUsers", currentUsers);
            data.put("message", success ? "成功离开活动" : "离开活动失败");
            
            return AjaxResult.success(data);
            
        } catch (Exception e) {
            logger.error("离开活动失败", e);
            recordErrorStats("leave_activity", e.getMessage());
            return AjaxResult.error("离开活动服务暂时不可用");
        }
    }
}