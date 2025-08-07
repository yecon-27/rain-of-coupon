import request from "@/utils/request";

/**
 * 红包雨优惠券相关API接口封装
 * 用于前端红包雨页面的业务逻辑
 */

// 参与红包雨（领取优惠券）
export function drawLottery(data = {}) {
  return request({
    url: "/redpacket/lottery/draw",
    method: "post",
    data: {
      clickedCount: data.clickedCount || 1, // 用户点击的红包数量（1-100）
      sessionId: data.sessionId || null, // 游戏会话ID（可选）
      ...data,
    },
    headers: {
      repeatSubmit: false, // 防止重复提交
    },
  });
  // 后端返回数据结构（插入到redpacket_user_participation_log表）：
  // {
  //   code: 200,
  //   msg: "操作成功",
  //   data: {
  //     id: 123,                    // redpacket_user_participation_log.id
  //     userId: 1,                  // user_id
  //     participationTime: "2025-08-07 14:30:00", // participation_time
  //     ipAddress: "192.168.1.100", // ip_address
  //     isWin: 1,                   // is_win (0未中奖 1中奖)
  //     prizeId: 5,                 // prize_id (未中奖时为null)
  //     prizeName: "188元优惠券",    // prize_name (未中奖时为null，对应redpacket_prize表)
  //     isUsed: 0                   // is_used (0未使用 1已使用)
  //   }
  // }
}

// 获取用户参与记录（包含所有参与行为）
export function getUserRecords(query = {}) {
  return request({
    url: "/redpacket/lottery/records",
    method: "get",
    params: {
      pageNum: query.pageNum || 1,
      pageSize: query.pageSize || 10,
      isWin: query.isWin || null, // 筛选：null=全部，0=未中奖，1=中奖
      isUsed: query.isUsed || null, // 筛选：null=全部，0=未使用，1=已使用
      startDate: query.startDate || null, // 开始日期
      endDate: query.endDate || null, // 结束日期
      ...query,
    },
  });
  // 后端返回数据结构（基于redpacket_user_participation_log表）：
  // {
  //   code: 200,
  //   msg: "查询成功",
  //   rows: [
  //     {
  //       id: 123,                    // redpacket_user_participation_log.id
  //       userId: 1,                  // user_id
  //       participationTime: "2025-08-07 14:30:00", // participation_time
  //       ipAddress: "192.168.1.100", // ip_address
  //       isWin: 1,                   // is_win (0未中奖 1中奖)
  //       prizeId: 5,                 // prize_id (未中奖时为null)
  //       prizeName: "188元优惠券",    // prize_name (未中奖时为null)
  //       isUsed: 0                   // is_used (0未使用 1已使用)
  //     }
  //   ],
  //   total: 15                       // 总记录数（用于前端分页）
  // }
}

// 获取用户参与次数信息
export function getDrawCount() {
  return request({
    url: "/redpacket/lottery/count",
    method: "get",
  });
  // 后端返回数据结构：
  // {
  //   remainingCount: 2,        // 剩余次数 = max_draws_per_day - todayDrawCount
  //   todayDrawCount: 1,        // 今日已参与次数（从redpacket_user_participation_log统计）
  //   maxDrawsPerDay: 3,        // 每日最大次数（从redpacket_event_config.max_draws_per_day读取）
  //   totalDrawCount: 5,        // 历史总参与次数（从redpacket_user_participation_log统计）
  //   todayWinCount: 0,         // 今日中奖次数（从redpacket_user_participation_log统计is_win=1）
  //   totalWinCount: 1,         // 历史总中奖次数
  //   hasEverWon: true          // 是否曾经中过奖（核心业务逻辑：查询is_win=1的记录）
  // }
}

// 获取优惠券列表（用于红包雨显示）
export function getPrizes() {
  return request({
    url: "/redpacket/lottery/prizes",
    method: "get",
  });
}

// 获取用户状态（是否可以参与红包雨）
export function getUserStatus() {
  return request({
    url: "/redpacket/lottery/status",
    method: "get",
  });
  // 后端返回数据结构：
  // {
  //   canDraw: true,            // 是否可以参与（活动进行中 && 剩余次数>0 && !hasEverWon）
  //   hasEverWon: false,        // 是否曾经中过奖（查询redpacket_user_participation_log表is_win=1）
  //   isCrowded: false,         // 是否拥挤（当前在线用户数 >= max_users）
  //   remainingCount: 2,        // 今日剩余参与次数
  //   todayParticipations: [    // 今日参与记录
  //     {
  //       id: 123,
  //       participationTime: "2025-08-07 14:30:00",
  //       isWin: 0,
  //       clickedCount: 35,
  //       winProbability: 0.089
  //     }
  //   ],
  //   winRecords: [             // 中奖记录列表（历史所有中奖）
  //     {
  //       id: 456,
  //       participationTime: "2025-08-06 16:20:00",
  //       prizeName: "满500减50优惠券",
  //       isUsed: 0,
  //       clickedCount: 78
  //     }
  //   ]
  // }
}

// 获取活动配置（活动时间、规则等）
export function getActivityConfig() {
  return request({
    url: "/redpacket/lottery/config",
    method: "get",
  });
}

// 获取领取公告列表（滚动显示）
export function getWinningAnnouncements(query = {}) {
  return request({
    url: "/redpacket/lottery/announcements",
    method: "get",
    params: {
      pageNum: query.pageNum || 1,
      pageSize: query.pageSize || 20,
      ...query,
    },
  });
}

// 使用优惠券
export function claimPrize(logId) {
  return request({
    url: `/redpacket/lottery/claim/${logId}`,
    method: "post",
    headers: {
      repeatSubmit: false,
    },
  });
}

// 获取用户今日参与统计
export function getTodayStats() {
  return request({
    url: "/redpacket/lottery/today-stats",
    method: "get",
  });
}

// 检查活动是否进行中
export function checkActivityStatus() {
  return request({
    url: "/redpacket/lottery/activity-status",
    method: "get",
  });
}
