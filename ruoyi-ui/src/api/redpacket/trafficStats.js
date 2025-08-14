import request from '@/utils/request'

// 查询红包流量统计列表
export function listTrafficStats(query) {
  return request({
    url: '/redpacket/trafficStats/list',
    method: 'get',
    params: query
  })
}

// 查询红包流量统计详细
export function getTrafficStats(id) {
  return request({
    url: '/redpacket/trafficStats/' + id,
    method: 'get'
  })
}

// 新增红包流量统计
export function addTrafficStats(data) {
  return request({
    url: '/redpacket/trafficStats',
    method: 'post',
    data: data
  })
}

// 修改红包流量统计
export function updateTrafficStats(data) {
  return request({
    url: '/redpacket/trafficStats',
    method: 'put',
    data: data
  })
}

// 删除红包流量统计
export function delTrafficStats(id) {
  return request({
    url: '/redpacket/trafficStats/' + id,
    method: 'delete'
  })
}
