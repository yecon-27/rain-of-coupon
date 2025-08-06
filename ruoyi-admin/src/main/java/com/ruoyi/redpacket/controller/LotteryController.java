package com.ruoyi.redpacket.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.redpacket.domain.DrawResult;
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.service.ILotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽奖控制器
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/api/lottery")
public class LotteryController extends BaseController {
    
    @Autowired
    private ILotteryService lotteryService;
    
    /**
     * 执行抽奖
     */
    @PostMapping("/draw")
    @Log(title = "用户抽奖", businessType = BusinessType.INSERT)
    public AjaxResult draw(HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            
            // 获取用户IP
            String ipAddress = IpUtils.getIpAddr(request);
            
            // 检查抽奖资格
            if (!lotteryService.checkDrawEligibility(userId, ipAddress)) {
                return error("抽奖资格检查失败，请检查是否已中奖或超过每日限制");
            }
            
            // 执行抽奖
            DrawResult result = lotteryService.executeDraw(userId);
            
            // 保存抽奖记录
            lotteryService.saveDrawRecord(userId, result, ipAddress);
            
            // 返回抽奖结果
            Map<String, Object> data = new HashMap<>();
            data.put("isWin", result.isWin());
            data.put("prizeName", result.getPrizeName());
            data.put("prizeValue", result.getPrizeValue());
            data.put("message", result.getMessage());
            data.put("remainingCount", lotteryService.getRemainingDrawCount(userId));
            
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
            
            int remainingCount = lotteryService.getRemainingDrawCount(userId);
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
    @GetMapping("/status")
    public AjaxResult getStatus(HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            
            String ipAddress = IpUtils.getIpAddr(request);
            boolean eligible = lotteryService.checkDrawEligibility(userId, ipAddress);
            boolean activityValid = lotteryService.isActivityValid();
            boolean hasWon = lotteryService.hasWonToday(userId);
            int remainingCount = lotteryService.getRemainingDrawCount(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("eligible", eligible);
            data.put("activityValid", activityValid);
            data.put("hasWonToday", hasWon);
            data.put("remainingCount", remainingCount);
            data.put("canDraw", eligible && activityValid && !hasWon && remainingCount > 0);
            
            // 返回不能抽奖的原因
            if (!eligible || !activityValid || hasWon || remainingCount <= 0) {
                String reason = "";
                if (!activityValid) {
                    reason = "活动未开始或已结束";
                } else if (hasWon) {
                    reason = "今日已中奖";
                } else if (remainingCount <= 0) {
                    reason = "今日抽奖次数已用完";
                } else {
                    reason = "IP请求过于频繁";
                }
                data.put("reason", reason);
            }
            
            return success(data);
            
        } catch (Exception e) {
            logger.error("获取用户状态失败", e);
            return error("获取用户状态失败");
        }
    }
}