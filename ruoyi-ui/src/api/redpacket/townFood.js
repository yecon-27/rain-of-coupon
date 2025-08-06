import request from '@/utils/request'

// 查询"一镇一品"特色菜列表
export function listTownFood(query) {
  return request({
    url: '/redpacket/townFood/list',
    method: 'get',
    params: query
  })
}

// 查询"一镇一品"特色菜详细
export function getTownFood(id) {
  return request({
    url: '/redpacket/townFood/' + id,
    method: 'get'
  })
}

// 新增"一镇一品"特色菜
export function addTownFood(data) {
  return request({
    url: '/redpacket/townFood',
    method: 'post',
    data: data
  })
}

// 修改"一镇一品"特色菜
export function updateTownFood(data) {
  return request({
    url: '/redpacket/townFood',
    method: 'put',
    data: data
  })
}

// 删除"一镇一品"特色菜
export function delTownFood(id) {
  return request({
    url: '/redpacket/townFood/' + id,
    method: 'delete'
  })
}
