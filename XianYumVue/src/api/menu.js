import request from '@/utils/request'

// 获取路由
export const getRouters = () => {
  return request({
    url: '/xianyum-system/v1/menu/nav',
    method: 'get'
  })
}
