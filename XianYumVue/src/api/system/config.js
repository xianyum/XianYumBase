import request from '@/utils/request'

// 查询参数列表
export function listConfig(query) {
  return request({
    url: '/xym-system/v1/systemConstant/getPage',
    method: 'get',
    params: query
  })
}

// 查询参数详细
export function getConfig(configId) {
  return request({
    url: '/xym-system/v1/systemConstant/getById/'+configId,
    method: 'get'
  })
}

// 根据参数键名查询参数值
export function getConfigKey(configKey) {
  return request({
    url: '/xym-system/v1/system/config/configKey/' + configKey,
    method: 'get'
  })
}

// 新增参数配置
export function addConfig(data) {
  return request({
    url: '/xym-system/v1/systemConstant/save',
    method: 'post',
    data: data
  })
}

// 修改参数配置
export function updateConfig(data) {
  return request({
    url: '/xym-system/v1/systemConstant/update',
    method: 'put',
    data: data
  })
}

// 删除参数配置
export function delConfig(configId) {
  return request({
    url: '/xym-system/v1/systemConstant/delete?key=' + configId,
    method: 'delete'
  })
}

// 刷新参数缓存
export function refreshCache() {
  return request({
    url: '/xym-system/v1/systemConstant/refreshCache',
    method: 'delete'
  })
}

// 从缓存查看系统常量
export function getConfigByCache(query){
  return request({
    url: '/xym-system/v1/systemConstant/getRedisCache',
    method: 'get',
    params: query
  })
}


// 从缓存查看系统常量
export function deleteConfigByCache(query){
  return request({
    url: '/xym-system/v1/systemConstant/deleteRedisCache',
    method: 'get',
    params: query
  })
}
