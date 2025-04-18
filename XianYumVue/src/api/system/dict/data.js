import request from '@/utils/request'

// 查询字典数据列表
export function listData(query) {
  return request({
    url: '/xym-system/v1/dict/data/getPage',
    method: 'get',
    params: query
  })
}

// 查询字典数据详细
export function getData(id) {
  return request({
    url: '/xym-system/v1/dict/data/' + id,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return request({
    url: '/xym-system/v1/dict/data/type/' + dictType,
    method: 'get'
  })
}

// 新增字典数据
export function addData(data) {
  return request({
    url: '/xym-system/v1/dict/data/save',
    method: 'post',
    data: data
  })
}

// 修改字典数据
export function updateData(data) {
  return request({
    url: '/xym-system/v1/dict/data/update',
    method: 'put',
    data: data
  })
}

// 删除字典数据
export function delData(dictCode) {
  return request({
    url: '/xym-system/v1/dict/data/' + dictCode,
    method: 'delete'
  })
}
