import request from '@/utils/request'


export function getEvDriveRecordsPage(query) {
  return request({
    url: 'xianyum-/v1/mqttFish',
    method: 'get',
    params: query
  })
}


