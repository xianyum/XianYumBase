import request from '@/utils/request'

/**
 * 获取路由列表
 * @param {Object} queryParams - 请求查询参数
 * @returns {Promise} 返回请求Promise对象
 */
export const getRouters = (queryParams = {}) => {
  return request({
    url: '/xym-system/v1/menu/nav',
    method: 'get',
    params: queryParams
  })
}
