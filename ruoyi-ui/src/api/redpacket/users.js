import request from '@/utils/request'

// 查询用户管理列表
export function listUsers(query) {
  return request({
    url: '/redpacket/users/list',
    method: 'get',
    params: query
  })
}

// 查询用户管理详细
export function getUsers(userId) {
  return request({
    url: '/redpacket/users/' + userId,
    method: 'get'
  })
}

// 新增用户管理
export function addUsers(data) {
  return request({
    url: '/redpacket/users',
    method: 'post',
    data: data
  })
}

// 修改用户管理
export function updateUsers(data) {
  return request({
    url: '/redpacket/users',
    method: 'put',
    data: data
  })
}

// 删除用户管理
export function delUsers(userId) {
  return request({
    url: '/redpacket/users/' + userId,
    method: 'delete'
  })
}
