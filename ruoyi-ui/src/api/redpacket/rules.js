import request from '@/utils/request'

// 查询红包活动规则列表
export function listRules(query) {
  return request({
    url: '/redpacket/rules/list',
    method: 'get',
    params: query
  })
}

// 查询红包活动规则详细
export function getRules(id) {
  return request({
    url: '/redpacket/rules/' + id,
    method: 'get'
  })
}

// 新增红包活动规则
export function addRules(data) {
  return request({
    url: '/redpacket/rules',
    method: 'post',
    data: data
  })
}

// 修改红包活动规则
export function updateRules(data) {
  return request({
    url: '/redpacket/rules',
    method: 'put',
    data: data
  })
}

// 删除红包活动规则
export function delRules(id) {
  return request({
    url: '/redpacket/rules/' + id,
    method: 'delete'
  })
}
