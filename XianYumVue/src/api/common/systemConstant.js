import request from '@/utils/request'

export function getPublicSystemConstant(key) {
  return request({
    url: '/xianyum-system/v1/systemConstant/getPublicConstant/'+key,
    method: 'get'
  })
}

export function getPrivateSystemConstant(key) {
  return request({
    url: '/xianyum-system/v1/systemConstant/getPrivateConstant/'+key,
    method: 'get'
  })
}
