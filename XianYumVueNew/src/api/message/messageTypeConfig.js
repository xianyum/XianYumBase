import request from '@/utils/request'


export function getMessageTypeList() {
  return request({
    url: '/v1/messageTypeConfig/getList',
    method: 'get'
  })
}
