import request from '@/utils/request'

// 查询黄金列表
export function getGoldPricePage(query) {
  return request({
    url: '/xym-extension/v1/goldPrice/getPage',
    method: 'get',
    params: query
  })
}
