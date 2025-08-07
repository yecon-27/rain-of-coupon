import request from '@/utils/request'

/**
 * 红包雨优惠券相关API接口封装
 * 用于前端红包雨页面的业务逻辑
 */

// 参与红包雨（领取优惠券）
export function drawLottery(data = {}) {
  return request({
    url: '/redpacket/lottery/draw',
    method: 'post',
    data: data,
    headers: {
      repeatSubmit: false // 防止重复提交
    }
  })
}

// 获取用户优惠券记录
export function getUserRecords(query = {}) {
  return request({
    url: '/redpacket/lottery/records',
    method: 'get',
    params: {
      pageNum: query.pageNum || 1,
      pageSize: query.pageSize || 10,
      ...query
    }
  })
}

// 获取用户参与次数信息
export function getDrawCount() {
  return request({
    url: '/redpacket/lottery/count',
    method: 'get'
  })
  // 后端返回数据结构：
  // {
  //   remainingCount: 2,        // 剩余次数 = max_draws_per_day - todayDrawCount
  //   todayDrawCount: 1,        // 今日已参与次数（从redpacket_user_prize_log统计）
  //   maxDrawsPerDay: 3,        // 每日最大次数（从redpacket_event_config读取）
  //   totalDrawCount: 5         // 历史总参与次数
  // }
}

// 获取优惠券列表（用于红包雨显示）
export function getPrizes() {
  return request({
    url: '/redpacket/lottery/prizes',
    method: 'get'
  })
}

// 获取用户状态（是否可以参与红包雨）
export function getUserStatus() {
  return request({
    url: '/redpacket/lottery/status',
    method: 'get'
  })
  // 后端返回数据结构：
  // {
  //   canDraw: true,            // 是否可以参与（活动进行中 && 剩余次数>0）
  //   hasWon: false,            // 是否已中奖（查询redpacket_user_prize_log表is_win=1）
  //   isCrowded: false,         // 是否拥挤（当前在线用户数 >= max_users）
  //   winRecords: [...]         // 中奖记录列表
  // }
}

// 获取活动配置（活动时间、规则等）
export function getActivityConfig() {
  return request({
    url: '/redpacket/lottery/config',
    method: 'get'
  })
}

// 获取领取公告列表（滚动显示）
export function getWinningAnnouncements(query = {}) {
  return request({
    url: '/redpacket/lottery/announcements',
    method: 'get',
    params: {
      pageNum: query.pageNum || 1,
      pageSize: query.pageSize || 20,
      ...query
    }
  })
}

// 使用优惠券
export function claimPrize(logId) {
  return request({
    url: `/redpacket/lottery/claim/${logId}`,
    method: 'post',
    headers: {
      repeatSubmit: false
    }
  })
}

// 获取用户今日参与统计
export function getTodayStats() {
  return request({
    url: '/redpacket/lottery/today-stats',
    method: 'get'
  })
}

// 检查活动是否进行中
export function checkActivityStatus() {
  return request({
    url: '/redpacket/lottery/activity-status',
    method: 'get'
  })
}