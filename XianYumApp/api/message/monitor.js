import request from '@/utils/request'


export function getMessageLogCount() {
  return request({
    url: '/xym-message/v1/messageMonitor/getMessageLogCount',
    method: 'get',
  })
}
