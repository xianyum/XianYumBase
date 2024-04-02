import request from '@/utils/request'

// 获取服务信息
export function getServer() {
  return request({
    url: '/xym-system/v1/server/get',
    method: 'get'
  })
}
