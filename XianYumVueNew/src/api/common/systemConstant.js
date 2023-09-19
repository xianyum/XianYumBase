import request from '@/utils/request'

export function getSystemConstant(key) {
  const requestParams = {
    "constantKey": key
  }
  return request({
    url: '/systemConstant/getPublicConstant',
    method: 'post',
    data: requestParams
  })
}
