import request from '@/utils/request'

// 查询参数列表
export function listConfig(query) {
  return request({
    url: '/systemConstant/getPage',
    method: 'post',
    data: query
  })
}

// 查询参数详细
export function getConfig(configId) {
  const requestParam = {
    "id":configId
  }
  return request({
    url: '/systemConstant/getById',
    method: 'post',
    data: requestParam
  })
}

// 根据参数键名查询参数值
export function getConfigKey(configKey) {
  return request({
    url: '/system/config/configKey/' + configKey,
    method: 'get'
  })
}

// 新增参数配置
export function addConfig(data) {
  return request({
    url: '/systemConstant/save',
    method: 'post',
    data: data
  })
}

// 修改参数配置
export function updateConfig(data) {
  return request({
    url: '/systemConstant/update',
    method: 'put',
    data: data
  })
}

// 删除参数配置
export function delConfig(configId) {
  return request({
    url: '/systemConstant/delete?key=' + configId,
    method: 'delete'
  })
}

// 刷新参数缓存
export function refreshCache() {
  return request({
    url: '/systemConstant/refreshCache',
    method: 'delete'
  })
}

// 从缓存查看系统常量
export function getConfigByCache(query){
  return request({
    url: '/systemConstant/getRedisCache',
    method: 'get',
    params: query
  })
}


// 从缓存查看系统常量
export function deleteConfigByCache(query){
  return request({
    url: '/systemConstant/deleteRedisCache',
    method: 'get',
    params: query
  })
}
