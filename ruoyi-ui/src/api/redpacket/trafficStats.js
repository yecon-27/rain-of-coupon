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