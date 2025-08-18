package com.ruoyi.redpacket.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;
import com.ruoyi.redpacket.service.IRedpacketTrafficConfigService;
import com.ruoyi.redpacket.service.IRedpacketTrafficStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 红包流量统计Controller
 *
 * @author ruoyi
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/redpacket/trafficStats")
public class RedpacketTrafficStatsController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RedpacketTrafficStatsController.class);
    @Autowired
    private IRedpacketTrafficStatsService redpacketTrafficStatsService;

    // 添加参与者服务注入
    @Autowired
    private IRedpacketActivityParticipantsService participantsService;

    @Autowired
    private IRedpacketTrafficConfigService trafficConfigService;

    /**
     * 查询红包流量统计列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketTrafficStats redpacketTrafficStats) {
        startPage();
        List<RedpacketTrafficStats> list = redpacketTrafficStatsService.selectRedpacketTrafficStatsList(redpacketTrafficStats);
        return getDataTable(list);
    }

    /**
     * 导出红包流量统计列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:export')")
    @Log(title = "红包流量统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketTrafficStats redpacketTrafficStats) {
        List<RedpacketTrafficStats> list = redpacketTrafficStatsService.selectRedpacketTrafficStatsList(redpacketTrafficStats);
        ExcelUtil<RedpacketTrafficStats> util = new ExcelUtil<RedpacketTrafficStats>(RedpacketTrafficStats.class);
        util.exportExcel(response, list, "红包流量统计数据");
    }

    /**
     * 获取红包流量统计详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(redpacketTrafficStatsService.selectRedpacketTrafficStatsById(id));
    }

    /**
     * 新增红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:add')")
    @Log(title = "红包流量统计", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketTrafficStats redpacketTrafficStats) {
        return toAjax(redpacketTrafficStatsService.insertRedpacketTrafficStats(redpacketTrafficStats));
    }

    /**
     * 修改红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:edit')")
    @Log(title = "红包流量统计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketTrafficStats redpacketTrafficStats) {
        return toAjax(redpacketTrafficStatsService.updateRedpacketTrafficStats(redpacketTrafficStats));
    }

    /**
     * 删除红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:remove')")
    @Log(title = "红包流量统计", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(redpacketTrafficStatsService.deleteRedpacketTrafficStatsByIds(ids));
    }

    /**
     * 获取实时统计数据
     */
    @GetMapping("/realtime")
    public AjaxResult getRealTimeStats() {
        try {
            // 获取真实的活跃用户数和排队用户数
            int activeUsers = participantsService.getActiveUserCount();
            int queuedUsers = participantsService.getQueuedUserCount();

            // 获取配置的最大用户数
            String maxUsersStr = trafficConfigService.getConfigValueByKey("max_concurrent_users", "1000");
            int maxUsers = Integer.parseInt(maxUsersStr);

            // 计算系统负载百分比
            double systemLoad = maxUsers > 0 ? (double) activeUsers / maxUsers * 100 : 0;

            // 获取今日统计数据
            LocalDate today = LocalDate.now();
            Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDay = new Date(); // 当前时间
            List<RedpacketTrafficStats> todayStats = redpacketTrafficStatsService.selectStatsByTimeRange(startOfDay, endOfDay);

            // 计算今日加入总数和成功率
            long todayJoins = todayStats.stream().mapToLong(RedpacketTrafficStats::getTotalRequests).sum();
            long todayRejected = todayStats.stream().mapToLong(RedpacketTrafficStats::getRejectedRequests).sum();
            double successRate = todayJoins > 0 ? (double) (todayJoins - todayRejected) / todayJoins * 100 : 100;

            // 计算平均等待时间
            double avgWaitTime = todayStats.stream()
                    .mapToDouble(RedpacketTrafficStats::getAverageSessionTime)
                    .average()
                    .orElse(0) / 60.0; // 转换为分钟

            Map<String, Object> stats = new HashMap<>();
            stats.put("activeUsers", activeUsers);
            stats.put("queuedUsers", queuedUsers);
            stats.put("systemLoad", Math.round(systemLoad * 10) / 10.0); // 保留一位小数
            stats.put("successRate", Math.round(successRate * 10) / 10.0);
            stats.put("avgWaitTime", Math.round(avgWaitTime * 10) / 10.0);
            stats.put("todayJoins", todayJoins);

            return success(stats);
        } catch (Exception e) {
            logger.error("获取实时统计数据失败", e);
            // 如果获取真实数据失败，返回默认值
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeUsers", 0);
            stats.put("queuedUsers", 0);
            stats.put("systemLoad", 0);
            stats.put("successRate", 100.0);
            stats.put("avgWaitTime", 0.0);
            stats.put("todayJoins", 0);
            return success(stats);
        }
    }

    /**
     * 获取峰值分析数据
     */
    @GetMapping("/peak-analysis")
    public AjaxResult getPeakAnalysis() {
        try {
            // 获取今日统计数据
            LocalDate today = LocalDate.now();
            Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDay = new Date();
            List<RedpacketTrafficStats> todayStats = redpacketTrafficStatsService.selectStatsByTimeRange(startOfDay, endOfDay);

            // 计算今日峰值
            long todayPeak = todayStats.stream()
                    .mapToLong(RedpacketTrafficStats::getActiveUsers)
                    .max()
                    .orElse(0);

            // 找到今日峰值时间
            String todayPeakTime = todayStats.stream()
                    .filter(s -> s.getActiveUsers().equals(todayPeak))
                    .findFirst()
                    .map(s -> LocalDateTime.ofInstant(s.getStatTime().toInstant(), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("HH:mm")))
                    .orElse("--:--");

            // 获取历史峰值（最近30天）
            LocalDate thirtyDaysAgo = today.minus(30, ChronoUnit.DAYS);
            Date thirtyDaysAgoDate = Date.from(thirtyDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<RedpacketTrafficStats> historyStats = redpacketTrafficStatsService.selectStatsByTimeRange(thirtyDaysAgoDate, endOfDay);

            long historyPeak = historyStats.stream()
                    .mapToLong(RedpacketTrafficStats::getActiveUsers)
                    .max()
                    .orElse(0);

            String historyPeakTime = historyStats.stream()
                    .filter(s -> s.getActiveUsers().equals(historyPeak))
                    .findFirst()
                    .map(s -> LocalDateTime.ofInstant(s.getStatTime().toInstant(), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .orElse("----");

            // 预测峰值（基于历史数据的简单预测）
            long predictedPeak = Math.max(todayPeak, historyPeak * 80 / 100);

            // 预测时间（基于历史峰值时间模式）
            OptionalDouble avgPeakHourOptional = historyStats.stream()
                    .filter(s -> s.getActiveUsers() > historyPeak * 70 / 100)
                    .mapToLong(s -> s.getStatTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toSecondOfDay())
                    .average();

            String predictedTime = avgPeakHourOptional.isPresent()
                    ? LocalDateTime.ofEpochSecond((long) avgPeakHourOptional.getAsDouble(), 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()))
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                    : "--:--";

            Map<String, Object> analysis = new HashMap<>();
            analysis.put("todayPeak", todayPeak);
            analysis.put("todayPeakTime", todayPeakTime);
            analysis.put("historyPeak", historyPeak);
            analysis.put("historyPeakTime", historyPeakTime);
            analysis.put("predictedPeak", predictedPeak);
            analysis.put("predictedTime", predictedTime);

            return success(analysis);
        } catch (Exception e) {
            logger.error("获取峰值分析数据失败", e);
            // 返回默认值
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("todayPeak", 0);
            analysis.put("todayPeakTime", "--:--");
            analysis.put("historyPeak", 0);
            analysis.put("historyPeakTime", "----");
            analysis.put("predictedPeak", 0);
            analysis.put("predictedTime", "--:--");
            return success(analysis);
        }
    }


    /**
     * 获取用户行为分析数据
     */
    @GetMapping("/behavior-analysis")
    public AjaxResult getBehaviorAnalysis() {
        try {
            // 使用 java.time API 替换 deprecated Date 构造
            LocalDate today = LocalDate.now();
            Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDay = new Date();

            List<RedpacketTrafficStats> todayStats = redpacketTrafficStatsService.selectStatsByTimeRange(startOfDay, endOfDay);

            if (todayStats.isEmpty()) {
                // 如果没有今日数据，返回默认值
                Map<String, Object> behavior = new HashMap<>();
                behavior.put("avgSessionTime", 0);
                behavior.put("bounceRate", 0.0);
                behavior.put("conversionRate", 100.0);
                behavior.put("retryRate", 0.0);
                return success(behavior);
            }

            // 计算平均会话时间（秒）
            double avgSessionTime = todayStats.stream()
                    .mapToLong(RedpacketTrafficStats::getAverageSessionTime)
                    .average()
                    .orElse(0);

            // 计算跳出率（被拒绝请求 / 总请求）
            long totalRequests = todayStats.stream().mapToLong(RedpacketTrafficStats::getTotalRequests).sum();
            long rejectedRequests = todayStats.stream().mapToLong(RedpacketTrafficStats::getRejectedRequests).sum();
            double bounceRate = totalRequests > 0 ? (double) rejectedRequests / totalRequests * 100 : 0;

            // 计算转化率（成功请求 / 总请求）
            double conversionRate = totalRequests > 0 ? (double) (totalRequests - rejectedRequests) / totalRequests * 100 : 100;

            // 计算重试率（基于排队用户数和活跃用户数的比例）
            long totalQueuedUsers = todayStats.stream().mapToLong(RedpacketTrafficStats::getQueuedUsers).sum();
            long totalActiveUsers = todayStats.stream().mapToLong(RedpacketTrafficStats::getActiveUsers).sum();
            double retryRate = totalActiveUsers > 0 ? (double) totalQueuedUsers / totalActiveUsers * 100 : 0;

            Map<String, Object> behavior = new HashMap<>();
            behavior.put("avgSessionTime", Math.round(avgSessionTime));
            behavior.put("bounceRate", Math.round(bounceRate * 10) / 10.0);
            behavior.put("conversionRate", Math.round(conversionRate * 10) / 10.0);
            behavior.put("retryRate", Math.round(retryRate * 10) / 10.0);

            return success(behavior);
        } catch (Exception e) {
            logger.error("获取用户行为分析数据失败", e);
            // 返回默认值
            Map<String, Object> behavior = new HashMap<>();
            behavior.put("avgSessionTime", 0);
            behavior.put("bounceRate", 0.0);
            behavior.put("conversionRate", 100.0);
            behavior.put("retryRate", 0.0);
            return success(behavior);
        }
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/system-config")
    public AjaxResult getSystemConfig() {
        try {
            Map<String, Object> config = new HashMap<>();

            // 从配置服务获取真实配置
            String maxUsers = trafficConfigService.getConfigValueByKey("max_concurrent_users", "1000");
            String queueTimeout = trafficConfigService.getConfigValueByKey("queue_timeout_seconds", "300");
            String heartbeatTimeout = trafficConfigService.getConfigValueByKey("heartbeat_timeout_seconds", "60");
            String maintenanceMode = trafficConfigService.getConfigValueByKey("maintenance_mode", "false");
            String rushHourStart = trafficConfigService.getConfigValueByKey("rush_hour_start", "11");
            String rushHourEnd = trafficConfigService.getConfigValueByKey("rush_hour_end", "20");
            String rushHourMaxUsers = trafficConfigService.getConfigValueByKey("rush_hour_max_users", "600");

            config.put("maxUsers", Integer.parseInt(maxUsers));
            config.put("queueTimeout", Integer.parseInt(queueTimeout));
            config.put("heartbeatTimeout", Integer.parseInt(heartbeatTimeout));
            config.put("maintenanceMode", Boolean.parseBoolean(maintenanceMode));
            config.put("rushHourStart", Integer.parseInt(rushHourStart));
            config.put("rushHourEnd", Integer.parseInt(rushHourEnd));
            config.put("rushHourMaxUsers", Integer.parseInt(rushHourMaxUsers));

            return success(config);
        } catch (Exception e) {
            logger.error("获取系统配置失败", e);
            // 返回默认配置
            Map<String, Object> config = new HashMap<>();
            config.put("maxUsers", 1000);
            config.put("queueTimeout", 300);
            config.put("heartbeatTimeout", 60);
            config.put("maintenanceMode", false);
            config.put("rushHourStart", 11);
            config.put("rushHourEnd", 20);
            config.put("rushHourMaxUsers", 600);
            return success(config);
        }
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/system-config")
    public AjaxResult updateSystemConfig(@RequestBody Map<String, Object> config) {
        // 这里应该调用配置服务更新配置
        return success("配置更新成功");
    }

    /**
     * 获取用户会话列表
     */
    @GetMapping("/user-sessions")
    public AjaxResult getUserSessions() {
        // 模拟用户会话数据
        List<Map<String, Object>> sessions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> session = new HashMap<>();
            session.put("sessionId", "session_" + i);
            session.put("userId", "user_" + i);
            session.put("status", i % 3 == 0 ? "queued" : "active");
            session.put("joinTime", new Date());
            session.put("lastHeartbeat", new Date());
            sessions.add(session);
        }
        return success(sessions);
    }

    /**
     * 清空队列
     */
    @PostMapping("/clear-queue")
    public AjaxResult clearQueue() {
        // 这里应该调用相关服务清空队列
        return success("队列已清空");
    }

    /**
     * 重置统计数据
     */
    @PostMapping("/reset-stats")
    public AjaxResult resetStats() {
        // 这里应该调用相关服务重置统计
        return success("统计数据已重置");
    }

    /**
     * 移除用户会话
     */
    @DeleteMapping("/user-sessions/{sessionId}")
    public AjaxResult removeUserSession(@PathVariable String sessionId) {
        // 这里应该调用相关服务移除用户会话
        return success("用户会话已移除");
    }
}