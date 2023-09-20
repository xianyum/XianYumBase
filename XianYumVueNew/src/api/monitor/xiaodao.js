import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function getPageList(query) {
  return request({
    url: '/xiaodao/getPage',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getDao(id) {
  return request({
    url: '/system/dao/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addDao(data) {
  return request({
    url: '/system/dao',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateDao(data) {
  return request({
    url: '/system/dao',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delDao(id) {
  return request({
    url: '/system/dao/' + id,
    method: 'delete'
  })
}
