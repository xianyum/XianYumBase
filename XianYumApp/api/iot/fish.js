import request from '@/utils/request'


export function queryLatestData() {
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/queryLatestData',
    method: 'get'
  })
}


