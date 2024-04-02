import request from '@/utils/request'
import data from '@/views/system/dict/data'

// 查询调度日志列表
export function listJobLog(query) {
  return request({
    url: '/xym-sheduler/v1/jobLog/getPage',
    method: 'get',
    params: query
  })
}

// 删除调度日志
export function delJobLog(data) {
  return request({
    url: '/xym-sheduler/v1/jobLog/delete',
    method: 'delete',
    data: data
  })
}

// 清空调度日志
export function cleanJobLog() {
  return request({
    url: '/xym-sheduler/v1/jobLog/truncateLog',
    method: 'delete'
  })
}

export function getJobLogCount() {
  return request({
    url: '/xym-sheduler/v1/jobLog/getJobLogCount',
    method: 'get'
  })
}
