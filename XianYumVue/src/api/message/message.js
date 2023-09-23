import request from '@/utils/request'

export function getMessageDetailInfo(id) {
  return request({
    url: '/v1/messageMonitor/getById?id='+id,
    method: 'get'
  })
}
