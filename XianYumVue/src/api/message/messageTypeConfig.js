import request from '@/utils/request'


export function getMessageTypeList() {
  return request({
    url: '/v1/messageTypeConfig/getList',
    method: 'get'
  })
}


// 分页查询消息类型配置
export function getMessageTypeConfigPage(query) {
  return request({
    url: '/v1/messageTypeConfig/getPage',
    method: 'get',
    params: query
  })
}


// 查询消息类型配置详细
export function getMessageTypeConfigById(id) {
  return request({
    url: '/v1/messageTypeConfig/getById/' + id,
    method: 'get'
  })
}

// 保存消息类型配置
export function addMessageTypeConfig(data) {
  return request({
    url: '/v1/messageTypeConfig/save',
    method: 'post',
    data: data
  })
}

// 更新消息类型配置
export function updateMessageTypeConfig(data) {
  return request({
    url: '/v1/messageTypeConfig/update',
    method: 'put',
    data: data
  })
}


// 删除消息类型配置
export function delMessageTypeConfig(data) {
  return request({
    url: '/v1/messageTypeConfig/delete',
    method: 'delete',
    data: data
  })
}




