import request from '@/utils/request'

// 查询红包流量控制配置列表
export function listTrafficConfig(query) {
  return request({
    url: '/redpacket/trafficConfig/list',
    method: 'get',
    params: query
  })
}

// 查询红包流量控制配置详细
export function getTrafficConfig(id) {
  return request({
    url: '/redpacket/trafficConfig/' + id,
    method: 'get'
  })
}

// 新增红包流量控制配置
export function addTrafficConfig(data) {
  return request({
    url: '/redpacket/trafficConfig',
    method: 'post',
    data: data
  })
}

// 修改红包流量控制配置
export function updateTrafficConfig(data) {
  return request({
    url: '/redpacket/trafficConfig',
    method: 'put',
    data: data
  })
}

// 删除红包流量控制配置
export function delTrafficConfig(id) {
  return request({
    url: '/redpacket/trafficConfig/' + id,
    method: 'delete'
  })
}
