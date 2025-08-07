import request from '@/utils/request'

// 查询用户参与记录（记录所有参与行为）列表
export function listParticipationLog(query) {
  return request({
    url: '/redpacket/participationLog/list',
    method: 'get',
    params: query
  })
}

// 查询用户参与记录（记录所有参与行为）详细
export function getParticipationLog(id) {
  return request({
    url: '/redpacket/participationLog/' + id,
    method: 'get'
  })
}

// 新增用户参与记录（记录所有参与行为）
export function addParticipationLog(data) {
  return request({
    url: '/redpacket/participationLog',
    method: 'post',
    data: data
  })
}

// 修改用户参与记录（记录所有参与行为）
export function updateParticipationLog(data) {
  return request({
    url: '/redpacket/participationLog',
    method: 'put',
    data: data
  })
}

// 删除用户参与记录（记录所有参与行为）
export function delParticipationLog(id) {
  return request({
    url: '/redpacket/participationLog/' + id,
    method: 'delete'
  })
}
