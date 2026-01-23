import request from '@/utils/request'

export function getLastAppVersion(data) {
  return request({
    url: '/xym-extension/v1/appVersionControl/getLastAppVersion',
    method: 'post',
    data: data
  })
}

