import request from '@/utils/request'

// 查询操作日志列表
export function list(data) {
  return request({
    url: '/log/getPage',
    method: 'post',
    data: data
  })
}

// 删除操作日志
export function delOperLog(data) {
  return request({
    url: '/log/delete',
    method: 'delete',
    data: data
  })
}

// 清空操作日志
export function cleanOperLog() {
  return request({
    url: '/log/truncateLog',
    method: 'delete'
  })
}
