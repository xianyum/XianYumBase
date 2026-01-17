import request from '@/utils/request'


export function queryLatestData() {
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/queryLatestData',
    method: 'get'
  })
}

export function getReportLineData(data){
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/getReportLineData',
    method: 'post',
    data: data
  })
}


