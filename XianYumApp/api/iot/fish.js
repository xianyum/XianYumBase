import request from '@/utils/request'


export function queryLatestData() {
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/queryLatestData',
    method: 'get'
  })
}

export function queryMqttTotalCount() {
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/queryTotalCount',
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


export function doAiAnalysis() {
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/aiAnalysis',
    method: 'get'
  })
}


export function recordWaterChange(){
  return request({
    url: '/xianyum-mqtt/v1/mqttFish/recordWaterChange',
    method: 'post'
  })
}

