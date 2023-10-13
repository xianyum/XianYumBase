import request from '@/utils/request'


// 分页查询webhook配置
export function getMessageConfigWebhookPage(query) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/getPage',
    method: 'get',
    params: query
  })
}


// 查询webhook配置详细
export function getMessageConfigWebhookById(id) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/getById/' + id,
    method: 'get'
  })
}

// 查询webhook配置
export function addMessageConfigWebhook(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/save',
    method: 'post',
    data: data
  })
}

// 更新webhook配置
export function updateMessageConfigWebhook(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/update',
    method: 'put',
    data: data
  })
}


// 删除webhook配置
export function delMessageConfigWebhook(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/delete',
    method: 'delete',
    data: data
  })
}


// webhook发信测试
export function sendWebhook(data) {
  return request({
    url: '/xianyum-message/v1/messageConfigWebhook/sendWebhook',
    method: 'put',
    data: data
  })
}

