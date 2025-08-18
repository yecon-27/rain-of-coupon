import request from '@/utils/request'

// 查询流量统计列表
export function listTrafficStats(query) {
  return request({
    url: '/redpacket/trafficStats/list',
    method: 'get',
    params: query
  })
}

// 查询流量统计详细
export function getTrafficStats(id) {
  return request({
    url: '/redpacket/trafficStats/' + id,
    method: 'get'
  })
}

// 导出流量统计
export function exportTrafficStats(query) {
  return request({
    url: '/redpacket/trafficStats/export',
    method: 'get',
    params: query
  })
}

// 新增：获取实时统计数据
export function getRealTimeStats() {
  return request({
    url: '/redpacket/trafficStats/realtime',
    method: 'get'
  })
}

// 新增：获取峰值分析数据
export function getPeakAnalysis() {
  return request({
    url: '/redpacket/trafficStats/peak-analysis',
    method: 'get'
  })
}

// 新增：获取用户行为分析数据
export function getBehaviorAnalysis() {
  return request({
    url: '/redpacket/trafficStats/behavior-analysis',
    method: 'get'
  })
}

// 新增：获取系统配置
export function getSystemConfig() {
  return request({
    url: '/redpacket/trafficStats/system-config',
    method: 'get'
  })
}

// 新增：更新系统配置
export function updateSystemConfig(data) {
  return request({
    url: '/redpacket/trafficStats/system-config',
    method: 'put',
    data: data
  })
}

// 新增：获取用户会话列表
export function getUserSessions() {
  return request({
    url: '/redpacket/trafficStats/user-sessions',
    method: 'get'
  })
}

// 新增：清空队列
export function clearQueue() {
  return request({
    url: '/redpacket/trafficStats/clear-queue',
    method: 'post'
  })
}

// 新增：重置统计数据
export function resetStats() {
  return request({
    url: '/redpacket/trafficStats/reset',
    method: 'post'
  })
}

// 新增：踢出用户
export function removeUserSession(sessionId) {
  return request({
    url: '/redpacket/trafficStats/remove-session/' + sessionId,
    method: 'delete'
  })
}