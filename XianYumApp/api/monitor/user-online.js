import request from '@/utils/request'


// 查询在线用户列表
export function queryUserOnlinePage(query) {
  return request({
    url: '/xym-system/v1/online/getPage',
    method: 'get',
    params: query
  })
}

// 强退用户
export function forceLogout(data) {
  return request({
    url: '/xym-system/v1/online/delete',
    method: 'delete',
    data: data
  })
}
