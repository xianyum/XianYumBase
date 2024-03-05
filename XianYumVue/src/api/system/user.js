import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/xianyum-system/v1/user/getPage',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(id) {
  return request({
    url: '/xianyum-system/v1/user/getById/'+id,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/xianyum-system/v1/user/save',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/xianyum-system/v1/user/update',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(data) {
  return request({
    url: '/xianyum-system/v1/user/delete',
    method: 'delete',
    data: data
  })
}

// 用户密码重置
export function resetUserPwd(userId, password) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/xianyum-system/v1/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 用户状态修改
export function changeUserStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/xianyum-system/v1/user/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/xianyum-system/v1/user/profile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/xianyum-system/v1/user/updateCurrentUser',
    method: 'put',
    data: data
  })
}

// 用户密码重置
export function updateUserPwd(oldPassword, newPassword) {

  const data = {
    "newPassword": newPassword,
    "password": oldPassword
  }
  return request({
    url: '/xianyum-system/v1/user/password',
    method: 'put',
    data: data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/xianyum-system/v1/user/upload',
    method: 'post',
    data: data
  })
}

// 查询授权角色
export function getAuthRole(userId) {
  return request({
    url: '/xianyum-system/v1/user/authRole/' + userId,
    method: 'get'
  })
}

// 保存授权角色
export function updateAuthRole(data) {
  return request({
    url: '/xianyum-system/v1/user/authRole',
    method: 'put',
    params: data
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/xianyum-system/v1/user/deptTree',
    method: 'get'
  })
}

// 获取绑定的三方用户列表
export function getCurrentUserThirdRelation() {
  return request({
    url: 'xianyum-system/v1/userThirdRelation/getCurrentUserThirdRelation',
    method: 'get'
  })
}
// 解绑三方用户
export function unbindUserThirdRelation(id) {
  return request({
    url: 'xianyum-system/v1/userThirdRelation/unbind',
    method: 'delete',
    data: id
  })
}
