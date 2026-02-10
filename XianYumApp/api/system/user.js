import upload from '@/utils/upload'
import request from '@/utils/request'

// 用户密码重置
export function updateUserPwd(password, newPassword) {
  const data = {
    password,
    newPassword
  }
  return request({
    url: '/xym-system/v1/user/password',
    method: 'put',
    data: data
  })
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/xym-system/v1/user/profile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/xym-system/v1/user/updateCurrentUser',
    method: 'put',
    data: data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return upload({
    url: '/xym-system/v1/user/upload',
    name: data.name,
    filePath: data.filePath
  })
}

// 获取绑定的三方用户列表
export function getCurrentUserThirdRelation() {
  return request({
    url: '/xym-system/v1/userThirdRelation/getCurrentUserThirdRelation',
    method: 'get'
  })
}

// 解绑三方用户
export function unbindUserThirdRelation(id) {
  return request({
    url: '/xym-system/v1/userThirdRelation/unbind',
    method: 'delete',
    data: id
  })
}