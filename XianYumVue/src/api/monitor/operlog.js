import request from '@/utils/request'

// 查询操作日志列表
export function list(query) {
  return request({
    url: '/xym-system/v1/log/getPage',
    method: 'get',
    params: query
  })
}

// 删除操作日志
export function delOperLog(data) {
  return request({
    url: '/xym-system/v1/log/delete',
    method: 'delete',
    data: data
  })
}

// 清空操作日志
export function cleanOperLog() {
  return request({
    url: '/xym-system/v1/log/truncateLog',
    method: 'delete'
  })
}



export function getLogVisitCountCharts() {
  return request({
    url: '/xym-system/v1/log/getVisitCountCharts',
    method: 'get'
  })
}


export function getOperLogCount() {
  return request({
    url: '/xym-system/v1/log/getLogCount',
    method: 'get'
  })
}
