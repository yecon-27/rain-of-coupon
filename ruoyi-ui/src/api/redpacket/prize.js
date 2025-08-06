import request from '@/utils/request'

// 查询奖品配置列表
export function listPrize(query) {
  return request({
    url: '/redpacket/prize/list',
    method: 'get',
    params: query
  })
}

// 查询奖品配置详细
export function getPrize(id) {
  return request({
    url: '/redpacket/prize/' + id,
    method: 'get'
  })
}

// 新增奖品配置
export function addPrize(data) {
  return request({
    url: '/redpacket/prize',
    method: 'post',
    data: data
  })
}

// 修改奖品配置
export function updatePrize(data) {
  return request({
    url: '/redpacket/prize',
    method: 'put',
    data: data
  })
}

// 删除奖品配置
export function delPrize(id) {
  return request({
    url: '/redpacket/prize/' + id,
    method: 'delete'
  })
}
