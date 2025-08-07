import request from '@/utils/request'

/**
 * 抽奖相关API接口封装
 * 用于前端抽奖页面的业务逻辑
 */

// 执行抽奖
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

// 获取用户抽奖记录
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

// 获取用户剩余抽奖次数
export function getDrawCount() {
  return request({
    url: '/redpacket/lottery/count',
    method: 'get'
  })
}

// 获取奖品列表（用于转盘显示）
export function getPrizes() {
  return request({
    url: '/redpacket/lottery/prizes',
    method: 'get'
  })
}

// 获取用户状态（是否可以参与抽奖）
export function getUserStatus() {
  return request({
    url: '/redpacket/lottery/status',
    method: 'get'
  })
}

// 获取活动配置（活动时间、规则等）
export function getActivityConfig() {
  return request({
    url: '/redpacket/lottery/config',
    method: 'get'
  })
}

// 获取中奖公告列表（滚动显示）
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

// 领取奖品
export function claimPrize(logId) {
  return request({
    url: `/redpacket/lottery/claim/${logId}`,
    method: 'post',
    headers: {
      repeatSubmit: false
    }
  })
}

// 获取用户今日抽奖统计
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