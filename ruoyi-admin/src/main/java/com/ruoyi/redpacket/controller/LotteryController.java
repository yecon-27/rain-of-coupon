package com.ruoyi.redpacket.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;
import com.ruoyi.redpacket.service.ILotteryService;
import com.ruoyi.redpacket.mapper.RedpacketPrizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * 抽奖控制器
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
// 修改控制器映射以匹配前端
@RestController
@RequestMapping("/redpacket/lottery")
public class LotteryController extends BaseController {
    
    @Autowired
    private ILotteryService lotteryService;
    
    @Autowired
    private RedpacketPrizeMapper prizeMapper;
    
    /**
     * 执行抽奖
     */
    @PostMapping("/draw")
    @Log(title = "用户抽奖", businessType = BusinessType.INSERT)
    public AjaxResult draw(@RequestBody(required = false) Map<String, Integer> payload, HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            
            int clickedCount = (payload != null && payload.containsKey("clickedCount")) ? payload.get("clickedCount") : 1;
            String sessionId = request.getSession().getId();
            String ipAddress = IpUtils.getIpAddr(request);
            
            if (!lotteryService.checkDrawEligibility(userId, ipAddress)) {
                return error("抽奖资格检查失败，请检查是否已中奖或超过每日限制");
            }
            
            DrawResult result = lotteryService.draw(userId, ipAddress, clickedCount, sessionId);
            
            lotteryService.saveDrawRecord(userId, result, ipAddress, sessionId, clickedCount);
            
            Map<String, Object> data = new HashMap<>();
            data.put("isWin", result.isWin());
            data.put("prizeName", result.getPrizeName());
            data.put("prizeValue", result.getPrizeValue());
            data.put("message", result.getMessage());
            data.put("remainingCount", lotteryService.getTodayRemainingCount(userId));
            
            return success(data);
            
        } catch (Exception e) {
            logger.error("抽奖失败", e);
            return error("抽奖失败，请稍后重试");
        }
    }
    
    /**
     * 获取用户抽奖记录
     */
    @GetMapping("/records")
    public AjaxResult getRecords() {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            
            List<Object> records = lotteryService.getUserDrawRecords(userId);
            return success(records);
            
        } catch (Exception e) {
            logger.error("获取抽奖记录失败", e);
            return error("获取抽奖记录失败");
        }
    }
    
    /**
     * 检查剩余抽奖次数
     */
    @GetMapping("/drawCount")
    public AjaxResult getDrawCount() {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            
            int remainingCount = lotteryService.getTodayRemainingCount(userId);
            boolean hasWon = lotteryService.hasWonToday(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("remainingCount", remainingCount);
            data.put("hasWonToday", hasWon);
            data.put("canDraw", remainingCount > 0 && !hasWon);
            
            return success(data);
            
        } catch (Exception e) {
            logger.error("获取抽奖次数失败", e);
            return error("获取抽奖次数失败");
        }
    }
    
    /**
     * 获取可用奖品
     */
    @GetMapping("/prizes")
    public AjaxResult getPrizes() {
        try {
            List<RedpacketPrize> prizes = lotteryService.getAvailablePrizes();
            return success(prizes);
            
        } catch (Exception e) {
            logger.error("获取奖品列表失败", e);
            return error("获取奖品列表失败");
        }
    }
    
    /**
     * 检查用户抽奖资格
     */
    /**
     * 获取用户状态（查询 redpacket_user_participation_log）
     */
    @GetMapping("/status")
    @Anonymous // 允许未登录访问
    public AjaxResult getStatus(HttpServletRequest request, @RequestParam(required = false) String sessionId) {
        try {
            Long userId = SecurityUtils.getUserId();
            String ipAddress = IpUtils.getIpAddr(request);
            
            logger.info("🔍 [用户状态查询] 当前用户ID: {}", userId);
            
            // 默认值处理，避免空指针
            int todayRemainingCount = 0;
            boolean hasEverWon = false;
            List<RedpacketUserParticipationLog> logs = Collections.emptyList();

            if (userId != null) {
                // 如果用户已登录，获取精确的参与和剩余次数
                todayRemainingCount = lotteryService.getTodayRemainingCount(userId);
                hasEverWon = lotteryService.hasEverWon(userId);
                // 获取用户所有参与记录
                logs = lotteryService.getUserParticipationLogs(userId);
            }

            // 获取当前活动轮次信息及流量拥挤状态
            Map<String, Object> roundInfo = lotteryService.getCurrentActiveRound();
            String roundName = roundInfo.containsKey("id") ? "round" + roundInfo.get("id") : "";
            boolean isCrowded = lotteryService.isCrowded(roundName);

            // 检查同一会话是否已参与，仅对已登录用户有效
            boolean hasParticipatedInSession = (userId != null && sessionId != null) && lotteryService.hasParticipatedInSession(userId, sessionId);

            // 最终抽奖资格判断：服务层检查 + 会话检查
            boolean canDraw = lotteryService.checkDrawEligibility(userId, ipAddress) && !hasParticipatedInSession;
            
            // 按日期和中奖状态过滤记录
            List<Map<String, Object>> todayParticipations = logs.stream()
                .filter(log -> log.getParticipationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now()))
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("participationTime", log.getParticipationTime());
                    map.put("isWin", log.getIsWin());
                    map.put("clickedCount", log.getClickedCount());
                    return map;
                })
                .collect(Collectors.toList());
            
            List<Map<String, Object>> winRecords = logs.stream()
                .filter(log -> log.getIsWin() == 1)
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("participationTime", log.getParticipationTime());
                    map.put("prizeName", log.getPrizeName());
                    return map;
                })
                .collect(Collectors.toList());

            // 组装响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("canDraw", canDraw);
            data.put("hasEverWon", hasEverWon);
            data.put("isCrowded", isCrowded);
            data.put("remainingCount", todayRemainingCount);
            data.put("todayParticipations", todayParticipations);
            data.put("winRecords", winRecords);
            data.put("todayParticipationsCount", todayParticipations.size()); // 根据过滤后的列表计算

            return AjaxResult.success("获取用户状态成功", data);

        } catch (Exception e) {
            logger.error("获取用户状态失败", e);
            return AjaxResult.error("获取用户状态失败");
        }
    }
    
    /**
     * 检查奖品库存
     */
    @GetMapping("/stock")
    public AjaxResult checkPrizeStock() {
        try {
            // 获取所有奖品信息（不过滤库存）
            RedpacketPrize queryPrize = new RedpacketPrize();
            List<RedpacketPrize> allPrizes = prizeMapper.selectRedpacketPrizeList(queryPrize);
            
            // 检查是否还有库存
            boolean hasStock = allPrizes.stream()
                    .anyMatch(prize -> prize.getRemainingCount() > 0);
            
            List<Map<String, Object>> prizeList = allPrizes.stream()
                    .map(prize -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", prize.getId());
                        map.put("prizeName", prize.getPrizeName());
                        map.put("totalCount", prize.getTotalCount());
                        map.put("remainingCount", prize.getRemainingCount());
                        return map;
                    }).collect(Collectors.toList());
            
            Map<String, Object> data = new HashMap<>();
            data.put("hasStock", hasStock);
            data.put("prizes", prizeList);
            
            return AjaxResult.success("操作成功", data);
        } catch (Exception e) {
            return AjaxResult.error("检查奖品库存失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前活跃轮次信息
     */
    @GetMapping("/current-round")
    public AjaxResult getCurrentActiveRound() {
        try {
            Map<String, Object> roundInfo = lotteryService.getCurrentActiveRound();
            return AjaxResult.success("获取轮次信息成功", roundInfo);
        } catch (Exception e) {
            logger.error("获取当前轮次失败", e);
            return AjaxResult.error("获取当前轮次失败: " + e.getMessage());
        }
    }
}