import request from '@/utils/request'

// 查询在线用户列表
export function list(data) {
  return request({
    url: '/online/list',
    method: 'post',
    data: data
  })
}

// 强退用户
export function forceLogout(data) {
  return request({
    url: '/online/delete',
    method: 'post',
    data: data
  })
}
