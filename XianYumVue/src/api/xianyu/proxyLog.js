import request from '@/utils/request'


// 查询远程代理日志接口
export function getProxyLogList(query) {
  return request({
    url: '/xym-proxy/v1/proxyLog/getPage',
    method: 'get',
    params: query
  })
}

// 删除远程代理日志接口
export function delProxyLog(data) {
  return request({
    url: '/xym-proxy/v1/proxyLog/delete',
    method: 'delete',
    data: data
  })
}


export function getLastProxyLog() {
  return request({
    url: '/xym-proxy/v1/proxyLog/getLastProxyLog',
    method: 'get'
  })
}
