import request from '@/utils/request'

// 查询客户端管理列表
export function listProxy(query) {
  return request({
    url: '/xianyum-proxy/v1/proxy/getPage',
    method: 'get',
    params: query
  })
}

// 查询客户端管理详细
export function getProxy(id) {
  return request({
    url: '/xianyum-proxy/v1/proxy/getById/' + id,
    method: 'get'
  })
}

// 新增客户端管理
export function addProxy(data) {
  return request({
    url: '/xianyum-proxy/v1/proxy/save',
    method: 'post',
    data: data
  })
}

// 修改客户端管理
export function updateProxy(data) {
  return request({
    url: '/xianyum-proxy/v1/proxy/update',
    method: 'put',
    data: data
  })
}

// 删除客户端管理
export function delProxy(data) {
  return request({
    url: '/xianyum-proxy/v1/proxy/delete',
    method: 'delete',
    data: data
  })
}


// 发送配置
export function sendConfigByEmail(id) {
  return request({
    url: '/xianyum-proxy/v1/proxy/sendEmail/'+id,
    method: 'get'
  })
}

// 刷新远程配置数据
export function flushProxy() {
  return request({
    url: '/xianyum-proxy/v1/proxy/flushProxy/',
    method: 'put'
  })
}

export function getAllProxyList() {
  return request({
    url: '/xianyum-proxy/v1/proxy/getList',
    method: 'get'
  })
}


export function getProxyBindUser(id) {
  return request({
    url: '/xianyum-proxy/v1/proxy/getProxyBindUser?id='+id,
    method: 'get'
  })
}


export function getOnlineProxyCount() {
  return request({
    url: '/xianyum-proxy/v1/proxy/getOnlineProxyCount',
    method: 'get'
  })
}

export function getCurrentProxy() {
  return request({
    url: '/xianyum-proxy/v1/proxy/getCurrentProxy',
    method: 'get'
  })
}
