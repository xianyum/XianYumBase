import request from '@/utils/request'

// 分页查询 API Key
export function listApiKey(query) {
  return request({
    url: '/xym-system/v1/apiKey/getPage',
    method: 'get',
    params: query
  })
}

// 获取授权范围下拉列表
export function getScopeList() {
  return request({
    url: '/xym-system/v1/apiKey/getScopeList',
    method: 'get'
  })
}

// 查询 API Key 详细
export function getApiKey(id) {
  return request({
    url: '/xym-system/v1/apiKey/getById/' + id,
    method: 'get'
  })
}

// 新增 API Key
export function addApiKey(data) {
  return request({
    url: '/xym-system/v1/apiKey/save',
    method: 'post',
    data: data
  })
}

// 修改 API Key
export function updateApiKey(data) {
  return request({
    url: '/xym-system/v1/apiKey/update',
    method: 'put',
    data: data
  })
}

// 删除 API Key
export function delApiKey(ids) {
  const idStr = Array.isArray(ids) ? ids.join(',') : ids
  return request({
    url: '/xym-system/v1/apiKey/' + idStr,
    method: 'delete'
  })
}
