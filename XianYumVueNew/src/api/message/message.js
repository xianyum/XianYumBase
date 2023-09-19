import request from '@/utils/request'

export function getMessageDetailInfo(data) {
  return request({
    url: '/v1/messageMonitor/getById',
    method: 'post',
    data: data
  })
}
