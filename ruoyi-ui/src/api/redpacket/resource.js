import request from '@/utils/request'

// 查询图片资源列表
export function listResource(query) {
  return request({
    url: '/redpacket/resource/list',
    method: 'get',
    params: query
  })
}

// 查询图片资源详细
export function getResource(id) {
  return request({
    url: '/redpacket/resource/' + id,
    method: 'get'
  })
}

// 新增图片资源
export function addResource(data) {
  return request({
    url: '/redpacket/resource',
    method: 'post',
    data: data
  })
}

// 修改图片资源
export function updateResource(data) {
  return request({
    url: '/redpacket/resource',
    method: 'put',
    data: data
  })
}

// 删除图片资源
export function delResource(id) {
  return request({
    url: '/redpacket/resource/' + id,
    method: 'delete'
  })
}
