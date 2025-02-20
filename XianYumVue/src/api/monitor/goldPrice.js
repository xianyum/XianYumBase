import request from '@/utils/request'

// 查询黄金列表
export function getGoldPricePage(query) {
  return request({
    url: '/xym-extension/v1/goldPrice/getPage',
    method: 'get',
    params: query
  })
}

// 获取最新金价
export function getGoldPriceLatestPrice() {
  return request({
    url: '/xym-extension/v1/goldPrice/getLatestPrice',
    method: 'get'
  })
}

// 获取金价趋势图
export function getGoldPriceTrend() {
  return request({
    url: '/xym-extension/v1/goldPrice/getTrend',
    method: 'get'
  })
}


// 获取K线图
export function getGoldPriceKLine() {
  return request({
    url: '/xym-extension/v1/goldPrice/getKLine',
    method: 'get'
  })
}
