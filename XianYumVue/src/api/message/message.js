import request from '@/utils/request'

export function getMessageDetailInfo(id) {
  return request({
    url: '/xianyum-message/v1/messageMonitor/getById/'+id,
    method: 'get'
  })
}
