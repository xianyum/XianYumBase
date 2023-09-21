import request from '@/utils/request'

// 查询客户端管理列表
export function listProxy(data) {
  return request({
    url: '/proxy/getPage',
    method: 'post',
    data: data
  })
}

// 查询客户端管理详细
export function getProxy(id) {
  return request({
    url: '/proxy/getById/' + id,
    method: 'get'
  })
}

// 新增客户端管理
export function addProxy(data) {
  return request({
    url: '/proxy/save',
    method: 'post',
    data: data
  })
}

// 修改客户端管理
export function updateProxy(data) {
  return request({
    url: '/proxy/update',
    method: 'put',
    data: data
  })
}

// 删除客户端管理
export function delProxy(data) {
  return request({
    url: '/proxy/delete',
    method: 'delete',
    data: data
  })
}


// 发送配置
export function sendConfigByEmail(id) {
  return request({
    url: '/proxy/sendEmail/'+id,
    method: 'get'
  })
}

// 刷新远程配置数据
export function flushProxy() {
  return request({
    url: '/proxy/flushProxy/',
    method: 'put'
  })
}

export function getAllProxyList() {
  return request({
    url: '/proxy/getList',
    method: 'get'
  })
}
