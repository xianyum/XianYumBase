import request from '@/utils/request'

export function getMessageAccountTypeList(data) {
  return request({
    url: '/systemConstant/getPublicConstant',
    method: 'post',
    data: data
  })
}
