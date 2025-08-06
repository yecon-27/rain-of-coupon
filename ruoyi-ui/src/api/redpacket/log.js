import request from '@/utils/request'

// 查询用户抽奖记录列表
export function listLog(query) {
  return request({
    url: '/redpacket/log/list',
    method: 'get',
    params: query
  })
}

// 查询用户抽奖记录详细
export function getLog(id) {
  return request({
    url: '/redpacket/log/' + id,
    method: 'get'
  })
}

// 新增用户抽奖记录
export function addLog(data) {
  return request({
    url: '/redpacket/log',
    method: 'post',
    data: data
  })
}

// 修改用户抽奖记录
export function updateLog(data) {
  return request({
    url: '/redpacket/log',
    method: 'put',
    data: data
  })
}

// 删除用户抽奖记录
export function delLog(id) {
  return request({
    url: '/redpacket/log/' + id,
    method: 'delete'
  })
}
