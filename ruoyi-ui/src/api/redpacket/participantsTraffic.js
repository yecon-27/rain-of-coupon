import request from '@/utils/request'

// 查询红包活动参与者记录列表
export function listParticipantsTraffic(query) {
  return request({
    url: '/redpacket/participantsTraffic/list',
    method: 'get',
    params: query
  })
}

// 查询红包活动参与者记录详细
export function getParticipantsTraffic(id) {
  return request({
    url: '/redpacket/participantsTraffic/' + id,
    method: 'get'
  })
}

// 新增红包活动参与者记录
export function addParticipantsTraffic(data) {
  return request({
    url: '/redpacket/participantsTraffic',
    method: 'post',
    data: data
  })
}

// 修改红包活动参与者记录
export function updateParticipantsTraffic(data) {
  return request({
    url: '/redpacket/participantsTraffic',
    method: 'put',
    data: data
  })
}

// 删除红包活动参与者记录
export function delParticipantsTraffic(id) {
  return request({
    url: '/redpacket/participantsTraffic/' + id,
    method: 'delete'
  })
}
