import request from '@/utils/request'


// 分页查询企微配置
export function getServerConfigPage(query) {
  return request({
    url: '/xyum-extension/v1/serverConfig/getPage',
    method: 'get',
    params: query
  })
}


// 查询企微配置详细
export function getServerConfigById(id) {
  return request({
    url: '/xyum-extension/v1/serverConfig/getById/' + id,
    method: 'get'
  })
}

// 查询企微配置
export function addServerConfig(data) {
  return request({
    url: '/xyum-extension/v1/serverConfig/save',
    method: 'post',
    data: data
  })
}

// 更新企微配置
export function updateServerConfig(data) {
  return request({
    url: '/xyum-extension/v1/serverConfig/update',
    method: 'put',
    data: data
  })
}


// 删除企微配置
export function delServerConfig(data) {
  return request({
    url: '/xyum-extension/v1/serverConfig/delete',
    method: 'delete',
    data: data
  })
}


// 使用企微配置测试发信
export function sendWechat(data) {
  return request({
    url: '/xym-message/v1/serverConfig/sendWechat',
    method: 'put',
    data: data
  })
}

