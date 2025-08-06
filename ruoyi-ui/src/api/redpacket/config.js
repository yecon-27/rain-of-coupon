import request from '@/utils/request'

// 查询活动配置列表
export function listConfig(query) {
  return request({
    url: '/redpacket/config/list',
    method: 'get',
    params: query
  })
}

// 查询活动配置详细
export function getConfig(id) {
  return request({
    url: '/redpacket/config/' + id,
    method: 'get'
  })
}

// 新增活动配置
export function addConfig(data) {
  return request({
    url: '/redpacket/config',
    method: 'post',
    data: data
  })
}

// 修改活动配置
export function updateConfig(data) {
  return request({
    url: '/redpacket/config',
    method: 'put',
    data: data
  })
}

// 删除活动配置
export function delConfig(id) {
  return request({
    url: '/redpacket/config/' + id,
    method: 'delete'
  })
}
