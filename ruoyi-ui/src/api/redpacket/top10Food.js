import request from '@/utils/request'

// 查询TOP10网络人气特色美食列表
export function listTop10Food(query) {
  return request({
    url: '/redpacket/top10Food/list',
    method: 'get',
    params: query
  })
}

// 查询TOP10网络人气特色美食详细
export function getTop10Food(id) {
  return request({
    url: '/redpacket/top10Food/' + id,
    method: 'get'
  })
}

// 新增TOP10网络人气特色美食
export function addTop10Food(data) {
  return request({
    url: '/redpacket/top10Food',
    method: 'post',
    data: data
  })
}

// 修改TOP10网络人气特色美食
export function updateTop10Food(data) {
  return request({
    url: '/redpacket/top10Food',
    method: 'put',
    data: data
  })
}

// 删除TOP10网络人气特色美食
export function delTop10Food(id) {
  return request({
    url: '/redpacket/top10Food/' + id,
    method: 'delete'
  })
}
