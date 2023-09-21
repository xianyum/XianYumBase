import request from '@/utils/request'


export function getMessageEmailConfigPage(query) {
  return request({
    url: '/v1/messageSendConfig/getPage',
    method: 'get',
    params: query
  })
}


export function getMessageSendConfigById(id) {
  return request({
    url: '/v1/messageSendConfig/getById/' + id,
    method: 'get'
  })
}

export function addMessageSendRelationConfig(data) {
  return request({
    url: '/v1/messageSendRelation/save',
    method: 'post',
    data: data
  })
}

export function updateMessageSendRelationConfig(data) {
  return request({
    url: '/v1/messageSendRelation/update',
    method: 'put',
    data: data
  })
}


export function delMessageSendConfig(data) {
  return request({
    url: '/v1/messageSendConfig/delete',
    method: 'delete',
    data: data
  })
}


export function getMessageSendRelationPage(query) {
  return request({
    url: '/v1/messageSendRelation/getPage',
    method: 'get',
    params: query
  })
}

export function saveOrUpdateSendConfig(data) {
  return request({
    url: '/v1/messageSendConfig/saveOrUpdate',
    method: 'post',
    data: data
  })
}


export function getMessageConfigByAccountType(data) {
  return request({
    url: '/v1/messageSendRelation/getMessageConfigByAccountType',
    method: 'post',
    data: data
  })
}



export function deleteMessageRelationById(id) {
  return request({
    url: '/v1/messageSendRelation/delete/'+id,
    method: 'delete'
  })
}


export function getMessageRelationById(id) {
  return request({
    url: '/v1/messageSendRelation/getById/' + id,
    method: 'get'
  })
}
