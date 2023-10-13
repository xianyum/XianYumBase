import request from '@/utils/request'

// 获取服务信息
export function getServer() {
  return request({
    url: '/xianyum-system/v1/server/get',
    method: 'get'
  })
}
