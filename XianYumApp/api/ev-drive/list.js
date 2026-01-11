import request from '@/utils/request'


export function getEvDriveRecordsPage(query) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/getPage',
    method: 'get',
    params: query
  })
}


export function saveEvDriveRecords(data) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/save',
    method: 'post',
    data: data
  })
}

export function updateEvDriveRecords(data) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/update',
    method: 'put',
    data: data
  })
}


export function delEvDriveRecords(data) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/delete',
    method: 'delete',
    data: data
  })
}


export function getEvDriveRecords(id) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/getById/' + id,
    method: 'get'
  })
}


export function getEvDriveRecordsReportLineData(query) {
  return request({
    url: '/xym-extension/v1/evDriveRecords/getReportLineData',
    method: 'get',
    params: query
  })
}
