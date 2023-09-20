import request from '@/utils/request'
import data from '@/views/system/dict/data'

// 查询调度日志列表
export function listJobLog(data) {
  return request({
    url: '/v1/jobLog/getPage',
    method: 'post',
    data: data
  })
}

// 删除调度日志
export function delJobLog(data) {
  return request({
    url: '/v1/jobLog/delete',
    method: 'delete',
    data: data
  })
}

// 清空调度日志
export function cleanJobLog() {
  return request({
    url: '/v1/jobLog/truncateLog',
    method: 'delete'
  })
}
