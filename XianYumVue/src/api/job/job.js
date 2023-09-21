import request from '@/utils/request'

// 查询定时任务调度列表
export function listJob(query) {
  return request({
    url: '/v1/job/getPage',
    method: 'get',
    params: query
  })
}

// 查询定时任务调度详细
export function getJob(jobId) {
  return request({
    url: '/v1/job/getById/'+jobId,
    method: 'get'
  })
}

// 新增定时任务调度
export function addJob(data) {
  return request({
    url: '/v1/job/save',
    method: 'post',
    data: data
  })
}

// 修改定时任务调度
export function updateJob(data) {
  return request({
    url: '/v1/job/update',
    method: 'put',
    data: data
  })
}

// 删除定时任务调度
export function delJob(data) {
  return request({
    url: '/v1/job/delete',
    method: 'delete',
    data: data
  })
}

// 任务状态修改
export function changeJobStatus(data) {
  return request({
    url: '/v1/job/changeStatus',
    method: 'put',
    data: data
  })
}


// 定时任务立即执行一次
export function runJob(jobId) {
  let requestParams = {
    jobId: jobId
  }
  return request({
    url: '/v1/job/runOnce',
    method: 'put',
    data: requestParams
  })
}
