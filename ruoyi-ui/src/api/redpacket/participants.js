import request from '@/utils/request'

// 查询活动参与者记录列表
export function listParticipants(query) {
  return request({
    url: '/redpacket/participants/list',
    method: 'get',
    params: query
  })
}

// 查询活动参与者记录详细
export function getParticipants(id) {
  return request({
    url: '/redpacket/participants/' + id,
    method: 'get'
  })
}

// 修改活动参与者记录
export function updateParticipants(data) {
  return request({
    url: '/redpacket/participants',
    method: 'put',
    data: data
  })
}

// 导出活动参与者记录
export function exportParticipants(query) {
  return request({
    url: '/redpacket/participants/export',
    method: 'get',
    params: query
  })
}