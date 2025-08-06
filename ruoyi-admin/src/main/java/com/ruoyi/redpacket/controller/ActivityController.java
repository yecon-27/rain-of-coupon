package com.ruoyi.redpacket.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.redpacket.domain.RedpacketEventConfig;
import com.ruoyi.redpacket.service.IRedpacketEventConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动配置控制器
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController extends BaseController {
    
    @Autowired
    private IRedpacketEventConfigService eventConfigService;
    
    /**
     * 获取活动设置
     */
    @GetMapping("/config")
    public AjaxResult getConfig() {
        try {
            List<RedpacketEventConfig> configs = eventConfigService.selectRedpacketEventConfigList(new RedpacketEventConfig());
            
            if (configs.isEmpty()) {
                return error("活动配置不存在");
            }
            
            RedpacketEventConfig config = configs.get(0);
            Date now = new Date();
            
            Map<String, Object> data = new HashMap<>();
            data.put("startTime", config.getStartTime());
            data.put("endTime", config.getEndTime());
            data.put("maxUsers", config.getMaxUsers());
            data.put("maxDrawsPerDay", config.getMaxDrawsPerDay());
            data.put("isActive", now.after(config.getStartTime()) && now.before(config.getEndTime()));
            data.put("status", getActivityStatus(config, now));
            
            return success(data);
            
        } catch (Exception e) {
            logger.error("获取活动配置失败", e);
            return error("获取活动配置失败");
        }
    }
    
    /**
     * 获取活动状态
     */
    private String getActivityStatus(RedpacketEventConfig config, Date now) {
        if (now.before(config.getStartTime())) {
            return "未开始";
        } else if (now.after(config.getEndTime())) {
            return "已结束";
        } else {
            return "进行中";
        }
    }
}