import request from '@/utils/request'



export function getAppVersionControlPage(query) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getPage',
    method: 'get',
    params: query
  })
}


export function saveAppVersionControl(data) {
  return request({
    url: '/xym-extension/v1/appVersionControl/save',
    method: 'post',
    data: data
  })
}

export function updateAppVersionControl(data) {
  return request({
    url: '/xym-extension/v1/appVersionControl/update',
    method: 'put',
    data: data
  })
}


export function delAppVersionControl(data) {
  return request({
    url: '/xym-extension/v1/appVersionControl/delete',
    method: 'delete',
    data: data
  })
}


export function getAppVersionControl(id) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getById/' + id,
    method: 'get'
  })
}


