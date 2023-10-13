import request from '@/utils/request'


// 分页查询企微配置
export function getMessageWechatConfigPage(query) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/getPage',
    method: 'get',
    params: query
  })
}


// 查询企微配置详细
export function getMessageWechatConfigById(id) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/getById/' + id,
    method: 'get'
  })
}

// 查询企微配置
export function addMessageWechatConfig(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/save',
    method: 'post',
    data: data
  })
}

// 更新企微配置
export function updateMessageWechatConfig(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/update',
    method: 'put',
    data: data
  })
}


// 删除企微配置
export function delMessageWechatConfig(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/delete',
    method: 'delete',
    data: data
  })
}


// 使用企微配置测试发信
export function sendWechat(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWechat/sendWechat',
    method: 'put',
    data: data
  })
}

