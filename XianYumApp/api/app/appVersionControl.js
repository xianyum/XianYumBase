import request from '@/utils/request'

export function getLastAppVersion(data) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getLastAppVersion',
    method: 'post',
    data: data
  })
}

export function getAppVersionControlPage(query) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getPage',
    method: 'get',
    params: query
  })
}

export function getAppVersionControl(id) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getById/' + id,
    method: 'get'
  })
}