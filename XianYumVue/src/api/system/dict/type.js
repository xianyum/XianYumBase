import request from '@/utils/request'

// 查询字典类型列表
export function listType(query) {
  return request({
    url: '/xym-system/v1/dict/type/getPage',
    method: 'get',
    params: query
  })
}

// 查询字典类型详细
export function getType(dictId) {
  return request({
    url: '/xym-system/v1/dict/type/' + dictId,
    method: 'get'
  })
}

// 新增字典类型
export function addType(data) {
  return request({
    url: '/xym-system/v1/dict/type/save',
    method: 'post',
    data: data
  })
}

// 修改字典类型
export function updateType(data) {
  return request({
    url: '/xym-system/v1/dict/type/update',
    method: 'put',
    data: data
  })
}

// 删除字典类型
export function delType(dictId) {
  return request({
    url: '/xym-system/v1/dict/type/' + dictId,
    method: 'delete'
  })
}

// 刷新字典缓存
export function refreshCache() {
  return request({
    url: '/xym-system/v1/dict/type/refreshCache',
    method: 'delete'
  })
}

// 获取字典选择框列表
export function optionselect() {
  return request({
    url: '/xym-system/v1/dict/type/optionSelect',
    method: 'get'
  })
}
