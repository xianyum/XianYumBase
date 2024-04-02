import request from '@/utils/request'


// 分页查询邮箱配置
export function getMessageEmailConfigPage(query) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/getPage',
    method: 'get',
    params: query
  })
}


// 查询邮箱配置详细
export function getMessageEmailConfigById(id) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/getById/' + id,
    method: 'get'
  })
}

// 查询邮箱配置
export function addMessageEmailConfig(data) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/save',
    method: 'post',
    data: data
  })
}

// 更新邮箱配置
export function updateMessageEmailConfig(data) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/update',
    method: 'put',
    data: data
  })
}


// 删除邮箱配置
export function delMessageEmailConfig(data) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/delete',
    method: 'delete',
    data: data
  })
}


// 使用邮箱发送测试
export function sendEmail(data) {
  return request({
    url: '/xym-message/v1/messageConfigEmail/sendEmail',
    method: 'put',
    data: data
  })
}

