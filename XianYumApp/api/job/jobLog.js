import request from '@/utils/request'

export function getJobLogCount() {
  return request({
    url: '/xym-sheduler/v1/jobLog/getJobLogCount',
    method: 'get'
  })
}
