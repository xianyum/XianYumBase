import request from '@/utils/request'


// 查询消息监控列表
export function getPageList(query) {
  return request({
    url: '/xianyum-message/v1/messageMonitor/getPage',
    method: 'get',
    params: query
  })
}
// 删除消息监控数据
export function delMessageMonitor(data) {
  return request({
    url: '/xianyum-message/v1/messageMonitor/delete',
    method: 'delete',
    data: data
  })
}

// 清空调度日志
export function cleanMessageMonitor() {
  return request({
    url: '/xianyum-message/v1/messageMonitor/truncate',
    method: 'delete'
  })
}

export function getMessageLogCount() {
  return request({
    url: '/xianyum-message/v1/messageMonitor/getMessageLogCount',
    method: 'get',
  })
}
