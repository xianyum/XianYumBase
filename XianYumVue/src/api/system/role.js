import request from '@/utils/request'

// 查询角色列表
export function listRole(query) {
  return request({
    url: '/xym-system/v1/role/getPage',
    method: 'get',
    params: query
  })
}

// 查询角色详细
export function getRole(roleId) {
  return request({
    url: '/xym-system/v1/role/getById/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/xym-system/v1/role/save',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/xym-system/v1/role/update',
    method: 'put',
    data: data
  })
}

// 角色数据权限
export function dataScope(data) {
  return request({
    url: '/xym-system/v1/role/',
    method: 'put',
    data: data
  })
}

// 删除角色
export function delRole(data) {
  return request({
    url: '/xym-system/v1/role/delete',
    method: 'delete',
    data: data
  })
}


// 角色状态修改
export function changeRoleStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/xym-system/v1/role/changeStatus',
    method: 'put',
    data: data
  })
}

// 角色权限范围修改
export function changeDataScope(data) {
  return request({
    url: '/xym-system/v1/role/changeDataScope',
    method: 'put',
    data: data
  })
}

export function authorizationMenu(data) {
  return request({
    url: '/xym-system/v1/role/authorizationMenu',
    method: 'put',
    data: data
  })
}


export function getRoleList() {
  return request({
    url: '/xym-system/v1/role/getList',
    method: 'get'
  })
}


export function getUserByRoleId(roleId) {
  return request({
    url: '/xym-system/v1/user/getByRoleId/'+roleId,
    method: 'get'
  })
}
