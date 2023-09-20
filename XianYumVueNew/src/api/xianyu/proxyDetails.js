import request from '@/utils/request'
import data from '@/views/system/dict/data'

// 查询客户端配置详细列表
export function listDetails(data) {
  return request({
    url: '/proxyDetails/getPage',
    method: 'post',
    data: data
  })
}

// 查询客户端配置详细详细
export function getDetails(id) {
  return request({
    url: '/proxyDetails/getById/' + id,
    method: 'get'
  })
}

// 新增客户端配置详细
export function addDetails(data) {
  return request({
    url: '/proxyDetails/save',
    method: 'post',
    data: data
  })
}

// 修改客户端配置详细
export function updateDetails(data) {
  return request({
    url: '/proxyDetails/update',
    method: 'put',
    data: data
  })
}

// 删除客户端配置详细
export function delDetails(data) {
  return request({
    url: '/proxyDetails/delete/',
    method: 'delete',
    data: data
  })
}
