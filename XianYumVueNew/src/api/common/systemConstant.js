import request from '@/utils/request'

export function getPublicSystemConstant(key) {
  return request({
    url: '/systemConstant/getPublicConstant/'+key,
    method: 'get'
  })
}

export function getPrivateSystemConstant(key) {
  return request({
    url: '/systemConstant/getPrivateConstant/'+key,
    method: 'get'
  })
}
