import request from '@/utils/request'

// 登录方法
export function login(username, password, verifyCode, uuid,loginType,code) {
  const data = {
    username,
    password,
    verifyCode,
    uuid,
    loginType,
    code
  }
  return request({
    'url': '/login',
    headers: {
      isToken: false
    },
    'method': 'post',
    'data': data
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    'url': '/xym-system/v1/user/info',
    'method': 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    'url': '/logout',
    'method': 'post'
  })
}

// 查询验证码类型
export function getCaptchaType() {
  return request({
    url: '/captcha/getType',
    method: 'get'
  })
}

/**
 * 发送凭证
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function sendLoginCredentials(data) {
  return request({
    url: '/sendLoginCredentials',
    method: 'post',
    data: data
  })
}

