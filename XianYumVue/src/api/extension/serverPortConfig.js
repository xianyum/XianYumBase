import request from '@/utils/request'


// 分页查询企微配置
export function getServerPortConfigPage(query) {
  return request({
    url: '/xym-extension/v1/serverPortConfig/getPage',
    method: 'get',
    params: query
  })
}


// 查询企微配置详细
export function getServerPortConfigById(id) {
  return request({
    url: '/xym-extension/v1/serverPortConfig/getById/' + id,
    method: 'get'
  })
}

// 查询企微配置
export function addServerPortConfig(data) {
  return request({
    url: '/xym-extension/v1/serverPortConfig/save',
    method: 'post',
    data: data
  })
}

// 更新企微配置
export function updateServerPortConfig(data) {
  return request({
    url: '/xym-extension/v1/serverPortConfig/update',
    method: 'put',
    data: data
  })
}


// 删除企微配置
export function delServerPortConfig(data) {
  return request({
    url: '/xym-extension/v1/serverPortConfig/delete',
    method: 'delete',
    data: data
  })
}

