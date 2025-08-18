package com.ruoyi.redpacket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.redpacket.service.IRedpacketTrafficConfigService;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;
import java.util.Map;
import java.util.HashMap;

/**
 * 流量检测Controller
 * 
 * @author ruoyi
 */
@Anonymous
@RestController
@RequestMapping("/api/traffic")
public class TrafficController extends BaseController {
    
    @Autowired
    private IRedpacketTrafficConfigService trafficConfigService;
    
    @Autowired
    private IRedpacketActivityParticipantsService participantsService;
    
    /**
     * 检查流量状态
     */
    @GetMapping("/check")
    public AjaxResult checkTraffic() {
        try {
            // 1. 获取当前活跃用户数（使用现有方法）
            com.ruoyi.redpacket.domain.RedpacketActivityParticipants query = new com.ruoyi.redpacket.domain.RedpacketActivityParticipants();
            // 可以根据需要设置查询条件，比如只查询活跃状态的参与者
            int activeUsers = participantsService.selectRedpacketActivityParticipantsList(query).size();
            
            // 2. 获取配置的最大用户数（从数据库配置表中读取）
            int maxUsers = 1000; // 默认值
            try {
                String maxUsersStr = trafficConfigService.getConfigValueByKey("max_concurrent_users", "1000");
                maxUsers = Integer.parseInt(maxUsersStr);
            } catch (NumberFormatException e) {
                logger.warn("解析max_concurrent_users配置失败，使用默认值1000", e);
            }
            
            // 3. 判断是否维护模式（从数据库配置表中读取）
            boolean isMaintenanceMode = false;
            try {
                String maintenanceModeStr = trafficConfigService.getConfigValueByKey("maintenance_mode", "0");
                isMaintenanceMode = "1".equals(maintenanceModeStr);
            } catch (Exception e) {
                logger.warn("读取maintenance_mode配置失败，使用默认值false", e);
            }
            
            if (isMaintenanceMode) {
                return AjaxResult.success(createResponse("maintenance", 0, maxUsers, null, null, null));
            }
            
            // 4. 判断是否拥挤
            if (activeUsers >= maxUsers) {
                // 简化队列逻辑，暂时使用固定值
                int queueCount = Math.max(0, activeUsers - maxUsers);
                int estimatedWaitTime = calculateEstimatedWaitTime(queueCount, activeUsers);
                
                return AjaxResult.success(createResponse("crowded", activeUsers, maxUsers, 
                    queueCount + 1, estimatedWaitTime, 60));
            }
            
            return AjaxResult.success(createResponse("ok", activeUsers, maxUsers, null, null, null));
            
        } catch (Exception e) {
            logger.error("流量检测失败", e);
            return AjaxResult.error("流量检测服务暂时不可用");
        }
    }
    
    /**
     * 用户加入活动
     */
    @PostMapping("/join")
    public AjaxResult joinActivity(@RequestBody JoinActivityRequest request) {
        try {
            // 实现加入活动的逻辑
            boolean success = participantsService.joinActivity(request.getUserId(), request.getSessionId());
            
            int currentUsers = participantsService.getActiveUserCount();
            String userStatus = success ? "active" : "queued";
            
            return AjaxResult.success(createJoinResponse(success, currentUsers, userStatus));
            
        } catch (Exception e) {
            logger.error("加入活动失败", e);
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
}